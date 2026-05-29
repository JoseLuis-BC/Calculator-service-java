package com.raven.strategy.impl;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

class MinStrategyTest {

    @Test
    void execute_returnsMinimumOfTwo() {
        MinStrategy strategy = new MinStrategy();

        BigDecimal a = new BigDecimal("5");
        BigDecimal b = new BigDecimal("3");
        BigDecimal result = strategy.execute(a, b);

        assertEquals(0, result.compareTo(new BigDecimal("3")));
    }
}