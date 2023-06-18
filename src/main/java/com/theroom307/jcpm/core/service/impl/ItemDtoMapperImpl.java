package com.theroom307.jcpm.core.service.impl;

import com.theroom307.jcpm.core.data.dto.ComponentRequestDto;
import com.theroom307.jcpm.core.data.dto.ComponentResponseDto;
import com.theroom307.jcpm.core.data.dto.ProductRequestDto;
import com.theroom307.jcpm.core.data.dto.ProductResponseDto;
import com.theroom307.jcpm.core.data.dto.wrapper.ListResponseWrapper;
import com.theroom307.jcpm.core.data.dto.wrapper.Pagination;
import com.theroom307.jcpm.core.data.model.Component;
import com.theroom307.jcpm.core.data.model.Product;
import com.theroom307.jcpm.core.service.ItemDtoMapper;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
public class ItemDtoMapperImpl implements ItemDtoMapper {
    @Override
    public Product map(ProductRequestDto productRequestDto) {
        return productRequestDto.toEntity();
    }

    @Override
    public ProductResponseDto map(Product product) {
        return ProductResponseDto.fromEntity(product);
    }

    @Override
    public Component map(ComponentRequestDto componentRequestDto) {
        return componentRequestDto.toEntity();
    }

    @Override
    public ComponentResponseDto map(Component component) {
        return ComponentResponseDto.fromEntity(component);
    }

    @Override
    public ListResponseWrapper<ProductResponseDto> mapProducts(Page<Product> items) {
        var pagination = Pagination.from(items);
        var data = items.stream().map(ProductResponseDto::fromEntity).toList();
        return ListResponseWrapper.<ProductResponseDto>builder()
                .pagination(pagination)
                .data(data)
                .build();
    }

    @Override
    public ListResponseWrapper<ComponentResponseDto> mapComponents(Page<Component> items) {
        var pagination = Pagination.from(items);
        var data = items.stream().map(ComponentResponseDto::fromEntity).toList();
        return ListResponseWrapper.<ComponentResponseDto>builder()
                .pagination(pagination)
                .data(data)
                .build();
    }
}
