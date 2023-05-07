package com.theroom307.management.data.dto.wrapper;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;

@Schema(name = "Get Product List Response Body")
@Builder
public record ListResponseWrapper<T> (List<T> data, Pagination pagination) {
}
