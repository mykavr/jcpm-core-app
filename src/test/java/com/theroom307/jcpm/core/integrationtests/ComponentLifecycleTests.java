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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.ZonedDateTime;
import java.util.function.Predicate;

import static com.theroom307.jcpm.core.TestTypes.INTEGRATION_TEST;
import static com.theroom307.jcpm.core.utils.data.TestComponentData.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Tag(INTEGRATION_TEST)
@SpringBootTest
@AutoConfigureMockMvc
class ComponentLifecycleTests {

    private final static String COMPONENTS_ENDPOINT = Endpoint.COMPONENTS.getEndpoint();

    private final static String COMPONENT_ENDPOINT = Endpoint.COMPONENT.getEndpoint();

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ComponentRepository componentRepository;

    @AfterEach
    void clearRepository() {
        componentRepository.deleteAll();
    }

    @Test
    void getEmptyComponentsList() throws Exception {
        mockMvc.perform(get(COMPONENTS_ENDPOINT))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(getEmptyComponentListAsString()));
    }

    @Test
    void createNewComponent() throws Exception {
        var response = mockMvc
                .perform(post(COMPONENTS_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(getComponentDtoToCreateComponent()))
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn().getResponse();

        long createdComponentId = Long.parseLong(
                response.getContentAsString());

        var createdComponent = componentRepository.findById(createdComponentId);

        assertThat(createdComponent)
                .as("The component should be saved to the DB")
                .isPresent()
                .get()
                .as("Component name should be properly saved")
                .hasFieldOrPropertyWithValue("name", getComponent().getName())
                .as("Component description should be properly saved")
                .hasFieldOrPropertyWithValue("description", getComponent().getDescription());
    }

    @Test
    void getExistingComponent() throws Exception {
        var component = componentRepository.save(getComponentToCreate());

        var response = mockMvc
                .perform(get(String.format(COMPONENT_ENDPOINT, component.getId())))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn().getResponse();

        var expectedComponentDtoJson = getResponseForComponent(component);

        assertThat(response.getContentAsString())
                .as("Response shouldn't be empty")
                .isNotBlank()
                .as("Response should contain the proper component data")
                .isEqualToIgnoringWhitespace(expectedComponentDtoJson);
    }

    @Test
    void editExistingComponent() throws Exception {
        var component = getComponentToCreate();
        component.setName("Component Name Before Editing");
        component.setDescription("Component description before editing.");
        final var originalComponent = componentRepository.save(component);

        mockMvc
                .perform(patch(String.format(COMPONENT_ENDPOINT, originalComponent.getId()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(getComponentDtoToCreateComponent()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").doesNotExist());

        var editedComponent = componentRepository.findById(originalComponent.getId());

        assertThat(editedComponent)
                .as("The component should remain in the repository")
                .isPresent()
                .get()
                .as("The component name should be updated")
                .hasFieldOrPropertyWithValue("name", getComponent().getName())
                .as("The component description should be updated")
                .hasFieldOrPropertyWithValue("description", getComponent().getDescription())
                .as("The created timestamp should not change")
                .extracting(Component::getCreated)
                .matches(isEqualTo(originalComponent.getCreated()),
                        "should be " + originalComponent.getCreated());

        // TODO: Test the modified timestamp updating after JCPM-53 is fixed
//        assertThat(editedComponent)
//                .get()
//                .extracting(Component::getModified)
//                .as("The modified timestamp should have changed")
//                .matches(not(isEqualTo(originalComponent.getModified())),
//                        "should be after " + originalComponent.getModified())
//                .as("The modified timestamp should be after the original value")
//                .matches((modified) -> modified.isAfter(originalComponent.getModified()),
//                        "should be after " + originalComponent.getModified());
    }

    private Predicate<ZonedDateTime> isEqualTo(ZonedDateTime expected) {
        return (actual) -> actual.toEpochSecond() == expected.toEpochSecond();
    }

    @Test
    void getComponentsList() throws Exception {
        var component = componentRepository.save(getComponentToCreate());

        var expectedSingleComponentResponse = getResponseForComponent(component);
        var expectedComponentsList = getComponentListResponseAsString(expectedSingleComponentResponse);

        mockMvc.perform(get(COMPONENTS_ENDPOINT))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expectedComponentsList));
    }

    @Test
    void deleteComponent() throws Exception {
        var component = componentRepository.save(getComponentToCreate());

        mockMvc.perform(delete(String.format(COMPONENT_ENDPOINT, component.getId())))
                .andDo(print())
                .andExpect(status().isOk());

        assertThat(componentRepository.findById(component.getId()))
                .as("The component should be not present")
                .isNotPresent();
    }

}
