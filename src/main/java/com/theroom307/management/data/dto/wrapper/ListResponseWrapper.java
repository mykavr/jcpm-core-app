package com.theroom307.management.data.dto.wrapper;

import com.theroom307.management.data.dto.ProductResponseDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

@Schema(name = "Get Product List Response Body")
@Getter
public class ListResponseWrapper<T> {

    private List<T> data;

    private Pagination pagination;

    public static ListResponseWrapper<ProductResponseDto> wrapperFor(
            @NotNull
            Page<ProductResponseDto> pageOfProducts
    ) {
        var wrapper = new ListResponseWrapper<ProductResponseDto>();
        wrapper.data = pageOfProducts.getContent();
        wrapper.pagination = Pagination.from(pageOfProducts);
        return wrapper;
    }
}
