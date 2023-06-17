package com.theroom307.jcpm.core.utils;

public class TestData {

    public static String getAddComponentToProductRequestBody(long componentId) {
        return getAddComponentToProductRequestBody(String.valueOf(componentId));
    }

    public static String getAddComponentToProductRequestBody(String componentId) {
        return String.format("""
                {
                    "component_id": "%s",
                    "add": true
                }
                """, componentId);
    }

    public static String getRemoveComponentFromProductRequestBody(long componentId) {
        return getRemoveComponentFromProductRequestBody(String.valueOf(componentId));
    }

    public static String getRemoveComponentFromProductRequestBody(String componentId) {
        return String.format("""
                {
                    "component_id": "%s",
                    "remove": true
                }
                """, componentId);
    }

}
