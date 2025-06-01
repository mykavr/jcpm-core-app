package com.theroom307.jcpm.core.data.repository;

import com.theroom307.jcpm.core.data.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;

public interface ProductRepository extends ItemRepository<Product> {
    
    @Query("SELECT DISTINCT p FROM Product p JOIN ProductComponent pc ON p.id = pc.product.id WHERE pc.component.id = :componentId")
    Page<Product> findDistinctByComponentId(@Param("componentId") @NonNull Long componentId, Pageable pageable);
}
