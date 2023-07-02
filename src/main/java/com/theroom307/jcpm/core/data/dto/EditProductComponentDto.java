package com.theroom307.jcpm.core.data.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.Optional;

@Schema(name = "Edit Product's Components Request Body")
public record EditProductComponentDto(

        @JsonProperty("component_id")
        @Schema(name = "component_id", example = "1")
        @NotNull(message = "'component_id' is required")
        Long componentId,

        @Schema(example = "true", defaultValue = "false")
        boolean add,

        @Schema(example = "false", defaultValue = "false")
        boolean remove,

        @Schema(example = "1", defaultValue = "1")
        Optional<@Positive(message = "Quantity must be greater than 0") Integer> quantity
) {
    public int getQuantity() {
        return quantity.orElse(1);
    }
}
