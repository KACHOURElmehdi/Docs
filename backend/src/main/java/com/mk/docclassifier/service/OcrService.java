package com.mk.docclassifier.service;

import java.io.File;

public interface OcrService {
    String extractText(File file);
}
