package com.raven.strategy.impl;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

class ModuloStrategyTest {

    @Test
    void execute_returnsRemainder() {
        ModuloStrategy strategy = new ModuloStrategy();

        BigDecimal a = new BigDecimal("17");
        BigDecimal b = new BigDecimal("5");
        BigDecimal result = strategy.execute(a, b);

        assertEquals(0, result.compareTo(new BigDecimal("2")));
    }

    @Test
    void execute_throwsWhenModuloByZero() {
        ModuloStrategy strategy = new ModuloStrategy();

        BigDecimal a = new BigDecimal("10");
        BigDecimal zero = BigDecimal.ZERO;

        RuntimeException ex = assertThrows(RuntimeException.class, () -> strategy.execute(a, zero));
        assertTrue(ex.getMessage().toLowerCase().contains("modulo by zero"));
    }
}