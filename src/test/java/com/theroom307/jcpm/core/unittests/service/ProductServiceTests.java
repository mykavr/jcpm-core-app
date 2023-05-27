package com.theroom307.jcpm.core.unittests.service;

import com.theroom307.jcpm.core.controller.exception.BadRequestException;
import com.theroom307.jcpm.core.controller.exception.ProductNotFoundException;
import com.theroom307.jcpm.core.data.dto.ProductRequestDto;
import com.theroom307.jcpm.core.data.dto.ProductResponseDto;
import com.theroom307.jcpm.core.data.dto.wrapper.ListResponseWrapper;
import com.theroom307.jcpm.core.data.dto.wrapper.Pagination;
import com.theroom307.jcpm.core.data.model.Product;
import com.theroom307.jcpm.core.data.repository.ProductRepository;
import com.theroom307.jcpm.core.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.theroom307.jcpm.core.utils.TestProductData.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTests {

    @InjectMocks
    private ProductServiceImpl productService;

    @Mock
    private ProductRepository productRepository;

    @Test
    void getProducts_whenOneProductExists_shouldReturnListWithOneProduct() {
        int pageNumber = 0;
        int pageSize = 10;
        var pageable = Pageable.ofSize(pageSize).withPage(pageNumber);

        Page<Product> pageWithOneProduct = new PageImpl<>(
                List.of(getProduct()),
                pageable,
                1);

        var listWithOneProductDto = ListResponseWrapper.<ProductResponseDto>builder()
                .data(List.of(getProductResponse()))
                .pagination(new Pagination(pageNumber, pageSize, 1, 1))
                .build();

        when(productRepository.findAll(pageable)).thenReturn(pageWithOneProduct);

        var actualResult = productService.getProducts(pageNumber, pageSize);

        assertThat(actualResult)
                .as("The service should return one expected product in the list")
                .isEqualTo(listWithOneProductDto);
    }

    @Test
    void getProducts_whenNoProductsExist_shouldReturnEmptyList() {
        int pageNumber = 0;
        int pageSize = 10;
        var pageable = Pageable.ofSize(pageSize).withPage(pageNumber);

        var zeroProductsList = ListResponseWrapper.<ProductResponseDto>builder()
                .data(Collections.emptyList())
                .pagination(new Pagination(pageNumber, pageSize, 0, 0))
                .build();

        when(productRepository.findAll(pageable)).thenReturn(null); // JPA returns null when there are no products

        var actualResult = productService.getProducts(pageNumber, pageSize);

        assertThat(actualResult)
                .as("The service should return zero products in the list")
                .isEqualTo(zeroProductsList);
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
    void getProduct_whenProductDoesNotExist_shouldThrowProductNotFoundException() {
        var productId = VALID_PRODUCT_ID;

        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.getProduct(productId))
                .isInstanceOf(ProductNotFoundException.class)
                .hasMessage("Product '%s' was not found", productId);
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

    @Test
    void editProduct_changeName_shouldUpdateEditedProduct() {
        var productDto = new ProductRequestDto("New Product Name", null);

        when(productRepository.findById(anyLong())).thenReturn(Optional.of(getProduct()));

        productService.editProduct(VALID_PRODUCT_ID, productDto);

        verify(productRepository).updateNameById("New Product Name", VALID_PRODUCT_ID);
    }

    @Test
    void editProduct_changeDescription_shouldUpdateEditedProduct() {
        var productDto = new ProductRequestDto(null, "New product description.");

        when(productRepository.findById(anyLong())).thenReturn(Optional.of(getProduct()));

        productService.editProduct(VALID_PRODUCT_ID, productDto);

        verify(productRepository).updateDescriptionById("New product description.", VALID_PRODUCT_ID);
    }

    @Test
    void editProduct_changeNameAndDescription_shouldUpdateEditedProduct() {
        var productDto = new ProductRequestDto("New Product Name", "New product description.");

        when(productRepository.findById(anyLong())).thenReturn(Optional.of(getProduct()));

        productService.editProduct(VALID_PRODUCT_ID, productDto);

        verify(productRepository).updateNameById("New Product Name", VALID_PRODUCT_ID);
        verify(productRepository).updateDescriptionById("New product description.", VALID_PRODUCT_ID);
    }

    @Test
    void editProduct_notExistingProductId_shouldThrowProductNotFoundException() {
        var notExistingProductId = VALID_PRODUCT_ID;
        var anyProductDto = getProductRequest();

        when(productRepository.findById(notExistingProductId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.editProduct(notExistingProductId, anyProductDto))
                .isInstanceOf(ProductNotFoundException.class)
                .hasMessage("Product '%s' was not found", notExistingProductId);
    }

    @Test
    void editProduct_missingProductNameAndDescription_shouldThrowBadRequest() {
        var productDto = new ProductRequestDto(null, null);

        assertThatThrownBy(() -> productService.editProduct(VALID_PRODUCT_ID, productDto))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("New value for the product name or description must be provided");
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "\t"})
    void editProduct_blankProductName_shouldThrowBadRequest(String blankProductName) {
        var productDto = new ProductRequestDto(blankProductName, null);

        assertThatThrownBy(() -> productService.editProduct(VALID_PRODUCT_ID, productDto))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Product name cannot be blank");
    }

}
