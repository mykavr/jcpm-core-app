package com.theroom307.jcpm.core.data.repository;

import com.theroom307.jcpm.core.data.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.transaction.annotation.Transactional;

public interface ItemRepository<T extends Item> extends JpaRepository<T, Long> {

    @Transactional
    @Modifying
    @Query("update #{#entityName} t set t.name = ?1 where t.id = ?2")
    void updateNameById(String name, @NonNull Long itemId);

    @Transactional
    @Modifying
    @Query("update #{#entityName} t set t.description = ?1 where t.id = ?2")
    void updateDescriptionById(String description, @NonNull Long itemId);

}
