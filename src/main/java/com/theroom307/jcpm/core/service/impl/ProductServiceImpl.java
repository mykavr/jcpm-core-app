package com.theroom307.jcpm.core.service.impl;

import com.theroom307.jcpm.core.data.model.Product;
import com.theroom307.jcpm.core.data.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductServiceImpl extends ItemServiceImpl<Product> {
    protected ProductServiceImpl(@Autowired ProductRepository repository) {
        super(repository, "Product");
    }

}
