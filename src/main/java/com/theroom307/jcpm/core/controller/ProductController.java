package com.theroom307.jcpm.core.controller;

import com.theroom307.jcpm.core.data.dto.EditProductComponentDto;
import com.theroom307.jcpm.core.data.dto.IResponseDto;
import com.theroom307.jcpm.core.data.dto.ProductRequestDto;
import com.theroom307.jcpm.core.data.dto.ProductResponseDto;
import com.theroom307.jcpm.core.data.dto.wrapper.ListResponseWrapper;
import com.theroom307.jcpm.core.data.model.Product;
import com.theroom307.jcpm.core.service.ItemDtoMapper;
import com.theroom307.jcpm.core.service.ItemService;
import com.theroom307.jcpm.core.service.ProductComponentsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/product")
@Validated
@Tag(name = "Product API")
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
    public ListResponseWrapper<ProductResponseDto>
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
    public IResponseDto
    getProductById(
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
    public long
    createNewProduct(
            @RequestBody
            @Valid
            ProductRequestDto productDto
    ) {
        var product = mapper.map(productDto);
        return service.createItem(product);
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
    public void
    deleteProductById(
            @PathVariable
            long productId
    ) {
        service.deleteItem(productId);
    }

    @Operation(summary = "Edit a product's components")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Changes to the product's components have been applied",
                    content = @Content),
            @ApiResponse(responseCode = "400",
                    description = "Bad Request, see details in the returned error message",
                    content = @Content),
            @ApiResponse(responseCode = "404",
                    description = "The product or component was not found",
                    content = @Content)
    })
    @PatchMapping("/{productId}/components")
    public void editProductComponents(
            @PathVariable
            @Min(value = 1, message = "Product ID must be greater than zero")
            long productId,

            @RequestBody
            @Valid
            EditProductComponentDto editProductComponentDto
    ) {
        var componentId = editProductComponentDto.componentId();
        var quantity = editProductComponentDto.getQuantity();
        var add = editProductComponentDto.add();
        var remove = editProductComponentDto.remove();
        productComponentsService.editComponent(productId, componentId, quantity, add, remove);
    }

    // for Open API Documentation
    private static class WrappedListOfProducts extends ListResponseWrapper<ProductResponseDto> {
    }
}
