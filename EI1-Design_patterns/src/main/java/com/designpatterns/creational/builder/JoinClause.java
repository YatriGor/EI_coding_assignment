package com.designpatterns.creational.builder;

import com.designpatterns.common.ValidationUtils;

import java.util.Objects;

/**
 * Represents a JOIN clause in SQL query
 * Immutable value object for join operations
 */
public final class JoinClause {
    private final JoinType joinType;
    private final String table;
    private final String condition;
    private final String alias;
    
    public enum JoinType {
        INNER("INNER JOIN"),
        LEFT("LEFT JOIN"),
        RIGHT("RIGHT JOIN"),
        FULL("FULL OUTER JOIN"),
        CROSS("CROSS JOIN");
        
        private final String sql;
        
        JoinType(String sql) {
            this.sql = sql;
        }
        
        public String getSql() {
            return sql;
        }
    }
    
    public JoinClause(JoinType joinType, String table, String condition, String alias) {
        this.joinType = ValidationUtils.requireNonNull(joinType, "Join type cannot be null");
        this.table = ValidationUtils.requireNonEmpty(table, "Table name cannot be empty");
        this.condition = condition; // Can be null for CROSS JOIN
        this.alias = alias; // Can be null
        
        // Validate condition for non-CROSS joins
        if (joinType != JoinType.CROSS && (condition == null || condition.trim().isEmpty())) {
            throw new IllegalArgumentException("Join condition is required for " + joinType + " joins");
        }
    }
    
    public JoinClause(JoinType joinType, String table, String condition) {
        this(joinType, table, condition, null);
    }
    
    // Getters
    public JoinType getJoinType() { return joinType; }
    public String getTable() { return table; }
    public String getCondition() { return condition; }
    public String getAlias() { return alias; }
    
    /**
     * Generates the SQL representation of this join clause
     */
    public String toSql() {
        StringBuilder sql = new StringBuilder();
        sql.append(joinType.getSql()).append(" ");
        sql.append(table);
        
        if (alias != null && !alias.trim().isEmpty()) {
            sql.append(" AS ").append(alias);
        }
        
        if (condition != null && !condition.trim().isEmpty()) {
            sql.append(" ON ").append(condition);
        }
        
        return sql.toString();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        JoinClause that = (JoinClause) obj;
        return joinType == that.joinType &&
               Objects.equals(table, that.table) &&
               Objects.equals(condition, that.condition) &&
               Objects.equals(alias, that.alias);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(joinType, table, condition, alias);
    }
    
    @Override
    public String toString() {
        return toSql();
    }
}

