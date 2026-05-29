package com.raven.strategy;

import org.springframework.stereotype.Component;
import java.util.Map;

@Component
public class OperationStrategyFactory {

    private final Map<String, OperationStrategy> strategies;

    public OperationStrategyFactory(Map<String, OperationStrategy> strategies) {
        this.strategies = strategies;
    }

    public OperationStrategy getStrategy(String operation) {

        OperationStrategy strategy = strategies.get(operation);

        if (strategy == null) {
            throw new RuntimeException("Invalid operation");
        }

        return strategy;
    }
}
