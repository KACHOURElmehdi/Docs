package com.mk.docclassifier.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class CategoryResponse {
    Long id;
    String name;
    String description;
    String color;
    long documentCount;
}
