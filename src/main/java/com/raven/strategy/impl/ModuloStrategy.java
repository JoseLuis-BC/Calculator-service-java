package com.raven.strategy.impl;

import com.raven.strategy.OperationStrategy;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;

@Component("MODULO")
public class ModuloStrategy implements OperationStrategy {

    @Override
    public BigDecimal execute(BigDecimal a, BigDecimal b) {

        if (b.compareTo(BigDecimal.ZERO) == 0)
            throw new RuntimeException("Modulo by zero");

        return a.remainder(b);
    }
}

