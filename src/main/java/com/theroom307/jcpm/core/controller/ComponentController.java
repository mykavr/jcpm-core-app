package com.theroom307.jcpm.core.controller;

import com.theroom307.jcpm.core.controller.exception.ConditionFailedException;
import com.theroom307.jcpm.core.data.dto.ComponentRequestDto;
import com.theroom307.jcpm.core.data.dto.ComponentResponseDto;
import com.theroom307.jcpm.core.data.dto.IResponseDto;
import com.theroom307.jcpm.core.data.dto.wrapper.ListResponseWrapper;
import com.theroom307.jcpm.core.data.model.Component;
import com.theroom307.jcpm.core.service.ItemDtoMapper;
import com.theroom307.jcpm.core.service.ItemService;
import com.theroom307.jcpm.core.service.ProductComponentsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/component")
@Validated
@Tag(name = "Component API")
public class ComponentController extends BaseItemController<Component> {

    private final ProductComponentsService productComponentsService;

    protected ComponentController(@Autowired ItemService<Component> service,
                                  @Autowired ItemDtoMapper mapper,
                                  @Autowired ProductComponentsService productComponentsService) {
        super(service, mapper);
        this.productComponentsService = productComponentsService;
    }

    @Operation(summary = "Get the list of all components (paginated)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = WrappedListOfComponents.class))),
            @ApiResponse(responseCode = "400", description = "Invalid pagination parameters", content = @Content)
    })
    @GetMapping
    public ListResponseWrapper<ComponentResponseDto>
    getComponents(
            @RequestParam(defaultValue = "0")
            @Schema(type = "integer", defaultValue = "0",
                    description = "Pagination: zero-based page index, must not be negative")
            @Min(value = 0, message = "Page must not be negative")
            int page,

            @RequestParam(defaultValue = DEFAULT_PAGE_SIZE)
            @Schema(type = "integer", defaultValue = DEFAULT_PAGE_SIZE,
                    description = "Pagination: the size of the page to be returned, must be greater than 0")
            @Min(value = 1, message = "Page size must be greater than 0")
            int size
    ) {
        var components = service.getItems(page, size);
        return mapper.mapComponents(components);
    }

    @Operation(summary = "Get a component by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", content = @Content(
                    mediaType = "application/json", schema = @Schema(implementation = ComponentResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Component not found or invalid ID", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid component ID", content = @Content)
    })
    @GetMapping("/{componentId}")
    public IResponseDto
    getComponentById(
            @PathVariable
            @Min(value = 1, message = "Component ID must be greater than zero")
            long componentId
    ) {
        var component = service.getItem(componentId);
        return mapper.map(component);
    }

    @Operation(summary = "Create a new component")
    @ApiResponse(responseCode = "201",
            description = "The component has been created, see the component ID in the response",
            content = @Content(schema = @Schema(type = "integer", description = "Component ID", example = "1")))
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public long
    createNewComponent(
            @RequestBody
            @Valid
            ComponentRequestDto componentDto
    ) {
        var component = mapper.map(componentDto);
        return service.createItem(component);
    }

    @Operation(summary = "Edit a component")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Component data sucessfully updated", content = @Content),
            @ApiResponse(responseCode = "404", description = "Component not found or invalid ID", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid component ID or data", content = @Content)
    })
    @PatchMapping("/{componentId}")
    public void
    editComponent(
            @PathVariable
            @Min(value = 1, message = "Component ID must be greater than zero")
            long componentId,

            @RequestBody
            ComponentRequestDto componentDto
    ) {
        var component = mapper.map(componentDto);
        service.editItem(componentId, component);
    }

    @Operation(summary = "Delete a component by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "The component has been deleted or doesn't exist",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid component ID", content = @Content),
            @ApiResponse(responseCode = "409",
                    description = "The component is used in some product(s) and cannot be deleted",
                    content = @Content)
    })
    @DeleteMapping("/{componentId}")
    public void
    deleteComponentById(
            @PathVariable
            long componentId
    ) {
        if (productComponentsService.isComponentInUse(componentId)) {
            throwComponentIsInUseException(componentId);
        }
        service.deleteItem(componentId);
    }

    private void throwComponentIsInUseException(long componentId) {
        throw new ConditionFailedException(String.format(
                "Component '%s' is used in some product(s)",
                componentId
        ));
    }

    // for Open API Documentation
    private static class WrappedListOfComponents extends ListResponseWrapper<ComponentResponseDto> {
    }
}
