package com.raven.strategy.impl;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

class SubtractionStrategyTest {

    @Test
    void execute_subtractsTwoNumbers() {
        SubtractionStrategy strategy = new SubtractionStrategy();
        BigDecimal a = new BigDecimal("10");
        BigDecimal b = new BigDecimal("4.5");
        BigDecimal result = strategy.execute(a, b);
        assertEquals(0, result.compareTo(new BigDecimal("5.5")));
    }

    @Test
    void execute_withNegativeNumbers() {
        SubtractionStrategy strategy = new SubtractionStrategy();
        BigDecimal a = new BigDecimal("-2");
        BigDecimal b = new BigDecimal("-3");
        BigDecimal result = strategy.execute(a, b);
        assertEquals(0, result.compareTo(new BigDecimal("1")));
    }
}