package com.theroom307.jcpm.core.data.repository;

import com.theroom307.jcpm.core.data.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;

public interface ItemRepository<T extends Item> extends JpaRepository<T, Long> {

    @Transactional
    @Modifying
    @Query("update #{#entityName} t set t.modified = :now where t.id = :id")
    void updateModified(@Param("id") Long id, @Param("now") ZonedDateTime now);

}
