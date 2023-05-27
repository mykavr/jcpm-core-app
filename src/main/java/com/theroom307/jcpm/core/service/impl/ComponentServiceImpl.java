package com.theroom307.jcpm.core.service.impl;

import com.theroom307.jcpm.core.controller.exception.BadRequestException;
import com.theroom307.jcpm.core.controller.exception.ComponentNotFoundException;
import com.theroom307.jcpm.core.data.dto.ComponentRequestDto;
import com.theroom307.jcpm.core.data.dto.ComponentResponseDto;
import com.theroom307.jcpm.core.data.dto.wrapper.ListResponseWrapper;
import com.theroom307.jcpm.core.data.dto.wrapper.Pagination;
import com.theroom307.jcpm.core.data.model.Component;
import com.theroom307.jcpm.core.data.repository.ComponentRepository;
import com.theroom307.jcpm.core.service.ComponentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ComponentServiceImpl implements ComponentService {

    private final ComponentRepository componentRepository;

    @Override
    public ListResponseWrapper<ComponentResponseDto> getComponents(int page, int size) {
        log.info("Handling the Get Components request with page={}, size={}", page, size);

        var pageable = PageRequest.of(page, size);
        var pageOfComponents = componentRepository.findAll(pageable);

        //noinspection ConstantConditions
        if (pageOfComponents == null) {
            // JPA may (unexpectedly) return null when there are no components
            log.warn("No components were found in the repository");
            return ListResponseWrapper.<ComponentResponseDto>builder()
                    .data(Collections.emptyList())
                    .pagination(Pagination.forEmptyPage(pageable))
                    .build();
        }

        log.info("Returning {} components", pageOfComponents.getNumberOfElements());
        return ListResponseWrapper.<ComponentResponseDto>builder()
                .data(getComponentDtoList(pageOfComponents))
                .pagination(Pagination.from(pageOfComponents))
                .build();
    }

    private List<ComponentResponseDto> getComponentDtoList(Page<Component> pageOfComponents) {
        return pageOfComponents.stream()
                .map(ComponentResponseDto::fromEntity)
                .toList();
    }

    @Override
    public ComponentResponseDto getComponent(long componentId) {
        log.info("Handling the Get Component request for componentId={}", componentId);
        var component = componentRepository.findById(componentId);
        if (component.isPresent()) {
            log.info("Found the component in the repository: {}", component.get());
            return ComponentResponseDto.fromEntity(component.get());
        } else {
            log.info("Couldn't find a component by componentId={} in the repository", componentId);
            throw new ComponentNotFoundException(componentId);
        }
    }

    @Override
    public long createComponent(ComponentRequestDto componentDto) {
        log.info("Handling the Create Component request for {}", componentDto);

        var entity = componentDto.toEntity();
        log.info("Component to be created: {}", entity);

        var savedEntity = componentRepository.save(entity);
        log.info("Created component: {}", savedEntity);

        return savedEntity.getId();
    }

    @Override
    public void editComponent(long componentId, ComponentRequestDto componentDto) {
        var newName = componentDto.name();
        var newDescription = componentDto.description();

        log.info("Handling a request to edit a component with componentId={}. New name: '{}'; new description: '{}'",
                componentId, newName, newDescription);

        checkThatComponentCanBeUpdated(newName, newDescription);

        var component = componentRepository.findById(componentId)
                .orElseThrow(() -> new ComponentNotFoundException(componentId));

        if (newName != null && !component.getName().equals(newName)) {
            log.info("Setting the component name for componentId={} to '{}'", componentId, newName);
            componentRepository.updateNameById(newName, componentId);
        }

        if (newDescription != null && !component.getDescription().equals(newDescription)) {
            log.info("Setting the component description for componentId={} to '{}'", componentId, newDescription);
            componentRepository.updateDescriptionById(newDescription, componentId);
        }
    }

    private void checkThatComponentCanBeUpdated(String newName, String newDescription) {
        if (newName == null && newDescription == null) {
            throw new BadRequestException("New value for the component name or description must be provided");
        }

        if (newName != null && newName.isBlank()) {
            throw new BadRequestException("Component name cannot be blank");
        }
    }

    @Override
    public void deleteComponent(long componentId) {
        log.info("Handling a request to delete a component by componentId={}", componentId);
        componentRepository.deleteById(componentId);
    }
}
