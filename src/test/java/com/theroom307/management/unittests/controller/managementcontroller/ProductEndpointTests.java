package com.theroom307.management.unittests.controller.managementcontroller;

import com.theroom307.management.controller.ProductController;
import com.theroom307.management.data.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static com.theroom307.management.utils.TestProductData.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
class ProductEndpointTests {

    private final static String ENDPOINT = "/api/v1/product/" + VALID_PRODUCT_ID;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductRepository productRepository;

    @Test
    void shouldReturnProductDtoJson() throws Exception {
        var product = getProduct();
        var productDtoAsJson = getProductResponseAsString();

        when(productRepository.findById(anyLong()))
                .thenReturn(Optional.of(product));

        this.mockMvc
                .perform(get(ENDPOINT))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(productDtoAsJson));

        verify(productRepository).findById(VALID_PRODUCT_ID);
    }

    @Test
    void shouldRespond404WhenProductDoesNotExist() throws Exception {
        when(productRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        this.mockMvc
                .perform(get(ENDPOINT))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().string(String.format(
                        "Product '%s' was not found", VALID_PRODUCT_ID)));

        verify(productRepository).findById(VALID_PRODUCT_ID);
    }

    @Test
    void shouldDeleteProduct() throws Exception {
        this.mockMvc
                .perform(delete(ENDPOINT))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").doesNotExist());

        verify(productRepository).deleteById(VALID_PRODUCT_ID);
    }

}
