package com.mk.docclassifier.service.impl;

import com.mk.docclassifier.domain.entity.Document;
import com.mk.docclassifier.domain.entity.DocumentStatus;
import com.mk.docclassifier.domain.entity.User;
import com.mk.docclassifier.repository.DocumentRepository;
import com.mk.docclassifier.repository.UserRepository;
import com.mk.docclassifier.service.DocumentService;
import com.mk.docclassifier.service.PipelineService;
import com.mk.docclassifier.service.StorageService;
import lombok.RequiredArgsConstructor;
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
    public List<Document> getAllDocuments() {
        return documentRepository.findAll();
    }

    @Override
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public Optional<Document> getDocument(Long id) {
        return documentRepository.findById(id);
    }

    @Override
    public Document save(Document document) {
        return documentRepository.save(document);
    }

    @Override
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public org.springframework.data.domain.Page<Document> searchDocuments(String query, String category,
            org.springframework.data.domain.Pageable pageable) {
        return documentRepository.searchDocuments(query, category, pageable);
    }

    @Override
    public Document reclassifyDocument(Long id, Long categoryId) {
        Document document = getDocument(id)
                .orElseThrow(() -> new RuntimeException("Document not found"));

        com.mk.docclassifier.domain.entity.Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        document.setCategory(category);
        return documentRepository.save(document);
    }
}
