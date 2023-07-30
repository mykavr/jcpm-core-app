package com.theroom307.jcpm.core.unittests.controller;

import com.theroom307.jcpm.core.controller.ComponentController;
import com.theroom307.jcpm.core.data.model.Component;
import com.theroom307.jcpm.core.service.ItemService;
import com.theroom307.jcpm.core.service.ProductComponentsService;
import com.theroom307.jcpm.core.service.impl.ItemDtoMapperImpl;
import com.theroom307.jcpm.core.utils.Endpoint;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static com.theroom307.jcpm.core.utils.TestComponentData.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ComponentController.class)
@Import(ItemDtoMapperImpl.class)
@MockBean(ProductComponentsService.class)
class ComponentsEndpointTests {

    private final static String ENDPOINT = Endpoint.COMPONENTS.getEndpoint();

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemService<Component> componentService;

    @Test
    void getComponents_whenNoComponentsExist_shouldReturnEmptyComponentListWrapper() throws Exception {
        Page<Component> emptyPage = Page.empty(PageRequest.of(0, 10));

        when(componentService.getItems(anyInt(), anyInt())).thenReturn(emptyPage);

        this.mockMvc
                .perform(get(ENDPOINT))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(getEmptyComponentListAsString()));
    }

    @Test
    void getComponents_whenOneComponentExists_shouldReturnComponentListWrapperWithOneComponent() throws Exception {
        var components = new PageImpl<>(List.of(getComponent()), PageRequest.of(0, 10), 1);
        when(componentService.getItems(anyInt(), anyInt())).thenReturn(components);

        this.mockMvc
                .perform(get(ENDPOINT))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(getComponentListResponseAsString()));
    }

    @Test
    void getComponents_shouldRequestFromComponentService() throws Exception {
        this.mockMvc
                .perform(get(ENDPOINT));
        verify(componentService).getItems(anyInt(), anyInt());
    }

    @Test
    void postComponent_shouldSaveComponent() throws Exception {
        when(componentService.createItem((any(Component.class))))
                .thenReturn(1L);

        this.mockMvc
                .perform(post(ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(getComponentDtoToCreateComponent()));

        verify(componentService).createItem(getComponentToCreate());
    }

    @Test
    void postComponent_shouldReturnComponentId() throws Exception {
        var savedComponentId = 1L;
        var savedComponentIdAsString = String.valueOf(savedComponentId);

        when(componentService.createItem(any(Component.class)))
                .thenReturn(savedComponentId);

        this.mockMvc
                .perform(post(ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(getComponentDtoToCreateComponent()))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().string(savedComponentIdAsString));
    }
}
