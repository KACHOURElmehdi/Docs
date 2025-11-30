package com.mk.docclassifier.service;

import com.mk.docclassifier.domain.entity.Document;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface DocumentService {
    Document uploadDocument(MultipartFile file, Long userId) throws IOException;

    List<Document> getAllDocuments();

    Optional<Document> getDocument(Long id);

    Document save(Document document);

    org.springframework.data.domain.Page<Document> searchDocuments(String query, String category,
            org.springframework.data.domain.Pageable pageable);

    Document reclassifyDocument(Long id, Long categoryId);

    void deleteDocument(Long id);

}
