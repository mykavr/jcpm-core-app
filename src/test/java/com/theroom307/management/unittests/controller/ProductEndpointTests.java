package com.theroom307.management.unittests.controller;

import com.theroom307.management.controller.ProductController;
import com.theroom307.management.controller.exception.ProductNotFoundException;
import com.theroom307.management.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

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
    private ProductService productService;

    @Test
    void getProduct_shouldReturnProductDto() throws Exception {
        when(productService.getProduct(anyLong()))
                .thenReturn(getProductResponse());

        var productDtoAsJson = getProductResponseAsString();

        this.mockMvc
                .perform(get(ENDPOINT))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(productDtoAsJson));

        verify(productService).getProduct(VALID_PRODUCT_ID);
    }

    @Test
    void getProduct_whenProductDoesNotExist_shouldRespond404() throws Exception {
        when(productService.getProduct(anyLong()))
                .thenThrow(new ProductNotFoundException(VALID_PRODUCT_ID));

        this.mockMvc
                .perform(get(ENDPOINT))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().string(String.format(
                        "Product '%s' was not found", VALID_PRODUCT_ID)));

        verify(productService).getProduct(VALID_PRODUCT_ID);
    }

    @Test
    void deleteProduct_shouldDeleteProduct() throws Exception {
        this.mockMvc
                .perform(delete(ENDPOINT))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").doesNotExist());

        verify(productService).deleteProduct(VALID_PRODUCT_ID);
    }

}
