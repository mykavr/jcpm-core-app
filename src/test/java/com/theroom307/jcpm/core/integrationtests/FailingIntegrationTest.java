package com.theroom307.jcpm.core.integrationtests;

import com.theroom307.jcpm.core.TestTypes;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag(TestTypes.INTEGRATION_TEST)
class FailingIntegrationTest {
    @Test
    void thisTestShouldFail() {
        Assertions.fail("This is an intentionally failing integration test.");
    }
}
