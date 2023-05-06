package com.theroom307.management.data.dto.wrapper;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public record Pagination(
        @Schema(description = "Page index", example = "0")
        long page,

        @Schema(description = "The size of the returned page", example = "10")
        long size,

        @Schema(description = "The number of items in the response", example = "1")
        long count,

        @Schema(description = "Total number of items", example = "1")
        long total
) {

    public static Pagination from(Page<?> page) {
        var pageable = page.getPageable();
        return new Pagination(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                page.getNumberOfElements(),
                page.getTotalElements()
        );
    }

    public static Pagination forEmptyPage(Pageable pageable) {
        return new Pagination(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                0,
                0
        );
    }
}
