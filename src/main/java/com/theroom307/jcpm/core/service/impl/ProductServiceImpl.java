package com.theroom307.jcpm.core.service.impl;

import com.theroom307.jcpm.core.controller.exception.BadRequestException;
import com.theroom307.jcpm.core.controller.exception.ProductNotFoundException;
import com.theroom307.jcpm.core.data.dto.ProductRequestDto;
import com.theroom307.jcpm.core.data.dto.ProductResponseDto;
import com.theroom307.jcpm.core.data.dto.wrapper.ListResponseWrapper;
import com.theroom307.jcpm.core.data.dto.wrapper.Pagination;
import com.theroom307.jcpm.core.data.model.Product;
import com.theroom307.jcpm.core.data.repository.ProductRepository;
import com.theroom307.jcpm.core.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Override
    public ListResponseWrapper<ProductResponseDto> getProducts(int page, int size) {
        log.info("Handling the Get Products request with page={}, size={}", page, size);

        var pageable = PageRequest.of(page, size);
        var pageOfProducts = productRepository.findAll(pageable);

        //noinspection ConstantConditions
        if (pageOfProducts == null) {
            // JPA may (unexpectedly) return null when there are no products
            log.warn("No products were found in the repository");
            return ListResponseWrapper.<ProductResponseDto>builder()
                    .data(Collections.emptyList())
                    .pagination(Pagination.forEmptyPage(pageable))
                    .build();
        }

        log.info("Returning {} products", pageOfProducts.getNumberOfElements());
        return ListResponseWrapper.<ProductResponseDto>builder()
                .data(getProductDtoList(pageOfProducts))
                .pagination(Pagination.from(pageOfProducts))
                .build();
    }

    private List<ProductResponseDto> getProductDtoList(Page<Product> pageOfProducts) {
        return pageOfProducts.stream()
                .map(ProductResponseDto::fromEntity)
                .toList();
    }

    @Override
    public ProductResponseDto getProduct(long productId) {
        log.info("Handling the Get Product request for productId={}", productId);
        var product = productRepository.findById(productId);
        if (product.isPresent()) {
            log.info("Found the product in the repository: {}", product.get());
            return ProductResponseDto.fromEntity(product.get());
        } else {
            log.info("Couldn't find a product by productId={} in the repository", productId);
            throw new ProductNotFoundException(productId);
        }
    }

    @Override
    public long createProduct(ProductRequestDto productDto) {
        log.info("Handling the Create Product request for {}", productDto);

        var entity = productDto.toEntity();
        log.info("Product to be created: {}", entity);

        var savedEntity = productRepository.save(entity);
        log.info("Created product: {}", savedEntity);

        return savedEntity.getId();
    }

    @Override
    public void editProduct(long productId, ProductRequestDto productDto) {
        var newName = productDto.name();
        var newDescription = productDto.description();

        log.info("Handling a request to edit a product with productId={}. New name: '{}'; new description: '{}'",
                productId, newName, newDescription);

        checkThatProductCanBeUpdated(newName, newDescription);

        var product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));

        if (newName != null && !product.getName().equals(newName)) {
            log.info("Setting the product name for productId={} to '{}'", productId, newName);
            productRepository.updateNameById(newName, productId);
        }

        if (newDescription != null && !product.getDescription().equals(newDescription)) {
            log.info("Setting the product description for productId={} to '{}'", productId, newDescription);
            productRepository.updateDescriptionById(newDescription, productId);
        }
    }

    private void checkThatProductCanBeUpdated(String newName, String newDescription) {
        if (newName == null && newDescription == null) {
            throw new BadRequestException("New value for the product name or description must be provided");
        }

        if (newName != null && newName.isBlank()) {
            throw new BadRequestException("Product name cannot be blank");
        }
    }

    @Override
    public void deleteProduct(long productId) {
        log.info("Handling a request to delete a product by productId={}", productId);
        productRepository.deleteById(productId);
    }
}
