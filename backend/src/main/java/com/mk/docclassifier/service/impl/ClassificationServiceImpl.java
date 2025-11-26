package com.mk.docclassifier.service.impl;

import com.mk.docclassifier.domain.entity.Document;
import com.mk.docclassifier.service.ClassificationService;
import org.springframework.stereotype.Service;

@Service
@lombok.RequiredArgsConstructor
public class ClassificationServiceImpl implements ClassificationService {

    private final com.mk.docclassifier.repository.CategoryRepository categoryRepository;

    @Override
    public void classify(Document document) {
        // Mock classification
        String categoryName = "INVOICE";
        com.mk.docclassifier.domain.entity.Category category = categoryRepository.findAll().stream()
                .filter(c -> c.getName().equalsIgnoreCase(categoryName))
                .findFirst()
                .orElseGet(() -> categoryRepository
                        .save(com.mk.docclassifier.domain.entity.Category.builder().name(categoryName).build()));

        document.setCategory(category);
        document.setConfidence(0.95);
    }
}
