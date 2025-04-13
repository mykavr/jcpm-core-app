package com.theroom307.jcpm.core.integrationtests;

import com.theroom307.jcpm.core.data.model.Product;
import com.theroom307.jcpm.core.data.repository.ProductRepository;
import com.theroom307.jcpm.core.utils.constant.Endpoint;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

import static com.theroom307.jcpm.core.TestTypes.INTEGRATION_TEST;
import static com.theroom307.jcpm.core.utils.data.TestProductData.getProductToCreate;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Tag(INTEGRATION_TEST)
@SpringBootTest
@AutoConfigureMockMvc
class ProductsPaginationTests {

    private final static String ENDPOINT = Endpoint.PRODUCTS.getEndpoint();

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository productRepository;

    @AfterEach
    void clearRepository() {
        productRepository.deleteAll();
    }

    @Test
    void onePageDefaultParamsTest() throws Exception {
        var tenProducts = createTenProducts();

        mockMvc.perform(get(ENDPOINT))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pagination.page").value(0))
                .andExpect(jsonPath("$.pagination.size").value(10))
                .andExpect(jsonPath("$.pagination.count").value(10))
                .andExpect(jsonPath("$.pagination.total").value(10))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data", hasSize(10)))
                .andExpect(jsonPath("$.data[*].name", containsInAnyOrder(
                        tenProducts.get(0).getName(),
                        tenProducts.get(1).getName(),
                        tenProducts.get(2).getName(),
                        tenProducts.get(3).getName(),
                        tenProducts.get(4).getName(),
                        tenProducts.get(5).getName(),
                        tenProducts.get(6).getName(),
                        tenProducts.get(7).getName(),
                        tenProducts.get(8).getName(),
                        tenProducts.get(9).getName()
                )));
    }

    @Test
    void onePageExplicitParamsTest() throws Exception {
        var tenProducts = createTenProducts();

        mockMvc.perform(get(ENDPOINT)
                        .queryParam("page", "0")
                        .queryParam("size", "10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pagination.page").value(0))
                .andExpect(jsonPath("$.pagination.size").value(10))
                .andExpect(jsonPath("$.pagination.count").value(10))
                .andExpect(jsonPath("$.pagination.total").value(10))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data", hasSize(10)))
                .andExpect(jsonPath("$.data[*].name", containsInAnyOrder(
                        tenProducts.get(0).getName(),
                        tenProducts.get(1).getName(),
                        tenProducts.get(2).getName(),
                        tenProducts.get(3).getName(),
                        tenProducts.get(4).getName(),
                        tenProducts.get(5).getName(),
                        tenProducts.get(6).getName(),
                        tenProducts.get(7).getName(),
                        tenProducts.get(8).getName(),
                        tenProducts.get(9).getName()
                )));
    }

    @Test
    void twoPagesTest() throws Exception {
        var tenProducts = createTenProducts();

        // request the first page
        mockMvc.perform(get(ENDPOINT)
                        .queryParam("page", "0")
                        .queryParam("size", "7"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pagination.page").value(0))
                .andExpect(jsonPath("$.pagination.size").value(7))
                .andExpect(jsonPath("$.pagination.count").value(7))
                .andExpect(jsonPath("$.pagination.total").value(10))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data", hasSize(7)))
                .andExpect(jsonPath("$.data[*].name", containsInAnyOrder(
                        tenProducts.get(0).getName(),
                        tenProducts.get(1).getName(),
                        tenProducts.get(2).getName(),
                        tenProducts.get(3).getName(),
                        tenProducts.get(4).getName(),
                        tenProducts.get(5).getName(),
                        tenProducts.get(6).getName()
                )));

        //request the second page
        mockMvc.perform(get(ENDPOINT)
                        .queryParam("page", "1")
                        .queryParam("size", "7"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pagination.page").value(1))
                .andExpect(jsonPath("$.pagination.size").value(7))
                .andExpect(jsonPath("$.pagination.count").value(3))
                .andExpect(jsonPath("$.pagination.total").value(10))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data", hasSize(3)))
                .andExpect(jsonPath("$.data[*].name", containsInAnyOrder(
                        tenProducts.get(7).getName(),
                        tenProducts.get(8).getName(),
                        tenProducts.get(9).getName()
                )));
    }

    private List<Product> createTenProducts() {
        var createdProducts = Collections.synchronizedList(new ArrayList<Product>());
        IntStream.rangeClosed(1, 10).forEach(i -> {
            var product = getProductToCreate();
            product.setName(product.getName() + " " + i);
            createdProducts.add(
                    productRepository.save(product));
        });
        return createdProducts;
    }
}
