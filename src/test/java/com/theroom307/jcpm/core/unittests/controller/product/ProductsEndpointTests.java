package com.theroom307.jcpm.core.unittests.controller.product;

import com.theroom307.jcpm.core.controller.ProductController;
import com.theroom307.jcpm.core.data.model.Product;
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

import static com.theroom307.jcpm.core.utils.TestProductData.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
@Import(ItemDtoMapperImpl.class)
@MockBean(ProductComponentsService.class)
class ProductsEndpointTests {

    private final static String ENDPOINT = Endpoint.PRODUCTS.getEndpoint();

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemService<Product> productService;

    @Test
    void getProducts_whenNoProductsExist_shouldReturnEmptyProductListWrapper() throws Exception {
        Page<Product> emptyPage = Page.empty(PageRequest.of(0, 10));

        when(productService.getItems(anyInt(), anyInt())).thenReturn(emptyPage);

        this.mockMvc
                .perform(get(ENDPOINT))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(getEmptyProductListAsString()));
    }

    @Test
    void getProducts_whenOneProductExists_shouldReturnProductListWrapperWithOneProduct() throws Exception {
        var products = new PageImpl<>(List.of(getProduct()), PageRequest.of(0, 10), 1);
        when(productService.getItems(anyInt(), anyInt())).thenReturn(products);

        this.mockMvc
                .perform(get(ENDPOINT))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(getProductListResponseAsString()));
    }

    @Test
    void getProducts_shouldRequestFromProductService() throws Exception {
        this.mockMvc
                .perform(get(ENDPOINT));
        verify(productService).getItems(anyInt(), anyInt());
    }

    @Test
    void postProduct_shouldSaveProduct() throws Exception {
        when(productService.createItem((any(Product.class))))
                .thenReturn(1L);

        this.mockMvc
                .perform(post(ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(getProductDtoToCreateProduct()));

        verify(productService).createItem(getProductToCreate());
    }

    @Test
    void postProduct_shouldReturnProductId() throws Exception {
        var savedProductId = 1L;
        var savedProductIdAsString = String.valueOf(savedProductId);

        when(productService.createItem(any(Product.class)))
                .thenReturn(savedProductId);

        this.mockMvc
                .perform(post(ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(getProductDtoToCreateProduct()))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().string(savedProductIdAsString));
    }
}
