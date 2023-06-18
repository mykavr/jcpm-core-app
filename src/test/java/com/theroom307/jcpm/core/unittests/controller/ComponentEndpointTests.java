package com.theroom307.jcpm.core.unittests.controller;

import com.theroom307.jcpm.core.controller.ComponentController;
import com.theroom307.jcpm.core.controller.exception.BadRequestException;
import com.theroom307.jcpm.core.controller.exception.ItemNotFoundException;
import com.theroom307.jcpm.core.data.model.Component;
import com.theroom307.jcpm.core.service.ItemService;
import com.theroom307.jcpm.core.service.impl.ItemDtoMapperImpl;
import com.theroom307.jcpm.core.utils.Endpoint;
import com.theroom307.jcpm.core.utils.ExpectedErrorMessage;
import com.theroom307.jcpm.core.utils.Item;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static com.theroom307.jcpm.core.utils.TestComponentData.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ComponentController.class)
@Import(ItemDtoMapperImpl.class)
class ComponentEndpointTests {

    private final static String ENDPOINT = Endpoint.COMPONENT.getEndpoint(VALID_COMPONENT_ID);

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemService<Component> componentService;

    @Test
    void getComponent_shouldReturnComponentDto() throws Exception {
        var component = getComponent();
        when(componentService.getItem(anyLong())).thenReturn(component);

        var componentDtoAsJson = getComponentResponseAsString();

        this.mockMvc
                .perform(get(ENDPOINT))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(componentDtoAsJson));
    }

    @Test
    void getComponent_whenComponentDoesNotExist_shouldRespond404() throws Exception {
        when(componentService.getItem(anyLong()))
                .thenThrow(new ItemNotFoundException(Item.COMPONENT.toString(), VALID_COMPONENT_ID));

        this.mockMvc
                .perform(get(ENDPOINT))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().string(ExpectedErrorMessage.componentNotFound(VALID_COMPONENT_ID)));
    }

    @Test
    void getComponent_shouldRequestFromService() throws Exception {
        when(componentService.getItem(anyLong()))
                .thenReturn(getComponent());

        this.mockMvc
                .perform(get(ENDPOINT))
                .andDo(print());

        verify(componentService).getItem(VALID_COMPONENT_ID);
    }

    @Test
    void deleteComponent_shouldReturn200() throws Exception {
        this.mockMvc
                .perform(delete(ENDPOINT))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").doesNotExist());
    }

    @Test
    void deleteComponent_shouldDeleteUsingComponentService() throws Exception {
        this.mockMvc
                .perform(delete(ENDPOINT))
                .andDo(print());

        verify(componentService).deleteItem(VALID_COMPONENT_ID);
    }

    @Test
    void editComponent_validInput_shouldReturn200() throws Exception {
        this.mockMvc
                .perform(patch(ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(getComponentDtoToCreateComponent()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").doesNotExist());
    }

    @Test
    void editComponent_invalidComponentId_shouldReturn404() throws Exception {
        doThrow(new ItemNotFoundException(Item.COMPONENT.toString(), VALID_COMPONENT_ID))
                .when(componentService).editItem(anyLong(), any());

        this.mockMvc
                .perform(patch(ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(getComponentDtoToCreateComponent()))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().string(ExpectedErrorMessage.componentNotFound(VALID_COMPONENT_ID)));
    }

    @Test
    void editComponent_invalidComponentData_shouldReturn400() throws Exception {
        var errorMessage = "Invalid Component Data Error Message";

        doThrow(new BadRequestException(errorMessage))
                .when(componentService).editItem(anyLong(), any());

        this.mockMvc
                .perform(patch(ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(getComponentDtoToCreateComponent()))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string(errorMessage));
    }

    @Test
    void editComponent_nameProvided_shouldCallComponentService() {
        var requestBody = "{\"name\": \"New Component Name\"}";
        var component = new Component();
        component.setName("New Component Name");

        sendPatchRequestAndVerifyCallToComponentService(requestBody, component);
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " "})
    void editComponent_blankNameProvided_shouldCallComponentService(String blankComponentName) {
        var requestBody = String.format("{\"name\": \"%s\", \"description\": \"New component description.\"}",
                blankComponentName);
        var component = new Component();
        component.setName(blankComponentName);
        component.setDescription("New component description.");

        sendPatchRequestAndVerifyCallToComponentService(requestBody, component);
    }

    @Test
    void editComponent_descriptionProvided_shouldCallComponentService() {
        var requestBody = "{\"description\": \"New component description.\"}";
        var component = new Component();
        component.setDescription("New component description.");

        sendPatchRequestAndVerifyCallToComponentService(requestBody, component);
    }

    @Test
    void editComponent_nameAndDescriptionProvided_shouldCallComponentService() {
        var requestBody = "{\"name\": \"New Component Name\", \"description\": \"New component description.\"}";
        var component = new Component();
        component.setName("New Component Name");
        component.setDescription("New component description.");

        sendPatchRequestAndVerifyCallToComponentService(requestBody, component);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "{}",
            "{\"unexpected\": \"field\"}"
    })
    void editComponent_noneProvided_shouldCallComponentService(String requestBody) {
        var component = new Component();
        sendPatchRequestAndVerifyCallToComponentService(requestBody, component);
    }

    @SneakyThrows
    private void sendPatchRequestAndVerifyCallToComponentService(String requestBody, Component expectedComponent) {
        this.mockMvc
                .perform(patch(ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print());

        verify(componentService).editItem(VALID_COMPONENT_ID, expectedComponent);
    }

}
