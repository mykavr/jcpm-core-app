package com.theroom307.jcpm.core.unittests.service;

import com.theroom307.jcpm.core.controller.exception.BadRequestException;
import com.theroom307.jcpm.core.controller.exception.ItemNotFoundException;
import com.theroom307.jcpm.core.controller.exception.NotFoundException;
import com.theroom307.jcpm.core.data.model.Component;
import com.theroom307.jcpm.core.data.model.Product;
import com.theroom307.jcpm.core.data.model.ProductComponent;
import com.theroom307.jcpm.core.data.repository.ProductComponentRepository;
import com.theroom307.jcpm.core.service.ItemService;
import com.theroom307.jcpm.core.service.ProductComponentsService;
import com.theroom307.jcpm.core.service.impl.ProductComponentsServiceImpl;
import com.theroom307.jcpm.core.utils.ExpectedErrorMessage;
import com.theroom307.jcpm.core.utils.Item;
import com.theroom307.jcpm.core.utils.TestComponentData;
import com.theroom307.jcpm.core.utils.TestProductData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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

    // Cannot use @InjectMocks because Mockito injects productService instead of componentService:
    // https://github.com/mockito/mockito/issues/1066
    private ProductComponentsService service;

    @Mock
    private ItemService<Product> productService;

    @Mock
    private ItemService<Component> componentService;

    @Mock
    private ProductComponentRepository productComponentRepository;

    @BeforeEach
    void initProductComponentsService() {
        service = new ProductComponentsServiceImpl(productService, componentService, productComponentRepository);
    }

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

        when(productService.getItem(anyLong())).thenReturn(product);
        when(componentService.getItem(anyLong())).thenReturn(component);

        service.editComponent(product.getId(), component.getId(), true, false);

        verify(productComponentRepository).save(productComponent);
    }

    @Test
    void editComponent_whenAddComponent_shouldRequestFromProductService() {
        mockServices();

        var productId = 123L;
        service.editComponent(productId, VALID_COMPONENT_ID, true, false);
        verify(productService).getItem(productId);
    }

    @Test
    void editComponent_whenAddComponent_shouldRequestFromComponentService() {
        mockServices();

        var componentId = 123L;
        service.editComponent(VALID_PRODUCT_ID, componentId, true, false);
        verify(componentService).getItem(componentId);
    }

    @Test
    void editComponent_whenAddComponent_nonExistingProduct_shouldThrowProductNotFoundException() {
        var productId = 1234L;
        var expectedException = new ItemNotFoundException(Item.PRODUCT.toString(), productId);
        when(productService.getItem(anyLong())).thenThrow(expectedException);

        assertThatThrownBy(() -> service.editComponent(productId, VALID_COMPONENT_ID, true, false))
                .isInstanceOf(expectedException.getClass())
                .hasMessage(expectedException.getMessage());
    }

    @Test
    void editComponent_whenAddComponent_nonExistingComponent_shouldThrowComponentNotFoundException() {
        mockProductService();

        var componentId = 1234L;
        var expectedException = new ItemNotFoundException(Item.COMPONENT.toString(), componentId);
        when(componentService.getItem(anyLong())).thenThrow(expectedException);

        assertThatThrownBy(() -> service.editComponent(VALID_PRODUCT_ID, componentId, true, false))
                .isInstanceOf(expectedException.getClass())
                .hasMessage(expectedException.getMessage());
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

        when(productService.getItem(anyLong())).thenReturn(product);
        when(productComponentRepository.findProductComponent(anyLong(), anyLong()))
                .thenReturn(Optional.of(productComponent));

        service.editComponent(product.getId(), component.getId(), false, true);

        verify(productComponentRepository).delete(productComponent);
    }

    @Test
    void editComponent_whenRemoveComponent_shouldRequestFromProductService() {
        mockProductService();
        when(productComponentRepository.findProductComponent(anyLong(), anyLong()))
                .thenReturn(anyProductComponent());

        var productId = 123L;
        service.editComponent(productId, VALID_COMPONENT_ID, false, true);
        verify(productService).getItem(productId);
    }

    @Test
    void editComponent_whenRemoveComponent_shouldRequestFromProductComponentRepository() {
        mockProductService();
        when(productComponentRepository.findProductComponent(anyLong(), anyLong()))
                .thenReturn(anyProductComponent());

        var productId = 123L;
        var componentId = 321L;
        service.editComponent(productId, componentId, false, true);
        verify(productComponentRepository).findProductComponent(productId, componentId);
    }

    @Test
    void editComponent_whenRemoveComponent_nonExistingProduct_shouldThrowProductNotFoundException() {
        var productId = 1234L;
        var expectedException = new ItemNotFoundException(Item.PRODUCT.toString(), productId);
        when(productService.getItem(anyLong())).thenThrow(expectedException);

        assertThatThrownBy(() -> service.editComponent(productId, VALID_COMPONENT_ID, false, true))
                .isInstanceOf(expectedException.getClass())
                .hasMessage(expectedException.getMessage());
    }

    @Test
    void editComponent_whenRemoveComponent_existingProduct_unrelatedComponent_shouldThrowNotFoundException() {
        mockProductService();
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

    private void mockServices() {
        mockProductService();
        when(componentService.getItem(anyLong())).thenReturn(TestComponentData.getComponent());
    }

    private void mockProductService() {
        when(productService.getItem(anyLong())).thenReturn(TestProductData.getProduct());
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
