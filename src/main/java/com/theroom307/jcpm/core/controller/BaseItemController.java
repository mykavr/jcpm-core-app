package com.theroom307.jcpm.core.controller;

import com.theroom307.jcpm.core.data.model.Item;
import com.theroom307.jcpm.core.service.ItemService;

abstract class BaseItemController<T extends Item> {

    protected final ItemService<T> service;

    protected static final String DEFAULT_PAGE_SIZE = "10";

    protected BaseItemController(ItemService<T> service) {
        this.service = service;
    }
}
