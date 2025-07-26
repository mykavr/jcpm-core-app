package com.theroom307.jcpm.core.data.repository;

import com.theroom307.jcpm.core.data.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository<T extends Item> extends JpaRepository<T, Long> {
}
