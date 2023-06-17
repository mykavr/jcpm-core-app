package com.theroom307.jcpm.core.service.impl;

import com.theroom307.jcpm.core.controller.exception.NotFoundException;
import com.theroom307.jcpm.core.data.repository.ComponentRepository;
import com.theroom307.jcpm.core.data.repository.ProductComponentRepository;
import com.theroom307.jcpm.core.data.repository.ProductRepository;
import com.theroom307.jcpm.core.service.ProductComponentsService;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ProductComponentsServiceImpl implements ProductComponentsService {

    private ProductRepository productRepository;

    private ComponentRepository componentRepository;

    private ProductComponentRepository productComponentRepository;

    @Override
    public void editComponent(long productId, long componentId, boolean add, boolean remove) {
        throw new NotImplementedException();
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
