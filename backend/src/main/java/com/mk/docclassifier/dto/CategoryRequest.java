package com.mk.docclassifier.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CategoryRequest {

    @NotBlank(message = "Name is required")
    @Size(max = 255, message = "Name must be at most 255 characters")
    private String name;

    @Size(max = 2048, message = "Description must be at most 2048 characters")
    private String description;

    @Size(max = 16, message = "Color must be at most 16 characters")
    private String color;
}
