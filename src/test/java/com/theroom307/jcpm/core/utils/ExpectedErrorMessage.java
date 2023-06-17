package com.theroom307.jcpm.core.utils;

public class ExpectedErrorMessage {

    public static String productNotFound(long productId) {
        return itemNotFound(Item.PRODUCT, productId);
    }

    public static String componentNotFound(long componentId) {
        return itemNotFound(Item.COMPONENT, componentId);
    }

    private static String itemNotFound(Item item, long id) {
        return String.format("%s '%s' was not found", item, id);
    }

    public static String productDoesNotContainComponent(long productId, long componentId) {
        return String.format("%s '%s' does not contain %s '%s'", Item.PRODUCT, productId,
                Item.COMPONENT.toString().toLowerCase(), componentId);
    }

    public static String invalidEditProductComponentRequest() {
        return "Invalid request to the Edit Product's Components endpoint: both 'add' and 'remove' cannot be true";
    }
}
