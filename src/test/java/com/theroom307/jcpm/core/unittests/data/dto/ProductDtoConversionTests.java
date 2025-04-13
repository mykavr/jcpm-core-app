package com.theroom307.jcpm.core.unittests.data.dto;

import com.theroom307.jcpm.core.data.dto.ProductResponseDto;
import org.junit.jupiter.api.Test;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import static com.theroom307.jcpm.core.utils.TestProductData.*;
import static org.assertj.core.api.Assertions.assertThat;

class ProductDtoConversionTests {

    @Test
    void convertProductDtoToProduct() {
        var productRequestDto = getProductRequest();
        var resultEntity = productRequestDto.toEntity();

        assertThat(resultEntity)
                .as("ProductRequestDto should be properly converted to a Product entity")
                .hasFieldOrPropertyWithValue("name", getProduct().getName())
                .hasFieldOrPropertyWithValue("description", getProduct().getDescription());
    }

    @Test
    void convertProductToProductDto() {
        var productEntity = getProduct();
        var resultProductDTO = ProductResponseDto.fromEntity(productEntity);

        assertThat(resultProductDTO)
                .as("Product entity should be properly converted to a ProductDTO instance")
                .isEqualTo(getProductResponse());
    }

    @Test
    void omitMillisecondsInProductDto() {
        var dateTime = ZonedDateTime.of(2023, 3, 18,
                18, 28, 3, 999999999, ZoneOffset.UTC);
        var expectedTimestamp = "2023-03-18T18:28:03Z";

        var product = getProduct();
        product.setCreated(dateTime);
        product.setModified(dateTime);

        assertThat(ProductResponseDto.fromEntity(product))
                .as("Milli- and nanoseconds should be skipped")
                .hasFieldOrPropertyWithValue("created", expectedTimestamp)
                .hasFieldOrPropertyWithValue("modified", expectedTimestamp);
    }

}
