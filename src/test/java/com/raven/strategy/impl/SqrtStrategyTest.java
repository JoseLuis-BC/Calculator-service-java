package com.raven.strategy.impl;

import com.raven.exceptions.BadRequestException;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

class SqrtStrategyTest {

    @Test
    void execute_returnsSquareRootForPositive() {
        SqrtStrategy strategy = new SqrtStrategy();
        BigDecimal a = new BigDecimal("16");
        BigDecimal result = strategy.execute(a, null);
        assertNotNull(result);
        assertEquals(0, result.compareTo(new BigDecimal("4")));
    }

    @Test
    void execute_throwsForNegativeInput() {
        SqrtStrategy strategy = new SqrtStrategy();
        BigDecimal negative = new BigDecimal("-9");
        BadRequestException ex = assertThrows(BadRequestException.class, () -> strategy.execute(negative, null));
        assertTrue(ex.getDetails().stream().anyMatch(d -> d.toLowerCase().contains("square root")),
                "Se esperaba un detalle indicando 'square root' en la excepción");
    }
}