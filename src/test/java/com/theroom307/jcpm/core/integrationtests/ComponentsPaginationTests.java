package com.theroom307.jcpm.core.integrationtests;

import com.theroom307.jcpm.core.data.model.Component;
import com.theroom307.jcpm.core.data.repository.ComponentRepository;
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
import static com.theroom307.jcpm.core.utils.data.TestComponentData.getComponentToCreate;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Tag(INTEGRATION_TEST)
@SpringBootTest
@AutoConfigureMockMvc
class ComponentsPaginationTests {

    private final static String ENDPOINT = Endpoint.COMPONENTS.getEndpoint();

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ComponentRepository componentRepository;

    @AfterEach
    void clearRepository() {
        componentRepository.deleteAll();
    }

    @Test
    void onePageDefaultParamsTest() throws Exception {
        var tenComponents = createTenComponents();

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
                        tenComponents.get(0).getName(),
                        tenComponents.get(1).getName(),
                        tenComponents.get(2).getName(),
                        tenComponents.get(3).getName(),
                        tenComponents.get(4).getName(),
                        tenComponents.get(5).getName(),
                        tenComponents.get(6).getName(),
                        tenComponents.get(7).getName(),
                        tenComponents.get(8).getName(),
                        tenComponents.get(9).getName()
                )));
    }

    @Test
    void onePageExplicitParamsTest() throws Exception {
        var tenComponents = createTenComponents();

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
                        tenComponents.get(0).getName(),
                        tenComponents.get(1).getName(),
                        tenComponents.get(2).getName(),
                        tenComponents.get(3).getName(),
                        tenComponents.get(4).getName(),
                        tenComponents.get(5).getName(),
                        tenComponents.get(6).getName(),
                        tenComponents.get(7).getName(),
                        tenComponents.get(8).getName(),
                        tenComponents.get(9).getName()
                )));
    }

    @Test
    void twoPagesTest() throws Exception {
        var tenComponents = createTenComponents();

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
                        tenComponents.get(0).getName(),
                        tenComponents.get(1).getName(),
                        tenComponents.get(2).getName(),
                        tenComponents.get(3).getName(),
                        tenComponents.get(4).getName(),
                        tenComponents.get(5).getName(),
                        tenComponents.get(6).getName()
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
                        tenComponents.get(7).getName(),
                        tenComponents.get(8).getName(),
                        tenComponents.get(9).getName()
                )));
    }

    private List<Component> createTenComponents() {
        var createdComponents = Collections.synchronizedList(new ArrayList<Component>());
        IntStream.rangeClosed(1, 10).forEach(i -> {
            var component = getComponentToCreate();
            component.setName(component.getName() + " " + i);
            createdComponents.add(
                    componentRepository.save(component));
        });
        return createdComponents;
    }
}
