package com.raven.strategy;

import java.math.BigDecimal;

public interface OperationStrategy {
    BigDecimal execute(BigDecimal a, BigDecimal b);
}
