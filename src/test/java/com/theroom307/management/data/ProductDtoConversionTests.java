package com.theroom307.management.data;

import com.theroom307.management.data.dto.ProductDTO;
import org.junit.jupiter.api.Test;

import static com.theroom307.management.utils.TestProductData.getProductDto;
import static com.theroom307.management.utils.TestProductData.getProduct;
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

}
