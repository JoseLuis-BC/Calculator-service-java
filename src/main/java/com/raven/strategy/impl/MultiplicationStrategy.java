package com.raven.strategy.impl;

import com.raven.strategy.OperationStrategy;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;

@Component("MULTIPLICATION")
public class MultiplicationStrategy implements OperationStrategy {

    @Override
    public BigDecimal execute(BigDecimal a, BigDecimal b) {
        return a.multiply(b);
    }
}
