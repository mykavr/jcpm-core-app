package com.theroom307.jcpm.core.data.repository;

import com.theroom307.jcpm.core.data.model.Component;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.transaction.annotation.Transactional;

public interface ComponentRepository extends JpaRepository<Component, Long> {
    @Transactional
    @Modifying
    @Query("update Component c set c.name = ?1 where c.id = ?2")
    void updateNameById(String name, @NonNull Long componentId);

    @Transactional
    @Modifying
    @Query("update Component c set c.description = ?1 where c.id = ?2")
    void updateDescriptionById(String description, @NonNull Long componentId);
}
