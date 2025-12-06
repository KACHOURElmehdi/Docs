package com.mk.docclassifier.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class TagResponse {
    Long id;
    String name;
    String color;
    long documentCount;
}
