package com.theroom307.jcpm.core;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Constants for test categories used in @Tag annotations.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TestTypes {
    public static final String UNIT_TEST = "unit";
    public static final String INTEGRATION_TEST = "integration";
}
