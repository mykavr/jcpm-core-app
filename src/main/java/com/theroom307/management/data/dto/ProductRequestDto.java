package com.theroom307.management.data.dto;

import com.theroom307.management.data.model.Product;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

@Schema(name = "Create Product Request Body")
public record ProductRequestDto(
        @Schema(example = "Product Name", requiredMode = REQUIRED)
        @NotBlank(message = "Product name is required")
        String name,

        @Schema(example = "Product description.")
        String description
) {
    public Product toEntity() {
        var entity = new Product();
        entity.setName(name);
        entity.setDescription(description);
        return entity;
    }
}
