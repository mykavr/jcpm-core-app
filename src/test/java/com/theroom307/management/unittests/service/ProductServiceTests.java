package com.theroom307.management.unittests.service;

import com.theroom307.management.controller.exception.ProductNotFoundException;
import com.theroom307.management.data.dto.ProductResponseDto;
import com.theroom307.management.data.model.Product;
import com.theroom307.management.data.repository.ProductRepository;
import com.theroom307.management.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.theroom307.management.utils.TestProductData.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTests {

    @InjectMocks
    private ProductServiceImpl productService;

    @Mock
    private ProductRepository productRepository;

    @Test
    void getProducts_whenOneProductExists_shouldReturnPageWithOneProduct() {
        int pageNumber = 3;
        int pageSize = 10;
        var pageable = Pageable.ofSize(pageSize).withPage(pageNumber);

        Page<Product> pageWithOneProduct = new PageImpl<>(
                List.of(getProduct()),
                pageable,
                1);

        Page<ProductResponseDto> pageWithOneProductDto = new PageImpl<>(
                List.of(getProductResponse()),
                pageable,
                1
        );

        when(productRepository.findAll(pageable)).thenReturn(pageWithOneProduct);

        var actualResult = productService.getProducts(pageNumber, pageSize);

        assertThat(actualResult.getContent())
                .as("Returned page should contain expected products")
                .isEqualTo(pageWithOneProductDto.getContent());

        assertThat(actualResult.getPageable())
                .as("Returned page's pagination data should match the JPA response")
                .isEqualTo(pageWithOneProductDto.getPageable());
    }

    @Test
    void getProducts_whenNoProductsExist_shouldReturnEmptyPage() {
        int pageNumber = 3;
        int pageSize = 10;
        var pageable = Pageable.ofSize(pageSize).withPage(pageNumber);

        Page<ProductResponseDto> pageWithZeroProductDto = new PageImpl<>(
                Collections.emptyList(),
                pageable,
                0
        );

        when(productRepository.findAll(pageable)).thenReturn(null); // JPA returns null when there are no products

        var actualResult = productService.getProducts(pageNumber, pageSize);

        assertThat(actualResult.getContent())
                .as("Returned page should contain expected products")
                .isEqualTo(pageWithZeroProductDto.getContent());

        assertThat(actualResult.getPageable())
                .as("Returned page's pagination data should match the JPA response")
                .isEqualTo(pageWithZeroProductDto.getPageable());
    }

    @Test
    void getProduct_whenProductExists_shouldReturnProductDto() {
        var product = getProduct();
        var productDto = getProductResponse();

        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));

        assertThat(productService.getProduct(product.getId()))
                .isEqualTo(productDto);
    }

    @Test
    void getProduct_whenProductDoesNotExists_shouldThrowProductNotFoundException() {
        var productId = VALID_PRODUCT_ID;

        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.getProduct(productId))
                .isInstanceOf(ProductNotFoundException.class)
                .hasMessage("Product '%s' was not found", VALID_PRODUCT_ID);
    }

    @Test
    void createProduct_shouldReturnProductId() {
        when(productRepository.save(getProductToCreate())).thenReturn(getProduct());

        assertThat(productService.createProduct(getProductRequest()))
                .isEqualTo(getProduct().getId());
    }

    @Test
    void deleteProduct_shouldDeleteProductFromRepository() {
        var productId = VALID_PRODUCT_ID;
        productService.deleteProduct(productId);
        verify(productRepository).deleteById(productId);
    }

}
