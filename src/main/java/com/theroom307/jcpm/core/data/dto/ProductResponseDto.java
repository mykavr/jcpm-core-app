package com.theroom307.jcpm.core.data.dto;

import com.theroom307.jcpm.core.data.model.Product;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.With;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Schema(name = "Get Product Response Body")
@With
public record ProductResponseDto (
        @Schema(example = "1")
        Long id,

        @Schema(example = "Product Name")
        String name,

        @Schema(example = "Product description.")
        String description,

        @Schema(example = "2023-03-19T12:31:16Z")
        String created,

        @Schema(example = "2023-03-19T12:31:16Z")
        String modified
) {
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ISO_INSTANT.withZone(ZoneOffset.UTC);

    public static ProductResponseDto fromEntity(Product entity) {
        return new ProductResponseDto(
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
