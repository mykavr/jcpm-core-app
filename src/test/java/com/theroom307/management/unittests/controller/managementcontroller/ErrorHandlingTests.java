package com.theroom307.management.unittests.controller.managementcontroller;

import com.theroom307.management.controller.InputValidationService;
import com.theroom307.management.controller.ProductController;
import com.theroom307.management.data.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
class ErrorHandlingTests {

    private final static String ENDPOINT = "/api/v1/product";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductRepository productRepository;

    @MockBean
    private InputValidationService inputValidationService;

    @Test
    void shouldReturnGeneralError() throws Exception {
        when(productRepository.findById(anyLong()))
                .thenThrow(new RuntimeException());

        this.mockMvc
                .perform(get(ENDPOINT + "/123"))
                .andDo(print())
                .andExpect(status().is5xxServerError())
                .andExpect(content().string("Sorry, something went wrong"));
    }
}
