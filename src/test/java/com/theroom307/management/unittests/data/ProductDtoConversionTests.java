package com.theroom307.management.unittests.data;

import com.theroom307.management.data.dto.ProductDTO;
import org.junit.jupiter.api.Test;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import static com.theroom307.management.utils.TestProductData.getProduct;
import static com.theroom307.management.utils.TestProductData.getProductDto;
import static org.assertj.core.api.Assertions.assertThat;

class ProductDtoConversionTests {

    @Test
    void convertProductDtoToProduct() {
        var productDto = getProductDto();
        var resultEntity = productDto.toEntity();

        assertThat(resultEntity)
                .as("ProductDTO should be properly converted to a Product entity")
                .isEqualTo(getProduct());
    }

    @Test
    void convertProductToProductDto() {
        var productEntity = getProduct();
        var resultProductDTO = ProductDTO.fromEntity(productEntity);

        assertThat(resultProductDTO)
                .as("Product entity should be properly converted to a ProductDTO instance")
                .isEqualTo(getProductDto());
    }

    @Test
    void omitMillisecondsInProductDto() {
        var dateTime = ZonedDateTime.of(2023, 3, 18,
                18, 28, 3, 999999999, ZoneOffset.UTC);
        var expectedTimestamp = "2023-03-18T18:28:03Z";

        var product = getProduct();
        product.setCreated(dateTime);
        product.setModified(dateTime);

        assertThat(ProductDTO.fromEntity(product))
                .as("Milli- and nanoseconds should be skipped")
                .hasFieldOrPropertyWithValue("created", expectedTimestamp)
                .hasFieldOrPropertyWithValue("modified", expectedTimestamp);
    }

}
