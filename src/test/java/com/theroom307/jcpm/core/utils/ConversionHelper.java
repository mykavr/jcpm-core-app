package com.theroom307.jcpm.core.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;

public class ConversionHelper {
    @SneakyThrows(JsonProcessingException.class)
    public static String getAsString(Object o) {
        return new ObjectMapper().writeValueAsString(o);
    }
}
