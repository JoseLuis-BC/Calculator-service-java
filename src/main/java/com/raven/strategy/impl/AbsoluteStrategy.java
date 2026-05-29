package com.raven.strategy.impl;

import com.raven.strategy.OperationStrategy;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;

@Component("ABSOLUTE")
public class AbsoluteStrategy implements OperationStrategy {

    @Override
    public BigDecimal execute(BigDecimal a, BigDecimal b) {
        return a.abs();
    }
}
