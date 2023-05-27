package com.theroom307.jcpm.core.data.dto;

import com.theroom307.jcpm.core.data.model.Component;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(name = "Create Component Request Body")
public record ComponentRequestDto(
        @Schema(example = "Component Name")
        @NotBlank(message = "Component name is required")
        String name,

        @Schema(example = "Component description.")
        String description
) implements IRequestDto<Component> {
    public Component toEntity() {
        var entity = new Component();
        entity.setName(name);
        entity.setDescription(description);
        return entity;
    }
}
