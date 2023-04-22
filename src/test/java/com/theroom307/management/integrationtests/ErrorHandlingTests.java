package com.theroom307.management.integrationtests;

import com.theroom307.management.data.model.Product;
import com.theroom307.management.data.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static com.theroom307.management.utils.TestProductData.getProduct;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ErrorHandlingTests {

    private final static String ENDPOINT = "/api/v1/product";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductRepository productRepository;

    @Test
    void shouldReturnGeneralError() throws Exception {
        when(productRepository.findById(anyLong()))
                .thenThrow(new RuntimeException());

        this.mockMvc
                .perform(get(ENDPOINT + "/123"))
                .andDo(print())
                .andExpect(status().is5xxServerError())
                .andExpect(content().string("Sorry, something went wrong"));
    }

    @Test
    void getProducts_pagination_negativePageSize_shouldReturnBadRequest() throws Exception {
        this.mockMvc
                .perform(get(ENDPOINT).queryParam("size", "-1"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Page size must be greater than 0"));
    }

    @Test
    void getProducts_pagination_zeroPageSize_shouldReturnBadRequest() throws Exception {
        this.mockMvc
                .perform(get(ENDPOINT).queryParam("size", "0"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Page size must be greater than 0"));
    }

    @Test
    void getProducts_pagination_negativePageNumber_shouldReturnBadRequest() throws Exception {
        this.mockMvc
                .perform(get(ENDPOINT).queryParam("page", "-1"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Page must not be negative"));
    }

    @Test
    void getProducts_pagination_negativePageNumber_zeroPageSize_shouldReturnBadRequest() throws Exception {
        this.mockMvc
                .perform(get(ENDPOINT)
                        .queryParam("page", "-1")
                        .queryParam("size", "0"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Page must not be negative")))
                .andExpect(content().string(containsString("Page size must be greater than 0")));
    }

    @ParameterizedTest
    @ValueSource(strings = {"page", "size"})
    void getProducts_pagination_stringAsValue_shouldReturnBadRequest(String parameter) throws Exception {
        this.mockMvc
                .perform(get(ENDPOINT).queryParam(parameter, "a"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string(String.format("'%s' must be a number", parameter)));
    }

    @Test
    void shouldRespond404WhenProductDoesNotExist() throws Exception {
        when(productRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        this.mockMvc
                .perform(get(ENDPOINT + "/1"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().string(String.format(
                        "Product '%s' was not found", 1)));

        verify(productRepository).findById(1L);
    }

    @ParameterizedTest
    @CsvSource({
            "a , 'productId' must be a number",
            "-1 , Product ID must be greater than zero"
    })
    void getProduct_invalidId_shouldReturnBadRequest(String productId, String expectedMessage) throws Exception {
        this.mockMvc
                .perform(get(ENDPOINT + "/" + productId))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string(expectedMessage));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "{}",
            "{\"name\": \"  \", \"description\": \"Valid description.\"}",
            "{\"description\": \"Valid description.\"}"
    })
    void createProduct_missingName_shouldReturnBadRequest(String createProductJson) throws Exception {
        when(productRepository.save(any(Product.class))).thenReturn(getProduct());

        this.mockMvc
                .perform(post(ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createProductJson))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Product name is required"));
    }
}
