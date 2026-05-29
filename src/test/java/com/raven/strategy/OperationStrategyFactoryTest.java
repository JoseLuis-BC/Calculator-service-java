package com.raven.strategy;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class OperationStrategyFactoryTest {

    @Test
    void getStrategy_returnsRegisteredStrategy() {
        OperationStrategy strategy = (a, b) -> BigDecimal.valueOf(42);
        Map<String, OperationStrategy> strategies = Map.of("TEST_OP", strategy);
        OperationStrategyFactory factory = new OperationStrategyFactory(strategies);
        OperationStrategy result = factory.getStrategy("TEST_OP");
        assertSame(strategy, result, "La fábrica debe devolver la misma instancia registrada");
    }

    @Test
    void getStrategy_throwsWhenOperationNotRegistered() {
        OperationStrategyFactory factory = new OperationStrategyFactory(Map.of());
        RuntimeException ex = assertThrows(RuntimeException.class, () -> factory.getStrategy("UNKNOWN"));
        assertTrue(ex.getMessage().toLowerCase().contains("invalid operation"));
    }

    @Test
    void getStrategy_throwsWhenOperationIsNull() {
        OperationStrategyFactory factory = new OperationStrategyFactory(Map.of());
        assertThrows(RuntimeException.class, () -> factory.getStrategy(null));
    }
}