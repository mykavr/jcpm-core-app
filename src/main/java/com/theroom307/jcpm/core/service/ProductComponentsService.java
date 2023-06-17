package com.theroom307.jcpm.core.service;

public interface ProductComponentsService {

    /**
     * Add (add=true, remove=false), remove (add=false, remove=true), or modify (add=false, remove=false)
     * components of a product.
     * @throws com.theroom307.jcpm.core.controller.exception.ItemNotFoundException if a product or a component
     * was not found.
     * @throws com.theroom307.jcpm.core.controller.exception.BadRequestException when add=true and remove=true
     */
    void editComponent(long productId, long componentId, boolean add, boolean remove);

}
