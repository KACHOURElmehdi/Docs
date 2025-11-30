package com.mk.docclassifier.service.impl;

import com.mk.docclassifier.domain.entity.AuditLog;
import com.mk.docclassifier.domain.entity.Document;
import com.mk.docclassifier.domain.entity.DocumentStatus;
import com.mk.docclassifier.repository.AuditLogRepository;
import com.mk.docclassifier.repository.DocumentRepository;
import com.mk.docclassifier.service.ClassificationService;
import com.mk.docclassifier.service.OcrService;
import com.mk.docclassifier.service.PipelineService;
import com.mk.docclassifier.service.SseService;
import com.mk.docclassifier.service.StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Path;

@Service
@RequiredArgsConstructor
public class PipelineServiceImpl implements PipelineService {

    private final DocumentRepository documentRepository;
    private final StorageService storageService;
    private final OcrService ocrService;
    private final ClassificationService classificationService;
    private final AuditLogRepository auditLogRepository;
    private final SseService sseService;

    @Async
    @Override
    @org.springframework.transaction.annotation.Transactional
    public void processDocument(Long documentId) {
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Document not found"));

        try {
            updateStatus(document, DocumentStatus.PROCESSING);
            logAction(document, "PROCESS_START", "Started processing document");
            sseService.sendEvent(documentId, "PROCESSING_STARTED", "Processing started");

            Path filePath = storageService.load(document.getFilename());
            File file = filePath.toFile();

            // OCR
            String text = ocrService.extractText(file);
            document.setOcrText(text);
            logAction(document, "OCR_DONE", "OCR completed");
            sseService.sendEvent(documentId, "OCR_DONE", "OCR completed");

            // Classification
            classificationService.classify(document);
            logAction(document, "CLASSIFIED", "Classified as " + document.getCategory());
            sseService.sendEvent(documentId, "CLASSIFIED", "Classified as " + document.getCategory());

            updateStatus(document, DocumentStatus.PROCESSED);
            logAction(document, "PROCESS_COMPLETE", "Processing completed successfully");
            sseService.sendEvent(documentId, "COMPLETED", "Processing completed");

        } catch (Exception e) {
            document.setErrorMessage(e.getMessage());
            updateStatus(document, DocumentStatus.ERROR);
            logAction(document, "PROCESS_ERROR", "Error: " + e.getMessage());
            sseService.sendEvent(documentId, "ERROR", "Error: " + e.getMessage());
        }
    }

    private void updateStatus(Document document, DocumentStatus status) {
        document.setStatus(status);
        documentRepository.save(document);
    }

    private void logAction(Document document, String action, String details) {
        AuditLog log = AuditLog.builder()
                .documentId(document.getId())
                .action(action)
                .details(details)
                .username(document.getUser() != null ? document.getUser().getEmail() : "SYSTEM")
                .build();
        auditLogRepository.save(log);
    }
}
