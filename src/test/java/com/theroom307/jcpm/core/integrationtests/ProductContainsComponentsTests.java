package com.theroom307.jcpm.core.integrationtests;

import com.theroom307.jcpm.core.data.model.Component;
import com.theroom307.jcpm.core.data.model.Product;
import com.theroom307.jcpm.core.data.model.ProductComponent;
import com.theroom307.jcpm.core.data.repository.ComponentRepository;
import com.theroom307.jcpm.core.data.repository.ProductComponentRepository;
import com.theroom307.jcpm.core.data.repository.ProductRepository;
import com.theroom307.jcpm.core.utils.constant.Endpoint;
import com.theroom307.jcpm.core.utils.constant.ExpectedErrorMessage;
import com.theroom307.jcpm.core.utils.data.TestComponentData;
import com.theroom307.jcpm.core.utils.data.TestData;
import com.theroom307.jcpm.core.utils.data.TestProductData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static com.theroom307.jcpm.core.TestTypes.INTEGRATION_TEST;
import static com.theroom307.jcpm.core.utils.data.TestData.DEFAULT_COMPONENT_QUANTITY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Tag(INTEGRATION_TEST)
@SpringBootTest
@AutoConfigureMockMvc
class ProductContainsComponentsTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ComponentRepository componentRepository;

    @Autowired
    private ProductComponentRepository productComponentRepository;

    private Product product;
    private Component component;

    @BeforeEach
    void createProductAndComponent() {
        product = productRepository.save(TestProductData.getProductToCreate());
        component = componentRepository.save(TestComponentData.getComponentToCreate());
    }

    @AfterEach
    void clearRepositories() {
        productComponentRepository.deleteAll();
        productRepository.deleteAll();
        componentRepository.deleteAll();
    }

    @Test
    void addComponentToProduct() throws Exception {
        var endpoint = Endpoint.PRODUCT_COMPONENTS.getEndpoint(product.getId());
        var payload = TestData.getAddComponentRequestBody(component.getId());

        mockMvc.perform(post(endpoint)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andDo(print())
                .andExpect(status().isCreated());

        assertThatProductComponentIsSavedInRepository(product, component);
    }

    @Test
    void removeComponentFromProduct() throws Exception {
        createProductComponentInRepository();

        var endpoint = Endpoint.PRODUCT_COMPONENT.getEndpoint(product.getId(), component.getId());

        mockMvc.perform(delete(endpoint))
                .andDo(print())
                .andExpect(status().isOk());

        assertThat(productComponentRepository.findProductComponent(product.getId(), component.getId()))
                .as("Product-component relation should be removed from the repository")
                .isNotPresent();
    }

    @Test
    void updateComponentQuantity() throws Exception {
        createProductComponentInRepository();

        // Verify initial quantity
        var initialProductComponent = productComponentRepository.findProductComponent(product.getId(), component.getId()).orElseThrow();
        assertThat(initialProductComponent.getQuantity())
                .as("Initial quantity should be %d", DEFAULT_COMPONENT_QUANTITY)
                .isEqualTo(DEFAULT_COMPONENT_QUANTITY);

        int newQuantity = 5;

        // Update the quantity through the API
        var endpoint = Endpoint.PRODUCT_COMPONENT.getEndpoint(product.getId(), component.getId());
        var payload = TestData.getUpdateQuantityRequestBody(newQuantity);

        mockMvc.perform(patch(endpoint)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andDo(print())
                .andExpect(status().isOk());

        // Verify the quantity was updated in the database
        var updatedProductComponent = productComponentRepository
                .findProductComponent(product.getId(), component.getId())
                .orElseThrow();
        assertThat(updatedProductComponent.getQuantity())
                .as("Quantity should be updated to the new value")
                .isEqualTo(newQuantity);
    }

    @Test
    void givenOneComponentInProduct_whenAddingAnotherComponent_thenBothComponentsAreStoredWithCorrectQuantities() throws Exception {
        // Given: one component already added to the product
        var firstComponent = componentRepository.save(TestComponentData.getComponentToCreate());
        int firstComponentQuantity = 3;
        createProductComponentInRepository(firstComponent, firstComponentQuantity);

        // When: adding another component to the product
        var secondComponent = componentRepository.save(TestComponentData.getComponentToCreate());
        int secondComponentQuantity = 5;

        var endpoint = Endpoint.PRODUCT_COMPONENTS.getEndpoint(product.getId());
        var payload = TestData.getAddComponentRequestBody(secondComponent.getId(), secondComponentQuantity);

        mockMvc.perform(post(endpoint)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andDo(print())
                .andExpect(status().isCreated());

        // Then: both components with correct quantities are stored for the product
        var firstProductComponent =
                productComponentRepository.findProductComponent(product.getId(), firstComponent.getId());
        var secondProductComponent =
                productComponentRepository.findProductComponent(product.getId(), secondComponent.getId());

        assertThat(firstProductComponent)
                .as("First component should remain")
                .isPresent();
        assertThat(firstProductComponent.get().getQuantity())
                .as("First component should have quantity %d", firstComponentQuantity)
                .isEqualTo(firstComponentQuantity);

        assertThat(secondProductComponent)
                .as("Second component should remain")
                .isPresent();
        assertThat(secondProductComponent.get().getQuantity())
                .as("Second component should have quantity %d", secondComponentQuantity)
                .isEqualTo(secondComponentQuantity);
    }

    @Test
    void givenTwoComponentsInProduct_whenRemovingOneComponent_thenOneComponentRemainsInProduct() throws Exception {
        // Given: two components added to the product
        var firstComponent = componentRepository.save(TestComponentData.getComponentToCreate());
        createProductComponentInRepository(firstComponent, 3);

        Component secondComponent = componentRepository.save(TestComponentData.getComponentToCreate());
        createProductComponentInRepository(secondComponent, 5);

        // When: removing one of the components
        var endpoint = Endpoint.PRODUCT_COMPONENT.getEndpoint(product.getId(), firstComponent.getId());

        mockMvc.perform(delete(endpoint))
                .andDo(print())
                .andExpect(status().isOk());

        // Then: there is still one component in the product
        assertThat(productComponentRepository.findProductComponent(product.getId(), secondComponent.getId()))
                .as("The second component should remain")
                .isPresent();

        // Verify the removed component is actually gone
        assertThat(productComponentRepository.findProductComponent(product.getId(), firstComponent.getId()))
                .as("The first component should be removed")
                .isNotPresent();
    }

    @Test
    void getComponentsForProduct() throws Exception {
        // Given: product with multiple components
        var firstComponent = componentRepository.save(TestComponentData.getComponentToCreate());
        int firstComponentQuantity = 3;
        createProductComponentInRepository(firstComponent, firstComponentQuantity);

        var secondComponent = componentRepository.save(TestComponentData.getComponentToCreate());
        int secondComponentQuantity = 5;
        createProductComponentInRepository(secondComponent, secondComponentQuantity);

        // When: getting components for the product
        var endpoint = Endpoint.PRODUCT_COMPONENTS.getEndpoint(product.getId());

        mockMvc.perform(get(endpoint))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(2)))

                // Verify first component structure and data
                .andExpect(jsonPath("$[0].component").exists())
                .andExpect(jsonPath("$[0].component.id").isNumber())
                .andExpect(jsonPath("$[0].component.name").isString())
                .andExpect(jsonPath("$[0].component.description").isString())
                .andExpect(jsonPath("$[0].component.created").isString())
                .andExpect(jsonPath("$[0].component.modified").isString())
                .andExpect(jsonPath("$[0].quantity").isNumber())

                // Verify second component structure and data
                .andExpect(jsonPath("$[1].component").exists())
                .andExpect(jsonPath("$[1].component.id").isNumber())
                .andExpect(jsonPath("$[1].component.name").isString())
                .andExpect(jsonPath("$[1].component.description").isString())
                .andExpect(jsonPath("$[1].component.created").isString())
                .andExpect(jsonPath("$[1].component.modified").isString())
                .andExpect(jsonPath("$[1].quantity").isNumber())

                // Verify that each component has the correct ID and quantity
                // Note: We can't guarantee order, so we check that the IDs and quantities exist somewhere
                .andExpect(jsonPath("$[?(@.component.id == " + firstComponent.getId() + ")].quantity").value(firstComponentQuantity))
                .andExpect(jsonPath("$[?(@.component.id == " + secondComponent.getId() + ")].quantity").value(secondComponentQuantity))

                // Verify component names are correctly associated
                .andExpect(jsonPath("$[?(@.component.id == " + firstComponent.getId() + ")].component.name").value(firstComponent.getName()))
                .andExpect(jsonPath("$[?(@.component.id == " + secondComponent.getId() + ")].component.name").value(secondComponent.getName()));
    }

    @Test
    void deleteComponentAddedToProduct() throws Exception {
        createProductComponentInRepository();

        mockMvc.perform(delete(Endpoint.COMPONENT.getEndpoint(component.getId())))
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(content().string(ExpectedErrorMessage.componentIsInUse(component.getId())));

        assertThat(componentRepository.findById(component.getId()))
                .as("The component should be present")
                .isPresent();
    }

    private void createProductComponentInRepository() {
        createProductComponentInRepository(component, DEFAULT_COMPONENT_QUANTITY);
    }

    private void createProductComponentInRepository(Component component, int quantity) {
        var productComponentEntity = ProductComponent.builder()
                .product(product)
                .component(component)
                .quantity(quantity)
                .build();
        productComponentRepository.save(productComponentEntity);

        assertThatProductComponentIsSavedInRepository(product, component);
    }

    @Test
    void getProductsByComponent_existingComponentInMultipleProducts_shouldReturnAllProducts() throws Exception {
        var secondProduct = productRepository.save(TestProductData.getProductToCreate());
        var thirdProduct = productRepository.save(TestProductData.getProductToCreate());
        
        createProductComponentInRepository(component, DEFAULT_COMPONENT_QUANTITY);
        createProductComponentInRepository(secondProduct, component, 3);
        createProductComponentInRepository(thirdProduct, component, 1);

        var endpoint = Endpoint.PRODUCTS.getEndpoint() + "?componentId=" + component.getId();

        mockMvc.perform(get(endpoint))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data", hasSize(3)))
                .andExpect(jsonPath("$.pagination.total").value(3))
                .andExpect(jsonPath("$.pagination.page").value(0))
                .andExpect(jsonPath("$.pagination.size").value(10));
    }

    @Test
    void getProductsByComponent_nonExistentComponent_shouldReturn404() throws Exception {
        var nonExistentComponentId = 99999L;
        var endpoint = Endpoint.PRODUCTS.getEndpoint() + "?componentId=" + nonExistentComponentId;

        mockMvc.perform(get(endpoint))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void getProductsByComponent_componentNotUsedInAnyProduct_shouldReturnEmptyList() throws Exception {
        var unusedComponent = componentRepository.save(TestComponentData.getComponentToCreate());
        var endpoint = Endpoint.PRODUCTS.getEndpoint() + "?componentId=" + unusedComponent.getId();

        mockMvc.perform(get(endpoint))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data", hasSize(0)))
                .andExpect(jsonPath("$.pagination.total").value(0));
    }

    @Test
    void getProductsByComponent_oneOfTwoProductsContainsComponent_shouldReturnOnlyProductWithComponent() throws Exception {
        var secondProduct = productRepository.save(TestProductData.getProductToCreate());
        
        createProductComponentInRepository(component, DEFAULT_COMPONENT_QUANTITY);

        var endpoint = Endpoint.PRODUCTS.getEndpoint() + "?componentId=" + component.getId();

        mockMvc.perform(get(endpoint))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.pagination.total").value(1))
                .andExpect(jsonPath("$.pagination.page").value(0))
                .andExpect(jsonPath("$.pagination.size").value(10))
                .andExpect(jsonPath("$.data[0].id").value(product.getId()));
    }

    private void createProductComponentInRepository(Product product, Component component, int quantity) {
        var productComponentEntity = ProductComponent.builder()
                .product(product)
                .component(component)
                .quantity(quantity)
                .build();
        productComponentRepository.save(productComponentEntity);
    }

    private void assertThatProductComponentIsSavedInRepository(Product product, Component component) {
        assertThat(productComponentRepository.findProductComponent(product.getId(), component.getId()))
                .as("Product-component relation should be saved in the repository")
                .isPresent();
    }
}
