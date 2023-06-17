package com.theroom307.jcpm.core.unittests.controller;

import com.theroom307.jcpm.core.controller.ProductController;
import com.theroom307.jcpm.core.controller.exception.BadRequestException;
import com.theroom307.jcpm.core.controller.exception.ItemNotFoundException;
import com.theroom307.jcpm.core.data.dto.ProductRequestDto;
import com.theroom307.jcpm.core.data.model.Product;
import com.theroom307.jcpm.core.service.ItemService;
import com.theroom307.jcpm.core.utils.Endpoint;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static com.theroom307.jcpm.core.utils.TestProductData.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
class ProductEndpointTests {

    private final static String ENDPOINT = String.format(Endpoint.PRODUCT.getEndpoint(), VALID_PRODUCT_ID);

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemService<Product> productService;

    @Test
    void getProduct_shouldReturnProductDto() throws Exception {
        when(productService.getItem(anyLong()))
                .thenReturn(getProductResponse());

        var productDtoAsJson = getProductResponseAsString();

        this.mockMvc
                .perform(get(ENDPOINT))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(productDtoAsJson));
    }

    @Test
    void getProduct_whenProductDoesNotExist_shouldRespond404() throws Exception {
        when(productService.getItem(anyLong()))
                .thenThrow(new ItemNotFoundException("Product", VALID_PRODUCT_ID));

        this.mockMvc
                .perform(get(ENDPOINT))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().string(String.format(
                        "Product '%s' was not found", VALID_PRODUCT_ID)));
    }

    @Test
    void getProduct_shouldRequestFromService() throws Exception {
        when(productService.getItem(anyLong()))
                .thenReturn(getProductResponse());

        this.mockMvc
                .perform(get(ENDPOINT))
                .andDo(print());

        verify(productService).getItem(VALID_PRODUCT_ID);
    }

    @Test
    void deleteProduct_shouldReturn200() throws Exception {
        this.mockMvc
                .perform(delete(ENDPOINT))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").doesNotExist());
    }

    @Test
    void deleteProduct_shouldDeleteUsingProductService() throws Exception {
        this.mockMvc
                .perform(delete(ENDPOINT))
                .andDo(print());

        verify(productService).deleteItem(VALID_PRODUCT_ID);
    }

    @Test
    void editProduct_validInput_shouldReturn200() throws Exception {
        this.mockMvc
                .perform(patch(String.format(ENDPOINT))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(getProductDtoToCreateProduct()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").doesNotExist());
    }

    @Test
    void editProduct_invalidProductId_shouldReturn404() throws Exception {
        doThrow(new ItemNotFoundException("Product", VALID_PRODUCT_ID))
                .when(productService).editItem(anyLong(), any());

        this.mockMvc
                .perform(patch(String.format(ENDPOINT))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(getProductDtoToCreateProduct()))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().string(String.format(
                        "Product '%s' was not found", VALID_PRODUCT_ID)));
    }

    @Test
    void editProduct_invalidProductData_shouldReturn400() throws Exception {
        var errorMessage = "Invalid Product Data Error Message";

        doThrow(new BadRequestException(errorMessage))
                .when(productService).editItem(anyLong(), any());

        this.mockMvc
                .perform(patch(String.format(ENDPOINT))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(getProductDtoToCreateProduct()))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string(errorMessage));
    }

    @Test
    void editProduct_nameProvided_shouldCallProductService() {
        var requestBody = "{\"name\": \"New Product Name\"}";
        var productDto = new ProductRequestDto("New Product Name", null);

        sendPatchRequestAndVerifyCallToProductService(requestBody, productDto);
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " "})
    void editProduct_blankNameProvided_shouldCallProductService(String blankProductName) {
        var requestBody = String.format("{\"name\": \"%s\", \"description\": \"New product description.\"}",
                blankProductName);
        var productDto = new ProductRequestDto(blankProductName, "New product description.");

        sendPatchRequestAndVerifyCallToProductService(requestBody, productDto);
    }

    @Test
    void editProduct_descriptionProvided_shouldCallProductService() {
        var requestBody = "{\"description\": \"New product description.\"}";
        var productDto = new ProductRequestDto(null, "New product description.");

        sendPatchRequestAndVerifyCallToProductService(requestBody, productDto);
    }

    @Test
    void editProduct_nameAndDescriptionProvided_shouldCallProductService() {
        var requestBody = "{\"name\": \"New Product Name\", \"description\": \"New product description.\"}";
        var productDto = new ProductRequestDto("New Product Name", "New product description.");

        sendPatchRequestAndVerifyCallToProductService(requestBody, productDto);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "{}",
            "{\"unexpected\": \"field\"}"
    })
    void editProduct_noneProvided_shouldCallProductService(String requestBody) {
        var productDto = new ProductRequestDto(null, null);

        sendPatchRequestAndVerifyCallToProductService(requestBody, productDto);
    }

    @SneakyThrows
    private void sendPatchRequestAndVerifyCallToProductService(String requestBody, ProductRequestDto expectedProductDto) {
        this.mockMvc
                .perform(patch(String.format(ENDPOINT))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print());

        verify(productService).editItem(VALID_PRODUCT_ID, expectedProductDto);
    }

}
