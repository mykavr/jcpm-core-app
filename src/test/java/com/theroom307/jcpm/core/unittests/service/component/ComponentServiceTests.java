package com.theroom307.jcpm.core.unittests.service.component;

import com.theroom307.jcpm.core.controller.exception.BadRequestException;
import com.theroom307.jcpm.core.controller.exception.ItemNotFoundException;
import com.theroom307.jcpm.core.data.model.Component;
import com.theroom307.jcpm.core.data.repository.ComponentRepository;
import com.theroom307.jcpm.core.service.impl.ComponentServiceImpl;
import com.theroom307.jcpm.core.utils.constant.ExpectedErrorMessage;
import org.junit.jupiter.api.Tag;
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

import java.util.List;
import java.util.Optional;

import static com.theroom307.jcpm.core.TestTypes.UNIT_TEST;
import static com.theroom307.jcpm.core.utils.data.TestComponentData.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Tag(UNIT_TEST)
@ExtendWith(MockitoExtension.class)
class ComponentServiceTests {

    @InjectMocks
    private ComponentServiceImpl componentService;

    @Mock
    private ComponentRepository componentRepository;

    private final int pageNumber = 0;
    private final int pageSize = 10;
    private final Pageable pageable = Pageable.ofSize(pageSize).withPage(pageNumber);

    @Test
    void getComponents_whenOneComponentExists_shouldReturnPageWithOneComponent() {
        Page<Component> pageWithOneComponent = new PageImpl<>(
                List.of(getComponent()),
                pageable,
                1);

        when(componentRepository.findAll(pageable)).thenReturn(pageWithOneComponent);

        var actualResult = componentService.getItems(pageNumber, pageSize);

        assertThat(actualResult)
                .as("The service should return a page with one expected component")
                .isEqualTo(pageWithOneComponent);
    }

    @Test
    void getComponents_whenNoComponentsExist_shouldReturnEmptyPage() {
        Page<Component> emptyPage = Page.empty(pageable);
        when(componentRepository.findAll(pageable)).thenReturn(emptyPage);

        var actualResult = componentService.getItems(pageNumber, pageSize);

        assertThat(actualResult)
                .as("The service should return an empty page")
                .isEqualTo(emptyPage);
    }

    @Test
    void getComponents_whenRepositoryReturnsNull_shouldReturnEmptyPage() {
        when(componentRepository.findAll(pageable)).thenReturn(null); // JPA may return null when there are no components

        var actualResult = componentService.getItems(pageNumber, pageSize);
        assertThat(actualResult)
                .as("The service should return an empty page")
                .isEqualTo(Page.empty(pageable));
    }

    @Test
    void getComponent_whenComponentExists_shouldReturnComponent() {
        var component = getComponent();

        when(componentRepository.findById(component.getId())).thenReturn(Optional.of(component));

        assertThat(componentService.getItem(component.getId()))
                .isEqualTo(component);
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
        var componentToCreate = getComponentToCreate();
        var createdComponent = getComponent();

        when(componentRepository.save(componentToCreate)).thenReturn(createdComponent);

        assertThat(componentService.createItem(componentToCreate))
                .isEqualTo(createdComponent.getId());
    }

    @Test
    void deleteComponent_shouldDeleteComponentFromRepository() {
        var componentId = VALID_COMPONENT_ID;
        componentService.deleteItem(componentId);
        verify(componentRepository).deleteById(componentId);
    }

    @Test
    void editComponent_changeName_shouldUpdateEditedComponent() {
        var editedComponent = new Component();
        editedComponent.setName("New Component Name");

        when(componentRepository.findById(anyLong())).thenReturn(Optional.of(getComponent()));

        componentService.editItem(VALID_COMPONENT_ID, editedComponent);

        verify(componentRepository).updateNameById("New Component Name", VALID_COMPONENT_ID);
    }

    @Test
    void editComponent_changeDescription_shouldUpdateEditedComponent() {
        var editedComponent = new Component();
        editedComponent.setDescription("New component description.");

        when(componentRepository.findById(anyLong())).thenReturn(Optional.of(getComponent()));

        componentService.editItem(VALID_COMPONENT_ID, editedComponent);

        verify(componentRepository).updateDescriptionById("New component description.", VALID_COMPONENT_ID);
    }

    @Test
    void editComponent_changeNameAndDescription_shouldUpdateEditedComponent() {
        var editedComponent = new Component();
        editedComponent.setName("New Component Name");
        editedComponent.setDescription("New component description.");

        when(componentRepository.findById(anyLong())).thenReturn(Optional.of(getComponent()));

        componentService.editItem(VALID_COMPONENT_ID, editedComponent);

        verify(componentRepository).updateNameById("New Component Name", VALID_COMPONENT_ID);
        verify(componentRepository).updateDescriptionById("New component description.", VALID_COMPONENT_ID);
    }

    @Test
    void editComponent_notExistingComponentId_shouldThrowItemNotFoundException() {
        var notExistingComponentId = VALID_COMPONENT_ID;
        var anyComponent = getComponent();

        when(componentRepository.findById(notExistingComponentId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> componentService.editItem(notExistingComponentId, anyComponent))
                .isInstanceOf(ItemNotFoundException.class)
                .hasMessage(ExpectedErrorMessage.componentNotFound(notExistingComponentId));
    }

    @Test
    void editComponent_missingComponentNameAndDescription_shouldThrowBadRequest() {
        var component = new Component();

        assertThatThrownBy(() -> componentService.editItem(VALID_COMPONENT_ID, component))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("New value for the component name or description must be provided");
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "\t"})
    void editComponent_blankComponentName_shouldThrowBadRequest(String blankComponentName) {
        var component = new Component();
        component.setName(blankComponentName);

        assertThatThrownBy(() -> componentService.editItem(VALID_COMPONENT_ID, component))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Component name cannot be blank");
    }

}
