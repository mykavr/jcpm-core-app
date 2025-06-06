package com.theroom307.jcpm.core.utils.constant;

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

    public static String componentIdIsRequired() {
        return "'component_id' is required";
    }

    public static String quantityIsRequired() {
        return "'quantity' is required";
    }

    public static String componentNameIsRequired() {
        return "Component name is required";
    }

    public static String productNameIsRequired() {
        return "Product name is required";
    }

    public static String productDoesNotContainComponent(long productId, long componentId) {
        return String.format("%s '%s' does not contain %s '%s'", Item.PRODUCT, productId,
                Item.COMPONENT.toLowerCase(), componentId);
    }

    public static String productAlreadyContainsComponent(long productId, long componentId) {
        return String.format("%s '%s' already contains %s '%s'", Item.PRODUCT, productId,
                Item.COMPONENT.toLowerCase(), componentId);
    }

    public static String somethingWentWrong() {
        return "Sorry, something went wrong";
    }

    public static String pageSizeMustBeGreaterThanZero() {
        return "Page size must be greater than 0";
    }

    public static String pageCannotBeNegative() {
        return "Page must not be negative";
    }

    public static String quantityMustBeGreaterThanZero() {
        return "Quantity must be greater than zero";
    }

    public static String parameterMustBeNumber(String parameter) {
        return String.format("'%s' must be a number", parameter);
    }

    public static String componentIsInUse(long componentId) {
        return String.format("%s '%s' is used in some %s(s)",
                Item.COMPONENT, componentId, Item.PRODUCT.toLowerCase());
    }
}
