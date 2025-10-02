package com.designpatterns.creational.builder;

import com.designpatterns.common.ValidationUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Immutable SQL Query object - Product in Builder pattern
 * Represents a complete SQL query with all its components
 */
public final class SqlQuery {
    private final QueryType queryType;
    private final List<String> selectColumns;
    private final List<String> fromTables;
    private final List<JoinClause> joins;
    private final List<WhereCondition> whereConditions;
    private final List<String> groupByColumns;
    private final List<HavingCondition> havingConditions;
    private final List<OrderByClause> orderByColumns;
    private final Integer limit;
    private final Integer offset;
    private final List<UnionClause> unions;
    private final Map<String, Object> parameters;
    private final boolean distinct;
    private final List<String> insertColumns;
    private final List<Object> insertValues;
    private final Map<String, Object> updateValues;
    private final String rawSql;
    
    public enum QueryType {
        SELECT, INSERT, UPDATE, DELETE, CREATE_TABLE, DROP_TABLE, ALTER_TABLE
    }
    
    // Package-private constructor - only accessible by builder
    SqlQuery(QueryType queryType, List<String> selectColumns, List<String> fromTables,
             List<JoinClause> joins, List<WhereCondition> whereConditions,
             List<String> groupByColumns, List<HavingCondition> havingConditions,
             List<OrderByClause> orderByColumns, Integer limit, Integer offset,
             List<UnionClause> unions, Map<String, Object> parameters, boolean distinct,
             List<String> insertColumns, List<Object> insertValues,
             Map<String, Object> updateValues, String rawSql) {
        
        this.queryType = ValidationUtils.requireNonNull(queryType, "Query type cannot be null");
        this.selectColumns = selectColumns != null ? List.copyOf(selectColumns) : List.of();
        this.fromTables = fromTables != null ? List.copyOf(fromTables) : List.of();
        this.joins = joins != null ? List.copyOf(joins) : List.of();
        this.whereConditions = whereConditions != null ? List.copyOf(whereConditions) : List.of();
        this.groupByColumns = groupByColumns != null ? List.copyOf(groupByColumns) : List.of();
        this.havingConditions = havingConditions != null ? List.copyOf(havingConditions) : List.of();
        this.orderByColumns = orderByColumns != null ? List.copyOf(orderByColumns) : List.of();
        this.limit = limit;
        this.offset = offset;
        this.unions = unions != null ? List.copyOf(unions) : List.of();
        this.parameters = parameters != null ? Map.copyOf(parameters) : Map.of();
        this.distinct = distinct;
        this.insertColumns = insertColumns != null ? List.copyOf(insertColumns) : List.of();
        this.insertValues = insertValues != null ? List.copyOf(insertValues) : List.of();
        this.updateValues = updateValues != null ? Map.copyOf(updateValues) : Map.of();
        this.rawSql = rawSql;
    }
    
    // Getters
    public QueryType getQueryType() { return queryType; }
    public List<String> getSelectColumns() { return selectColumns; }
    public List<String> getFromTables() { return fromTables; }
    public List<JoinClause> getJoins() { return joins; }
    public List<WhereCondition> getWhereConditions() { return whereConditions; }
    public List<String> getGroupByColumns() { return groupByColumns; }
    public List<HavingCondition> getHavingConditions() { return havingConditions; }
    public List<OrderByClause> getOrderByColumns() { return orderByColumns; }
    public Integer getLimit() { return limit; }
    public Integer getOffset() { return offset; }
    public List<UnionClause> getUnions() { return unions; }
    public Map<String, Object> getParameters() { return parameters; }
    public boolean isDistinct() { return distinct; }
    public List<String> getInsertColumns() { return insertColumns; }
    public List<Object> getInsertValues() { return insertValues; }
    public Map<String, Object> getUpdateValues() { return updateValues; }
    public String getRawSql() { return rawSql; }
    
    /**
     * Generates the SQL string representation of this query
     */
    public String toSql() {
        if (rawSql != null && !rawSql.trim().isEmpty()) {
            return rawSql;
        }
        
        return switch (queryType) {
            case SELECT -> generateSelectSql();
            case INSERT -> generateInsertSql();
            case UPDATE -> generateUpdateSql();
            case DELETE -> generateDeleteSql();
            default -> throw new UnsupportedOperationException("Query type not supported: " + queryType);
        };
    }
    
    private String generateSelectSql() {
        StringBuilder sql = new StringBuilder("SELECT ");
        
        if (distinct) {
            sql.append("DISTINCT ");
        }
        
        if (selectColumns.isEmpty()) {
            sql.append("*");
        } else {
            sql.append(String.join(", ", selectColumns));
        }
        
        if (!fromTables.isEmpty()) {
            sql.append(" FROM ").append(String.join(", ", fromTables));
        }
        
        for (JoinClause join : joins) {
            sql.append(" ").append(join.toSql());
        }
        
        if (!whereConditions.isEmpty()) {
            sql.append(" WHERE ");
            for (int i = 0; i < whereConditions.size(); i++) {
                if (i > 0) {
                    sql.append(" AND ");
                }
                sql.append(whereConditions.get(i).toSql());
            }
        }
        
        if (!groupByColumns.isEmpty()) {
            sql.append(" GROUP BY ").append(String.join(", ", groupByColumns));
        }
        
        if (!havingConditions.isEmpty()) {
            sql.append(" HAVING ");
            for (int i = 0; i < havingConditions.size(); i++) {
                if (i > 0) {
                    sql.append(" AND ");
                }
                sql.append(havingConditions.get(i).toSql());
            }
        }
        
        if (!orderByColumns.isEmpty()) {
            sql.append(" ORDER BY ");
            sql.append(orderByColumns.stream()
                      .map(OrderByClause::toSql)
                      .reduce((a, b) -> a + ", " + b)
                      .orElse(""));
        }
        
        if (limit != null) {
            sql.append(" LIMIT ").append(limit);
        }
        
        if (offset != null) {
            sql.append(" OFFSET ").append(offset);
        }
        
        for (UnionClause union : unions) {
            sql.append(" ").append(union.toSql());
        }
        
        return sql.toString();
    }
    
    private String generateInsertSql() {
        if (fromTables.isEmpty()) {
            throw new IllegalStateException("INSERT query must have a table specified");
        }
        
        StringBuilder sql = new StringBuilder("INSERT INTO ");
        sql.append(fromTables.get(0));
        
        if (!insertColumns.isEmpty()) {
            sql.append(" (").append(String.join(", ", insertColumns)).append(")");
        }
        
        sql.append(" VALUES (");
        for (int i = 0; i < insertValues.size(); i++) {
            if (i > 0) sql.append(", ");
            sql.append("?");
        }
        sql.append(")");
        
        return sql.toString();
    }
    
    private String generateUpdateSql() {
        if (fromTables.isEmpty()) {
            throw new IllegalStateException("UPDATE query must have a table specified");
        }
        
        StringBuilder sql = new StringBuilder("UPDATE ");
        sql.append(fromTables.get(0));
        sql.append(" SET ");
        
        boolean first = true;
        for (Map.Entry<String, Object> entry : updateValues.entrySet()) {
            if (!first) sql.append(", ");
            sql.append(entry.getKey()).append(" = ?");
            first = false;
        }
        
        if (!whereConditions.isEmpty()) {
            sql.append(" WHERE ");
            for (int i = 0; i < whereConditions.size(); i++) {
                if (i > 0) {
                    sql.append(" AND ");
                }
                sql.append(whereConditions.get(i).toSql());
            }
        }
        
        return sql.toString();
    }
    
    private String generateDeleteSql() {
        if (fromTables.isEmpty()) {
            throw new IllegalStateException("DELETE query must have a table specified");
        }
        
        StringBuilder sql = new StringBuilder("DELETE FROM ");
        sql.append(fromTables.get(0));
        
        if (!whereConditions.isEmpty()) {
            sql.append(" WHERE ");
            for (int i = 0; i < whereConditions.size(); i++) {
                if (i > 0) {
                    sql.append(" AND ");
                }
                sql.append(whereConditions.get(i).toSql());
            }
        }
        
        return sql.toString();
    }
    
    /**
     * Gets all parameter values in the order they appear in the SQL
     */
    public Object[] getParameterValues() {
        return switch (queryType) {
            case INSERT -> insertValues.toArray();
            case UPDATE -> updateValues.values().toArray();
            default -> parameters.values().toArray();
        };
    }
    
    /**
     * Validates the query structure
     */
    public void validate() {
        switch (queryType) {
            case SELECT -> {
                if (fromTables.isEmpty() && selectColumns.stream().noneMatch(col -> col.contains("("))) {
                    throw new IllegalStateException("SELECT query must have FROM clause or use functions");
                }
            }
            case INSERT -> {
                if (fromTables.isEmpty()) {
                    throw new IllegalStateException("INSERT query must specify a table");
                }
                if (!insertColumns.isEmpty() && insertColumns.size() != insertValues.size()) {
                    throw new IllegalStateException("INSERT columns and values count mismatch");
                }
            }
            case UPDATE -> {
                if (fromTables.isEmpty()) {
                    throw new IllegalStateException("UPDATE query must specify a table");
                }
                if (updateValues.isEmpty()) {
                    throw new IllegalStateException("UPDATE query must have SET values");
                }
            }
            case DELETE -> {
                if (fromTables.isEmpty()) {
                    throw new IllegalStateException("DELETE query must specify a table");
                }
            }
        }
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        SqlQuery sqlQuery = (SqlQuery) obj;
        return distinct == sqlQuery.distinct &&
               queryType == sqlQuery.queryType &&
               Objects.equals(selectColumns, sqlQuery.selectColumns) &&
               Objects.equals(fromTables, sqlQuery.fromTables) &&
               Objects.equals(joins, sqlQuery.joins) &&
               Objects.equals(whereConditions, sqlQuery.whereConditions) &&
               Objects.equals(groupByColumns, sqlQuery.groupByColumns) &&
               Objects.equals(havingConditions, sqlQuery.havingConditions) &&
               Objects.equals(orderByColumns, sqlQuery.orderByColumns) &&
               Objects.equals(limit, sqlQuery.limit) &&
               Objects.equals(offset, sqlQuery.offset);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(queryType, selectColumns, fromTables, joins, whereConditions,
                           groupByColumns, havingConditions, orderByColumns, limit, offset, distinct);
    }
    
    @Override
    public String toString() {
        return String.format("SqlQuery{type=%s, sql='%s'}", queryType, toSql());
    }
}

