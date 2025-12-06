package com.mk.docclassifier.controller;

import com.mk.docclassifier.domain.entity.Document;
import com.mk.docclassifier.domain.entity.User;
import com.mk.docclassifier.service.DocumentService;
import com.mk.docclassifier.service.StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.util.List;

@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;
    private final StorageService storageService;

    @PostMapping("/upload")
    public ResponseEntity<Document> uploadDocument(
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal User user) throws IOException {
        return ResponseEntity.ok(documentService.uploadDocument(file, user.getId()));
    }

    @GetMapping
    public ResponseEntity<List<Document>> getAllDocuments(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(documentService.getDocumentsForUser(user));
    }

    @GetMapping("/{id}")
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public ResponseEntity<Document> getDocument(@PathVariable Long id, @AuthenticationPrincipal User user) {
        try {
            Document document = documentService.getDocumentForUser(id, user);
            return ResponseEntity.ok(document);
        } catch (org.springframework.security.access.AccessDeniedException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{id}/file")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long id, @AuthenticationPrincipal User user)
            throws MalformedURLException {
        try {
            Document document = documentService.getDocumentForUser(id, user);

            Path filePath = storageService.load(document.getFilename());
            Resource resource = new UrlResource(filePath.toUri());

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(document.getContentType()))
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + document.getOriginalFilename() + "\"")
                    .body(resource);
        } catch (org.springframework.security.access.AccessDeniedException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/search")
    public ResponseEntity<org.springframework.data.domain.Page<Document>> searchDocuments(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String status,
            org.springframework.data.domain.Pageable pageable,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(documentService.searchDocuments(q, category, status, user, pageable));
    }

    @PostMapping("/{id}/reclassify")
    public ResponseEntity<Document> reclassifyDocument(
            @PathVariable Long id,
            @RequestBody java.util.Map<String, Object> body,
            @AuthenticationPrincipal User user) {
        try {
            Object categoryIdObj = body.get("categoryId");
            if (categoryIdObj == null) {
                return ResponseEntity.badRequest().build();
            }
            Long categoryId = Long.valueOf(categoryIdObj.toString());
            return ResponseEntity.ok(documentService.reclassifyDocument(id, categoryId, user));
        } catch (org.springframework.security.access.AccessDeniedException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDocument(@PathVariable Long id, @AuthenticationPrincipal User user) {
        try {
            documentService.deleteDocument(id, user);
            return ResponseEntity.noContent().build();
        } catch (org.springframework.security.access.AccessDeniedException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }

}
