package com.theroom307.management.unittests.controller;

import com.theroom307.management.controller.ProductController;
import com.theroom307.management.data.dto.ProductRequestDto;
import com.theroom307.management.data.dto.ProductResponseDto;
import com.theroom307.management.data.dto.wrapper.ListResponseWrapper;
import com.theroom307.management.data.dto.wrapper.Pagination;
import com.theroom307.management.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static com.theroom307.management.utils.TestProductData.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
class ProductsEndpointTests {

    private final static String ENDPOINT = "/api/v1/product";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @Test
    void getProducts_whenNoProductsExist_shouldReturnEmptyProductListWrapper() throws Exception {
        var zeroProducts = ListResponseWrapper.<ProductResponseDto>builder()
                .data(Collections.emptyList())
                .pagination(new Pagination(0, 10, 0, 0))
                .build();

        when(productService.getProducts(anyInt(), anyInt())).thenReturn(zeroProducts);

        this.mockMvc
                .perform(get(ENDPOINT))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(getEmptyProductListAsString()));
    }

    @Test
    void getProducts_whenOneProductExists_shouldReturnProductListWrapperWithOneProduct() throws Exception {
        var products = ListResponseWrapper.<ProductResponseDto>builder()
                .data(List.of(getProductResponse()))
                .pagination(new Pagination(0, 10, 1, 1))
                .build();

        when(productService.getProducts(anyInt(), anyInt())).thenReturn(products);

        this.mockMvc
                .perform(get(ENDPOINT))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(getProductListResponseAsString()));
    }

    @Test
    void getProducts_shouldRequestFromProductService() throws Exception {
        this.mockMvc
                .perform(get(ENDPOINT));
        verify(productService).getProducts(anyInt(), anyInt());
    }

    @Test
    void postProduct_shouldSaveProduct() throws Exception {
        when(productService.createProduct((any(ProductRequestDto.class))))
                .thenReturn(1L);

        this.mockMvc
                .perform(post(ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(getProductDtoToCreateProduct()));

        verify(productService).createProduct(getProductRequest());
    }

    @Test
    void postProduct_shouldReturnProductId() throws Exception {
        var savedProductId = 1L;
        var savedProductIdAsString = String.valueOf(savedProductId);

        when(productService.createProduct(any(ProductRequestDto.class)))
                .thenReturn(savedProductId);

        this.mockMvc
                .perform(post(ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(getProductDtoToCreateProduct()))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().string(savedProductIdAsString));
    }
}
