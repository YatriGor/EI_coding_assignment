package com.designpatterns.creational.builder;

import com.designpatterns.common.ValidationUtils;

import java.util.Objects;

/**
 * Represents a HAVING condition in SQL query
 * Similar to WHERE but for aggregate functions
 */
public final class HavingCondition {
    private final String expression;
    private final WhereCondition.Operator operator;
    private final Object value;
    private final WhereCondition.LogicalOperator logicalOperator;
    
    public HavingCondition(String expression, WhereCondition.Operator operator, Object value, 
                          WhereCondition.LogicalOperator logicalOperator) {
        this.expression = ValidationUtils.requireNonEmpty(expression, "Expression cannot be empty");
        this.operator = ValidationUtils.requireNonNull(operator, "Operator cannot be null");
        this.value = value; // Can be null for IS_NULL/IS_NOT_NULL
        this.logicalOperator = logicalOperator; // Can be null for first condition
        
        // Validate value requirements (same as WhereCondition)
        validateValueForOperator(operator, value);
    }
    
    public HavingCondition(String expression, WhereCondition.Operator operator, Object value) {
        this(expression, operator, value, null);
    }
    
    private void validateValueForOperator(WhereCondition.Operator operator, Object value) {
        switch (operator) {
            case IS_NULL, IS_NOT_NULL -> {
                if (value != null) {
                    throw new IllegalArgumentException(operator + " operator should not have a value");
                }
            }
            case IN, NOT_IN, BETWEEN, NOT_BETWEEN -> {
                if (value == null) {
                    throw new IllegalArgumentException(operator + " operator requires a value");
                }
            }
            default -> {
                if (value == null) {
                    throw new IllegalArgumentException(operator + " operator requires a value");
                }
            }
        }
    }
    
    // Getters
    public String getExpression() { return expression; }
    public WhereCondition.Operator getOperator() { return operator; }
    public Object getValue() { return value; }
    public WhereCondition.LogicalOperator getLogicalOperator() { return logicalOperator; }
    
    /**
     * Generates the SQL representation of this having condition
     */
    public String toSql() {
        StringBuilder sql = new StringBuilder();
        
        if (logicalOperator != null) {
            sql.append(logicalOperator.name()).append(" ");
        }
        
        sql.append(expression).append(" ").append(operator.getSql());
        
        if (value != null) {
            switch (operator) {
                case IN, NOT_IN -> {
                    if (value instanceof Object[] array) {
                        sql.append(" (");
                        for (int i = 0; i < array.length; i++) {
                            if (i > 0) sql.append(", ");
                            sql.append("?");
                        }
                        sql.append(")");
                    } else {
                        sql.append(" (?)");
                    }
                }
                case BETWEEN, NOT_BETWEEN -> {
                    if (value instanceof Object[] array && array.length == 2) {
                        sql.append(" ? AND ?");
                    } else {
                        sql.append(" ").append(value);
                    }
                }
                default -> sql.append(" ?");
            }
        }
        
        return sql.toString();
    }
    
    /**
     * Creates common HAVING conditions for aggregate functions
     */
    public static HavingCondition count(String column, WhereCondition.Operator operator, Object value) {
        return new HavingCondition("COUNT(" + column + ")", operator, value);
    }
    
    public static HavingCondition sum(String column, WhereCondition.Operator operator, Object value) {
        return new HavingCondition("SUM(" + column + ")", operator, value);
    }
    
    public static HavingCondition avg(String column, WhereCondition.Operator operator, Object value) {
        return new HavingCondition("AVG(" + column + ")", operator, value);
    }
    
    public static HavingCondition max(String column, WhereCondition.Operator operator, Object value) {
        return new HavingCondition("MAX(" + column + ")", operator, value);
    }
    
    public static HavingCondition min(String column, WhereCondition.Operator operator, Object value) {
        return new HavingCondition("MIN(" + column + ")", operator, value);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        HavingCondition that = (HavingCondition) obj;
        return Objects.equals(expression, that.expression) &&
               operator == that.operator &&
               Objects.equals(value, that.value) &&
               logicalOperator == that.logicalOperator;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(expression, operator, value, logicalOperator);
    }
    
    @Override
    public String toString() {
        return toSql();
    }
}

