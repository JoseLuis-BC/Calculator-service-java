package com.raven.services;

import com.raven.dto.request.CalculationRequest;
import com.raven.entities.Operation;
import com.raven.entities.OperationType;
import com.raven.entities.User;
import com.raven.exceptions.BadRequestException;
import com.raven.strategy.OperationStrategy;
import com.raven.strategy.OperationStrategyFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class CalculationService {


    private static final BigDecimal MIN = BigDecimal.valueOf(-1_000_000);
    private static final BigDecimal MAX = BigDecimal.valueOf(1_000_000);

    private final OperationStrategyFactory factory;
    private final OperationAsyncService asyncService;

    public Operation calculate(CalculationRequest req, User user) {

        BigDecimal a = req.getOperandA();
        BigDecimal b = req.getOperandB();

        validateOperands(req.getOperation(), a, b);

        OperationStrategy strategy =
                factory.getStrategy(req.getOperation().name());

        BigDecimal result = strategy.execute(a, b);

        Operation op = new Operation();
        op.setUserId(user.getId());
        op.setOperation(req.getOperation());
        op.setOperandA(a);
        op.setOperandB(b);
        op.setResult(result);
        op.setTimestamp(LocalDateTime.now());
        op.setId(UUID.randomUUID());

        asyncService.saveAsync(op);

        return op;
    }


    private void validateOperands(OperationType op, BigDecimal a, BigDecimal b) {

        if (a == null) {
            throw new BadRequestException("Invalid operation parameters",  List.of("OperandA is required"));
        }

        if (requiresSecondOperand(op) && b == null) {
            throw new BadRequestException("Invalid operation parameters",  List.of("OperandB is required"));
        }

        validateRange(a);
        validateRange(b);

    }

    private void validateRange(BigDecimal value) {
        if (value == null) return;

        if (value.compareTo(MIN) < 0 || value.compareTo(MAX) > 0) {
            throw new BadRequestException("Invalid operation parameters",
                    List.of("Operand out of allowed range (-1,000,000 to 1,000,000)"));
        }
    }

    private boolean requiresSecondOperand(OperationType op) {
        return op != OperationType.SQRT && op != OperationType.ABSOLUTE;
    }
}
