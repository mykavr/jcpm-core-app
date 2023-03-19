package com.theroom307.management.controller;

import com.theroom307.management.controller.exception.BadRequestException;
import com.theroom307.management.controller.exception.ProductNotFoundException;
import com.theroom307.management.data.dto.ProductRequestDto;
import com.theroom307.management.data.dto.ProductResponseDto;
import com.theroom307.management.data.repository.ProductRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("/api/v1/product")
@RequiredArgsConstructor
@Tag(name = "Product API")
public class ProductController {

    private final ProductRepository productRepository;

    @Operation(summary = "Get the list of all products")
    @GetMapping
    public List<ProductResponseDto> getProducts() {
        var allProducts = productRepository.findAll();
        return StreamSupport.stream(allProducts.spliterator(), false)
                .map(ProductResponseDto::fromEntity)
                .toList();
    }

    @Operation(summary = "Get a product by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "404", description = "Product not found or invalid ID", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid product ID", content = @Content)
    })
    @GetMapping("/{productId}")
    public ProductResponseDto getProductById(
            @PathVariable
            String productId
    ) {
        var id = parseProductIdFromString(productId);
        var product = productRepository.findById(id);
        if (product.isPresent()) {
            return ProductResponseDto.fromEntity(product.get());
        } else {
            throw new ProductNotFoundException();
        }
    }

    @Operation(summary = "Create a new product")
    @ApiResponse(responseCode = "201",
            description = "The product has been created, see the product ID in the response",
            content = @Content(schema = @Schema(type = "integer", description = "Product ID", example = "1")))
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public long createNewProduct(
            @RequestBody
            ProductRequestDto product
    ) {
        var entity = product.toEntity();
        var savedEntity = productRepository.save(entity);
        return savedEntity.getId();
    }

    @Operation(summary = "Delete a product by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "The product has been deleted or doesn't exist",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid product ID", content = @Content)
    })
    @DeleteMapping("/{productId}")
    public void deleteProductById(
            @PathVariable
            String productId
    ) {
        var id = parseProductIdFromString(productId);
        productRepository.deleteById(id);
    }

    private long parseProductIdFromString(String productId) {
        try {
            return Long.parseLong(productId);
        } catch (NumberFormatException e) {
            throw new BadRequestException();
        }
    }
}
