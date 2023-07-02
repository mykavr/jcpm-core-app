package com.theroom307.jcpm.core.service.impl;

import com.theroom307.jcpm.core.controller.exception.BadRequestException;
import com.theroom307.jcpm.core.controller.exception.NotFoundException;
import com.theroom307.jcpm.core.data.model.Component;
import com.theroom307.jcpm.core.data.model.Product;
import com.theroom307.jcpm.core.data.model.ProductComponent;
import com.theroom307.jcpm.core.data.repository.ProductComponentRepository;
import com.theroom307.jcpm.core.service.ItemService;
import com.theroom307.jcpm.core.service.ProductComponentsService;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ProductComponentsServiceImpl implements ProductComponentsService {

    private ItemService<Product> productService;

    private ItemService<Component> componentService;

    private ProductComponentRepository productComponentRepository;

    @Override
    public void editComponent(long productId, long componentId, int quantity, boolean add, boolean remove) {
        if (add && remove) {
            var error = "Invalid request: both 'add' and 'remove' cannot be true";
            throw new BadRequestException(error);
        }
        var product = productService.getItem(productId);
        if (add) {
            var component = componentService.getItem(componentId);
            addComponentToProduct(product, component, quantity);
        } else if (remove) {
            deleteComponent(productId, componentId);
        } else {
            // modify - will be implemented in a separate story
            throw new NotImplementedException("Modify Product's Component is not implemented");
        }
    }

    private void addComponentToProduct(Product product, Component component, int quantity) {
        var productComponent = ProductComponent.builder()
                .product(product)
                .component(component)
                .quantity(quantity)
                .build();
        productComponentRepository.save(productComponent);
    }

    private void deleteComponent(long productId, long componentId) {
        productComponentRepository
                .findProductComponent(productId, componentId)
                .ifPresentOrElse(productComponentRepository::delete,
                        () -> throwProductDoesNotContainComponentException(productId, componentId));

    }

    private void throwProductDoesNotContainComponentException(long productId, long componentId) {
        throw new NotFoundException(String.format("Product '%s' does not contain component '%s'",
                productId, componentId));
    }
}
