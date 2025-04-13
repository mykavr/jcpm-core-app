package com.theroom307.jcpm.core.unittests.controller.product;

import com.theroom307.jcpm.core.controller.ProductController;
import com.theroom307.jcpm.core.controller.exception.BadRequestException;
import com.theroom307.jcpm.core.controller.exception.ItemNotFoundException;
import com.theroom307.jcpm.core.data.model.Product;
import com.theroom307.jcpm.core.service.ItemService;
import com.theroom307.jcpm.core.service.ProductComponentsService;
import com.theroom307.jcpm.core.service.impl.ItemDtoMapperImpl;
import com.theroom307.jcpm.core.utils.constant.Endpoint;
import com.theroom307.jcpm.core.utils.constant.ExpectedErrorMessage;
import com.theroom307.jcpm.core.utils.constant.Item;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static com.theroom307.jcpm.core.TestTypes.UNIT_TEST;
import static com.theroom307.jcpm.core.utils.data.TestProductData.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Tag(UNIT_TEST)
@WebMvcTest(ProductController.class)
@MockBean(ProductComponentsService.class)
@Import(ItemDtoMapperImpl.class)
class ProductEndpointTests {

    private final static String ENDPOINT = Endpoint.PRODUCT.getEndpoint(VALID_PRODUCT_ID);

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemService<Product> productService;

    @Test
    void getProduct_shouldReturnProductDto() throws Exception {
        when(productService.getItem(anyLong()))
                .thenReturn(getProduct());

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
                .thenThrow(new ItemNotFoundException(Item.PRODUCT.toString(), VALID_PRODUCT_ID));

        this.mockMvc
                .perform(get(ENDPOINT))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().string(ExpectedErrorMessage.productNotFound(VALID_PRODUCT_ID)));
    }

    @Test
    void getProduct_shouldRequestFromService() throws Exception {
        when(productService.getItem(anyLong()))
                .thenReturn(getProduct());

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
                .perform(patch(ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(getProductDtoToCreateProduct()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").doesNotExist());
    }

    @Test
    void editProduct_invalidProductId_shouldReturn404() throws Exception {
        doThrow(new ItemNotFoundException(Item.PRODUCT.toString(), VALID_PRODUCT_ID))
                .when(productService).editItem(anyLong(), any());

        this.mockMvc
                .perform(patch(ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(getProductDtoToCreateProduct()))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().string(ExpectedErrorMessage.productNotFound(VALID_PRODUCT_ID)));
    }

    @Test
    void editProduct_invalidProductData_shouldReturn400() throws Exception {
        var errorMessage = "Invalid Product Data Error Message";

        doThrow(new BadRequestException(errorMessage))
                .when(productService).editItem(anyLong(), any());

        this.mockMvc
                .perform(patch(ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(getProductDtoToCreateProduct()))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string(errorMessage));
    }

    @Test
    void editProduct_nameProvided_shouldCallProductService() {
        var requestBody = "{\"name\": \"New Product Name\"}";
        var product = new Product();
        product.setName("New Product Name");

        sendPatchRequestAndVerifyCallToProductService(requestBody, product);
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " "})
    void editProduct_blankNameProvided_shouldCallProductService(String blankProductName) {
        var requestBody = String.format("{\"name\": \"%s\", \"description\": \"New product description.\"}",
                blankProductName);
        var product = new Product();
        product.setName(blankProductName);
        product.setDescription("New product description.");

        sendPatchRequestAndVerifyCallToProductService(requestBody, product);
    }

    @Test
    void editProduct_descriptionProvided_shouldCallProductService() {
        var requestBody = "{\"description\": \"New product description.\"}";
        var product = new Product();
        product.setDescription("New product description.");

        sendPatchRequestAndVerifyCallToProductService(requestBody, product);
    }

    @Test
    void editProduct_nameAndDescriptionProvided_shouldCallProductService() {
        var requestBody = "{\"name\": \"New Product Name\", \"description\": \"New product description.\"}";
        var product = new Product();
        product.setName("New Product Name");
        product.setDescription("New product description.");

        sendPatchRequestAndVerifyCallToProductService(requestBody, product);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "{}",
            "{\"unexpected\": \"field\"}"
    })
    void editProduct_noneProvided_shouldCallProductService(String requestBody) {
        var product = new Product();
        sendPatchRequestAndVerifyCallToProductService(requestBody, product);
    }

    @SneakyThrows
    private void sendPatchRequestAndVerifyCallToProductService(String requestBody, Product expectedProduct) {
        this.mockMvc
                .perform(patch(ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print());

        verify(productService).editItem(VALID_PRODUCT_ID, expectedProduct);
    }

}
