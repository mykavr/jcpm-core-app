package com.theroom307.jcpm.core.data.dto;

import com.theroom307.jcpm.core.data.model.Item;

public interface IRequestDto<T extends Item> {

    String name();

    String description();

    T toEntity();
}
