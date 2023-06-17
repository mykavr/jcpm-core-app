package com.theroom307.jcpm.core.unittests.controller;

import com.theroom307.jcpm.core.controller.ComponentController;
import com.theroom307.jcpm.core.controller.exception.BadRequestException;
import com.theroom307.jcpm.core.controller.exception.ItemNotFoundException;
import com.theroom307.jcpm.core.data.dto.ComponentRequestDto;
import com.theroom307.jcpm.core.data.model.Component;
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

import static com.theroom307.jcpm.core.utils.TestComponentData.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ComponentController.class)
class ComponentEndpointTests {

    private final static String ENDPOINT = String.format(Endpoint.COMPONENT.getEndpoint(), VALID_COMPONENT_ID);

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemService<Component> componentService;

    @Test
    void getComponent_shouldReturnComponentDto() throws Exception {
        when(componentService.getItem(anyLong()))
                .thenReturn(getComponentResponse());

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
                .thenThrow(new ItemNotFoundException("Component", VALID_COMPONENT_ID));

        this.mockMvc
                .perform(get(ENDPOINT))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().string(String.format(
                        "Component '%s' was not found", VALID_COMPONENT_ID)));
    }

    @Test
    void getComponent_shouldRequestFromService() throws Exception {
        when(componentService.getItem(anyLong()))
                .thenReturn(getComponentResponse());

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
                .perform(patch(String.format(ENDPOINT))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(getComponentDtoToCreateComponent()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").doesNotExist());
    }

    @Test
    void editComponent_invalidComponentId_shouldReturn404() throws Exception {
        doThrow(new ItemNotFoundException("Component", VALID_COMPONENT_ID))
                .when(componentService).editItem(anyLong(), any());

        this.mockMvc
                .perform(patch(String.format(ENDPOINT))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(getComponentDtoToCreateComponent()))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().string(String.format(
                        "Component '%s' was not found", VALID_COMPONENT_ID)));
    }

    @Test
    void editComponent_invalidComponentData_shouldReturn400() throws Exception {
        var errorMessage = "Invalid Component Data Error Message";

        doThrow(new BadRequestException(errorMessage))
                .when(componentService).editItem(anyLong(), any());

        this.mockMvc
                .perform(patch(String.format(ENDPOINT))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(getComponentDtoToCreateComponent()))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string(errorMessage));
    }

    @Test
    void editComponent_nameProvided_shouldCallComponentService() {
        var requestBody = "{\"name\": \"New Component Name\"}";
        var componentDto = new ComponentRequestDto("New Component Name", null);

        sendPatchRequestAndVerifyCallToComponentService(requestBody, componentDto);
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " "})
    void editComponent_blankNameProvided_shouldCallComponentService(String blankComponentName) {
        var requestBody = String.format("{\"name\": \"%s\", \"description\": \"New component description.\"}",
                blankComponentName);
        var componentDto = new ComponentRequestDto(blankComponentName, "New component description.");

        sendPatchRequestAndVerifyCallToComponentService(requestBody, componentDto);
    }

    @Test
    void editComponent_descriptionProvided_shouldCallComponentService() {
        var requestBody = "{\"description\": \"New component description.\"}";
        var componentDto = new ComponentRequestDto(null, "New component description.");

        sendPatchRequestAndVerifyCallToComponentService(requestBody, componentDto);
    }

    @Test
    void editComponent_nameAndDescriptionProvided_shouldCallComponentService() {
        var requestBody = "{\"name\": \"New Component Name\", \"description\": \"New component description.\"}";
        var componentDto = new ComponentRequestDto("New Component Name", "New component description.");

        sendPatchRequestAndVerifyCallToComponentService(requestBody, componentDto);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "{}",
            "{\"unexpected\": \"field\"}"
    })
    void editComponent_noneProvided_shouldCallComponentService(String requestBody) {
        var componentDto = new ComponentRequestDto(null, null);

        sendPatchRequestAndVerifyCallToComponentService(requestBody, componentDto);
    }

    @SneakyThrows
    private void sendPatchRequestAndVerifyCallToComponentService(String requestBody, ComponentRequestDto expectedComponentDto) {
        this.mockMvc
                .perform(patch(String.format(ENDPOINT))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print());

        verify(componentService).editItem(VALID_COMPONENT_ID, expectedComponentDto);
    }

}
