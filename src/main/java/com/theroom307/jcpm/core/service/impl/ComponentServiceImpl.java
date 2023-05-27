package com.theroom307.jcpm.core.service.impl;

import com.theroom307.jcpm.core.data.model.Component;
import com.theroom307.jcpm.core.data.repository.ComponentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ComponentServiceImpl extends ItemServiceImpl<Component> {
    protected ComponentServiceImpl(@Autowired ComponentRepository repository) {
        super(repository, "Component");
    }
}
