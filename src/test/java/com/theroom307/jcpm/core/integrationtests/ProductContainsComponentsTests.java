package com.theroom307.jcpm.core.integrationtests;

import com.theroom307.jcpm.core.data.model.Component;
import com.theroom307.jcpm.core.data.model.Product;
import com.theroom307.jcpm.core.data.model.ProductComponent;
import com.theroom307.jcpm.core.data.repository.ComponentRepository;
import com.theroom307.jcpm.core.data.repository.ProductComponentRepository;
import com.theroom307.jcpm.core.data.repository.ProductRepository;
import com.theroom307.jcpm.core.utils.Endpoint;
import com.theroom307.jcpm.core.utils.TestComponentData;
import com.theroom307.jcpm.core.utils.TestData;
import com.theroom307.jcpm.core.utils.TestProductData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
        var payload = TestData.getAddComponentToProductRequestBody(component.getId());

        mockMvc.perform(patch(endpoint)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andDo(print())
                .andExpect(status().isOk());

        assertThatProductComponentIsSavedInRepository();
    }

    @Test
    void removeComponentFromProduct() throws Exception {
        createProductComponentInRepository();

        var endpoint = Endpoint.PRODUCT_COMPONENTS.getEndpoint(product.getId());
        var payload = TestData.getRemoveComponentFromProductRequestBody(component.getId());

        mockMvc.perform(patch(endpoint)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andDo(print())
                .andExpect(status().isOk());

        assertThat(productComponentRepository.findProductComponent(product.getId(), component.getId()))
                .as("Product-component relation should be removed from the repository")
                .isNotPresent();
    }

    private void createProductComponentInRepository() {
        var productComponentEntity = ProductComponent.builder()
                .product(product)
                .component(component)
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
