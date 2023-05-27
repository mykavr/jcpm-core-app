package com.theroom307.jcpm.core.data.dto.wrapper;

import com.theroom307.jcpm.core.data.dto.IResponseDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;

@Schema(name = "Get Product List Response Body")
@Builder
public record ListResponseWrapper<T extends IResponseDto> (List<T> data, Pagination pagination) {
}
