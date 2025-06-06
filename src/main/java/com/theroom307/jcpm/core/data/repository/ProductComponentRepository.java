package com.theroom307.jcpm.core.data.repository;

import com.theroom307.jcpm.core.data.model.ProductComponent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Optional;

public interface ProductComponentRepository extends JpaRepository<ProductComponent, Long> {

    @Query("select p from ProductComponent p where p.product.id = ?1 and p.component.id = ?2")
    Optional<ProductComponent> findProductComponent(@NonNull Long productId, @NonNull Long componentId);

    @Query("select count(p) from ProductComponent p where p.component.id = :id")
    long countComponentUsage(@Param("id") @NonNull Long id);

    @Query("select p from ProductComponent p where p.product.id = :productId")
    List<ProductComponent> findAllByProductId(@Param("productId") @NonNull Long productId);

    @Query("select p from ProductComponent p where p.component.id = :componentId")
    List<ProductComponent> findAllByComponentId(@Param("componentId") @NonNull Long componentId);
}
