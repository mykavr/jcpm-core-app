package com.theroom307.management.controller;

import com.theroom307.management.controller.exception.BadRequestException;
import com.theroom307.management.controller.exception.ProductNotFoundException;
import com.theroom307.management.data.dto.ProductDTO;
import com.theroom307.management.data.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ManagementController {

    private static final String PRODUCT_ENDPOINT = "/product";

    private final ProductRepository productRepository;

    @GetMapping(PRODUCT_ENDPOINT)
    public List<ProductDTO> getProducts() {
        var allProducts = productRepository.findAll();
        return allProducts.stream()
                .map(ProductDTO::fromEntity)
                .toList();
    }

    @GetMapping(PRODUCT_ENDPOINT + "/{productId}")
    public ProductDTO getProductById(@PathVariable String productId) {
        var id = parseProductIdFromString(productId);
        var product = productRepository.findById(id);
        if (product.isPresent()) {
            return ProductDTO.fromEntity(product.get());
        } else {
            throw new ProductNotFoundException();
        }
    }

    @PostMapping(PRODUCT_ENDPOINT)
    @ResponseStatus(HttpStatus.CREATED)
    public String createNewProduct(@RequestBody ProductDTO product) {
        var entity = product.toEntity();
        var savedEntity = productRepository.save(entity);
        return String.valueOf(savedEntity.getId());
    }

    @DeleteMapping(PRODUCT_ENDPOINT + "/{productId}")
    public void deleteProductById(@PathVariable String productId) {
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
