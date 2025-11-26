package com.mk.docclassifier.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "documents")
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String filename;

    @Column(nullable = false)
    private String originalFilename;

    @Column(nullable = false)
    private String storagePath;

    @Column(nullable = false)
    private String contentType;

    private long size;

    @Enumerated(EnumType.STRING)
    private DocumentStatus status;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    private Double confidence;

    @Column(columnDefinition = "TEXT")
    private String ocrText;

    private String errorMessage;

    @com.fasterxml.jackson.annotation.JsonIgnore
    @Builder.Default
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "document_tags", joinColumns = @JoinColumn(name = "document_id"), inverseJoinColumns = @JoinColumn(name = "tag_id"))
    private Set<Tag> tags = new HashSet<>();

    @CreationTimestamp
    private LocalDateTime uploadedAt;

    @UpdateTimestamp
    private LocalDateTime processedAt;

    @com.fasterxml.jackson.annotation.JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}
