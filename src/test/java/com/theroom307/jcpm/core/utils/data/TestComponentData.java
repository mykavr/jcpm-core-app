package com.theroom307.jcpm.core.utils.data;

import com.theroom307.jcpm.core.data.dto.ComponentRequestDto;
import com.theroom307.jcpm.core.data.dto.ComponentResponseDto;
import com.theroom307.jcpm.core.data.model.Component;
import com.theroom307.jcpm.core.utils.helper.ConversionHelper;
import lombok.experimental.UtilityClass;

import java.time.ZonedDateTime;

import static com.theroom307.jcpm.core.data.dto.IResponseDto.DATE_TIME_FORMATTER;

@UtilityClass
public class TestComponentData {

    public static final Long VALID_COMPONENT_ID = Long.MAX_VALUE;

    public static Component getComponent() {
        var entity = new Component();
        entity.setId(VALID_COMPONENT_ID);
        entity.setName("component name");
        entity.setDescription("component description");
        entity.setCreated(ZonedDateTime.parse("2023-03-12T18:23:01Z", DATE_TIME_FORMATTER));
        entity.setModified(ZonedDateTime.parse("2023-03-12T18:24:59Z", DATE_TIME_FORMATTER));
        return entity;
    }

    public static ComponentResponseDto getComponentResponse() {
        return new ComponentResponseDto(
                VALID_COMPONENT_ID,
                "component name",
                "component description",
                "2023-03-12T18:23:01Z",
                "2023-03-12T18:24:59Z"
        );
    }

    public static ComponentRequestDto getComponentRequest() {
        return new ComponentRequestDto(
                "component name",
                "component description"
        );
    }

    public static String getComponentResponseAsString() {
        return ConversionHelper.getAsString(getComponentResponse());
    }

    public static String getResponseForComponent(Component component) {
        var response = ComponentResponseDto.fromEntity(component);
        return ConversionHelper.getAsString(response);
    }

    public static String getComponentDtoToCreateComponent() {
        var componentDto = getComponentRequest();
        return ConversionHelper.getAsString(componentDto);
    }

    public static Component getComponentToCreate() {
        var entity = getComponent();
        entity.setId(null);
        entity.setCreated(null);
        entity.setModified(null);
        return entity;
    }

    public static String getComponentListResponseAsString() {
        return getComponentListResponseAsString(getComponentResponseAsString());
    }

    public static String getComponentListResponseAsString(String componentDtoAsString) {
        return String.format("{\"data\":[%s],\"pagination\":{\"page\":0,\"size\":10,\"count\":1,\"total\":1}}",
                componentDtoAsString);
    }

    public static String getEmptyComponentListAsString() {
        return "{\"data\":[],\"pagination\":{\"page\":0,\"size\":10,\"count\":0,\"total\":0}}";
    }
}
