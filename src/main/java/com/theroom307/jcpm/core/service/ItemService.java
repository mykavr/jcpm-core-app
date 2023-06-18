package com.theroom307.jcpm.core.service;

import com.theroom307.jcpm.core.data.model.Item;
import org.springframework.data.domain.Page;

public interface ItemService<T extends Item> {

    Page<T> getItems(int page, int size);

    T getItem(long id);

    long createItem(T item);

    void editItem(long id, T updatedItem);

    void deleteItem(long id);
}
