package com.mk.docclassifier.service;

import com.mk.docclassifier.domain.entity.Document;
import com.mk.docclassifier.domain.entity.User;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface DocumentService {
    Document uploadDocument(MultipartFile file, Long userId) throws IOException;

        List<Document> getDocumentsForUser(User user);

        Optional<Document> getDocument(Long id);

        Document getDocumentForUser(Long id, User user);

    Document save(Document document);

        org.springframework.data.domain.Page<Document> searchDocuments(String query, String category, String status,
            User user, org.springframework.data.domain.Pageable pageable);

        Document reclassifyDocument(Long id, Long categoryId, User user);

        void deleteDocument(Long id, User user);

}
