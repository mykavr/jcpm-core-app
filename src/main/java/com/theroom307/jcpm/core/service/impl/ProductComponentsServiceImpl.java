package com.theroom307.jcpm.core.service.impl;

import com.theroom307.jcpm.core.controller.exception.BadRequestException;
import com.theroom307.jcpm.core.controller.exception.ConditionFailedException;
import com.theroom307.jcpm.core.controller.exception.NotFoundException;
import com.theroom307.jcpm.core.data.model.Component;
import com.theroom307.jcpm.core.data.model.Product;
import com.theroom307.jcpm.core.data.model.ProductComponent;
import com.theroom307.jcpm.core.data.repository.ProductComponentRepository;
import com.theroom307.jcpm.core.service.ItemService;
import com.theroom307.jcpm.core.service.ProductComponentsService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

@Service
@AllArgsConstructor
public class ProductComponentsServiceImpl implements ProductComponentsService {

    private ItemService<Product> productService;

    private ItemService<Component> componentService;

    private ProductComponentRepository productComponentRepository;

    @Override
    public void addComponentToProduct(long productId, long componentId, int quantity) {
        validateQuantity(quantity);

        var product = productService.getItem(productId);
        var component = componentService.getItem(componentId);

        if (productComponentRepository
                .findProductComponent(product.getId(), component.getId())
                .isPresent()) {
            throw new ConditionFailedException(
                    String.format("Product '%s' already contains component '%s'", productId, componentId)
            );
        }

        var productComponent = ProductComponent.builder()
                .product(product)
                .component(component)
                .quantity(quantity)
                .build();
        productComponentRepository.save(productComponent);
    }

    @Override
    public void removeComponentFromProduct(long productId, long componentId) {
        // Verify product exists
        productService.getItem(productId);

        productComponentRepository
                .findProductComponent(productId, componentId)
                .ifPresentOrElse(
                        productComponentRepository::delete,
                        () -> {
                            throw new NotFoundException(
                                    String.format("Product '%s' does not contain component '%s'", productId, componentId)
                            );
                        }
                );
    }

    @Override
    public void updateComponentQuantity(long productId, long componentId, int quantity) {
        validateQuantity(quantity);

        // Verify product exists
        productService.getItem(productId);

        // Verify component exists
        componentService.getItem(componentId);

        // Find the product-component relationship
        var productComponentOpt = productComponentRepository.findProductComponent(productId, componentId);

        if (productComponentOpt.isEmpty()) {
            throw new NotFoundException(
                    String.format("Product '%s' does not contain component '%s'", productId, componentId)
            );
        }

        // Update the quantity
        var productComponent = productComponentOpt.get();
        productComponent.setQuantity(quantity);
        productComponentRepository.save(productComponent);
    }

    @Override
    public boolean isComponentInUse(long componentId) {
        return productComponentRepository.countComponentUsage(componentId) > 0;
    }

    @Override
    public Map<Component, Integer> getComponentsForProduct(long productId) {
        // Verify product exists
        productService.getItem(productId);

        return productComponentRepository.findAllByProductId(productId).stream()
                .collect(toMap(ProductComponent::getComponent, ProductComponent::getQuantity));
    }

    @Override
    public Page<Product> getProductsByComponent(long componentId, int page, int size) {
        componentService.getItem(componentId);

        var productComponents = productComponentRepository.findAllByComponentId(componentId);

        var products = productComponents.stream()
                .map(ProductComponent::getProduct)
                .distinct()
                .collect(toList());

        var pageable = PageRequest.of(page, size);
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), products.size());
        var pageContent = start >= products.size() ? List.<Product>of() : products.subList(start, end);

        return new PageImpl<>(pageContent, pageable, products.size());
    }

    private void validateQuantity(int quantity) {
        if (quantity <= 0) {
            throw new BadRequestException("Quantity must be greater than zero");
        }
    }
}
