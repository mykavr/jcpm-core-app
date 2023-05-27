package com.theroom307.jcpm.core.controller;

import com.theroom307.jcpm.core.data.dto.IResponseDto;
import com.theroom307.jcpm.core.data.dto.ProductRequestDto;
import com.theroom307.jcpm.core.data.dto.ProductResponseDto;
import com.theroom307.jcpm.core.data.dto.wrapper.ListResponseWrapper;
import com.theroom307.jcpm.core.data.model.Product;
import com.theroom307.jcpm.core.service.ItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/product")
@Validated
@RequiredArgsConstructor
@Tag(name = "Product API")
@Slf4j
public class ProductController {

    private final ItemService<Product> productService;

    private static final String DEFAULT_PAGE_SIZE = "10";

    @Operation(summary = "Get the list of all products (paginated)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", content = @Content(
                    mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = ProductResponseDto.class)))),
            @ApiResponse(responseCode = "400", description = "Invalid pagination parameters", content = @Content)
    })
    @GetMapping
    public ListResponseWrapper<IResponseDto>
    getProducts(
            @RequestParam(defaultValue = "0")
            @Schema(type = "integer", defaultValue = "0",
                    description = "Pagination: zero-based page index, must not be negative")
            @Min(value = 0, message = "Page must not be negative")
            int page,

            @RequestParam(defaultValue = DEFAULT_PAGE_SIZE)
            @Schema(type = "integer", defaultValue = DEFAULT_PAGE_SIZE,
                    description = "Pagination: the size of the page to be returned, must be greater than 0")
            @Min(value = 1, message = "Page size must be greater than 0")
            int size
    ) {
        log.info("Received a Get Products request with pagination parameters: page={}, size={}", page, size);
        var products = productService.getItems(page, size);
        log.info("Responding with {} products", products == null ? "null" : products.data().size());
        return products;
    }

    @Operation(summary = "Get a product by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", content = @Content(
                    mediaType = "application/json", schema =  @Schema(implementation = ProductResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Product not found or invalid ID", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid product ID", content = @Content)
    })
    @GetMapping("/{productId}")
    public IResponseDto
    getProductById(
            @PathVariable
            @Min(value = 1, message = "Product ID must be greater than zero")
            long productId
    ) {
        log.info("Received a Get Product request for productId={}", productId);
        var product = productService.getItem(productId);
        log.info("Responding with {}", product);
        return product;
    }

    @Operation(summary = "Create a new product")
    @ApiResponse(responseCode = "201",
            description = "The product has been created, see the product ID in the response",
            content = @Content(schema = @Schema(type = "integer", description = "Product ID", example = "1")))
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public long
    createNewProduct(
            @RequestBody
            @Valid
            ProductRequestDto product
    ) {
        log.info("Received a Create Product request: {}", product);
        return productService.createItem(product);
    }

    @Operation(summary = "Edit a product")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product data sucessfully updated", content = @Content),
            @ApiResponse(responseCode = "404", description = "Product not found or invalid ID", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid product ID or data", content = @Content)
    })
    @PatchMapping("/{productId}")
    public void
    editProduct(
            @PathVariable
            @Min(value = 1, message = "Product ID must be greater than zero")
            long productId,

            @RequestBody
            ProductRequestDto product
    ) {
        log.info("Received an Edit Product request for productId={} with input: {}", productId, product);
        productService.editItem(productId, product);
    }

    @Operation(summary = "Delete a product by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "The product has been deleted or doesn't exist",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid product ID", content = @Content)
    })
    @DeleteMapping("/{productId}")
    public void
    deleteProductById(
            @PathVariable
            long productId
    ) {
        log.info("Received a Delete Product request for productId={}", productId);
        productService.deleteItem(productId);
    }
}
