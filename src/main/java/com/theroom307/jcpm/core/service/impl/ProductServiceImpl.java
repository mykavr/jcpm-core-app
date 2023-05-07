package com.theroom307.jcpm.core.service.impl;

import com.theroom307.jcpm.core.controller.exception.ProductNotFoundException;
import com.theroom307.jcpm.core.data.model.Product;
import com.theroom307.jcpm.core.data.repository.ProductRepository;
import com.theroom307.jcpm.core.service.ProductService;
import com.theroom307.jcpm.core.controller.exception.BadRequestException;
import com.theroom307.jcpm.core.data.dto.ProductRequestDto;
import com.theroom307.jcpm.core.data.dto.ProductResponseDto;
import com.theroom307.jcpm.core.data.dto.wrapper.ListResponseWrapper;
import com.theroom307.jcpm.core.data.dto.wrapper.Pagination;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Override
    public ListResponseWrapper<ProductResponseDto> getProducts(int page, int size) {
        var pageable = PageRequest.of(page, size);
        var pageOfProducts = productRepository.findAll(pageable);

        //noinspection ConstantConditions
        if (pageOfProducts == null) {
            // JPA (unexpectedly) returns null when there are no products
            return ListResponseWrapper.<ProductResponseDto>builder()
                    .data(Collections.emptyList())
                    .pagination(Pagination.forEmptyPage(pageable))
                    .build();
        }

        return ListResponseWrapper.<ProductResponseDto>builder()
                .data(getProductDtoList(pageOfProducts))
                .pagination(Pagination.from(pageOfProducts))
                .build();
    }

    private List<ProductResponseDto> getProductDtoList(Page<Product> pageOfProducts) {
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
    public void editProduct(long productId, ProductRequestDto productDto) {
        var newName = productDto.name();
        var newDescription = productDto.description();

        checkThatProductCanBeUpdated(newName, newDescription);

        var product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));

        if (newName != null && !product.getName().equals(newName)) {
            productRepository.updateNameById(newName, productId);
        }

        if (newDescription != null && !product.getDescription().equals(newDescription)) {
            productRepository.updateDescriptionById(newDescription, productId);
        }
    }

    private void checkThatProductCanBeUpdated(String newName, String newDescription) {
        if (newName == null && newDescription == null) {
            throw new BadRequestException("New value for the product name or description must be provided");
        }

        if (newName != null && newName.isBlank()) {
            throw new BadRequestException("Product name cannot be blank");
        }
    }

    @Override
    public void deleteProduct(long productId) {
        productRepository.deleteById(productId);
    }
}
