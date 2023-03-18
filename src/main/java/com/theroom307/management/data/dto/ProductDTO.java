package com.theroom307.management.data.dto;

import com.theroom307.management.data.model.Product;
import lombok.With;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@With
public record ProductDTO(
        Long id,
        String name,
        String description,
        String created,
        String modified
) {
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ISO_INSTANT.withZone(ZoneOffset.UTC);

    public static ProductDTO fromEntity(Product entity) {
        return new ProductDTO(
                entity.getId(),
                entity.getName(),
                entity.getDescription(),
                dateTimeToString(entity.getCreated()),
                dateTimeToString(entity.getModified())
        );
    }

    public Product toEntity() {
        var entity = new Product();
        entity.setId(id);
        entity.setName(name);
        entity.setDescription(description);
        entity.setCreated(stringToDateTime(created));
        entity.setModified(stringToDateTime(modified));
        return entity;
    }

    private static String dateTimeToString(ZonedDateTime dateTime) {
        return dateTime == null
                ? null
                : DATE_TIME_FORMATTER.format(dateTime.withNano(0));
    }

    private static ZonedDateTime stringToDateTime(String timestamp) {
        return timestamp == null
                ? null
                : ZonedDateTime.parse(timestamp, DATE_TIME_FORMATTER);
    }
}
