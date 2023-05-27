package com.theroom307.jcpm.core.data.dto;

import com.theroom307.jcpm.core.data.model.Component;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.With;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

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
) {
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ISO_INSTANT.withZone(ZoneOffset.UTC);

    public static ComponentResponseDto fromEntity(Component entity) {
        return new ComponentResponseDto(
                entity.getId(),
                entity.getName(),
                entity.getDescription(),
                dateTimeToString(entity.getCreated()),
                dateTimeToString(entity.getModified())
        );
    }

    private static String dateTimeToString(ZonedDateTime dateTime) {
        return dateTime == null
                ? null
                : DATE_TIME_FORMATTER.format(dateTime.withNano(0));
    }
}
