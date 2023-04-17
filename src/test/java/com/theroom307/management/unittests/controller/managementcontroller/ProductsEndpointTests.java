package com.theroom307.management.unittests.controller.managementcontroller;

import com.theroom307.management.controller.ProductController;
import com.theroom307.management.data.model.Product;
import com.theroom307.management.data.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

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
    private ProductRepository productRepository;

    @Test
    void shouldReturnEmptyProductListWrapperWhenNoProductsInDb() throws Exception {
        this.mockMvc
                .perform(get(ENDPOINT))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(getEmptyProductListAsString()));
    }

    @Test
    void shouldReturnProductListWrapperWithOneProduct() throws Exception {
        Page<Product> pageWithOneProduct = new PageImpl<>(
                List.of(getProduct()),
                Pageable.ofSize(10),
                1);

        when(productRepository.findAll(any(Pageable.class)))
                .thenReturn(pageWithOneProduct);

        this.mockMvc
                .perform(get(ENDPOINT))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(getProductListResponseAsString()));
    }

    @Test
    void shouldRequestFromProductRepository() throws Exception {
        this.mockMvc
                .perform(get(ENDPOINT));
        verify(productRepository).findAll(any(Pageable.class));
    }

    @Test
    void shouldSaveToProductRepository() throws Exception {
        when(productRepository.save(any(Product.class)))
                .thenReturn(getProduct());

        this.mockMvc
                .perform(post(ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(getProductDtoToCreateProduct()));

        verify(productRepository).save(getProductToCreate());
    }

    @Test
    void shouldReturnIdOnCreateProduct() throws Exception {
        var savedProduct = getProduct();
        var savedProductId = String.valueOf(savedProduct.getId());

        when(productRepository.save(any(Product.class)))
                .thenReturn(savedProduct);

        this.mockMvc
                .perform(post(ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(getProductDtoToCreateProduct()))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().string(savedProductId));
    }
}
