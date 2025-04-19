package com.theroom307.jcpm.core.unittests.controller.product;

import com.theroom307.jcpm.core.controller.ProductController;
import com.theroom307.jcpm.core.controller.exception.ConditionFailedException;
import com.theroom307.jcpm.core.controller.exception.ItemNotFoundException;
import com.theroom307.jcpm.core.controller.exception.NotFoundException;
import com.theroom307.jcpm.core.service.ItemDtoMapper;
import com.theroom307.jcpm.core.service.ItemService;
import com.theroom307.jcpm.core.service.ProductComponentsService;
import com.theroom307.jcpm.core.utils.constant.ExpectedErrorMessage;
import com.theroom307.jcpm.core.utils.constant.Item;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static com.theroom307.jcpm.core.TestTypes.UNIT_TEST;
import static com.theroom307.jcpm.core.utils.data.TestComponentData.VALID_COMPONENT_ID;
import static com.theroom307.jcpm.core.utils.data.TestData.*;
import static com.theroom307.jcpm.core.utils.data.TestProductData.VALID_PRODUCT_ID;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Tag(UNIT_TEST)
@WebMvcTest(ProductController.class)
@MockitoBean(types = {ItemService.class, ItemDtoMapper.class})
class ProductComponentEndpointTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductComponentsService productComponentsService;

    /*
        ADD A COMPONENT TO THE PRODUCT
     */

    @Test
    void addComponent_shouldReturn201() throws Exception {
        this.mockMvc
                .perform(createAddComponentRequest(VALID_PRODUCT_ID, getAddComponentRequestBody()))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$").doesNotExist());
    }

    @Test
    void addComponent_shouldCallProductComponentsService() throws Exception {
        var productId = 123;
        var componentId = 321;
        var quantity = 10;
        this.mockMvc
                .perform(createAddComponentRequest(productId, getAddComponentRequestBody(componentId, quantity)))
                .andDo(print());
        verify(productComponentsService)
                .addComponentToProduct(productId, componentId, quantity);
    }

    @Test
    void addComponent_toNonExistingProduct_shouldReturn404() throws Exception {
        long productId = 123;

        doThrow(new ItemNotFoundException(Item.PRODUCT.toString(), productId))
                .when(productComponentsService).addComponentToProduct(eq(productId), anyLong(), anyInt());

        this.mockMvc
                .perform(createAddComponentRequest(productId, getAddComponentRequestBody()))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().string(ExpectedErrorMessage.productNotFound(productId)));
    }

    @Test
    void addComponent_nonExistingComponent_shouldReturn404() throws Exception {
        long componentId = 321;

        doThrow(new ItemNotFoundException(Item.COMPONENT.toString(), componentId))
                .when(productComponentsService)
                .addComponentToProduct(anyLong(), eq(componentId), anyInt());

        this.mockMvc
                .perform(createAddComponentRequest(VALID_PRODUCT_ID, getAddComponentRequestBody(componentId)))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().string(ExpectedErrorMessage.componentNotFound(componentId)));
    }

    @Test
    void addComponent_componentIdNotProvided_shouldReturnBadRequest() throws Exception {
        this.mockMvc
                .perform(createAddComponentRequest(VALID_PRODUCT_ID, getInvalidRequestWithoutComponentId()))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string(ExpectedErrorMessage.componentIdIsRequired()));
    }

    @Test
    void addComponent_quantityNotProvided_shouldUseDefaultValue() throws Exception {
        this.mockMvc
                .perform(createAddComponentRequest(VALID_PRODUCT_ID, getAddComponentRequestBody(VALID_COMPONENT_ID)))
                .andDo(print());
        verify(productComponentsService)
                .addComponentToProduct(VALID_PRODUCT_ID, VALID_COMPONENT_ID, DEFAULT_COMPONENT_QUANTITY);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1})
    void addComponent_invalidQuantity_shouldReturnBadRequest(int invalidQuantity) throws Exception {
        var request = createAddComponentRequest(
                VALID_PRODUCT_ID, getAddComponentRequestBody(VALID_COMPONENT_ID, invalidQuantity));
        this.mockMvc
                .perform(request)
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string(ExpectedErrorMessage.quantityMustBeGreaterThanZero()));
    }

    @Test
    void addComponent_componentAlreadyExists_shouldReturn409() throws Exception {
        var errorMessage = ExpectedErrorMessage.productAlreadyContainsComponent(226, 729);

        doThrow(new ConditionFailedException(errorMessage))
                .when(productComponentsService)
                .addComponentToProduct(anyLong(), anyLong(), anyInt());

        var payload = getAddComponentRequestBody();
        var request = createAddComponentRequest(VALID_PRODUCT_ID, payload);

        this.mockMvc
                .perform(request)
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(content().string(errorMessage));
    }

    /*
        REMOVE A COMPONENT FROM THE PRODUCT
     */

    @Test
    void removeComponent_shouldReturn200() throws Exception {
        this.mockMvc
                .perform(createRemoveComponentRequest(VALID_PRODUCT_ID, VALID_COMPONENT_ID))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").doesNotExist());
    }

    @Test
    void removeComponent_shouldCallProductComponentsService() throws Exception {
        var productId = 123;
        var componentId = 321;
        this.mockMvc
                .perform(createRemoveComponentRequest(productId, componentId))
                .andDo(print());
        verify(productComponentsService)
                .removeComponentFromProduct(productId, componentId);
    }

    @Test
    void removeComponent_fromNonExistingProduct_shouldReturn404() throws Exception {
        long productId = 123;
        long componentId = 456;

        doThrow(new ItemNotFoundException(Item.PRODUCT.toString(), productId))
                .when(productComponentsService).removeComponentFromProduct(eq(productId), anyLong());

        this.mockMvc
                .perform(createRemoveComponentRequest(productId, componentId))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().string(ExpectedErrorMessage.productNotFound(productId)));
    }

    @Test
    void removeComponent_notAddedComponent_shouldReturn404() throws Exception {
        long productId = 123;
        long componentId = 321;
        var errorMessage = ExpectedErrorMessage.productDoesNotContainComponent(productId, componentId);

        doThrow(new NotFoundException(errorMessage))
                .when(productComponentsService).removeComponentFromProduct(eq(productId), eq(componentId));

        this.mockMvc
                .perform(createRemoveComponentRequest(productId, componentId))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().string(errorMessage));
    }

    /*
        UPDATE COMPONENT QUANTITY
     */

    @Test
    void updateComponentQuantity_shouldReturn200() throws Exception {
        this.mockMvc
                .perform(createUpdateQuantityRequest(VALID_PRODUCT_ID, VALID_COMPONENT_ID, getUpdateQuantityRequestBody(5)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").doesNotExist());
    }

    @Test
    void updateComponentQuantity_shouldCallProductComponentsService() throws Exception {
        var productId = 123;
        var componentId = 321;
        var quantity = 10;
        this.mockMvc
                .perform(createUpdateQuantityRequest(productId, componentId, getUpdateQuantityRequestBody(quantity)))
                .andDo(print());
        verify(productComponentsService)
                .updateComponentQuantity(productId, componentId, quantity);
    }

    @Test
    void updateComponentQuantity_nonExistingProduct_shouldReturn404() throws Exception {
        long productId = 123;
        long componentId = 456;

        doThrow(new ItemNotFoundException(Item.PRODUCT.toString(), productId))
                .when(productComponentsService).updateComponentQuantity(eq(productId), anyLong(), anyInt());

        this.mockMvc
                .perform(createUpdateQuantityRequest(productId, componentId, getUpdateQuantityRequestBody(5)))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().string(ExpectedErrorMessage.productNotFound(productId)));
    }

    @Test
    void updateComponentQuantity_nonExistingComponent_shouldReturn404() throws Exception {
        long productId = 123;
        long componentId = 321;

        doThrow(new ItemNotFoundException(Item.COMPONENT.toString(), componentId))
                .when(productComponentsService).updateComponentQuantity(anyLong(), eq(componentId), anyInt());

        this.mockMvc
                .perform(createUpdateQuantityRequest(productId, componentId, getUpdateQuantityRequestBody(5)))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().string(ExpectedErrorMessage.componentNotFound(componentId)));
    }

    @Test
    void updateComponentQuantity_componentNotInProduct_shouldReturn404() throws Exception {
        long productId = 123;
        long componentId = 321;
        var errorMessage = ExpectedErrorMessage.productDoesNotContainComponent(productId, componentId);

        doThrow(new NotFoundException(errorMessage))
                .when(productComponentsService).updateComponentQuantity(eq(productId), eq(componentId), anyInt());

        this.mockMvc
                .perform(createUpdateQuantityRequest(productId, componentId, getUpdateQuantityRequestBody(5)))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().string(errorMessage));
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1})
    void updateComponentQuantity_invalidQuantity_shouldReturnBadRequest(int invalidQuantity) throws Exception {
        var request = createUpdateQuantityRequest(
                VALID_PRODUCT_ID, VALID_COMPONENT_ID, getUpdateQuantityRequestBody(invalidQuantity));
        this.mockMvc
                .perform(request)
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string(ExpectedErrorMessage.quantityMustBeGreaterThanZero()));
    }

    @Test
    void updateComponentQuantity_quantityNotProvided_shouldReturnBadRequest() throws Exception {
        this.mockMvc
                .perform(createUpdateQuantityRequest(VALID_PRODUCT_ID, VALID_COMPONENT_ID, getInvalidRequestWithoutQuantity()))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string(ExpectedErrorMessage.quantityIsRequired()));
    }

    /*
        HELPER METHODS
     */

    private MockHttpServletRequestBuilder createAddComponentRequest(long productId, String payload) {
        return post(String.format("/api/v1/product/%d/components", productId))
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload);
    }

    private MockHttpServletRequestBuilder createRemoveComponentRequest(long productId, long componentId) {
        return delete(String.format("/api/v1/product/%d/components/%d", productId, componentId));
    }

    private MockHttpServletRequestBuilder createUpdateQuantityRequest(long productId, long componentId, String payload) {
        return patch(String.format("/api/v1/product/%d/components/%d", productId, componentId))
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload);
    }
}
