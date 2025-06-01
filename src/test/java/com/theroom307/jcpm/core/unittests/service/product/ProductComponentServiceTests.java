package com.theroom307.jcpm.core.unittests.service.product;

import com.theroom307.jcpm.core.controller.exception.BadRequestException;
import com.theroom307.jcpm.core.controller.exception.ConditionFailedException;
import com.theroom307.jcpm.core.controller.exception.ItemNotFoundException;
import com.theroom307.jcpm.core.controller.exception.NotFoundException;
import com.theroom307.jcpm.core.data.model.Component;
import com.theroom307.jcpm.core.data.model.Product;
import com.theroom307.jcpm.core.data.model.ProductComponent;
import com.theroom307.jcpm.core.data.repository.ProductComponentRepository;
import com.theroom307.jcpm.core.service.ItemService;
import com.theroom307.jcpm.core.service.ProductComponentsService;
import com.theroom307.jcpm.core.service.impl.ProductComponentsServiceImpl;
import com.theroom307.jcpm.core.utils.constant.ExpectedErrorMessage;
import com.theroom307.jcpm.core.utils.constant.Item;
import com.theroom307.jcpm.core.utils.data.TestComponentData;
import com.theroom307.jcpm.core.utils.data.TestProductData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static com.theroom307.jcpm.core.TestTypes.UNIT_TEST;
import static com.theroom307.jcpm.core.utils.data.TestComponentData.VALID_COMPONENT_ID;
import static com.theroom307.jcpm.core.utils.data.TestData.DEFAULT_COMPONENT_QUANTITY;
import static com.theroom307.jcpm.core.utils.data.TestProductData.VALID_PRODUCT_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Tag(UNIT_TEST)
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
    void addComponentToProduct_existingProduct_existingComponent_shouldSaveToRepository() {
        var product = TestProductData.getProduct();
        var component = TestComponentData.getComponent();
        var quantity = 7;
        var productComponent = ProductComponent.builder()
                .product(product)
                .component(component)
                .quantity(quantity)
                .build();

        when(productService.getItem(anyLong())).thenReturn(product);
        when(componentService.getItem(anyLong())).thenReturn(component);

        service.addComponentToProduct(product.getId(), component.getId(), quantity);

        verify(productComponentRepository).save(productComponent);
    }

    @Test
    void addComponentToProduct_shouldRequestFromProductService() {
        mockServices();

        var productId = 123L;
        service.addComponentToProduct(productId, VALID_COMPONENT_ID, DEFAULT_COMPONENT_QUANTITY);
        verify(productService).getItem(productId);
    }

    @Test
    void addComponentToProduct_shouldRequestFromComponentService() {
        mockServices();

        var componentId = 123L;
        service.addComponentToProduct(VALID_PRODUCT_ID, componentId, DEFAULT_COMPONENT_QUANTITY);
        verify(componentService).getItem(componentId);
    }

    @Test
    void addComponentToProduct_nonExistingProduct_shouldThrowProductNotFoundException() {
        var productId = 1234L;
        var expectedException = new ItemNotFoundException(Item.PRODUCT.toString(), productId);
        when(productService.getItem(anyLong())).thenThrow(expectedException);

        assertThatThrownBy(() -> service.addComponentToProduct(productId, VALID_COMPONENT_ID, DEFAULT_COMPONENT_QUANTITY))
                .isInstanceOf(expectedException.getClass())
                .hasMessage(expectedException.getMessage());
    }

    @Test
    void addComponentToProduct_nonExistingComponent_shouldThrowComponentNotFoundException() {
        mockProductService();

        var componentId = 1234L;
        var expectedException = new ItemNotFoundException(Item.COMPONENT.toString(), componentId);
        when(componentService.getItem(anyLong())).thenThrow(expectedException);

        assertThatThrownBy(() -> service.addComponentToProduct(VALID_PRODUCT_ID, componentId, DEFAULT_COMPONENT_QUANTITY))
                .isInstanceOf(expectedException.getClass())
                .hasMessage(expectedException.getMessage());
    }

    @Test
    void addComponentToProduct_componentAlreadyAdded_shouldThrowConditionFailedException() {
        mockServices();

        var productComponent = anyProductComponent().orElseThrow(/*should always be present*/);
        var productId = productComponent.getProduct().getId();
        var componentId = productComponent.getComponent().getId();

        when(productComponentRepository.findProductComponent(productId, componentId))
                .thenReturn(Optional.of(productComponent));

        assertThatThrownBy(() -> service.addComponentToProduct(productId, componentId, DEFAULT_COMPONENT_QUANTITY))
                .isInstanceOf(ConditionFailedException.class)
                .hasMessage(ExpectedErrorMessage.productAlreadyContainsComponent(productId, componentId));
    }

    /*
        REMOVE COMPONENT FROM PRODUCT
     */

    @Test
    void removeComponentFromProduct_existingProduct_addedComponent_shouldDeleteFromRepository() {
        var product = TestProductData.getProduct();
        var component = TestComponentData.getComponent();
        var productComponent = ProductComponent.builder()
                .product(product)
                .component(component)
                .build();

        when(productService.getItem(anyLong())).thenReturn(product);
        when(productComponentRepository.findProductComponent(anyLong(), anyLong()))
                .thenReturn(Optional.of(productComponent));

        service.removeComponentFromProduct(product.getId(), component.getId());

        verify(productComponentRepository).delete(productComponent);
    }

    @Test
    void removeComponentFromProduct_shouldRequestFromProductService() {
        mockProductService();
        when(productComponentRepository.findProductComponent(anyLong(), anyLong()))
                .thenReturn(anyProductComponent());

        var productId = 123L;
        service.removeComponentFromProduct(productId, VALID_COMPONENT_ID);
        verify(productService).getItem(productId);
    }

    @Test
    void removeComponentFromProduct_shouldRequestFromProductComponentRepository() {
        mockProductService();
        when(productComponentRepository.findProductComponent(anyLong(), anyLong()))
                .thenReturn(anyProductComponent());

        var productId = 123L;
        var componentId = 321L;
        service.removeComponentFromProduct(productId, componentId);
        verify(productComponentRepository).findProductComponent(productId, componentId);
    }

    @Test
    void removeComponentFromProduct_nonExistingProduct_shouldThrowProductNotFoundException() {
        var productId = 1234L;
        var expectedException = new ItemNotFoundException(Item.PRODUCT.toString(), productId);
        when(productService.getItem(anyLong())).thenThrow(expectedException);

        assertThatThrownBy(() -> service.removeComponentFromProduct(productId, VALID_COMPONENT_ID))
                .isInstanceOf(expectedException.getClass())
                .hasMessage(expectedException.getMessage());
    }

    @Test
    void removeComponentFromProduct_existingProduct_unrelatedComponent_shouldThrowNotFoundException() {
        mockProductService();
        when(productComponentRepository.findProductComponent(anyLong(), anyLong()))
                .thenReturn(Optional.empty());

        var productId = 123L;
        var componentId = 321L;
        assertThatThrownBy(() -> service.removeComponentFromProduct(productId, componentId))
                .isInstanceOf(NotFoundException.class)
                .hasMessage(ExpectedErrorMessage.productDoesNotContainComponent(productId, componentId));
    }

    /*
        UPDATE COMPONENT QUANTITY
     */

    @Test
    void updateComponentQuantity_existingProduct_existingComponent_shouldUpdateAndSave() {
        var product = TestProductData.getProduct();
        var component = TestComponentData.getComponent();
        var productComponent = ProductComponent.builder()
                .product(product)
                .component(component)
                .quantity(1) // Initial quantity
                .build();

        var newQuantity = 5;

        when(productService.getItem(anyLong())).thenReturn(product);
        when(componentService.getItem(anyLong())).thenReturn(component);
        when(productComponentRepository.findProductComponent(product.getId(), component.getId()))
                .thenReturn(Optional.of(productComponent));

        service.updateComponentQuantity(product.getId(), component.getId(), newQuantity);

        // Verify the quantity was updated
        assertThat(productComponent.getQuantity()).isEqualTo(newQuantity);
        verify(productComponentRepository).save(productComponent);
    }

    @Test
    void updateComponentQuantity_nonExistingProduct_shouldThrowProductNotFoundException() {
        var productId = 1234L;
        var expectedException = new ItemNotFoundException(Item.PRODUCT.toString(), productId);
        when(productService.getItem(anyLong())).thenThrow(expectedException);

        assertThatThrownBy(() -> service.updateComponentQuantity(productId, VALID_COMPONENT_ID, 5))
                .isInstanceOf(expectedException.getClass())
                .hasMessage(expectedException.getMessage());
    }

    @Test
    void updateComponentQuantity_nonExistingComponent_shouldThrowComponentNotFoundException() {
        mockProductService();

        var componentId = 1234L;
        var expectedException = new ItemNotFoundException(Item.COMPONENT.toString(), componentId);
        when(componentService.getItem(anyLong())).thenThrow(expectedException);

        assertThatThrownBy(() -> service.updateComponentQuantity(VALID_PRODUCT_ID, componentId, 5))
                .isInstanceOf(expectedException.getClass())
                .hasMessage(expectedException.getMessage());
    }

    @Test
    void updateComponentQuantity_componentNotInProduct_shouldThrowNotFoundException() {
        mockServices();
        when(productComponentRepository.findProductComponent(anyLong(), anyLong()))
                .thenReturn(Optional.empty());

        var productId = 123L;
        var componentId = 321L;

        assertThatThrownBy(() -> service.updateComponentQuantity(productId, componentId, 5))
                .isInstanceOf(NotFoundException.class)
                .hasMessage(ExpectedErrorMessage.productDoesNotContainComponent(productId, componentId));
    }

    @Test
    void updateComponentQuantity_invalidQuantity_shouldThrowBadRequestException() {
        assertThatThrownBy(() -> service.updateComponentQuantity(VALID_PRODUCT_ID, VALID_COMPONENT_ID, 0))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Quantity must be greater than zero");
    }

    /*
        GET COMPONENTS FOR PRODUCT
     */

    @Test
    void getComponentsForProduct_existingProductWithComponents_shouldReturnComponentsMap() {
        var product = TestProductData.getProduct();
        var component1 = TestComponentData.getComponent();
        var component2 = TestComponentData.getComponent();
        component2.setId(456L);
        component2.setName("second component");

        var productComponent1 = ProductComponent.builder()
                .product(product)
                .component(component1)
                .quantity(3)
                .build();

        var productComponent2 = ProductComponent.builder()
                .product(product)
                .component(component2)
                .quantity(7)
                .build();

        when(productService.getItem(product.getId())).thenReturn(product);
        when(productComponentRepository.findAllByProductId(product.getId()))
                .thenReturn(List.of(productComponent1, productComponent2));

        var result = service.getComponentsForProduct(product.getId());

        assertThat(result)
                .hasSize(2)
                .containsEntry(component1, 3)
                .containsEntry(component2, 7);
    }

    @Test
    void getComponentsForProduct_existingProductWithNoComponents_shouldReturnEmptyMap() {
        var product = TestProductData.getProduct();

        when(productService.getItem(product.getId())).thenReturn(product);
        when(productComponentRepository.findAllByProductId(product.getId()))
                .thenReturn(List.of());

        var result = service.getComponentsForProduct(product.getId());

        assertThat(result)
                .isEmpty();
    }

    @Test
    void getComponentsForProduct_nonExistingProduct_shouldThrowProductNotFoundException() {
        var productId = 1234L;
        var expectedException = new ItemNotFoundException(Item.PRODUCT.toString(), productId);
        when(productService.getItem(productId)).thenThrow(expectedException);

        assertThatThrownBy(() -> service.getComponentsForProduct(productId))
                .isInstanceOf(expectedException.getClass())
                .hasMessage(expectedException.getMessage());
    }

    /*
        GET PRODUCTS BY COMPONENT
     */

    @Test
    void getProductsByComponent_existingComponentWithProducts_shouldReturnProductsPage() {
        var component = TestComponentData.getComponent();
        var product1 = TestProductData.getProduct();
        var product2 = TestProductData.getProduct();
        product2.setId(456L);
        product2.setName("second product");

        var productComponent1 = ProductComponent.builder()
                .product(product1)
                .component(component)
                .quantity(3)
                .build();

        var productComponent2 = ProductComponent.builder()
                .product(product2)
                .component(component)
                .quantity(7)
                .build();

        when(componentService.getItem(component.getId())).thenReturn(component);
        when(productComponentRepository.findAllByComponentId(component.getId()))
                .thenReturn(List.of(productComponent1, productComponent2));

        var result = service.getProductsByComponent(component.getId(), 0, 10);

        assertThat(result.getContent())
                .hasSize(2)
                .contains(product1, product2);
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getNumber()).isEqualTo(0);
        assertThat(result.getSize()).isEqualTo(10);
    }

    @Test
    void getProductsByComponent_existingComponentWithNoProducts_shouldReturnEmptyPage() {
        var component = TestComponentData.getComponent();

        when(componentService.getItem(component.getId())).thenReturn(component);
        when(productComponentRepository.findAllByComponentId(component.getId()))
                .thenReturn(List.of());

        var result = service.getProductsByComponent(component.getId(), 0, 10);

        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isEqualTo(0);
    }

    @Test
    void getProductsByComponent_nonExistingComponent_shouldThrowComponentNotFoundException() {
        var componentId = 1234L;
        var expectedException = new ItemNotFoundException(Item.COMPONENT.toString(), componentId);
        when(componentService.getItem(componentId)).thenThrow(expectedException);

        assertThatThrownBy(() -> service.getProductsByComponent(componentId, 0, 10))
                .isInstanceOf(expectedException.getClass())
                .hasMessage(expectedException.getMessage());
    }

    /*
        IS COMPONENT IN USE
     */

    @Test
    void isComponentInUse_whenComponentUsedInOneProduct_shouldReturnTrue() {
        var componentId = 419L;
        when(productComponentRepository.countComponentUsage(componentId))
                .thenReturn(1L);

        assertThat(service.isComponentInUse(componentId))
                .as("Should return true because the component is used one time")
                .isTrue();
    }

    @Test
    void isComponentInUse_whenComponentNotUsedInProducts_shouldReturnFalse() {
        var componentId = 419L;
        when(productComponentRepository.countComponentUsage(componentId))
                .thenReturn(0L);

        assertThat(service.isComponentInUse(componentId))
                .as("Should return false because the component is used zero times")
                .isFalse();
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
