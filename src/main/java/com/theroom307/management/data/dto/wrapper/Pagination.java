package com.theroom307.management.data.dto.wrapper;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.domain.Page;

@AllArgsConstructor
@Getter
public class Pagination {

    @Schema(description = "Page index", example = "0")
    private long page;

    @Schema(description = "The size of the returned page", example = "10")
    private long size;

    @Schema(description = "The number of items in the response", example = "1")
    private long count;

    @Schema(description = "Total number of items", example = "1")
    private long total;

    static Pagination from(Page<?> page) {
        var pageable = page.getPageable();
        return new Pagination(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                page.getNumberOfElements(),
                page.getTotalElements()
        );
    }
}
