package com.raven.entities;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
public class Operation {

    @Id
    @GeneratedValue
    private UUID id;
    private UUID userId;
    @Enumerated(EnumType.STRING)
    private OperationType operation;
    private BigDecimal operandA;
    private BigDecimal operandB;
    private BigDecimal result;

    private LocalDateTime timestamp;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

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

    public BigDecimal getResult() {
        return result;
    }

    public void setResult(BigDecimal result) {
        this.result = result;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
