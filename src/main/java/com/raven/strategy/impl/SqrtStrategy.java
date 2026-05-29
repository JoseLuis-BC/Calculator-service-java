package com.raven.strategy.impl;

import com.raven.exceptions.BadRequestException;
import com.raven.strategy.OperationStrategy;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.util.List;

@Component("SQRT")
public class SqrtStrategy implements OperationStrategy {

    @Override
    public BigDecimal execute(BigDecimal a, BigDecimal b) {

        if (a.compareTo(BigDecimal.ZERO) < 0) {
            throw new BadRequestException("Invalid operation parameters",
                    List.of("Square root of positive numbers only"));
        }

        return BigDecimal.valueOf(Math.sqrt(a.doubleValue()));
    }
}
