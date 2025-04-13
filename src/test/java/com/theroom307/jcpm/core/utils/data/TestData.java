package com.theroom307.jcpm.core.utils.data;

import static com.theroom307.jcpm.core.utils.data.TestComponentData.VALID_COMPONENT_ID;

public class TestData {

    public static final int DEFAULT_COMPONENT_QUANTITY = 1;

    public static String getAddComponentToProductRequestBody() {
        return getAddComponentToProductRequestBody(VALID_COMPONENT_ID);
    }

    public static String getAddComponentToProductRequestBody(long componentId) {
        return String.format("""
                {
                    "component_id": %s,
                    "add": true
                }
                """, componentId);
    }

    public static String getAddComponentToProductRequestBody(long componentId, int quantity) {
        return String.format("""
                {
                    "component_id": %s,
                    "add": true,
                    "quantity": %s
                }
                """, componentId, quantity);
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
