package com.raven.strategy.impl;

import com.raven.strategy.OperationStrategy;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;

@Component("POWER")
public class PowerStrategy implements OperationStrategy {

    @Override
    public BigDecimal execute(BigDecimal a, BigDecimal b) {
        return BigDecimal.valueOf(
                Math.pow(a.doubleValue(), b.doubleValue())
        );
    }
}
