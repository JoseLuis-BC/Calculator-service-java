package com.raven.dto.request;

import com.raven.entities.OperationType;

import java.math.BigDecimal;

public class CalculationRequest {

    private OperationType operation;
    private BigDecimal operandA;
    private BigDecimal operandB;

    public OperationType getOperation() {
        return operation;
    }

    public void setOperation(OperationType operation) {
        this.operation = operation;
    }

    public BigDecimal getOperandA() {
        return operandA;
    }

    public void setOperandA(BigDecimal operandA) {
        this.operandA = operandA;
    }

    public BigDecimal getOperandB() {
        return operandB;
    }

    public void setOperandB(BigDecimal operandB) {
        this.operandB = operandB;
    }
}
