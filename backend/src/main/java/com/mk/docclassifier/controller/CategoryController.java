package com.mk.docclassifier.controller;

import com.mk.docclassifier.domain.entity.User;
import com.mk.docclassifier.dto.CategoryRequest;
import com.mk.docclassifier.dto.CategoryResponse;
import com.mk.docclassifier.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getAllCategories(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(categoryService.getAllCategories(user));
    }

    @PostMapping
    public ResponseEntity<?> createCategory(@Valid @RequestBody CategoryRequest request,
            @AuthenticationPrincipal User user) {
        try {
            return ResponseEntity.ok(categoryService.createCategory(request, user));
        } catch (org.springframework.security.access.AccessDeniedException ex) {
            return ResponseEntity.status(org.springframework.http.HttpStatus.FORBIDDEN).body(ex.getMessage());
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(org.springframework.http.HttpStatus.CONFLICT).body(ex.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCategory(@PathVariable Long id,
            @Valid @RequestBody CategoryRequest request,
            @AuthenticationPrincipal User user) {
        try {
            return ResponseEntity.ok(categoryService.updateCategory(id, request, user));
        } catch (org.springframework.security.access.AccessDeniedException ex) {
            return ResponseEntity.status(org.springframework.http.HttpStatus.FORBIDDEN).body(ex.getMessage());
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(org.springframework.http.HttpStatus.CONFLICT).body(ex.getMessage());
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCategory(@PathVariable Long id,
            @AuthenticationPrincipal User user) {
        try {
            categoryService.deleteCategory(id, user);
            return ResponseEntity.noContent().build();
        } catch (org.springframework.security.access.AccessDeniedException ex) {
            return ResponseEntity.status(org.springframework.http.HttpStatus.FORBIDDEN).body(ex.getMessage());
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }
}
