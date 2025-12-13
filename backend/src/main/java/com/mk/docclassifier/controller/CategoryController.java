package com.mk.docclassifier.controller;

import com.mk.docclassifier.domain.entity.User;
import com.mk.docclassifier.dto.CategoryRequest;
import com.mk.docclassifier.dto.CategoryResponse;
import com.mk.docclassifier.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@Tag(name = "Categories", description = "Category management APIs - CRUD operations for document categories")
public class CategoryController {

    private final CategoryService categoryService;

    @Operation(summary = "Get all categories", description = "Retrieve all document categories")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Categories retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping
    public ResponseEntity<?> getAllCategories(@AuthenticationPrincipal User user) {
        try {
            return ResponseEntity.ok(categoryService.getAllCategories(user));
        } catch (org.springframework.security.access.AccessDeniedException ex) {
            return ResponseEntity.status(org.springframework.http.HttpStatus.FORBIDDEN).body(ex.getMessage());
        }
    }

    @Operation(summary = "Create category", description = "Create a new document category")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Category created successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "409", description = "Category name already exists")
    })
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

    @Operation(summary = "Update category", description = "Update an existing category")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Category updated successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Category not found"),
            @ApiResponse(responseCode = "409", description = "Category name already exists")
    })
    @PutMapping("/{id}")
    public ResponseEntity<?> updateCategory(
            @Parameter(description = "Category ID", required = true) @PathVariable Long id,
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

    @Operation(summary = "Delete category", description = "Delete a category by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Category deleted successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Category not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCategory(
            @Parameter(description = "Category ID", required = true) @PathVariable Long id,
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
