package com.theroom307.jcpm.core.utils;

import com.theroom307.jcpm.core.data.dto.ProductRequestDto;
import com.theroom307.jcpm.core.data.dto.ProductResponseDto;
import com.theroom307.jcpm.core.data.model.Product;
import lombok.experimental.UtilityClass;

import java.time.ZonedDateTime;

import static com.theroom307.jcpm.core.data.dto.IResponseDto.DATE_TIME_FORMATTER;

@UtilityClass
public class TestProductData {

    public static final Long VALID_PRODUCT_ID = Long.MAX_VALUE;

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
        return ConversionHelper.getAsString(getProductResponse());
    }

    public static String getResponseForProduct(Product product) {
        var response = ProductResponseDto.fromEntity(product);
        return ConversionHelper.getAsString(response);
    }

    public static String getProductDtoToCreateProduct() {
        var productDto = getProductRequest();
        return ConversionHelper.getAsString(productDto);
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
