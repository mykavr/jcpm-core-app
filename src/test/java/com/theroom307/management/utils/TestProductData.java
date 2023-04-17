package com.theroom307.management.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.theroom307.management.data.dto.ProductRequestDto;
import com.theroom307.management.data.dto.ProductResponseDto;
import com.theroom307.management.data.model.Product;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

import java.time.ZonedDateTime;

import static com.theroom307.management.data.dto.ProductResponseDto.DATE_TIME_FORMATTER;

@UtilityClass
public class TestProductData {

    public static final Long VALID_PRODUCT_ID = Long.MAX_VALUE;
//    public static final Long VALID_PRODUCT_ID = 1L;

    public static Product getProduct() {
        var entity = new Product();
        entity.setId(123L);
        entity.setName("product name");
        entity.setDescription("product description");
        entity.setCreated(ZonedDateTime.parse("2023-03-12T18:23:01Z", DATE_TIME_FORMATTER));
        entity.setModified(ZonedDateTime.parse("2023-03-12T18:24:59Z", DATE_TIME_FORMATTER));
        return entity;
    }

    public static ProductResponseDto getProductResponse() {
        return new ProductResponseDto(
                123L,
                "product name",
                "product description",
                "2023-03-12T18:23:01Z",
                "2023-03-12T18:24:59Z"
        );
    }

    public static ProductRequestDto getProductRequest() {
        return new ProductRequestDto(
                "product name",
                "product description"
        );
    }

    public static String getProductResponseAsString() {
        return getAsString(getProductResponse());
    }

    public static String getResponseForProduct(Product product) {
        var response = ProductResponseDto.fromEntity(product);
        return getAsString(response);
    }

    @SneakyThrows(JsonProcessingException.class)
    public static String getAsString(Object o) {
        return new ObjectMapper().writeValueAsString(o);
    }

    public static String getProductDtoToCreateProduct() {
        var productDto = getProductRequest();
        return getAsString(productDto);
    }

    public static Product getProductToCreate() {
        var entity = getProduct();
        entity.setId(null);
        entity.setCreated(null);
        entity.setModified(null);
        return entity;
    }

    public static String getProductListResponseAsString() {
        return getProductListResponseAsString(getProductResponseAsString());
    }

    public static String getProductListResponseAsString(String productDtoAsString) {
        return String.format("{\"data\":[%s],\"pagination\":{\"page\":0,\"size\":10,\"count\":1,\"total\":1}}",
                productDtoAsString);
    }

    public static String getEmptyProductListAsString() {
        return "{\"data\":[],\"pagination\":{\"page\":0,\"size\":10,\"count\":0,\"total\":0}}";
    }
}
