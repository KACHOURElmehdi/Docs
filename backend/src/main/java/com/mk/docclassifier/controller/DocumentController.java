package com.mk.docclassifier.controller;

import com.mk.docclassifier.domain.entity.Document;
import com.mk.docclassifier.domain.entity.User;
import com.mk.docclassifier.service.DocumentService;
import com.mk.docclassifier.service.StorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Documents", description = "Document management APIs - Upload, search, classify and manage documents")
public class DocumentController {

    private final DocumentService documentService;
    private final StorageService storageService;

    @Operation(
            summary = "Upload a document",
            description = "Upload a document file (PDF, DOCX, images, etc.) for OCR processing and automatic classification"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Document uploaded successfully",
                    content = @Content(schema = @Schema(implementation = Document.class))),
            @ApiResponse(responseCode = "400", description = "Invalid file"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Document> uploadDocument(
            @Parameter(description = "File to upload (PDF, DOCX, PNG, JPG, etc.)", required = true)
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal User user) throws IOException {
        return ResponseEntity.ok(documentService.uploadDocument(file, user.getId()));
    }

    @Operation(summary = "Get all documents", description = "Retrieve all documents for the authenticated user (admins see all)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Documents retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping
    public ResponseEntity<List<Document>> getAllDocuments(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(documentService.getDocumentsForUser(user));
    }

    @Operation(summary = "Get document by ID", description = "Retrieve a specific document by its ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Document found"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Document not found")
    })
    @GetMapping("/{id}")
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public ResponseEntity<Document> getDocument(
            @Parameter(description = "Document ID", required = true) @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        try {
            Document document = documentService.getDocumentForUser(id, user);
            return ResponseEntity.ok(document);
        } catch (org.springframework.security.access.AccessDeniedException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Download document file", description = "Download the original document file")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "File downloaded successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Document not found")
    })
    @GetMapping("/{id}/file")
    public ResponseEntity<Resource> downloadFile(
            @Parameter(description = "Document ID", required = true) @PathVariable Long id,
            @AuthenticationPrincipal User user)
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

    @Operation(
            summary = "Search documents",
            description = "Search documents by content, filename, category or status with pagination"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Search results returned"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/search")
    public ResponseEntity<org.springframework.data.domain.Page<Document>> searchDocuments(
            @Parameter(description = "Search query (searches in filename and OCR text)") @RequestParam(required = false) String q,
            @Parameter(description = "Filter by category name") @RequestParam(required = false) String category,
            @Parameter(description = "Filter by status (UPLOADED, PROCESSING, PROCESSED, ERROR)") @RequestParam(required = false) String status,
            @Parameter(description = "Pagination parameters") org.springframework.data.domain.Pageable pageable,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(documentService.searchDocuments(q, category, status, user, pageable));
    }

    @Operation(summary = "Reclassify document", description = "Manually change the category of a document")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Document reclassified successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Document or category not found")
    })
    @PostMapping("/{id}/reclassify")
    public ResponseEntity<Document> reclassifyDocument(
            @Parameter(description = "Document ID", required = true) @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Category ID to assign")
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

    @Operation(summary = "Delete document", description = "Delete a document and its associated file")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Document deleted successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Document not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDocument(
            @Parameter(description = "Document ID", required = true) @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        try {
            documentService.deleteDocument(id, user);
            return ResponseEntity.noContent().build();
        } catch (org.springframework.security.access.AccessDeniedException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Add tag to document", description = "Associate a tag with a document")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tag added successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Document or tag not found")
    })
    @PostMapping("/{id}/tags/{tagId}")
    public ResponseEntity<Document> addTagToDocument(
            @Parameter(description = "Document ID", required = true) @PathVariable Long id,
            @Parameter(description = "Tag ID", required = true) @PathVariable Long tagId,
            @AuthenticationPrincipal User user) {
        try {
            return ResponseEntity.ok(documentService.addTagToDocument(id, tagId, user));
        } catch (org.springframework.security.access.AccessDeniedException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Remove tag from document", description = "Remove a tag association from a document")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tag removed successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Document or tag not found")
    })
    @DeleteMapping("/{id}/tags/{tagId}")
    public ResponseEntity<Document> removeTagFromDocument(
            @Parameter(description = "Document ID", required = true) @PathVariable Long id,
            @Parameter(description = "Tag ID", required = true) @PathVariable Long tagId,
            @AuthenticationPrincipal User user) {
        try {
            return ResponseEntity.ok(documentService.removeTagFromDocument(id, tagId, user));
        } catch (org.springframework.security.access.AccessDeniedException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }

}
