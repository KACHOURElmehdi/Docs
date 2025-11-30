package com.mk.docclassifier.controller;

import com.mk.docclassifier.domain.entity.Document;
import com.mk.docclassifier.domain.entity.User;
import com.mk.docclassifier.service.DocumentService;
import com.mk.docclassifier.service.StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
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
    public ResponseEntity<List<Document>> getAllDocuments() {
        return ResponseEntity.ok(documentService.getAllDocuments());
    }

    @GetMapping("/{id}")
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public ResponseEntity<Document> getDocument(@PathVariable Long id) {
        return documentService.getDocument(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/file")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long id) throws MalformedURLException {
        Document document = documentService.getDocument(id)
                .orElseThrow(() -> new RuntimeException("Document not found"));

        Path filePath = storageService.load(document.getFilename());
        Resource resource = new UrlResource(filePath.toUri());

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(document.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + document.getOriginalFilename() + "\"")
                .body(resource);
    }

    @GetMapping("/search")
    public ResponseEntity<org.springframework.data.domain.Page<Document>> searchDocuments(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String category,
            org.springframework.data.domain.Pageable pageable) {
        return ResponseEntity.ok(documentService.searchDocuments(q, category, pageable));
    }

    @PostMapping("/{id}/reclassify")
    public ResponseEntity<Document> reclassifyDocument(
            @PathVariable Long id,
            @RequestParam Long category) {
        return ResponseEntity.ok(documentService.reclassifyDocument(id, category));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDocument(@PathVariable Long id) {
        documentService.deleteDocument(id);
        return ResponseEntity.noContent().build();
    }

}
