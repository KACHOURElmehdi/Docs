package com.mk.docclassifier.controller;

import com.mk.docclassifier.domain.entity.DocumentStatus;
import com.mk.docclassifier.domain.entity.Role;
import com.mk.docclassifier.domain.entity.User;
import com.mk.docclassifier.repository.CategoryRepository;
import com.mk.docclassifier.repository.DocumentRepository;
import com.mk.docclassifier.repository.TagRepository;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/stats")
@RequiredArgsConstructor
public class StatsController {

    private final DocumentRepository documentRepository;
    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;

    @GetMapping("/overview")
    public ResponseEntity<Map<String, Object>> getOverview(@AuthenticationPrincipal User user) {
        Map<String, Object> stats = new HashMap<>();
        
        if (user.getRole() == Role.ADMIN) {
            // Admin sees global stats
            stats.put("totalDocuments", documentRepository.count());
            stats.put("processedDocuments", documentRepository.countByStatus(DocumentStatus.PROCESSED));
            stats.put("errorDocuments", documentRepository.countByStatus(DocumentStatus.ERROR));
            stats.put("totalCategories", categoryRepository.count());
            stats.put("totalTags", tagRepository.count());
            Double avgConfidence = documentRepository.averageConfidence();
            stats.put("averageConfidence", avgConfidence != null ? avgConfidence : 0.0);
        } else {
            // Normal user sees only their own stats
            Long userId = user.getId();
            stats.put("totalDocuments", documentRepository.countByUserId(userId));
            stats.put("processedDocuments", documentRepository.countByUserIdAndStatus(userId, DocumentStatus.PROCESSED));
            stats.put("errorDocuments", documentRepository.countByUserIdAndStatus(userId, DocumentStatus.ERROR));
            stats.put("totalCategories", documentRepository.countDistinctCategoriesByUserId(userId));
            stats.put("totalTags", documentRepository.countDistinctTagsByUserId(userId));
            Double avgConfidence = documentRepository.averageConfidenceByUserId(userId);
            stats.put("averageConfidence", avgConfidence != null ? avgConfidence : 0.0);
        }
        
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/categories")
    public ResponseEntity<List<Map<String, Object>>> getCategoryStats(@AuthenticationPrincipal User user) {
        Map<String, Long> categoryCounts = new HashMap<>();
        
        if (user.getRole() == Role.ADMIN) {
            // Admin sees all documents
            documentRepository.findAll().forEach(doc -> {
                String category = doc.getCategory() != null ? doc.getCategory().getName() : "Uncategorized";
                categoryCounts.put(category, categoryCounts.getOrDefault(category, 0L) + 1);
            });
        } else {
            // Normal user sees only their own documents
            documentRepository.findByUserId(user.getId()).forEach(doc -> {
                String category = doc.getCategory() != null ? doc.getCategory().getName() : "Uncategorized";
                categoryCounts.put(category, categoryCounts.getOrDefault(category, 0L) + 1);
            });
        }

        List<Map<String, Object>> result = categoryCounts.entrySet().stream()
                .map(entry -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("name", entry.getKey());
                    map.put("count", entry.getValue());
                    return map;
                })
                .toList();

        return ResponseEntity.ok(result);
    }
}
