package com.theroom307.jcpm.core.service;

import com.theroom307.jcpm.core.data.dto.IRequestDto;
import com.theroom307.jcpm.core.data.dto.IResponseDto;
import com.theroom307.jcpm.core.data.dto.wrapper.ListResponseWrapper;
import com.theroom307.jcpm.core.data.model.Item;

public interface ItemService<T extends Item> {

    ListResponseWrapper<IResponseDto> getItems(int page, int size);

    IResponseDto getItem(long id);

    long createItem(IRequestDto<T> dto);

    void editItem(long id, IRequestDto<T> dto);

    void deleteItem(long id);
}
