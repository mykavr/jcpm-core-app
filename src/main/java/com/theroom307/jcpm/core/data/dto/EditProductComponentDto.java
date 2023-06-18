package com.theroom307.jcpm.core.data.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(name = "Edit Product's Components Request Body")
public record EditProductComponentDto(

        @JsonProperty("component_id")
        @Schema(name = "component_id", example = "1")
        @NotNull(message = "'component_id' is required")
        Long componentId,

        @Schema(example = "true", defaultValue = "false")
        boolean add,

        @Schema(example = "false", defaultValue = "false")
        boolean remove
) {
}
