package com.theroom307.jcpm.core.data.repository;

import com.theroom307.jcpm.core.data.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.transaction.annotation.Transactional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    @Transactional
    @Modifying
    @Query("update Product p set p.name = ?1 where p.id = ?2")
    void updateNameById(String name, @NonNull Long productId);

    @Transactional
    @Modifying
    @Query("update Product p set p.description = ?1 where p.id = ?2")
    void updateDescriptionById(String description, @NonNull Long productId);
}
