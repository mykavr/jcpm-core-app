package com.theroom307.jcpm.core.service;

import com.theroom307.jcpm.core.data.dto.ComponentRequestDto;
import com.theroom307.jcpm.core.data.dto.ComponentResponseDto;
import com.theroom307.jcpm.core.data.dto.wrapper.ListResponseWrapper;

public interface ComponentService {

    ListResponseWrapper<ComponentResponseDto> getComponents(int page, int size);

    ComponentResponseDto getComponent(long productId);

    long createComponent(ComponentRequestDto productDto);

    void editComponent(long productId, ComponentRequestDto productDto);

    void deleteComponent(long productId);
}
