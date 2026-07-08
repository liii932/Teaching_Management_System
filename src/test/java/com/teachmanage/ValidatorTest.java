package com.teachmanage;

import com.teachmanage.common.exception.BusinessException;
import com.teachmanage.common.util.Validators;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ValidatorTest {
    @Test
    void validatesCodeLength() {
        assertDoesNotThrow(() -> Validators.requireLength("D001", 4, "系编号"));
        assertThrows(BusinessException.class, () -> Validators.requireLength("D01", 4, "系编号"));
    }

    @Test
    void validatesGradeRange() {
        assertDoesNotThrow(() -> Validators.validateGrade(new BigDecimal("88.5")));
        assertThrows(BusinessException.class, () -> Validators.validateGrade(new BigDecimal("101")));
    }
}
