package com.theroom307.jcpm.core.unittests.data.dto;

import com.theroom307.jcpm.core.data.dto.ComponentResponseDto;
import org.junit.jupiter.api.Test;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import static com.theroom307.jcpm.core.utils.TestComponentData.*;
import static org.assertj.core.api.Assertions.assertThat;

class ComponentDtoConversionTests {

    @Test
    void convertComponentDtoToComponent() {
        var componentRequestDto = getComponentRequest();
        var resultEntity = componentRequestDto.toEntity();

        assertThat(resultEntity)
                .as("ComponentRequestDto should be properly converted to a Component entity")
                .hasFieldOrPropertyWithValue("name", getComponent().getName())
                .hasFieldOrPropertyWithValue("description", getComponent().getDescription());
    }

    @Test
    void convertComponentToComponentDto() {
        var componentEntity = getComponent();
        var resultComponentDTO = ComponentResponseDto.fromEntity(componentEntity);

        assertThat(resultComponentDTO)
                .as("Component entity should be properly converted to a ComponentDTO instance")
                .isEqualTo(getComponentResponse());
    }

    @Test
    void omitMillisecondsInComponentDto() {
        var dateTime = ZonedDateTime.of(2023, 3, 18,
                18, 28, 3, 999999999, ZoneOffset.UTC);
        var expectedTimestamp = "2023-03-18T18:28:03Z";

        var component = getComponent();
        component.setCreated(dateTime);
        component.setModified(dateTime);

        assertThat(ComponentResponseDto.fromEntity(component))
                .as("Milli- and nanoseconds should be skipped")
                .hasFieldOrPropertyWithValue("created", expectedTimestamp)
                .hasFieldOrPropertyWithValue("modified", expectedTimestamp);
    }

}
