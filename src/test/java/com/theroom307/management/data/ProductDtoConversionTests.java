package com.theroom307.management.data;

import com.theroom307.management.data.dto.ProductDTO;
import com.theroom307.management.data.model.Product;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;

import static com.theroom307.management.data.dto.ProductDTO.DATE_TIME_FORMATTER;
import static org.assertj.core.api.Assertions.assertThat;

class ProductDtoConversionTests {

    @Test
    void convertProductDtoToProduct() {
        var productDto = getProductDtoForTest();
        var resultEntity = productDto.toEntity();

        assertThat(resultEntity)
                .as("ProductDTO should be properly converted to a Product entity")
                .isEqualTo(getProductEntityForTest());
    }

    @Test
    void convertProductToProductDto() {
        var productEntity = getProductEntityForTest();
        var resultProductDTO = ProductDTO.fromEntity(productEntity);

        assertThat(resultProductDTO)
                .as("Product entity should be properly converted to a ProductDTO instance")
                .isEqualTo(getProductDtoForTest());
    }

    private ProductDTO getProductDtoForTest() {
        return new ProductDTO(
                123L,
                "product name",
                "product description",
                456.78F,
                "2023-03-12T18:23:01Z",
                "2023-03-12T18:24:59Z"
        );
    }

    private Product getProductEntityForTest() {
        var entity = new Product();
        entity.setId(123L);
        entity.setName("product name");
        entity.setDescription("product description");
        entity.setPrice(456.78F);
        entity.setCreated(ZonedDateTime.parse("2023-03-12T18:23:01Z", DATE_TIME_FORMATTER));
        entity.setModified(ZonedDateTime.parse("2023-03-12T18:24:59Z", DATE_TIME_FORMATTER));
        return entity;
    }

}
