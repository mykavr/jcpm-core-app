package com.theroom307.jcpm.core.integrationtests;

import com.theroom307.jcpm.core.data.model.Component;
import com.theroom307.jcpm.core.data.model.Product;
import com.theroom307.jcpm.core.data.model.ProductComponent;
import com.theroom307.jcpm.core.data.repository.ComponentRepository;
import com.theroom307.jcpm.core.data.repository.ProductComponentRepository;
import com.theroom307.jcpm.core.data.repository.ProductRepository;
import com.theroom307.jcpm.core.utils.constant.Endpoint;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

        assertThatProductComponentIsSavedInRepository();
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

    private void createProductComponentInRepository() {
        var productComponentEntity = ProductComponent.builder()
                .product(product)
                .component(component)
                .quantity(1)  // Default quantity
                .build();
        productComponentRepository.save(productComponentEntity);
        assertThatProductComponentIsSavedInRepository();
    }

    private void assertThatProductComponentIsSavedInRepository() {
        assertThat(productComponentRepository.findProductComponent(product.getId(), component.getId()))
                .as("Product-component relation should be saved in the repository")
                .isPresent();
    }
}
