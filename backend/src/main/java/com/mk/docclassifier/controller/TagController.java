package com.mk.docclassifier.controller;

import com.mk.docclassifier.domain.entity.User;
import com.mk.docclassifier.dto.TagRequest;
import com.mk.docclassifier.dto.TagResponse;
import com.mk.docclassifier.service.TagService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tags")
@RequiredArgsConstructor
public class TagController {

    private final TagService tagService;

    @GetMapping
    public ResponseEntity<List<TagResponse>> getAllTags() {
        return ResponseEntity.ok(tagService.getAllTags());
    }

    @PostMapping
    public ResponseEntity<?> createTag(@Valid @RequestBody TagRequest request,
            @AuthenticationPrincipal User user) {
        try {
            return ResponseEntity.ok(tagService.createTag(request, user));
        } catch (AccessDeniedException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateTag(@PathVariable Long id,
            @Valid @RequestBody TagRequest request,
            @AuthenticationPrincipal User user) {
        try {
            return ResponseEntity.ok(tagService.updateTag(id, request, user));
        } catch (AccessDeniedException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTag(@PathVariable Long id,
            @AuthenticationPrincipal User user) {
        try {
            tagService.deleteTag(id, user);
            return ResponseEntity.noContent().build();
        } catch (AccessDeniedException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }
}
