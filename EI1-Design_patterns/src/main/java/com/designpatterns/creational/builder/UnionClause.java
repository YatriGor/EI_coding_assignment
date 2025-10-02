package com.designpatterns.creational.builder;

import com.designpatterns.common.ValidationUtils;

import java.util.Objects;

/**
 * Represents a UNION clause in SQL query
 * Immutable value object for union operations
 */
public final class UnionClause {
    private final UnionType unionType;
    private final SqlQuery query;
    
    public enum UnionType {
        UNION("UNION"),
        UNION_ALL("UNION ALL");
        
        private final String sql;
        
        UnionType(String sql) {
            this.sql = sql;
        }
        
        public String getSql() {
            return sql;
        }
    }
    
    public UnionClause(UnionType unionType, SqlQuery query) {
        this.unionType = ValidationUtils.requireNonNull(unionType, "Union type cannot be null");
        this.query = ValidationUtils.requireNonNull(query, "Query cannot be null");
        
        // Validate that the query is a SELECT query
        if (query.getQueryType() != SqlQuery.QueryType.SELECT) {
            throw new IllegalArgumentException("UNION can only be used with SELECT queries");
        }
    }
    
    // Getters
    public UnionType getUnionType() { return unionType; }
    public SqlQuery getQuery() { return query; }
    
    /**
     * Generates the SQL representation of this union clause
     */
    public String toSql() {
        return unionType.getSql() + " (" + query.toSql() + ")";
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        UnionClause that = (UnionClause) obj;
        return unionType == that.unionType &&
               Objects.equals(query, that.query);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(unionType, query);
    }
    
    @Override
    public String toString() {
        return toSql();
    }
}

