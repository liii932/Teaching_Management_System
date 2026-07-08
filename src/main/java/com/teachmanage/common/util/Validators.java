package com.teachmanage.common.util;

import com.teachmanage.common.exception.BusinessException;

import java.math.BigDecimal;

public final class Validators {
    private Validators() {
    }

    public static String requireText(String value, String name) {
        if (value == null || value.trim().isEmpty()) {
            throw BusinessException.badRequest(name + "不能为空");
        }
        return value.trim();
    }

    public static String trimToNull(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        return value.trim();
    }

    public static void requireLength(String value, int length, String name) {
        requireText(value, name);
        if (value.trim().length() != length) {
            throw BusinessException.badRequest(name + "长度必须为" + length);
        }
    }

    public static void validateSex(String sex, String name) {
        if (sex == null || sex.isBlank()) {
            return;
        }
        if (!"男".equals(sex) && !"女".equals(sex)) {
            throw BusinessException.badRequest(name + "只能为男或女");
        }
    }

    public static void validateAge(Integer age) {
        if (age != null && (age < 0 || age > 120)) {
            throw BusinessException.badRequest("年龄必须在0到120之间");
        }
    }

    public static void validateCredit(BigDecimal credit) {
        if (credit != null && credit.compareTo(BigDecimal.ZERO) < 0) {
            throw BusinessException.badRequest("学分不能为负数");
        }
    }

    public static void validateGrade(BigDecimal grade) {
        if (grade != null && (grade.compareTo(BigDecimal.ZERO) < 0 || grade.compareTo(new BigDecimal("100.0")) > 0)) {
            throw BusinessException.badRequest("成绩必须在0到100之间");
        }
    }
}
