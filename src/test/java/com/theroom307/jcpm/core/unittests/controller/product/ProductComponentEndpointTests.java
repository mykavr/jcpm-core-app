package com.theroom307.jcpm.core.unittests.controller.product;

import com.theroom307.jcpm.core.controller.ProductController;
import com.theroom307.jcpm.core.controller.exception.BadRequestException;
import com.theroom307.jcpm.core.controller.exception.ConditionFailedException;
import com.theroom307.jcpm.core.controller.exception.ItemNotFoundException;
import com.theroom307.jcpm.core.controller.exception.NotFoundException;
import com.theroom307.jcpm.core.service.ItemDtoMapper;
import com.theroom307.jcpm.core.service.ItemService;
import com.theroom307.jcpm.core.service.ProductComponentsService;
import com.theroom307.jcpm.core.utils.constant.Endpoint;
import com.theroom307.jcpm.core.utils.constant.ExpectedErrorMessage;
import com.theroom307.jcpm.core.utils.constant.Item;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static com.theroom307.jcpm.core.TestTypes.UNIT_TEST;
import static com.theroom307.jcpm.core.utils.data.TestComponentData.VALID_COMPONENT_ID;
import static com.theroom307.jcpm.core.utils.data.TestData.*;
import static com.theroom307.jcpm.core.utils.data.TestProductData.VALID_PRODUCT_ID;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Tag(UNIT_TEST)
@WebMvcTest(ProductController.class)
@MockitoBean(types = ItemService.class)
@MockitoBean(types = ItemDtoMapper.class)
class ProductComponentEndpointTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductComponentsService productComponentsService;

    /*
        ADD A COMPONENT TO THE PRODUCT
     */

    @Test
    void editComponents_whenAddComponent_shouldReturn200() throws Exception {
        this.mockMvc
                .perform(createRequestWithPayload(getAddComponentRequestBody()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").doesNotExist());
    }

    @Test
    void editComponents_whenAddComponent_shouldCallProductComponentsService() throws Exception {
        var productId = 123;
        var componentId = 321;
        var quantity = 10;
        this.mockMvc
                .perform(createRequestWithPayload(productId, getAddComponentToProductRequestBody(componentId, quantity)))
                .andDo(print());
        verify(productComponentsService)
                .editComponent(productId, componentId, quantity, true, false);
    }

    @Test
    void editComponents_whenAddComponentToNonExistingProduct_shouldReturn404() throws Exception {
        var productId = 123;

        doThrow(new ItemNotFoundException(Item.PRODUCT.toString(), productId))
                .when(productComponentsService).editComponent(anyLong(), anyLong(), anyInt(), anyBoolean(), anyBoolean());

        this.mockMvc
                .perform(createRequestWithPayload(productId, getAddComponentRequestBody()))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().string(ExpectedErrorMessage.productNotFound(productId)));
    }

    @Test
    void editComponents_whenAddNonExistingComponent_shouldReturn404() throws Exception {
        var componentId = 321;

        doThrow(new ItemNotFoundException(Item.COMPONENT.toString(), componentId))
                .when(productComponentsService)
                .editComponent(anyLong(), anyLong(), anyInt(), anyBoolean(), anyBoolean());

        this.mockMvc
                .perform(createRequestWithPayload(getAddComponentToProductRequestBody(componentId)))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().string(ExpectedErrorMessage.componentNotFound(componentId)));
    }

    @Test
    void editComponents_whenAddComponentIdNotProvided_shouldReturnBadRequest() throws Exception {
        this.mockMvc
                .perform(createRequestWithPayload(getInvalidAddRequestWithoutComponentId()))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string(ExpectedErrorMessage.componentIdIsRequired()));
    }

    @Test
    void editComponents_whenQuantityIsNotProvided_shouldUseDefaultValue() throws Exception {
        this.mockMvc
                .perform(createRequestWithPayload(getAddComponentToProductRequestBody()))
                .andDo(print());
        verify(productComponentsService)
                .editComponent(VALID_PRODUCT_ID, VALID_COMPONENT_ID,
                        DEFAULT_COMPONENT_QUANTITY, true, false);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1})
    void editComponents_whenQuantityIsInvalid_shouldReturnBadRequest(int invalidQuantity) throws Exception {
        var request = createRequestWithPayload(
                getAddComponentToProductRequestBody(VALID_COMPONENT_ID, invalidQuantity));
        this.mockMvc
                .perform(request)
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string(ExpectedErrorMessage.quantityMustBePositive()));
    }

    @Test
    void editComponents_whenConditionFailed_ShouldReturn409() throws Exception {
        var errorMessage = ExpectedErrorMessage.productAlreadyContainsComponent(226, 729);

        doThrow(new ConditionFailedException(errorMessage))
                .when(productComponentsService)
                .editComponent(anyLong(), anyLong(), anyInt(), anyBoolean(), anyBoolean());

        var payload = getAddComponentToProductRequestBody();
        var request = createRequestWithPayload(payload);

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
    void editComponents_whenRemoveComponent_shouldReturn200() throws Exception {
        this.mockMvc
                .perform(createRequestWithPayload(getRemoveComponentRequestBody()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").doesNotExist());
    }

    @Test
    void editComponents_whenRemoveComponent_shouldCallProductComponentsService() throws Exception {
        var productId = 123;
        var componentId = 321;
        this.mockMvc
                .perform(createRequestWithPayload(productId, getRemoveComponentFromProductRequestBody(componentId)))
                .andDo(print());
        verify(productComponentsService)
                .editComponent(productId, componentId, DEFAULT_COMPONENT_QUANTITY, false, true);
    }

    @Test
    void editComponents_whenRemoveFromNonExistingProduct_shouldReturn404() throws Exception {
        var productId = 123;

        doThrow(new ItemNotFoundException(Item.PRODUCT.toString(), productId))
                .when(productComponentsService).editComponent(anyLong(), anyLong(), anyInt(), anyBoolean(), anyBoolean());

        this.mockMvc
                .perform(createRequestWithPayload(productId, getRemoveComponentRequestBody()))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().string(ExpectedErrorMessage.productNotFound(productId)));
    }

    @Test
    void editComponents_whenRemoveNotAddedComponent_shouldReturn404() throws Exception {
        var productId = 123;
        var componentId = 321;
        var errorMessage = ExpectedErrorMessage.productDoesNotContainComponent(productId, componentId);

        doThrow(new NotFoundException(errorMessage))
                .when(productComponentsService).editComponent(anyLong(), anyLong(), anyInt(), anyBoolean(), anyBoolean());

        this.mockMvc
                .perform(createRequestWithPayload(productId, getAddComponentToProductRequestBody(componentId)))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().string(errorMessage));
    }

    @Test
    void editComponents_whenRemoveComponentIdNotProvided_shouldReturnBadRequest() throws Exception {
        this.mockMvc
                .perform(createRequestWithPayload(getInvalidRemoveRequestWithoutComponentId()))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string(ExpectedErrorMessage.componentIdIsRequired()));
    }

    /*
        INVALID REQUEST
     */

    @Test
    void editComponents_whenServiceThrowsBadRequestException_shouldReturnBadRequest() throws Exception {
        var errorMessage = "Bad Request Error Message";

        doThrow(new BadRequestException(errorMessage))
                .when(productComponentsService).editComponent(anyLong(), anyLong(), anyInt(), anyBoolean(), anyBoolean());

        this.mockMvc
                .perform(createRequestWithPayload(getInvalidRequestWithBothAddAndRemoveAreTrue()))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string(errorMessage));
    }

    /*
        HELPER METHODS
     */

    private String getAddComponentRequestBody() {
        return getAddComponentToProductRequestBody(VALID_COMPONENT_ID);
    }

    private String getRemoveComponentRequestBody() {
        return getRemoveComponentFromProductRequestBody(VALID_COMPONENT_ID);
    }


    private String getInvalidAddRequestWithoutComponentId() {
        return """
                {
                    "add": true
                }
                """;
    }

    private String getInvalidRemoveRequestWithoutComponentId() {
        return """
                {
                    "remove": true
                }
                """;
    }

    private String getInvalidRequestWithBothAddAndRemoveAreTrue() {
        return String.format("""
                {
                    "component_id": "%s",
                    "add": true,
                    "remove": true
                }
                """, VALID_COMPONENT_ID);
    }

    private MockHttpServletRequestBuilder createRequestWithPayload(String payload) {
        return createRequestWithPayload(VALID_PRODUCT_ID, payload);
    }

    private MockHttpServletRequestBuilder createRequestWithPayload(long productId, String payload) {
        var endpoint = Endpoint.PRODUCT_COMPONENTS.getEndpoint(productId);
        return patch(endpoint)
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload);
    }

}
