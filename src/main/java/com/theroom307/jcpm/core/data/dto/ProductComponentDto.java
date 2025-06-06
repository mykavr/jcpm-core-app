package com.theroom307.jcpm.core.data.dto;

import com.theroom307.jcpm.core.data.model.Component;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.With;

@Schema(name = "Product Component Response")
@With
public record ProductComponentDto(
        @Schema(description = "Component details")
        ComponentResponseDto component,

        @Schema(example = "2", description = "Quantity of this component in the product")
        Integer quantity
) {
        public static ProductComponentDto from(Component component, Integer quantity) {
                return new ProductComponentDto(ComponentResponseDto.fromEntity(component), quantity);
        }
}
