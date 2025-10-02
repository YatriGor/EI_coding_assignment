package com.designpatterns.creational.builder;

import com.designpatterns.common.ValidationUtils;

import java.util.Objects;

/**
 * Represents a WHERE condition in SQL query
 * Immutable value object for where clauses
 */
public final class WhereCondition {
    private final String column;
    private final Operator operator;
    private final Object value;
    private final LogicalOperator logicalOperator;
    
    public enum Operator {
        EQUALS("="),
        NOT_EQUALS("!="),
        GREATER_THAN(">"),
        GREATER_THAN_OR_EQUAL(">="),
        LESS_THAN("<"),
        LESS_THAN_OR_EQUAL("<="),
        LIKE("LIKE"),
        NOT_LIKE("NOT LIKE"),
        IN("IN"),
        NOT_IN("NOT IN"),
        IS_NULL("IS NULL"),
        IS_NOT_NULL("IS NOT NULL"),
        BETWEEN("BETWEEN"),
        NOT_BETWEEN("NOT BETWEEN");
        
        private final String sql;
        
        Operator(String sql) {
            this.sql = sql;
        }
        
        public String getSql() {
            return sql;
        }
    }
    
    public enum LogicalOperator {
        AND, OR, NOT
    }
    
    public WhereCondition(String column, Operator operator, Object value, LogicalOperator logicalOperator) {
        this.column = ValidationUtils.requireNonEmpty(column, "Column name cannot be empty");
        this.operator = ValidationUtils.requireNonNull(operator, "Operator cannot be null");
        this.value = value; // Can be null for IS_NULL/IS_NOT_NULL
        this.logicalOperator = logicalOperator; // Can be null for first condition
        
        // Validate value requirements
        validateValueForOperator(operator, value);
    }
    
    public WhereCondition(String column, Operator operator, Object value) {
        this(column, operator, value, null);
    }
    
    private void validateValueForOperator(Operator operator, Object value) {
        switch (operator) {
            case IS_NULL, IS_NOT_NULL -> {
                if (value != null) {
                    throw new IllegalArgumentException(operator + " operator should not have a value");
                }
            }
            case IN, NOT_IN -> {
                if (value == null) {
                    throw new IllegalArgumentException(operator + " operator requires a value");
                }
                // Value should be a collection or array for IN/NOT IN
            }
            case BETWEEN, NOT_BETWEEN -> {
                if (value == null) {
                    throw new IllegalArgumentException(operator + " operator requires a value");
                }
                // Value should be an array or string with "AND" for BETWEEN
            }
            default -> {
                if (value == null) {
                    throw new IllegalArgumentException(operator + " operator requires a value");
                }
            }
        }
    }
    
    // Getters
    public String getColumn() { return column; }
    public Operator getOperator() { return operator; }
    public Object getValue() { return value; }
    public LogicalOperator getLogicalOperator() { return logicalOperator; }
    
    /**
     * Generates the SQL representation of this where condition
     */
    public String toSql() {
        StringBuilder sql = new StringBuilder();
        
        if (logicalOperator != null) {
            sql.append(logicalOperator.name()).append(" ");
        }
        
        sql.append(column).append(" ").append(operator.getSql());
        
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
     * Creates a new condition with AND logical operator
     */
    public WhereCondition and(String column, Operator operator, Object value) {
        return new WhereCondition(column, operator, value, LogicalOperator.AND);
    }
    
    /**
     * Creates a new condition with OR logical operator
     */
    public WhereCondition or(String column, Operator operator, Object value) {
        return new WhereCondition(column, operator, value, LogicalOperator.OR);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        WhereCondition that = (WhereCondition) obj;
        return Objects.equals(column, that.column) &&
               operator == that.operator &&
               Objects.equals(value, that.value) &&
               logicalOperator == that.logicalOperator;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(column, operator, value, logicalOperator);
    }
    
    @Override
    public String toString() {
        return toSql();
    }
}

