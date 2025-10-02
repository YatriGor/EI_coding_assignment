package com.designpatterns.creational.builder;

import com.designpatterns.common.ValidationUtils;

import java.util.Objects;

/**
 * Represents an ORDER BY clause in SQL query
 * Immutable value object for ordering specifications
 */
public final class OrderByClause {
    private final String column;
    private final Direction direction;
    private final NullsHandling nullsHandling;
    
    public enum Direction {
        ASC, DESC
    }
    
    public enum NullsHandling {
        NULLS_FIRST, NULLS_LAST
    }
    
    public OrderByClause(String column, Direction direction, NullsHandling nullsHandling) {
        this.column = ValidationUtils.requireNonEmpty(column, "Column name cannot be empty");
        this.direction = direction != null ? direction : Direction.ASC; // Default to ASC
        this.nullsHandling = nullsHandling; // Can be null
    }
    
    public OrderByClause(String column, Direction direction) {
        this(column, direction, null);
    }
    
    public OrderByClause(String column) {
        this(column, Direction.ASC, null);
    }
    
    // Getters
    public String getColumn() { return column; }
    public Direction getDirection() { return direction; }
    public NullsHandling getNullsHandling() { return nullsHandling; }
    
    /**
     * Generates the SQL representation of this order by clause
     */
    public String toSql() {
        StringBuilder sql = new StringBuilder();
        sql.append(column).append(" ").append(direction.name());
        
        if (nullsHandling != null) {
            sql.append(" ").append(nullsHandling.name().replace("_", " "));
        }
        
        return sql.toString();
    }
    
    /**
     * Creates a new OrderByClause with ascending direction
     */
    public static OrderByClause asc(String column) {
        return new OrderByClause(column, Direction.ASC);
    }
    
    /**
     * Creates a new OrderByClause with descending direction
     */
    public static OrderByClause desc(String column) {
        return new OrderByClause(column, Direction.DESC);
    }
    
    /**
     * Creates a new OrderByClause with ascending direction and nulls first
     */
    public static OrderByClause ascNullsFirst(String column) {
        return new OrderByClause(column, Direction.ASC, NullsHandling.NULLS_FIRST);
    }
    
    /**
     * Creates a new OrderByClause with descending direction and nulls last
     */
    public static OrderByClause descNullsLast(String column) {
        return new OrderByClause(column, Direction.DESC, NullsHandling.NULLS_LAST);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        OrderByClause that = (OrderByClause) obj;
        return Objects.equals(column, that.column) &&
               direction == that.direction &&
               nullsHandling == that.nullsHandling;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(column, direction, nullsHandling);
    }
    
    @Override
    public String toString() {
        return toSql();
    }
}

