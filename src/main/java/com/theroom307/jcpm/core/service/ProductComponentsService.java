package com.theroom307.jcpm.core.service;

import com.theroom307.jcpm.core.controller.exception.BadRequestException;
import com.theroom307.jcpm.core.controller.exception.ConditionFailedException;
import com.theroom307.jcpm.core.controller.exception.ItemNotFoundException;
import com.theroom307.jcpm.core.data.dto.ProductComponentDto;

import java.util.List;

public interface ProductComponentsService {

    /**
     * Adds a component to a product with the specified quantity.
     *
     * @param productId The ID of the product
     * @param componentId The ID of the component to add
     * @param quantity The quantity of the component to add
     * @throws ItemNotFoundException if product or component not found
     * @throws ConditionFailedException if product already contains the component
     * @throws BadRequestException if quantity is invalid
     */
    void addComponentToProduct(long productId, long componentId, int quantity);

    /**
     * Removes a component from a product.
     *
     * @param productId The ID of the product
     * @param componentId The ID of the component to remove
     * @throws ItemNotFoundException if product or component not found or if product does not contain the component
     */
    void removeComponentFromProduct(long productId, long componentId);

    /**
     * Updates the quantity of an existing component in a product.
     *
     * @param productId   The ID of the product
     * @param componentId The ID of the component to modify
     * @param quantity    The new quantity of the component
     * @throws ItemNotFoundException if product or component not found or if product does not contain the component
     * @throws BadRequestException   if quantity is invalid
     */
    void updateComponentQuantity(long productId, long componentId, int quantity);

    /**
     * Checks if a component is used in any product.
     *
     * @param componentId The ID of the component to check
     * @return true if the component is used in at least one product, false otherwise
     */
    boolean isComponentInUse(long componentId);

    /**
     * Gets all components with their quantities for a specific product.
     *
     * @param productId The ID of the product
     * @return List of components with their quantities
     * @throws ItemNotFoundException if the product doesn't exist
     */
    List<ProductComponentDto> getComponentsForProduct(long productId);
}
