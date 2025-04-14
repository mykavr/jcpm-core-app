package com.theroom307.jcpm.core.unittests;

import com.theroom307.jcpm.core.TestTypes;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag(TestTypes.UNIT_TEST)
class FailingUnitTest {
    @Test
    void thisTestShouldFail() {
        Assertions.fail("This is an intentionally failing unit test.");
    }
}
