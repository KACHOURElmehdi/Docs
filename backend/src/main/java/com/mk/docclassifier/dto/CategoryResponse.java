package com.mk.docclassifier.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;

@Value
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CategoryResponse {
    Long id;
    String name;
    String description;
    String color;
    long documentCount;
    String createdByUsername;
    Long createdByUserId;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
