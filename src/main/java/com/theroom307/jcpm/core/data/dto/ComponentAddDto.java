package com.theroom307.jcpm.core.data.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ComponentAddDto {

    @JsonProperty("component_id")
    @NotNull(message = "'component_id' is required")
    private Long componentId;

    @JsonProperty("quantity")
    @Min(value = 1, message = "Quantity must be greater than zero")
    private int quantity = 1; // Default value if not provided
}