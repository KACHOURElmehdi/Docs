package com.mk.docclassifier.service.impl;

import com.mk.docclassifier.service.OcrService;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class OcrServiceImpl implements OcrService {
    @Override
    public String extractText(File file) {
        // Mock OCR implementation
        return "Extracted text content from " + file.getName();
    }
}
