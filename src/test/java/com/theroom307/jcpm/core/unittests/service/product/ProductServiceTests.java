package com.theroom307.jcpm.core.unittests.service.product;

import com.theroom307.jcpm.core.controller.exception.BadRequestException;
import com.theroom307.jcpm.core.controller.exception.ItemNotFoundException;
import com.theroom307.jcpm.core.data.model.Product;
import com.theroom307.jcpm.core.data.repository.ProductRepository;
import com.theroom307.jcpm.core.service.impl.ProductServiceImpl;
import com.theroom307.jcpm.core.utils.ExpectedErrorMessage;
import org.junit.jupiter.api.Tag;
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

import java.util.List;
import java.util.Optional;

import static com.theroom307.jcpm.core.utils.TestProductData.*;
import static com.theroom307.jcpm.core.utils.TestTypes.UNIT_TEST;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Tag(UNIT_TEST)
@ExtendWith(MockitoExtension.class)
class ProductServiceTests {

    @InjectMocks
    private ProductServiceImpl productService;

    @Mock
    private ProductRepository productRepository;

    private final int pageNumber = 0;
    private final int pageSize = 10;
    private final Pageable pageable = Pageable.ofSize(pageSize).withPage(pageNumber);

    @Test
    void getProducts_whenOneProductExists_shouldReturnPageWithOneProduct() {
        Page<Product> pageWithOneProduct = new PageImpl<>(
                List.of(getProduct()),
                pageable,
                1);

        when(productRepository.findAll(pageable)).thenReturn(pageWithOneProduct);

        var actualResult = productService.getItems(pageNumber, pageSize);

        assertThat(actualResult)
                .as("The service should return a page with one expected product")
                .isEqualTo(pageWithOneProduct);
    }

    @Test
    void getProducts_whenNoProductsExist_shouldReturnEmptyPage() {
        Page<Product> emptyPage = Page.empty(pageable);
        when(productRepository.findAll(pageable)).thenReturn(emptyPage);

        var actualResult = productService.getItems(pageNumber, pageSize);

        assertThat(actualResult)
                .as("The service should return an empty page")
                .isEqualTo(emptyPage);
    }

    @Test
    void getProducts_whenRepositoryReturnsNull_shouldReturnEmptyPage() {
        when(productRepository.findAll(pageable)).thenReturn(null); // JPA may return null when there are no products

        var actualResult = productService.getItems(pageNumber, pageSize);
        assertThat(actualResult)
                .as("The service should return an empty page")
                .isEqualTo(Page.empty(pageable));
    }

    @Test
    void getProduct_whenProductExists_shouldReturnProduct() {
        var product = getProduct();

        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));

        assertThat(productService.getItem(product.getId()))
                .isEqualTo(product);
    }

    @Test
    void getProduct_whenProductDoesNotExist_shouldThrowItemNotFoundException() {
        var productId = VALID_PRODUCT_ID;

        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.getItem(productId))
                .isInstanceOf(ItemNotFoundException.class)
                .hasMessage(ExpectedErrorMessage.productNotFound(productId));
    }

    @Test
    void createProduct_shouldReturnProductId() {
        var productToCreate = getProductToCreate();
        var createdProduct = getProduct();

        when(productRepository.save(productToCreate)).thenReturn(createdProduct);

        assertThat(productService.createItem(productToCreate))
                .isEqualTo(createdProduct.getId());
    }

    @Test
    void deleteProduct_shouldDeleteProductFromRepository() {
        var productId = VALID_PRODUCT_ID;
        productService.deleteItem(productId);
        verify(productRepository).deleteById(productId);
    }

    @Test
    void editProduct_changeName_shouldUpdateEditedProduct() {
        var editedProduct = new Product();
        editedProduct.setName("New Product Name");

        when(productRepository.findById(anyLong())).thenReturn(Optional.of(getProduct()));

        productService.editItem(VALID_PRODUCT_ID, editedProduct);

        verify(productRepository).updateNameById("New Product Name", VALID_PRODUCT_ID);
    }

    @Test
    void editProduct_changeDescription_shouldUpdateEditedProduct() {
        var editedProduct = new Product();
        editedProduct.setDescription("New product description.");

        when(productRepository.findById(anyLong())).thenReturn(Optional.of(getProduct()));

        productService.editItem(VALID_PRODUCT_ID, editedProduct);

        verify(productRepository).updateDescriptionById("New product description.", VALID_PRODUCT_ID);
    }

    @Test
    void editProduct_changeNameAndDescription_shouldUpdateEditedProduct() {
        var editedProduct = new Product();
        editedProduct.setName("New Product Name");
        editedProduct.setDescription("New product description.");

        when(productRepository.findById(anyLong())).thenReturn(Optional.of(getProduct()));

        productService.editItem(VALID_PRODUCT_ID, editedProduct);

        verify(productRepository).updateNameById("New Product Name", VALID_PRODUCT_ID);
        verify(productRepository).updateDescriptionById("New product description.", VALID_PRODUCT_ID);
    }

    @Test
    void editProduct_notExistingProductId_shouldThrowItemNotFoundException() {
        var notExistingProductId = VALID_PRODUCT_ID;
        var anyProduct = getProduct();

        when(productRepository.findById(notExistingProductId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.editItem(notExistingProductId, anyProduct))
                .isInstanceOf(ItemNotFoundException.class)
                .hasMessage(ExpectedErrorMessage.productNotFound(notExistingProductId));
    }

    @Test
    void editProduct_missingProductNameAndDescription_shouldThrowBadRequest() {
        var product = new Product();

        assertThatThrownBy(() -> productService.editItem(VALID_PRODUCT_ID, product))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("New value for the product name or description must be provided");
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "\t"})
    void editProduct_blankProductName_shouldThrowBadRequest(String blankProductName) {
        var product = new Product();
        product.setName(blankProductName);

        assertThatThrownBy(() -> productService.editItem(VALID_PRODUCT_ID, product))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Product name cannot be blank");
    }

}
