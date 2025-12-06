package com.mk.docclassifier.service.impl;

import com.mk.docclassifier.domain.entity.Document;
import com.mk.docclassifier.domain.entity.DocumentStatus;
import com.mk.docclassifier.domain.entity.Role;
import com.mk.docclassifier.domain.entity.User;
import com.mk.docclassifier.repository.DocumentRepository;
import com.mk.docclassifier.repository.UserRepository;
import com.mk.docclassifier.service.DocumentService;
import com.mk.docclassifier.service.PipelineService;
import com.mk.docclassifier.service.StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DocumentServiceImpl implements DocumentService {

    private final DocumentRepository documentRepository;
    private final StorageService storageService;
    private final UserRepository userRepository;
    private final PipelineService pipelineService;
    private final com.mk.docclassifier.repository.CategoryRepository categoryRepository;

    @Override
    public Document uploadDocument(MultipartFile file, Long userId) throws IOException {
        String filename = storageService.store(file);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Document document = Document.builder()
                .filename(filename)
                .originalFilename(file.getOriginalFilename())
                .contentType(file.getContentType())
                .size(file.getSize())
                .storagePath(storageService.load(filename).toString())
                .status(DocumentStatus.UPLOADED)
                .user(user)
                .build();

        document = documentRepository.save(document);

        // Trigger pipeline
        pipelineService.processDocument(document.getId());

        return document;
    }

    @Override
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public List<Document> getDocumentsForUser(User user) {
        if (isAdmin(user)) {
            return documentRepository.findAll();
        }
        return documentRepository.findByUserId(user.getId());
    }

    @Override
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public Optional<Document> getDocument(Long id) {
        return documentRepository.findById(id);
    }

    @Override
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public Document getDocumentForUser(Long id, User user) {
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Document not found"));

        if (!isAdmin(user) && (document.getUser() == null || !document.getUser().getId().equals(user.getId()))) {
            throw new AccessDeniedException("You do not have permission to access this document");
        }

        return document;
    }

    @Override
    public Document save(Document document) {
        return documentRepository.save(document);
    }

    @Override
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public org.springframework.data.domain.Page<Document> searchDocuments(String query, String category, String status,
            User user, org.springframework.data.domain.Pageable pageable) {
        Long userId = isAdmin(user) ? null : user.getId();
        DocumentStatus docStatus = null;
        if (status != null && !status.isBlank()) {
            try {
                docStatus = DocumentStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException ignored) {
                // Invalid status, ignore filter
            }
        }
        return documentRepository.searchDocuments(query, category, docStatus, userId, pageable);
    }

    @Override
    public Document reclassifyDocument(Long id, Long categoryId, User user) {
        Document document = getDocumentForUser(id, user);

        com.mk.docclassifier.domain.entity.Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        document.setCategory(category);
        return documentRepository.save(document);
    }

    @Override
    public void deleteDocument(Long id, User user) {
        Document document = getDocumentForUser(id, user);

        // Delete physical file
        try {
            storageService.delete(document.getFilename());
        } catch (Exception e) {
            // Log error but continue with database deletion
            System.err.println("Failed to delete file: " + e.getMessage());
        }

        // Delete from database
        documentRepository.deleteById(id);
    }

    private boolean isAdmin(User user) {
        return user != null && user.getRole() == Role.ADMIN;
    }
}
