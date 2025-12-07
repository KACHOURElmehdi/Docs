package com.mk.docclassifier.service;

import com.mk.docclassifier.domain.entity.Category;
import com.mk.docclassifier.domain.entity.Role;
import com.mk.docclassifier.domain.entity.User;
import com.mk.docclassifier.dto.CategoryRequest;
import com.mk.docclassifier.dto.CategoryResponse;
import com.mk.docclassifier.repository.CategoryRepository;
import com.mk.docclassifier.repository.DocumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final DocumentRepository documentRepository;

    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public CategoryResponse createCategory(CategoryRequest request, User user) {
        // Allow all authenticated users to create categories
        if (user == null) {
            throw new AccessDeniedException("Authentication required to create categories");
        }

        if (categoryRepository.existsByNameIgnoreCase(request.getName())) {
            throw new IllegalArgumentException("Category name already exists");
        }

        Category category = Category.builder()
                .name(request.getName().trim())
                .description(valueOrNull(request.getDescription()))
                .color(valueOrNull(request.getColor()))
                .createdBy(user)
                .build();

        Category saved = categoryRepository.save(category);
        return toResponse(saved);
    }

    @Transactional
    public CategoryResponse updateCategory(Long id, CategoryRequest request, User user) {
        ensureAdmin(user);

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        if (!category.getName().equalsIgnoreCase(request.getName())
                && categoryRepository.existsByNameIgnoreCase(request.getName())) {
            throw new IllegalArgumentException("Category name already exists");
        }

        category.setName(request.getName().trim());
        category.setDescription(valueOrNull(request.getDescription()));
        category.setColor(valueOrNull(request.getColor()));

        Category saved = categoryRepository.save(category);
        return toResponse(saved);
    }

    @Transactional
    public void deleteCategory(Long id, User user) {
        ensureAdmin(user);

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        // Optional: ensure no documents assigned? For now allow deletion; set category null in docs
        var documents = documentRepository.findByCategoryId(id);
        documents.forEach(document -> document.setCategory(null));
        if (!documents.isEmpty()) {
            documentRepository.saveAll(documents);
        }

        categoryRepository.delete(category);
    }

    private CategoryResponse toResponse(Category category) {
        long docCount = category.getId() == null ? 0 : documentRepository.countByCategoryId(category.getId());
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .color(category.getColor())
                .documentCount(docCount)
                .createdByUsername(category.getCreatedBy() != null ? category.getCreatedBy().getUsername() : null)
                .createdByUserId(category.getCreatedBy() != null ? category.getCreatedBy().getId() : null)
                .createdAt(category.getCreatedAt())
                .updatedAt(category.getUpdatedAt())
                .build();
    }

    private void ensureAdmin(User user) {
        if (user == null || user.getRole() != Role.ADMIN) {
            throw new AccessDeniedException("Only administrators can manage categories");
        }
    }

    private String valueOrNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
