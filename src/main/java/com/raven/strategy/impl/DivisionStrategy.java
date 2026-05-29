package com.raven.strategy.impl;

import com.raven.exceptions.BadRequestException;
import com.raven.strategy.OperationStrategy;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Component("DIVISION")
public class DivisionStrategy implements OperationStrategy {

    @Override
    public BigDecimal execute(BigDecimal a, BigDecimal b) {

        if (b == null) {
            throw new BadRequestException("Invalid operation parameters",  List.of("OperandB required"));
        }
        if (a == null) {
            throw new BadRequestException("Invalid operation parameters",  List.of("OperandA required"));
        }

        if (b.compareTo(BigDecimal.ZERO) == 0 || a.compareTo(BigDecimal.ZERO) == 0) {
            throw new BadRequestException("Invalid operation parameters",
                    List.of("Division by zero is not allowed"));
        }

        return a.divide(b, 10, RoundingMode.HALF_UP);
    }
}