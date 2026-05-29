package com.raven.strategy.impl;

import com.raven.exceptions.BadRequestException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class DivisionStrategyTest {

    @Test
    void execute_dividesTwoNumbers() {
        DivisionStrategy strategy = new DivisionStrategy();

        BigDecimal a = new BigDecimal("20");
        BigDecimal b = new BigDecimal("4");
        BigDecimal result = strategy.execute(a, b);

        // La implementación devuelve escala 10
        assertEquals(0, result.compareTo(new BigDecimal("5.0000000000")));
    }

    @Test
    void execute_throwsWhenBIsNull() {
        DivisionStrategy strategy = new DivisionStrategy();
        BigDecimal a = new BigDecimal("5");

        BadRequestException ex = assertThrows(BadRequestException.class, () -> strategy.execute(a, null));
        // La clase lanza con detalles que contienen "OperandB required"
        assertTrue(ex.getDetails().stream().anyMatch(d -> d.toLowerCase().contains("operandb required") || d.toLowerCase().contains("operandb")));
    }

    @Test
    void execute_throwsWhenAIsNull() {
        DivisionStrategy strategy = new DivisionStrategy();

        BadRequestException ex = assertThrows(BadRequestException.class, () -> strategy.execute(null, BigDecimal.ONE));
        // La clase lanza con detalles que contienen "OperandA required"
        assertTrue(ex.getDetails().stream().anyMatch(d -> d.toLowerCase().contains("operanda required") || d.toLowerCase().contains("operanda")));
    }

    @Test
    void execute_throwsWhenDivisionByZero_dueToZeroDenominator() {
        DivisionStrategy strategy = new DivisionStrategy();

        BigDecimal a = new BigDecimal("5");
        BigDecimal zero = BigDecimal.ZERO;

        BadRequestException ex = assertThrows(BadRequestException.class, () -> strategy.execute(a, zero));
        assertTrue(ex.getDetails().stream().anyMatch(d -> d.toLowerCase().contains("division by zero") || d.toLowerCase().contains("not allowed")));
    }

    @Test
    void execute_throwsWhenDivisionByZero_dueToZeroNumerator() {
        DivisionStrategy strategy = new DivisionStrategy();

        BigDecimal zero = BigDecimal.ZERO;
        BigDecimal b = new BigDecimal("2");

        BadRequestException ex = assertThrows(BadRequestException.class, () -> strategy.execute(zero, b));
        assertTrue(ex.getDetails().stream().anyMatch(d -> d.toLowerCase().contains("division by zero") || d.toLowerCase().contains("not allowed")));
    }
}