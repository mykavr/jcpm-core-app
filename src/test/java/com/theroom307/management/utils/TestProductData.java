package com.theroom307.management.utils;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.theroom307.management.data.dto.ProductDTO;
import com.theroom307.management.data.model.Product;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

import java.time.ZonedDateTime;

import static com.theroom307.management.data.dto.ProductDTO.DATE_TIME_FORMATTER;

@UtilityClass
public class TestProductData {

    public static final Long VALID_PRODUCT_ID = Long.MAX_VALUE;

    public static ProductDTO getProductDto() {
        return new ProductDTO(
                123L,
                "product name",
                "product description",
                "2023-03-12T18:23:01Z",
                "2023-03-12T18:24:59Z"
        );
    }

    public static String getProductDtoAsString() {
        return getProductDtoAsString(getProductDto());
    }

    @SneakyThrows(JsonProcessingException.class)
    private static String getProductDtoAsString(ProductDTO productDTO) {
        return new ObjectMapper()
                .setSerializationInclusion(Include.NON_NULL)
                .writeValueAsString(productDTO);
    }

    public static String getProductDtoToCreateProduct() {
        var template = getProductDto();
        var productDto = new ProductDTO(
                null,
                template.name(),
                template.description(),
                null,
                null
        );
        return getProductDtoAsString(productDto);
    }

    public static Product getProduct() {
        var entity = new Product();
        entity.setId(123L);
        entity.setName("product name");
        entity.setDescription("product description");
        entity.setCreated(ZonedDateTime.parse("2023-03-12T18:23:01Z", DATE_TIME_FORMATTER));
        entity.setModified(ZonedDateTime.parse("2023-03-12T18:24:59Z", DATE_TIME_FORMATTER));
        return entity;
    }

    public static Product getProductToCreate() {
        var entity = getProduct();
        entity.setId(null);
        entity.setCreated(null);
        entity.setModified(null);
        return entity;
    }
}
