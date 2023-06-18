package com.theroom307.jcpm.core.utils;

public class TestData {

    public static String getAddComponentToProductRequestBody(long componentId) {
        return String.format("""
                {
                    "component_id": %s,
                    "add": true
                }
                """, componentId);
    }

    public static String getRemoveComponentFromProductRequestBody(long componentId) {
        return String.format("""
                {
                    "component_id": %s,
                    "remove": true
                }
                """, componentId);
    }

}
