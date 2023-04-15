package com.theroom307.management.controller;

import com.theroom307.management.controller.exception.BadRequestException;
import com.theroom307.management.controller.exception.ProductNotFoundException;
import com.theroom307.management.data.model.Product;
import com.theroom307.management.data.repository.ProductRepository;
import com.theroom307.management.data.dto.wrapper.ListResponseWrapper;
import com.theroom307.management.data.dto.ProductRequestDto;
import com.theroom307.management.data.dto.ProductResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import static com.theroom307.management.data.dto.wrapper.ListResponseWrapper.wrapperFor;
import static com.theroom307.management.data.dto.wrapper.ListResponseWrapper.wrapperForEmptyList;

@RestController
@RequestMapping("/api/v1/product")
@RequiredArgsConstructor
@Tag(name = "Product API")
public class ProductController {

    private final ProductRepository productRepository;

    private final InputValidationService validationService;

    private static final String DEFAULT_PAGE_SIZE = "10";

    @Operation(summary = "Get the list of all products (paginated)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400", description = "Invalid pagination parameters", content = @Content)
    })
    @GetMapping
    public ListResponseWrapper<ProductResponseDto> getProducts(
            @RequestParam(defaultValue = "0")
            @Schema(type = "integer", defaultValue = "0",
                    description = "Pagination: zero-based page index, must not be negative")
            int page,

            @RequestParam(defaultValue = DEFAULT_PAGE_SIZE)
            @Schema(type = "integer", defaultValue = DEFAULT_PAGE_SIZE,
                    description = "Pagination: the size of the page to be returned, must be greater than 0")
            int size
    ) {
        validationService.validatePaginationParams(page, size);

        var pageable = PageRequest.of(page, size);
        Page<Product> pageOfProducts = productRepository.findAll(pageable);

        if (pageOfProducts == null) {
            // JPA shouldn't return null, but it does when there are no products
            return wrapperForEmptyList(pageable);
        }
        return wrapperFor(pageOfProducts);
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
            throw new ProductNotFoundException(productId);
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
