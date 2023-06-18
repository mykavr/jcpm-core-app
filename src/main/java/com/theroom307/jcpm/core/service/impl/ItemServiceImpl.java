package com.theroom307.jcpm.core.service.impl;

import com.theroom307.jcpm.core.controller.exception.BadRequestException;
import com.theroom307.jcpm.core.controller.exception.ItemNotFoundException;
import com.theroom307.jcpm.core.data.model.Item;
import com.theroom307.jcpm.core.data.repository.ItemRepository;
import com.theroom307.jcpm.core.service.ItemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

@Slf4j
public abstract class ItemServiceImpl<T extends Item> implements ItemService<T> {

    protected final ItemRepository<T> repository;

    private final String itemType; // for logging purposes

    protected ItemServiceImpl(ItemRepository<T> repository, String itemType) {
        this.repository = repository;
        this.itemType = itemType;
    }

    @Override
    public Page<T> getItems(int page, int size) {
        log.info("Looking for {}s with page={}, size={}", itemType, page, size);

        var pageable = PageRequest.of(page, size);
        var items = repository.findAll(pageable);

        //noinspection ConstantConditions
        if (items == null) {
            // JPA may (unexpectedly) return null when there are no products
            log.warn("No {}s were found in the repository", itemType);
            return Page.empty(pageable);
        }

        log.info("Returning {} {}s", items.getNumberOfElements(), itemType);
        return items;
    }

    @Override
    public T getItem(long itemId) {
        log.info("Looking for a {} with ID={}", itemType, itemId);
        var item = repository.findById(itemId);
        if (item.isPresent()) {
            log.info("Found a {} in the repository: {}", itemType, item.get());
            return item.get();
        } else {
            log.info("Couldn't find a {} by ID={} in the repository", itemType, itemId);
            throw new ItemNotFoundException(itemType, itemId);
        }
    }

    @Override
    public long createItem(T entity) {
        log.info("Handling the Create {} request for {}", itemType, entity);
        var savedEntity = repository.save(entity);
        log.info("Created a {} in the repository: {}", itemType, savedEntity);

        return savedEntity.getId();
    }

    @Override
    public void editItem(long id, T updatedItem) {
        var newName = updatedItem.getName();
        var newDescription = updatedItem.getDescription();

        log.info("Handling a request to edit a {} with ID={}. New name: '{}'; new description: '{}'",
                itemType, id, newName, newDescription);

        checkThatItemCanBeUpdated(newName, newDescription);

        var item = repository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException(itemType, id));

        if (newName != null && !item.getName().equals(newName)) {
            log.info("Setting the {} name for {} to '{}'", itemType, id, newName);
            repository.updateNameById(newName, id);
        }

        if (newDescription != null && !item.getDescription().equals(newDescription)) {
            log.info("Setting the {} description for {} to '{}'", itemType, id, newDescription);
            repository.updateDescriptionById(newDescription, id);
        }
    }

    private void checkThatItemCanBeUpdated(String newName, String newDescription) {
        if (newName == null && newDescription == null) {
            throw new BadRequestException(String.format(
                    "New value for the %s name or description must be provided", itemType.toLowerCase()));
        }

        if (newName != null && newName.isBlank()) {
            throw new BadRequestException(String.format("%s name cannot be blank", itemType));
        }
    }

    @Override
    public void deleteItem(long id) {
        log.info("Handling a request to delete a {} with ID={}", itemType, id);
        repository.deleteById(id);
    }
}
