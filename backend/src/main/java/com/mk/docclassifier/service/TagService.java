package com.mk.docclassifier.service;

import com.mk.docclassifier.domain.entity.Role;
import com.mk.docclassifier.domain.entity.Tag;
import com.mk.docclassifier.domain.entity.User;
import com.mk.docclassifier.dto.TagRequest;
import com.mk.docclassifier.dto.TagResponse;
import com.mk.docclassifier.repository.DocumentRepository;
import com.mk.docclassifier.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TagService {

    private final TagRepository tagRepository;
    private final DocumentRepository documentRepository;

    @Transactional(readOnly = true)
    public List<TagResponse> getAllTags(User user) {
        return tagRepository.findAll().stream()
                .map(tag -> toResponse(tag, user))
                .collect(Collectors.toList());
    }

    @Transactional
    public TagResponse createTag(TagRequest request, User user) {
        // Allow all authenticated users to create tags
        if (user == null) {
            throw new AccessDeniedException("Authentication required to create tags");
        }

        if (tagRepository.existsByNameIgnoreCase(request.getName())) {
            throw new IllegalArgumentException("Tag name already exists");
        }

        Tag tag = Tag.builder()
                .name(request.getName().trim())
                .color(valueOrNull(request.getColor()))
                .createdBy(user)
                .build();

        Tag saved = tagRepository.save(tag);
        return toResponse(saved, user);
    }

    @Transactional
    public TagResponse updateTag(Long id, TagRequest request, User user) {
        ensureAdmin(user);

        Tag tag = tagRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tag not found"));

        if (!tag.getName().equalsIgnoreCase(request.getName())
                && tagRepository.existsByNameIgnoreCase(request.getName())) {
            throw new IllegalArgumentException("Tag name already exists");
        }

        tag.setName(request.getName().trim());
        tag.setColor(valueOrNull(request.getColor()));

        Tag saved = tagRepository.save(tag);
        return toResponse(saved, user);
    }

    @Transactional
    public void deleteTag(Long id, User user) {
        ensureAdmin(user);

        Tag tag = tagRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tag not found"));

        // Remove tag from all documents
        documentRepository.findByTagsContaining(tag).forEach(doc -> {
            doc.getTags().remove(tag);
        });

        tagRepository.delete(tag);
    }

    private TagResponse toResponse(Tag tag, User user) {
        long docCount = 0;
        if (tag.getId() != null && user != null) {
            // For regular users, count only their documents; for admins, count all
            if (user.getRole() == Role.ADMIN) {
                docCount = documentRepository.countByTagsContaining(tag);
            } else {
                docCount = documentRepository.countByTagsContainingAndUserId(tag, user.getId());
            }
        }
        return TagResponse.builder()
                .id(tag.getId())
                .name(tag.getName())
                .color(tag.getColor())
                .documentCount(docCount)
                .createdByUsername(tag.getCreatedBy() != null ? tag.getCreatedBy().getUsername() : null)
                .createdByUserId(tag.getCreatedBy() != null ? tag.getCreatedBy().getId() : null)
                .createdAt(tag.getCreatedAt())
                .updatedAt(tag.getUpdatedAt())
                .build();
    }

    private void ensureAdmin(User user) {
        if (user == null || user.getRole() != Role.ADMIN) {
            throw new AccessDeniedException("Only administrators can manage tags");
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
