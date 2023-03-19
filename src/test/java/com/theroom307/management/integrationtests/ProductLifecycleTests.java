package com.theroom307.management.integrationtests;

import com.theroom307.management.data.dto.ProductResponseDto;
import com.theroom307.management.data.repository.ProductRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static com.theroom307.management.utils.TestProductData.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ProductLifecycleTests {

    private final static String PRODUCTS_ENDPOINT = "/api/v1/product";

    private final static String PRODUCT_ENDPOINT = PRODUCTS_ENDPOINT + "/%1s"; // %1s: product ID

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository productRepository;

    @AfterEach
    void clearRepository() {
        productRepository.deleteAll();
    }

    @Test
    void getEmptyProductsList() throws Exception {
        mockMvc.perform(get(PRODUCTS_ENDPOINT))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("[]"));
    }

    @Test
    void createNewProduct() throws Exception {
        var response = mockMvc
                .perform(post(PRODUCTS_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(getProductDtoToCreateProduct()))
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn().getResponse();

        long createdProductId = Long.parseLong(
                response.getContentAsString());

        var createdProduct = productRepository.findById(createdProductId);

        assertThat(createdProduct)
                .as("The product should be saved to the DB")
                .isPresent();

        assertThat(createdProduct.get())
                .as("Product name should be properly saved")
                .hasFieldOrPropertyWithValue("name", getProduct().getName())
                .as("Product description should be properly saved")
                .hasFieldOrPropertyWithValue("description", getProduct().getDescription());
    }

    @Test
    void getExistingProduct() throws Exception {
        var product = productRepository.save(getProduct());

        var response = mockMvc
                .perform(get(String.format(PRODUCT_ENDPOINT, product.getId())))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn().getResponse();

        var expectedProductDtoJson = getAsString(ProductResponseDto.fromEntity(product));

        assertThat(response.getContentAsString())
                .as("Response shouldn't be empty")
                .isNotBlank()
                .as("Response should contain the proper product data")
                .isEqualToIgnoringWhitespace(expectedProductDtoJson);
    }

    @Test
    void getProductsList() throws Exception {
        var product = productRepository.save(getProduct());
        var expectedProductsListJson = "[" + getAsString(ProductResponseDto.fromEntity(product)) + "]";

        mockMvc.perform(get(PRODUCTS_ENDPOINT))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expectedProductsListJson));
    }

    @Test
    void deleteProduct() throws Exception {
        var product = productRepository.save(getProduct());

        mockMvc.perform(delete(String.format(PRODUCT_ENDPOINT, product.getId())))
                .andDo(print())
                .andExpect(status().isOk());

        assertThat(productRepository.findById(product.getId()))
                .as("The product should be not present")
                .isNotPresent();
    }

}
