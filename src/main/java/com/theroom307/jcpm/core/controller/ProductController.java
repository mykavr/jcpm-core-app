package com.theroom307.jcpm.core.controller;

import com.theroom307.jcpm.core.data.dto.*;
import com.theroom307.jcpm.core.data.dto.wrapper.ListResponseWrapper;
import com.theroom307.jcpm.core.data.model.Product;
import com.theroom307.jcpm.core.service.ItemDtoMapper;
import com.theroom307.jcpm.core.service.ItemService;
import com.theroom307.jcpm.core.service.ProductComponentsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/product")
@Validated
@Tag(name = "Product API")
@SuppressWarnings("unused")
public class ProductController extends BaseItemController<Product> {

    private final ProductComponentsService productComponentsService;

    protected ProductController(@Autowired ItemService<Product> service,
                                @Autowired ItemDtoMapper mapper,
                                @Autowired ProductComponentsService productComponentsService) {
        super(service, mapper);
        this.productComponentsService = productComponentsService;
    }

    @Operation(summary = "Get the list of all products (paginated)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = WrappedListOfProducts.class))),
            @ApiResponse(responseCode = "400", description = "Invalid pagination parameters", content = @Content)
    })
    @GetMapping
    public ListResponseWrapper<ProductResponseDto> getProducts(
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
        var products = service.getItems(page, size);
        return mapper.mapProducts(products);
    }

    @Operation(summary = "Get a product by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", content = @Content(
                    mediaType = "application/json", schema = @Schema(implementation = ProductResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Product not found or invalid ID", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid product ID", content = @Content)
    })
    @GetMapping("/{productId}")
    public IResponseDto getProductById(
            @PathVariable
            @Min(value = 1, message = "Product ID must be greater than zero")
            long productId
    ) {
        var product = service.getItem(productId);
        return mapper.map(product);
    }

    @Operation(summary = "Create a new product")
    @ApiResponse(responseCode = "201",
            description = "The product has been created, see the product ID in the response",
            content = @Content(schema = @Schema(type = "integer", description = "Product ID", example = "1")))
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public long createNewProduct(
            @RequestBody
            @Valid
            ProductRequestDto productDto
    ) {
        var product = mapper.map(productDto);
        return service.createItem(product);
    }

    @Operation(summary = "Edit a product")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product data successfully updated", content = @Content),
            @ApiResponse(responseCode = "404", description = "Product not found or invalid ID", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid product ID or data", content = @Content)
    })
    @PatchMapping("/{productId}")
    public void editProduct(
            @PathVariable
            @Min(value = 1, message = "Product ID must be greater than zero")
            long productId,

            @RequestBody
            ProductRequestDto productDto
    ) {
        var product = mapper.map(productDto);
        service.editItem(productId, product);
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
            long productId
    ) {
        service.deleteItem(productId);
    }

    @Operation(summary = "Add a component to a product")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201",
                    description = "Component successfully added to the product",
                    content = @Content),
            @ApiResponse(responseCode = "400",
                    description = "Bad Request - Invalid component ID or quantity",
                    content = @Content),
            @ApiResponse(responseCode = "404",
                    description = "Product or component not found",
                    content = @Content),
            @ApiResponse(responseCode = "409",
                    description = "Component already exists in the product",
                    content = @Content)
    })
    @PostMapping("/{productId}/components")
    @ResponseStatus(HttpStatus.CREATED)
    public void addComponentToProduct(
            @PathVariable
            @Min(value = 1, message = "Product ID must be greater than zero")
            long productId,

            @RequestBody
            @Valid
            ComponentAddDto componentAddDto
    ) {
        productComponentsService.addComponentToProduct(
                productId,
                componentAddDto.getComponentId(),
                componentAddDto.getQuantity());
    }

    @Operation(summary = "Remove a component from a product")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Component successfully removed from the product",
                    content = @Content),
            @ApiResponse(responseCode = "400",
                    description = "Bad Request - Invalid product or component ID",
                    content = @Content),
            @ApiResponse(responseCode = "404",
                    description = "Product not found or component not found in product",
                    content = @Content)
    })
    @DeleteMapping("/{productId}/components/{componentId}")
    public void removeComponentFromProduct(
            @PathVariable
            @Min(value = 1, message = "Product ID must be greater than zero")
            long productId,

            @PathVariable
            @Min(value = 1, message = "Component ID must be greater than zero")
            long componentId
    ) {
        productComponentsService.removeComponentFromProduct(productId, componentId);
    }

    @Operation(summary = "Update the quantity of a component in a product")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Component quantity successfully updated",
                    content = @Content),
            @ApiResponse(responseCode = "400",
                    description = "Bad Request - Invalid product/component ID or quantity",
                    content = @Content),
            @ApiResponse(responseCode = "404",
                    description = "Product not found, component not found, or component not in product",
                    content = @Content)
    })
    @PatchMapping("/{productId}/components/{componentId}")
    public void updateComponentQuantity(
            @PathVariable
            @Min(value = 1, message = "Product ID must be greater than zero")
            long productId,

            @PathVariable
            @Min(value = 1, message = "Component ID must be greater than zero")
            long componentId,

            @RequestBody
            @Valid
            ComponentQuantityDto quantityDto
    ) {
        productComponentsService.updateComponentQuantity(
                productId,
                componentId,
                quantityDto.getQuantity());
    }

    @Operation(summary = "Get all components for a product with quantities")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "List of components with quantities for the product",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = ProductComponentDto.class)))),
            @ApiResponse(responseCode = "404", description = "Product not found", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid product ID", content = @Content)
    })
    @GetMapping("/{productId}/components")
    public ResponseEntity<List<ProductComponentDto>> getComponentsForProduct(
            @PathVariable
            @Min(value = 1, message = "Product ID must be greater than zero")
            long productId
    ) {
        List<ProductComponentDto> components = productComponentsService.getComponentsForProduct(productId);
        return ResponseEntity.ok(components);
    }

    // for Open API Documentation
    private static class WrappedListOfProducts extends ListResponseWrapper<ProductResponseDto> {
    }
}
