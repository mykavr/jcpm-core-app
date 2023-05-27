package com.theroom307.jcpm.core.controller;

import com.theroom307.jcpm.core.service.ComponentService;
import com.theroom307.jcpm.core.data.dto.ComponentRequestDto;
import com.theroom307.jcpm.core.data.dto.ComponentResponseDto;
import com.theroom307.jcpm.core.data.dto.wrapper.ListResponseWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/component")
@Validated
@RequiredArgsConstructor
@Tag(name = "Component API")
@Slf4j
public class ComponentController {

    private final ComponentService componentService;

    private static final String DEFAULT_PAGE_SIZE = "10";

    @Operation(summary = "Get the list of all components (paginated)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"),
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
        log.info("Received a Get Components request with pagination parameters: page={}, size={}", page, size);
        var components = componentService.getComponents(page, size);
        log.info("Responding with {} components", components == null ? "null" : components.data().size());
        return components;
    }

    @Operation(summary = "Get a component by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "404", description = "Component not found or invalid ID", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid component ID", content = @Content)
    })
    @GetMapping("/{componentId}")
    public ComponentResponseDto
    getComponentById(
            @PathVariable
            @Min(value = 1, message = "Component ID must be greater than zero")
            long componentId
    ) {
        log.info("Received a Get Component request for componentId={}", componentId);
        var component = componentService.getComponent(componentId);
        log.info("Responding with {}", component);
        return component;
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
            ComponentRequestDto component
    ) {
        log.info("Received a Create Component request: {}", component);
        return componentService.createComponent(component);
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
            ComponentRequestDto component
    ) {
        log.info("Received an Edit Component request for componentId={} with input: {}", componentId, component);
        componentService.editComponent(componentId, component);
    }

    @Operation(summary = "Delete a component by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "The component has been deleted or doesn't exist",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid component ID", content = @Content)
    })
    @DeleteMapping("/{componentId}")
    public void
    deleteComponentById(
            @PathVariable
            long componentId
    ) {
        log.info("Received a Delete Component request for componentId={}", componentId);
        componentService.deleteComponent(componentId);
    }
}
