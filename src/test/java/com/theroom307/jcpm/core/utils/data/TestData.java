package com.theroom307.jcpm.core.utils.data;

import static com.theroom307.jcpm.core.utils.data.TestComponentData.VALID_COMPONENT_ID;

public class TestData {

    public static final int DEFAULT_COMPONENT_QUANTITY = 1;

    /**
     * Creates a JSON payload for adding a component with default component ID
     */
    public static String getAddComponentRequestBody() {
        return getAddComponentRequestBody(VALID_COMPONENT_ID);
    }

    /**
     * Creates a JSON payload for adding a component with specified component ID
     */
    public static String getAddComponentRequestBody(long componentId) {
        return String.format("""
                {
                    "component_id": %s
                }
                """, componentId);
    }

    /**
     * Creates a JSON payload for adding a component with specified component ID and quantity
     */
    public static String getAddComponentRequestBody(long componentId, int quantity) {
        return String.format("""
                {
                    "component_id": %s,
                    "quantity": %s
                }
                """, componentId, quantity);
    }

    /**
     * Creates a JSON payload for updating a component's quantity
     */
    public static String getUpdateQuantityRequestBody(int quantity) {
        return String.format("""
                {
                    "quantity": %s
                }
                """, quantity);
    }

    /**
     * Creates a JSON payload without component ID (for testing validation)
     */
    public static String getInvalidRequestWithoutComponentId() {
        return """
                {
                    "quantity": 1
                }
                """;
    }

    /**
     * Creates a JSON payload without quantity (for testing validation)
     */
    public static String getInvalidRequestWithoutQuantity() {
        return """
                {
                }
                """;
    }
}
