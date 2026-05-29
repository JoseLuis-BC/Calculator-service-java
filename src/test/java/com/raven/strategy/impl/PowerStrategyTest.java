package com.raven.strategy.impl;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

class PowerStrategyTest {

    @Test
    void execute_powerOfTwoNumbers() {
        PowerStrategy strategy = new PowerStrategy();
        BigDecimal a = new BigDecimal("2");
        BigDecimal b = new BigDecimal("8");
        BigDecimal result = strategy.execute(a, b);
        assertEquals(0, result.compareTo(new BigDecimal("256")));
    }

    @Test
    void execute_powerWithNonIntegerExponent() {
        PowerStrategy strategy = new PowerStrategy();
        BigDecimal a = new BigDecimal("9");
        BigDecimal b = new BigDecimal("0.5"); // sqrt(9) = 3
        BigDecimal result = strategy.execute(a, b);
        BigDecimal expected = BigDecimal.valueOf(Math.pow(9.0, 0.5));
        assertEquals(0, result.compareTo(expected));
    }
}