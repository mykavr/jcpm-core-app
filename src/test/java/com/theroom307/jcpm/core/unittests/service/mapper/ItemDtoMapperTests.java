package com.theroom307.jcpm.core.unittests.service.mapper;

import com.theroom307.jcpm.core.data.dto.IResponseDto;
import com.theroom307.jcpm.core.data.dto.wrapper.ListResponseWrapper;
import com.theroom307.jcpm.core.data.dto.wrapper.Pagination;
import com.theroom307.jcpm.core.data.model.Component;
import com.theroom307.jcpm.core.data.model.Product;
import com.theroom307.jcpm.core.service.ItemDtoMapper;
import com.theroom307.jcpm.core.service.impl.ItemDtoMapperImpl;
import com.theroom307.jcpm.core.utils.TestComponentData;
import com.theroom307.jcpm.core.utils.TestProductData;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ItemDtoMapperTests {

    private final ItemDtoMapper mapper = new ItemDtoMapperImpl();

    @Test
    void mapProductRequestDtoToProduct() {
        var productRequestDto = TestProductData.getProductRequest();
        var product = TestProductData.getProductToCreate();

        assertThat(mapper.map(productRequestDto))
                .isEqualTo(product);
    }

    @Test
    void mapProductToProductResponseDto() {
        var product = TestProductData.getProduct();
        var productResponseDto = TestProductData.getProductResponse();

        assertThat(mapper.map(product))
                .isEqualTo(productResponseDto);
    }

    @Test
    void mapComponentRequestDtoToComponent() {
        var componentRequestDto = TestComponentData.getComponentRequest();
        var component = TestComponentData.getComponentToCreate();

        assertThat(mapper.map(componentRequestDto))
                .isEqualTo(component);
    }

    @Test
    void mapComponentToComponentResponseDto() {
        var component = TestComponentData.getComponent();
        var componentResponseDto = TestComponentData.getComponentResponse();

        assertThat(mapper.map(component))
                .isEqualTo(componentResponseDto);
    }

    @Test
    void mapProductsPageToListResponseWrapper() {
        List<Product> products = List.of(TestProductData.getProduct());
        var pageable = Pageable.ofSize(10).withPage(0);
        Page<Product> productPage = new PageImpl<>(products, pageable, 1);

        List<IResponseDto> responseData = List.of(TestProductData.getProductResponse());
        var listResponseWrapper = ListResponseWrapper.builder()
                .pagination(Pagination.from(productPage))
                .data(responseData)
                .build();

        assertThat(mapper.mapProducts(productPage))
                .isEqualTo(listResponseWrapper);
    }

    @Test
    void mapComponentsPageToListResponseWrapper() {
        List<Component> components = List.of(TestComponentData.getComponent());
        var pageable = Pageable.ofSize(10).withPage(0);
        Page<Component> componentPage = new PageImpl<>(components, pageable, 1);

        List<IResponseDto> responseData = List.of(TestComponentData.getComponentResponse());
        var listResponseWrapper = ListResponseWrapper.builder()
                .pagination(Pagination.from(componentPage))
                .data(responseData)
                .build();

        assertThat(mapper.mapComponents(componentPage))
                .isEqualTo(listResponseWrapper);
    }

}
