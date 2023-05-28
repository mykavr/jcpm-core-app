package com.theroom307.jcpm.core.data.dto;

import com.theroom307.jcpm.core.data.model.Item;
import com.theroom307.jcpm.core.data.model.Product;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public interface IResponseDto {

    DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ISO_INSTANT.withZone(ZoneOffset.UTC);

    static String dateTimeToString(ZonedDateTime dateTime) {
        return dateTime == null
                ? null
                : DATE_TIME_FORMATTER.format(dateTime.withNano(0));
    }

    static IResponseDto fromEntity(Item entity) {
        return entity instanceof Product
                ? ProductResponseDto.fromEntity(entity)
                : ComponentResponseDto.fromEntity(entity);
    }

}
