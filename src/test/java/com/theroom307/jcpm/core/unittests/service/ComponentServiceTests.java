package com.theroom307.jcpm.core.unittests.service;

import com.theroom307.jcpm.core.controller.exception.BadRequestException;
import com.theroom307.jcpm.core.controller.exception.ItemNotFoundException;
import com.theroom307.jcpm.core.data.dto.ComponentRequestDto;
import com.theroom307.jcpm.core.data.dto.ComponentResponseDto;
import com.theroom307.jcpm.core.data.dto.wrapper.ListResponseWrapper;
import com.theroom307.jcpm.core.data.dto.wrapper.Pagination;
import com.theroom307.jcpm.core.data.model.Component;
import com.theroom307.jcpm.core.data.repository.ComponentRepository;
import com.theroom307.jcpm.core.service.impl.ComponentServiceImpl;
import com.theroom307.jcpm.core.utils.ExpectedErrorMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.theroom307.jcpm.core.utils.TestComponentData.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ComponentServiceTests {

    @InjectMocks
    private ComponentServiceImpl componentService;

    @Mock
    private ComponentRepository componentRepository;

    @Test
    void getComponents_whenOneComponentExists_shouldReturnListWithOneComponent() {
        int pageNumber = 0;
        int pageSize = 10;
        var pageable = Pageable.ofSize(pageSize).withPage(pageNumber);

        Page<Component> pageWithOneComponent = new PageImpl<>(
                List.of(getComponent()),
                pageable,
                1);

        var listWithOneComponentDto = ListResponseWrapper.<ComponentResponseDto>builder()
                .data(List.of(getComponentResponse()))
                .pagination(new Pagination(pageNumber, pageSize, 1, 1))
                .build();

        when(componentRepository.findAll(pageable)).thenReturn(pageWithOneComponent);

        var actualResult = componentService.getItems(pageNumber, pageSize);

        assertThat(actualResult)
                .as("The service should return one expected component in the list")
                .isEqualTo(listWithOneComponentDto);
    }

    @Test
    void getComponents_whenNoComponentsExist_shouldReturnEmptyList() {
        int pageNumber = 0;
        int pageSize = 10;
        var pageable = Pageable.ofSize(pageSize).withPage(pageNumber);

        var zeroComponentsList = ListResponseWrapper.<ComponentResponseDto>builder()
                .data(Collections.emptyList())
                .pagination(new Pagination(pageNumber, pageSize, 0, 0))
                .build();

        when(componentRepository.findAll(pageable)).thenReturn(null); // JPA returns null when there are no components

        var actualResult = componentService.getItems(pageNumber, pageSize);

        assertThat(actualResult)
                .as("The service should return zero components in the list")
                .isEqualTo(zeroComponentsList);
    }

    @Test
    void getComponent_whenComponentExists_shouldReturnComponentDto() {
        var component = getComponent();
        var componentDto = getComponentResponse();

        when(componentRepository.findById(component.getId())).thenReturn(Optional.of(component));

        assertThat(componentService.getItem(component.getId()))
                .isEqualTo(componentDto);
    }

    @Test
    void getComponent_whenComponentDoesNotExist_shouldThrowItemNotFoundException() {
        var componentId = VALID_COMPONENT_ID;

        when(componentRepository.findById(componentId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> componentService.getItem(componentId))
                .isInstanceOf(ItemNotFoundException.class)
                .hasMessage(ExpectedErrorMessage.componentNotFound(componentId));
    }

    @Test
    void createComponent_shouldReturnComponentId() {
        when(componentRepository.save(getComponentToCreate())).thenReturn(getComponent());

        assertThat(componentService.createItem(getComponentRequest()))
                .isEqualTo(getComponent().getId());
    }

    @Test
    void deleteComponent_shouldDeleteComponentFromRepository() {
        var componentId = VALID_COMPONENT_ID;
        componentService.deleteItem(componentId);
        verify(componentRepository).deleteById(componentId);
    }

    @Test
    void editComponent_changeName_shouldUpdateEditedComponent() {
        var componentDto = new ComponentRequestDto("New Component Name", null);

        when(componentRepository.findById(anyLong())).thenReturn(Optional.of(getComponent()));

        componentService.editItem(VALID_COMPONENT_ID, componentDto);

        verify(componentRepository).updateNameById("New Component Name", VALID_COMPONENT_ID);
    }

    @Test
    void editComponent_changeDescription_shouldUpdateEditedComponent() {
        var componentDto = new ComponentRequestDto(null, "New component description.");

        when(componentRepository.findById(anyLong())).thenReturn(Optional.of(getComponent()));

        componentService.editItem(VALID_COMPONENT_ID, componentDto);

        verify(componentRepository).updateDescriptionById("New component description.", VALID_COMPONENT_ID);
    }

    @Test
    void editComponent_changeNameAndDescription_shouldUpdateEditedComponent() {
        var componentDto = new ComponentRequestDto("New Component Name", "New component description.");

        when(componentRepository.findById(anyLong())).thenReturn(Optional.of(getComponent()));

        componentService.editItem(VALID_COMPONENT_ID, componentDto);

        verify(componentRepository).updateNameById("New Component Name", VALID_COMPONENT_ID);
        verify(componentRepository).updateDescriptionById("New component description.", VALID_COMPONENT_ID);
    }

    @Test
    void editComponent_notExistingComponentId_shouldThrowItemNotFoundException() {
        var notExistingComponentId = VALID_COMPONENT_ID;
        var anyComponentDto = getComponentRequest();

        when(componentRepository.findById(notExistingComponentId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> componentService.editItem(notExistingComponentId, anyComponentDto))
                .isInstanceOf(ItemNotFoundException.class)
                .hasMessage(ExpectedErrorMessage.componentNotFound(notExistingComponentId));
    }

    @Test
    void editComponent_missingComponentNameAndDescription_shouldThrowBadRequest() {
        var componentDto = new ComponentRequestDto(null, null);

        assertThatThrownBy(() -> componentService.editItem(VALID_COMPONENT_ID, componentDto))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("New value for the component name or description must be provided");
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "\t"})
    void editComponent_blankComponentName_shouldThrowBadRequest(String blankComponentName) {
        var componentDto = new ComponentRequestDto(blankComponentName, null);

        assertThatThrownBy(() -> componentService.editItem(VALID_COMPONENT_ID, componentDto))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Component name cannot be blank");
    }

}
