package com.mk.docclassifier.controller;

import com.mk.docclassifier.service.DataSeederService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final DataSeederService dataSeederService;

    @PostMapping("/seed-data")
    public ResponseEntity<Map<String, String>> seedData() {
        try {
            dataSeederService.seedCategoriesAndTags();
            return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Database seeded successfully with categories and tags"
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                "status", "error",
                "message", "Failed to seed data: " + e.getMessage()
            ));
        }
    }
}
