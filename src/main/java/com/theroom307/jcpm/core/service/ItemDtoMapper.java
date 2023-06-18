package com.theroom307.jcpm.core.service;

import com.theroom307.jcpm.core.data.dto.ComponentRequestDto;
import com.theroom307.jcpm.core.data.dto.ComponentResponseDto;
import com.theroom307.jcpm.core.data.dto.ProductRequestDto;
import com.theroom307.jcpm.core.data.dto.ProductResponseDto;
import com.theroom307.jcpm.core.data.dto.wrapper.ListResponseWrapper;
import com.theroom307.jcpm.core.data.model.Component;
import com.theroom307.jcpm.core.data.model.Product;
import org.springframework.data.domain.Page;

public interface ItemDtoMapper {

    Product map(ProductRequestDto productRequestDto);

    ProductResponseDto map(Product product);

    Component map(ComponentRequestDto componentRequestDto);

    ComponentResponseDto map(Component component);

    ListResponseWrapper<ProductResponseDto> mapProducts(Page<Product> items);

    ListResponseWrapper<ComponentResponseDto> mapComponents(Page<Component> items);

}
