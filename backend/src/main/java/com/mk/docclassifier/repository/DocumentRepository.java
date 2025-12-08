package com.mk.docclassifier.repository;

import com.mk.docclassifier.domain.entity.Document;
import com.mk.docclassifier.domain.entity.DocumentStatus;
import com.mk.docclassifier.domain.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {
        List<Document> findByStatus(DocumentStatus status);

        List<Document> findByUserId(Long userId);

        List<Document> findByCategoryId(Long categoryId);

        long countByCategoryId(Long categoryId);

        List<Document> findByTagsContaining(Tag tag);

        long countByTagsContaining(Tag tag);

        long countByCategoryIdAndUserId(Long categoryId, Long userId);

        long countByTagsContainingAndUserId(Tag tag, Long userId);

        long countByStatus(DocumentStatus status);

        @Query("SELECT d FROM Document d LEFT JOIN d.category c WHERE "
                        + "(:query IS NULL OR :query = '' OR LOWER(d.originalFilename) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(d.ocrText) LIKE LOWER(CONCAT('%', :query, '%'))) AND "
                        + "(:category IS NULL OR :category = '' OR c.name = :category) AND "
                        + "(:status IS NULL OR d.status = :status) AND "
                        + "(:userId IS NULL OR d.user.id = :userId)")
        org.springframework.data.domain.Page<Document> searchDocuments(@Param("query") String query,
                        @Param("category") String category, @Param("status") DocumentStatus status,
                        @Param("userId") Long userId,
                        org.springframework.data.domain.Pageable pageable);

        @Query("SELECT AVG(d.confidence) FROM Document d WHERE d.confidence IS NOT NULL")
        Double averageConfidence();

        // User-specific counts
        long countByUserId(Long userId);

        long countByUserIdAndStatus(Long userId, DocumentStatus status);

        @Query("SELECT AVG(d.confidence) FROM Document d WHERE d.confidence IS NOT NULL AND d.user.id = :userId")
        Double averageConfidenceByUserId(@Param("userId") Long userId);

        @Query("SELECT COUNT(DISTINCT d.category.id) FROM Document d WHERE d.user.id = :userId AND d.category IS NOT NULL")
        long countDistinctCategoriesByUserId(@Param("userId") Long userId);

        @Query("SELECT COUNT(DISTINCT t.id) FROM Document d JOIN d.tags t WHERE d.user.id = :userId")
        long countDistinctTagsByUserId(@Param("userId") Long userId);
}
