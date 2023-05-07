package com.theroom307.jcpm.core.service;

import com.theroom307.jcpm.core.data.dto.ProductRequestDto;
import com.theroom307.jcpm.core.data.dto.ProductResponseDto;
import com.theroom307.jcpm.core.data.dto.wrapper.ListResponseWrapper;

public interface ProductService {

    ListResponseWrapper<ProductResponseDto> getProducts(int page, int size);

    ProductResponseDto getProduct(long productId);

    long createProduct(ProductRequestDto productDto);

    void editProduct(long productId, ProductRequestDto productDto);

    void deleteProduct(long productId);
}
