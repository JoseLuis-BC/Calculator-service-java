package com.raven.strategy.impl;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

class MultiplicationStrategyTest {

    @Test
    void execute_multipliesTwoNumbers() {
        MultiplicationStrategy strategy = new MultiplicationStrategy();

        BigDecimal a = new BigDecimal("6");
        BigDecimal b = new BigDecimal("7");
        BigDecimal result = strategy.execute(a, b);

        assertEquals(0, result.compareTo(new BigDecimal("42")));
    }

    @Test
    void execute_withDecimalNumbers() {
        MultiplicationStrategy strategy = new MultiplicationStrategy();

        BigDecimal a = new BigDecimal("2.5");
        BigDecimal b = new BigDecimal("4");
        BigDecimal result = strategy.execute(a, b);

        assertEquals(0, result.compareTo(new BigDecimal("10.0")));
    }
}