package com.mk.docclassifier.service;

import com.mk.docclassifier.domain.entity.Document;

public interface ClassificationService {
    void classify(Document document);
}
