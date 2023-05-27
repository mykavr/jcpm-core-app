package com.theroom307.jcpm.core.data.dto;

import com.theroom307.jcpm.core.data.model.Item;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.With;

@Schema(name = "Get Component Response Body")
@With
public record ComponentResponseDto(
        @Schema(example = "1")
        Long id,

        @Schema(example = "Component Name")
        String name,

        @Schema(example = "Component description.")
        String description,

        @Schema(example = "2023-03-19T12:31:16Z")
        String created,

        @Schema(example = "2023-03-19T12:31:16Z")
        String modified
) implements IResponseDto {

    public static ComponentResponseDto fromEntity(Item entity) {
        return new ComponentResponseDto(
                entity.getId(),
                entity.getName(),
                entity.getDescription(),
                IResponseDto.dateTimeToString(entity.getCreated()),
                IResponseDto.dateTimeToString(entity.getModified())
        );
    }
}
