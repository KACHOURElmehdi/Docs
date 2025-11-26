package com.mk.docclassifier.repository;

import com.mk.docclassifier.domain.entity.Document;
import com.mk.docclassifier.domain.entity.DocumentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {
        List<Document> findByStatus(DocumentStatus status);

        List<Document> findByUserId(Long userId);

        @org.springframework.data.jpa.repository.Query("SELECT d FROM Document d LEFT JOIN d.category c WHERE " +
                        "(:query IS NULL OR :query = '' OR LOWER(d.originalFilename) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(d.ocrText) LIKE LOWER(CONCAT('%', :query, '%'))) AND "
                        +
                        "(:category IS NULL OR :category = '' OR c.name = :category)")
        org.springframework.data.domain.Page<Document> searchDocuments(String query, String category,
                        org.springframework.data.domain.Pageable pageable);
}
