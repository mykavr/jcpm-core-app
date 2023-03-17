package com.theroom307.management.data.repository;

import com.theroom307.management.data.model.Product;
import org.springframework.data.repository.CrudRepository;

public interface ProductRepository extends CrudRepository<Product, Long> {
}
