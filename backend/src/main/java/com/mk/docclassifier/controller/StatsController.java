package com.mk.docclassifier.controller;

import com.mk.docclassifier.domain.entity.DocumentStatus;
import com.mk.docclassifier.repository.DocumentRepository;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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

    @GetMapping("/overview")
    public ResponseEntity<Map<String, Object>> getOverview() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalDocuments", documentRepository.count());
        stats.put("processed", documentRepository.findByStatus(DocumentStatus.PROCESSED).size());
        stats.put("errors", documentRepository.findByStatus(DocumentStatus.ERROR).size());
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/categories")
    public ResponseEntity<List<Map<String, Object>>> getCategoryStats() {
        // Since we don't have a direct "groupBy" in repository yet, we can do it in
        // Java or add a custom query.
        // For simplicity, let's fetch all and group by category.
        // Or better, add a query to repository.
        // Let's use a simple Java grouping for now as the dataset is likely small.
        Map<String, Long> categoryCounts = new HashMap<>();
        documentRepository.findAll().forEach(doc -> {
            String category = doc.getCategory() != null ? doc.getCategory().getName() : "Uncategorized";
            categoryCounts.put(category, categoryCounts.getOrDefault(category, 0L) + 1);
        });

        List<Map<String, Object>> result = categoryCounts.entrySet().stream()
                .map(entry -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("category", entry.getKey());
                    map.put("count", entry.getValue());
                    return map;
                })
                .toList();

        return ResponseEntity.ok(result);
    }
}
