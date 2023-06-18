package com.theroom307.jcpm.core.unittests.service;

import com.theroom307.jcpm.core.controller.exception.BadRequestException;
import com.theroom307.jcpm.core.controller.exception.ItemNotFoundException;
import com.theroom307.jcpm.core.controller.exception.NotFoundException;
import com.theroom307.jcpm.core.data.model.Component;
import com.theroom307.jcpm.core.data.model.Product;
import com.theroom307.jcpm.core.data.model.ProductComponent;
import com.theroom307.jcpm.core.data.repository.ComponentRepository;
import com.theroom307.jcpm.core.data.repository.ProductComponentRepository;
import com.theroom307.jcpm.core.data.repository.ProductRepository;
import com.theroom307.jcpm.core.service.impl.ProductComponentsServiceImpl;
import com.theroom307.jcpm.core.utils.ExpectedErrorMessage;
import com.theroom307.jcpm.core.utils.TestComponentData;
import com.theroom307.jcpm.core.utils.TestProductData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.theroom307.jcpm.core.utils.TestComponentData.VALID_COMPONENT_ID;
import static com.theroom307.jcpm.core.utils.TestProductData.VALID_PRODUCT_ID;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductComponentServiceTests {

    @InjectMocks
    private ProductComponentsServiceImpl service;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ComponentRepository componentRepository;

    @Mock
    private ProductComponentRepository productComponentRepository;

    /*
        ADD COMPONENT TO PRODUCT
     */

    @Test
    void editComponent_whenAddComponent_existingProduct_existingComponent_shouldSaveToRepository() {
        var product = TestProductData.getProduct();
        var component = TestComponentData.getComponent();
        var productComponent = ProductComponent.builder()
                .product(product)
                .component(component)
                .build();

        when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));
        when(componentRepository.findById(anyLong())).thenReturn(Optional.of(component));

        service.editComponent(product.getId(), component.getId(), true, false);

        verify(productComponentRepository).save(productComponent);
    }

    @Test
    void editComponent_whenAddComponent_shouldRequestFromProductRepository() {
        mockRepositories();

        var productId = 123L;
        service.editComponent(productId, VALID_COMPONENT_ID, true, false);
        verify(productRepository).findById(productId);
    }

    @Test
    void editComponent_whenAddComponent_shouldRequestFromComponentRepository() {
        mockRepositories();

        var componentId = 123L;
        service.editComponent(VALID_PRODUCT_ID, componentId, true, false);
        verify(componentRepository).findById(componentId);
    }

    @Test
    void editComponent_whenAddComponent_nonExistingProduct_shouldThrowProductNotFoundException() {
        when(productRepository.findById(anyLong())).thenReturn(Optional.empty());

        var productId = 1234L;
        assertThatThrownBy(() -> service.editComponent(productId, VALID_COMPONENT_ID, true, false))
                .isInstanceOf(ItemNotFoundException.class)
                .hasMessage(ExpectedErrorMessage.productNotFound(productId));
    }

    @Test
    void editComponent_whenAddComponent_nonExistingComponent_shouldThrowComponentNotFoundException() {
        when(productRepository.findById(anyLong())).thenReturn(anyProduct());
        when(componentRepository.findById(anyLong())).thenReturn(Optional.empty());

        var componentId = 1234L;
        assertThatThrownBy(() -> service.editComponent(VALID_PRODUCT_ID, componentId, true, false))
                .isInstanceOf(ItemNotFoundException.class)
                .hasMessage(ExpectedErrorMessage.componentNotFound(componentId));
    }

    /*
        REMOVE COMPONENT FROM PRODUCT
     */

    @Test
    void editComponent_whenRemoveComponent_existingProduct_addedComponent_shouldDeleteFromRepository() {
        var product = TestProductData.getProduct();
        var component = TestComponentData.getComponent();
        var productComponent = ProductComponent.builder()
                .product(product)
                .component(component)
                .build();

        when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));
        when(productComponentRepository.findProductComponent(anyLong(), anyLong()))
                .thenReturn(Optional.of(productComponent));

        service.editComponent(product.getId(), component.getId(), false, true);

        verify(productComponentRepository).delete(productComponent);
    }

    @Test
    void editComponent_whenRemoveComponent_shouldRequestFromProductRepository() {
        when(productRepository.findById(anyLong())).thenReturn(anyProduct());
        when(productComponentRepository.findProductComponent(anyLong(), anyLong())).thenReturn(anyProductComponent());

        var productId = 123L;
        service.editComponent(productId, VALID_COMPONENT_ID, false, true);
        verify(productRepository).findById(productId);
    }

    @Test
    void editComponent_whenRemoveComponent_shouldRequestFromProductComponentRepository() {
        when(productRepository.findById(anyLong())).thenReturn(anyProduct());
        when(productComponentRepository.findProductComponent(anyLong(), anyLong())).thenReturn(anyProductComponent());

        var productId = 123L;
        var componentId = 321L;
        service.editComponent(productId, componentId, false, true);
        verify(productComponentRepository).findProductComponent(productId, componentId);
    }

    @Test
    void editComponent_whenRemoveComponent_nonExistingProduct_shouldThrowProductNotFoundException() {
        when(productRepository.findById(anyLong())).thenReturn(Optional.empty());

        var productId = 1234L;
        assertThatThrownBy(() -> service.editComponent(productId, VALID_COMPONENT_ID, false, true))
                .isInstanceOf(ItemNotFoundException.class)
                .hasMessage(ExpectedErrorMessage.productNotFound(productId));
    }

    @Test
    void editComponent_whenRemoveComponent_existingProduct_unrelatedComponent_shouldThrowNotFoundException() {
        when(productRepository.findById(anyLong())).thenReturn(anyProduct());
        when(productComponentRepository.findProductComponent(anyLong(), anyLong()))
                .thenReturn(Optional.empty());

        var productId = 123L;
        var componentId = 321L;
        assertThatThrownBy(() -> service.editComponent(productId, componentId, false, true))
                .isInstanceOf(NotFoundException.class)
                .hasMessage(ExpectedErrorMessage.productDoesNotContainComponent(productId, componentId));
    }

    /*
        INVALID PARAMETERS HANDLING
     */

    @Test
    void editComponent_whenBothAddComponentAndRemoveComponentAreTrue_shouldThrowBadRequestException() {
        assertThatThrownBy(() -> service.editComponent(VALID_PRODUCT_ID, VALID_COMPONENT_ID, true, true))
                .isInstanceOf(BadRequestException.class)
                .hasMessage(ExpectedErrorMessage.invalidEditProductComponentRequest());
    }

    /*
        HELPER METHODS
     */

    private void mockRepositories() {
        when(productRepository.findById(anyLong())).thenReturn(anyProduct());
        when(componentRepository.findById(anyLong())).thenReturn(anyComponent());
    }

    private Optional<Product> anyProduct() {
        return Optional.of(TestProductData.getProduct());
    }

    private Optional<Component> anyComponent() {
        return Optional.of(TestComponentData.getComponent());
    }

    private Optional<ProductComponent> anyProductComponent() {
        var product = TestProductData.getProduct();
        var component = TestComponentData.getComponent();
        var productComponent = ProductComponent.builder()
                .product(product)
                .component(component)
                .build();
        return Optional.of(productComponent);
    }
}
