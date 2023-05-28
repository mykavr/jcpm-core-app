package com.theroom307.jcpm.core.data.dto.wrapper;

import com.theroom307.jcpm.core.data.dto.IResponseDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Schema(name = "Get Product List Response Body")
@Builder
@Getter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class ListResponseWrapper<T extends IResponseDto> {
    private List<T> data;
    private Pagination pagination;
}
