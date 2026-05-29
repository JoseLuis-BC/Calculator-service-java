package com.raven.strategy.impl;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

class AdditionStrategyTest {

    @Test
    void execute_addsTwoIntegers() {
        AdditionStrategy strategy = new AdditionStrategy();

        BigDecimal a = new BigDecimal("5");
        BigDecimal b = new BigDecimal("3");
        BigDecimal result = strategy.execute(a, b);

        assertEquals(0, result.compareTo(new BigDecimal("8")));
    }

    @Test
    void execute_addsDecimalNumbers() {
        AdditionStrategy strategy = new AdditionStrategy();

        BigDecimal a = new BigDecimal("2.5");
        BigDecimal b = new BigDecimal("1.75");
        BigDecimal result = strategy.execute(a, b);
        assertEquals(0, result.compareTo(new BigDecimal("4.25")));
    }

    @Test
    void execute_handlesLargeNumbers() {
        AdditionStrategy strategy = new AdditionStrategy();

        BigDecimal a = new BigDecimal("1000000000000000000");
        BigDecimal b = new BigDecimal("2000000000000000000");
        BigDecimal result = strategy.execute(a, b);

        assertEquals(0, result.compareTo(new BigDecimal("3000000000000000000")));
    }

    @Test
    void execute_nullArguments_throwNullPointerException() {
        AdditionStrategy strategy = new AdditionStrategy();

        BigDecimal a = new BigDecimal("5");
        assertThrows(NullPointerException.class, () -> strategy.execute(a, null));
        assertThrows(NullPointerException.class, () -> strategy.execute(null, BigDecimal.ONE));
    }
}