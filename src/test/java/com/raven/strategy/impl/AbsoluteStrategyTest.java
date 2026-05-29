package com.raven.strategy.impl;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class AbsoluteStrategyTest {

    @Test
    void execute_returnsAbsoluteForNegative() {
        AbsoluteStrategy strategy = new AbsoluteStrategy();

        BigDecimal a = new BigDecimal("-12.34");
        BigDecimal result = strategy.execute(a, null);

        assertNotNull(result);
        assertEquals(new BigDecimal("12.34"), result);
    }

    @Test
    void execute_returnsSameForPositive() {
        AbsoluteStrategy strategy = new AbsoluteStrategy();

        BigDecimal a = new BigDecimal("7.5");
        BigDecimal result = strategy.execute(a, BigDecimal.valueOf(-1));

        assertEquals(new BigDecimal("7.5"), result);
    }

    @Test
    void execute_returnsZeroForZero() {
        AbsoluteStrategy strategy = new AbsoluteStrategy();

        BigDecimal a = BigDecimal.ZERO;
        BigDecimal result = strategy.execute(a, BigDecimal.ONE);

        assertEquals(BigDecimal.ZERO, result);
    }

    @Test
    void execute_ignoresSecondParameter() {
        AbsoluteStrategy strategy = new AbsoluteStrategy();

        BigDecimal a = new BigDecimal("-3");
        BigDecimal resultWithNullB = strategy.execute(a, null);
        BigDecimal resultWithB = strategy.execute(a, BigDecimal.valueOf(100));

        assertEquals(resultWithNullB, resultWithB);
        assertEquals(new BigDecimal("3"), resultWithB);
    }

    @Test
    void execute_nullA_throwsNullPointerException() {
        AbsoluteStrategy strategy = new AbsoluteStrategy();

        assertThrows(NullPointerException.class, () -> strategy.execute(null, null));
    }
}