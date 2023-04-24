package com.theroom307.management.service.impl;

import com.theroom307.management.controller.exception.ProductNotFoundException;
import com.theroom307.management.data.dto.ProductRequestDto;
import com.theroom307.management.data.dto.ProductResponseDto;
import com.theroom307.management.data.model.Product;
import com.theroom307.management.data.repository.ProductRepository;
import com.theroom307.management.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;


@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Override
    public Page<ProductResponseDto> getProducts(int page, int size) {
        var pageable = PageRequest.of(page, size);
        var pageOfProducts = productRepository.findAll(pageable);
        return getPageOfProductDto(pageOfProducts, pageable);
    }

    private Page<ProductResponseDto> getPageOfProductDto(
            Page<Product> pageOfProducts,

            // When there are no products, JPA (unexpectedly) returns null
            // which is why we cannot retrieve it from pageOfProducts:
            // pageOfProducts.getPageable() would lead to an NPE
            Pageable pageable
    ) {
        var listOfProducts = getProductDtoList(pageOfProducts);
        var total = pageOfProducts == null ? 0 : pageOfProducts.getTotalElements();
        return new PageImpl<>(listOfProducts, pageable, total);
    }

    private List<ProductResponseDto> getProductDtoList(Page<Product> pageOfProducts) {
        if (pageOfProducts == null) {
            return Collections.emptyList();
        }
        return pageOfProducts.stream()
                .map(ProductResponseDto::fromEntity)
                .toList();
    }

    @Override
    public ProductResponseDto getProduct(long productId) {
        var product = productRepository.findById(productId);
        if (product.isPresent()) {
            return ProductResponseDto.fromEntity(product.get());
        } else {
            throw new ProductNotFoundException(productId);
        }
    }

    @Override
    public long createProduct(ProductRequestDto productDto) {
        var entity = productDto.toEntity();
        var savedEntity = productRepository.save(entity);
        return savedEntity.getId();
    }

    @Override
    public void deleteProduct(long productId) {
        productRepository.deleteById(productId);
    }
}
