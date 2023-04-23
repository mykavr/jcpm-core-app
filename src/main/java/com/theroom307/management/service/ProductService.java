package com.theroom307.management.service;

import com.theroom307.management.data.dto.ProductRequestDto;
import com.theroom307.management.data.dto.ProductResponseDto;
import org.springframework.data.domain.Page;

public interface ProductService {

    Page<ProductResponseDto> getProducts(int page, int size);

    ProductResponseDto getProduct(long productId);

    long createProduct(ProductRequestDto productDto);

    void deleteProduct(long productId);
}
