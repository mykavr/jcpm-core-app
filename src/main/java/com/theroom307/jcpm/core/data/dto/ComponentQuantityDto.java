package com.theroom307.jcpm.core.data.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ComponentQuantityDto {

    @JsonProperty("quantity")
    @NotNull(message = "'quantity' is required")
    @Min(value = 1, message = "Quantity must be greater than zero")
    private Integer quantity;
}