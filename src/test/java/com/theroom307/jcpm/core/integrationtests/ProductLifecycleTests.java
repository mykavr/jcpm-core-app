package com.theroom307.jcpm.core.integrationtests;

import com.theroom307.jcpm.core.data.model.Product;
import com.theroom307.jcpm.core.data.repository.ProductRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.ZonedDateTime;
import java.util.function.Predicate;

import static com.theroom307.jcpm.core.utils.TestProductData.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
                .andExpect(content().string(getEmptyProductListAsString()));
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
                .isPresent()
                .get()
                .as("Product name should be properly saved")
                .hasFieldOrPropertyWithValue("name", getProduct().getName())
                .as("Product description should be properly saved")
                .hasFieldOrPropertyWithValue("description", getProduct().getDescription());
    }

    @Test
    void getExistingProduct() throws Exception {
        var product = productRepository.save(getProductToCreate());

        var response = mockMvc
                .perform(get(String.format(PRODUCT_ENDPOINT, product.getId())))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn().getResponse();

        var expectedProductDtoJson = getResponseForProduct(product);

        assertThat(response.getContentAsString())
                .as("Response shouldn't be empty")
                .isNotBlank()
                .as("Response should contain the proper product data")
                .isEqualToIgnoringWhitespace(expectedProductDtoJson);
    }

    @Test
    void editExistingProduct() throws Exception {
        var product = getProductToCreate();
        product.setName("Product Name Before Editing");
        product.setDescription("Product description before editing.");
        final var originalProduct = productRepository.save(product);

        mockMvc
                .perform(patch(String.format(PRODUCT_ENDPOINT, originalProduct.getId()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(getProductDtoToCreateProduct()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").doesNotExist());

        var editedProduct = productRepository.findById(originalProduct.getId());

        assertThat(editedProduct)
                .as("The product should remain in the repository")
                .isPresent()
                .get()
                .as("The product name should be updated")
                .hasFieldOrPropertyWithValue("name", getProduct().getName())
                .as("The product description should be updated")
                .hasFieldOrPropertyWithValue("description", getProduct().getDescription())
                .as("The created timestamp should not change")
                .extracting(Product::getCreated)
                .matches(isEqualTo(originalProduct.getCreated()),
                        "should be " + originalProduct.getCreated());

        // TODO: Test the modified timestamp updating after JCPM-53 is fixed
//        assertThat(editedProduct)
//                .get()
//                .extracting(Product::getModified)
//                .as("The modified timestamp should have changed")
//                .matches(not(isEqualTo(originalProduct.getModified())),
//                        "should be after " + originalProduct.getModified())
//                .as("The modified timestamp should be after the original value")
//                .matches((modified) -> modified.isAfter(originalProduct.getModified()),
//                        "should be after " + originalProduct.getModified());
    }

    private Predicate<ZonedDateTime> isEqualTo(ZonedDateTime expected) {
        return (actual) -> actual.toEpochSecond() == expected.toEpochSecond();
    }

    @Test
    void getProductsList() throws Exception {
        var product = productRepository.save(getProduct());

        var expectedSingleProductResponse = getResponseForProduct(product);
        var expectedProductsList = getProductListResponseAsString(expectedSingleProductResponse);

        mockMvc.perform(get(PRODUCTS_ENDPOINT))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expectedProductsList));
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
