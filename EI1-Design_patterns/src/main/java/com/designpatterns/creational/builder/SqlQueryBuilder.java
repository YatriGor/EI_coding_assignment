package com.designpatterns.creational.builder;

import com.designpatterns.common.ValidationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * SQL Query Builder - Concrete Builder in Builder pattern
 * Provides fluent API for constructing complex SQL queries
 */
public class SqlQueryBuilder {
    private static final Logger logger = LoggerFactory.getLogger(SqlQueryBuilder.class);
    
    private SqlQuery.QueryType queryType;
    private final List<String> selectColumns = new ArrayList<>();
    private final List<String> fromTables = new ArrayList<>();
    private final List<JoinClause> joins = new ArrayList<>();
    private final List<WhereCondition> whereConditions = new ArrayList<>();
    private final List<String> groupByColumns = new ArrayList<>();
    private final List<HavingCondition> havingConditions = new ArrayList<>();
    private final List<OrderByClause> orderByColumns = new ArrayList<>();
    private Integer limit;
    private Integer offset;
    private final List<UnionClause> unions = new ArrayList<>();
    private final Map<String, Object> parameters = new HashMap<>();
    private boolean distinct = false;
    private final List<String> insertColumns = new ArrayList<>();
    private final List<Object> insertValues = new ArrayList<>();
    private final Map<String, Object> updateValues = new HashMap<>();
    private String rawSql;
    
    public SqlQueryBuilder() {
        logger.debug("New SQL Query Builder created");
    }
    
    // SELECT query methods
    public SqlQueryBuilder select(String... columns) {
        this.queryType = SqlQuery.QueryType.SELECT;
        this.selectColumns.addAll(Arrays.asList(columns));
        logger.debug("Added SELECT columns: {}", Arrays.toString(columns));
        return this;
    }
    
    public SqlQueryBuilder selectAll() {
        return select("*");
    }
    
    public SqlQueryBuilder selectDistinct(String... columns) {
        this.distinct = true;
        return select(columns);
    }
    
    public SqlQueryBuilder selectCount(String column) {
        return select("COUNT(" + column + ")");
    }
    
    public SqlQueryBuilder selectSum(String column) {
        return select("SUM(" + column + ")");
    }
    
    public SqlQueryBuilder selectAvg(String column) {
        return select("AVG(" + column + ")");
    }
    
    public SqlQueryBuilder selectMax(String column) {
        return select("MAX(" + column + ")");
    }
    
    public SqlQueryBuilder selectMin(String column) {
        return select("MIN(" + column + ")");
    }
    
    public SqlQueryBuilder selectAs(String expression, String alias) {
        ValidationUtils.requireNonEmpty(expression, "Expression cannot be empty");
        ValidationUtils.requireNonEmpty(alias, "Alias cannot be empty");
        return select(expression + " AS " + alias);
    }
    
    // FROM clause methods
    public SqlQueryBuilder from(String... tables) {
        ValidationUtils.requireNonEmpty(Arrays.asList(tables), "Tables list cannot be empty");
        this.fromTables.addAll(Arrays.asList(tables));
        logger.debug("Added FROM tables: {}", Arrays.toString(tables));
        return this;
    }
    
    public SqlQueryBuilder fromAs(String table, String alias) {
        ValidationUtils.requireNonEmpty(table, "Table name cannot be empty");
        ValidationUtils.requireNonEmpty(alias, "Alias cannot be empty");
        return from(table + " AS " + alias);
    }
    
    // JOIN methods
    public SqlQueryBuilder innerJoin(String table, String condition) {
        return join(JoinClause.JoinType.INNER, table, condition, null);
    }
    
    public SqlQueryBuilder leftJoin(String table, String condition) {
        return join(JoinClause.JoinType.LEFT, table, condition, null);
    }
    
    public SqlQueryBuilder rightJoin(String table, String condition) {
        return join(JoinClause.JoinType.RIGHT, table, condition, null);
    }
    
    public SqlQueryBuilder fullJoin(String table, String condition) {
        return join(JoinClause.JoinType.FULL, table, condition, null);
    }
    
    public SqlQueryBuilder crossJoin(String table) {
        return join(JoinClause.JoinType.CROSS, table, null, null);
    }
    
    public SqlQueryBuilder joinAs(JoinClause.JoinType joinType, String table, String condition, String alias) {
        return join(joinType, table, condition, alias);
    }
    
    private SqlQueryBuilder join(JoinClause.JoinType joinType, String table, String condition, String alias) {
        JoinClause joinClause = new JoinClause(joinType, table, condition, alias);
        this.joins.add(joinClause);
        logger.debug("Added {} join: {}", joinType, joinClause.toSql());
        return this;
    }
    
    // WHERE clause methods
    public SqlQueryBuilder where(String column, WhereCondition.Operator operator, Object value) {
        WhereCondition condition = new WhereCondition(column, operator, value);
        this.whereConditions.add(condition);
        logger.debug("Added WHERE condition: {}", condition.toSql());
        return this;
    }
    
    public SqlQueryBuilder whereEquals(String column, Object value) {
        return where(column, WhereCondition.Operator.EQUALS, value);
    }
    
    public SqlQueryBuilder whereNotEquals(String column, Object value) {
        return where(column, WhereCondition.Operator.NOT_EQUALS, value);
    }
    
    public SqlQueryBuilder whereGreaterThan(String column, Object value) {
        return where(column, WhereCondition.Operator.GREATER_THAN, value);
    }
    
    public SqlQueryBuilder whereLessThan(String column, Object value) {
        return where(column, WhereCondition.Operator.LESS_THAN, value);
    }
    
    public SqlQueryBuilder whereLike(String column, String pattern) {
        return where(column, WhereCondition.Operator.LIKE, pattern);
    }
    
    public SqlQueryBuilder whereIn(String column, Object... values) {
        return where(column, WhereCondition.Operator.IN, values);
    }
    
    public SqlQueryBuilder whereNotIn(String column, Object... values) {
        return where(column, WhereCondition.Operator.NOT_IN, values);
    }
    
    public SqlQueryBuilder whereIsNull(String column) {
        return where(column, WhereCondition.Operator.IS_NULL, null);
    }
    
    public SqlQueryBuilder whereIsNotNull(String column) {
        return where(column, WhereCondition.Operator.IS_NOT_NULL, null);
    }
    
    public SqlQueryBuilder whereBetween(String column, Object value1, Object value2) {
        return where(column, WhereCondition.Operator.BETWEEN, new Object[]{value1, value2});
    }
    
    // AND/OR WHERE methods
    public SqlQueryBuilder and(String column, WhereCondition.Operator operator, Object value) {
        WhereCondition condition = new WhereCondition(column, operator, value, WhereCondition.LogicalOperator.AND);
        this.whereConditions.add(condition);
        logger.debug("Added AND condition: {}", condition.toSql());
        return this;
    }
    
    public SqlQueryBuilder or(String column, WhereCondition.Operator operator, Object value) {
        WhereCondition condition = new WhereCondition(column, operator, value, WhereCondition.LogicalOperator.OR);
        this.whereConditions.add(condition);
        logger.debug("Added OR condition: {}", condition.toSql());
        return this;
    }
    
    public SqlQueryBuilder andEquals(String column, Object value) {
        return and(column, WhereCondition.Operator.EQUALS, value);
    }
    
    public SqlQueryBuilder orEquals(String column, Object value) {
        return or(column, WhereCondition.Operator.EQUALS, value);
    }
    
    // GROUP BY methods
    public SqlQueryBuilder groupBy(String... columns) {
        ValidationUtils.requireNonEmpty(Arrays.asList(columns), "Group by columns cannot be empty");
        this.groupByColumns.addAll(Arrays.asList(columns));
        logger.debug("Added GROUP BY columns: {}", Arrays.toString(columns));
        return this;
    }
    
    // HAVING methods
    public SqlQueryBuilder having(String expression, WhereCondition.Operator operator, Object value) {
        HavingCondition condition = new HavingCondition(expression, operator, value);
        this.havingConditions.add(condition);
        logger.debug("Added HAVING condition: {}", condition.toSql());
        return this;
    }
    
    public SqlQueryBuilder havingCount(String column, WhereCondition.Operator operator, Object value) {
        return having("COUNT(" + column + ")", operator, value);
    }
    
    public SqlQueryBuilder havingSum(String column, WhereCondition.Operator operator, Object value) {
        return having("SUM(" + column + ")", operator, value);
    }
    
    public SqlQueryBuilder havingAvg(String column, WhereCondition.Operator operator, Object value) {
        return having("AVG(" + column + ")", operator, value);
    }
    
    // ORDER BY methods
    public SqlQueryBuilder orderBy(String column) {
        return orderBy(column, OrderByClause.Direction.ASC);
    }
    
    public SqlQueryBuilder orderBy(String column, OrderByClause.Direction direction) {
        OrderByClause orderBy = new OrderByClause(column, direction);
        this.orderByColumns.add(orderBy);
        logger.debug("Added ORDER BY: {}", orderBy.toSql());
        return this;
    }
    
    public SqlQueryBuilder orderByAsc(String column) {
        return orderBy(column, OrderByClause.Direction.ASC);
    }
    
    public SqlQueryBuilder orderByDesc(String column) {
        return orderBy(column, OrderByClause.Direction.DESC);
    }
    
    public SqlQueryBuilder orderByNullsFirst(String column, OrderByClause.Direction direction) {
        OrderByClause orderBy = new OrderByClause(column, direction, OrderByClause.NullsHandling.NULLS_FIRST);
        this.orderByColumns.add(orderBy);
        logger.debug("Added ORDER BY with NULLS FIRST: {}", orderBy.toSql());
        return this;
    }
    
    // LIMIT and OFFSET methods
    public SqlQueryBuilder limit(int limit) {
        ValidationUtils.requireInRange(limit, 1, Integer.MAX_VALUE, "Limit must be positive");
        this.limit = limit;
        logger.debug("Added LIMIT: {}", limit);
        return this;
    }
    
    public SqlQueryBuilder offset(int offset) {
        ValidationUtils.requireInRange(offset, 0, Integer.MAX_VALUE, "Offset must be non-negative");
        this.offset = offset;
        logger.debug("Added OFFSET: {}", offset);
        return this;
    }
    
    public SqlQueryBuilder limitOffset(int limit, int offset) {
        return limit(limit).offset(offset);
    }
    
    // UNION methods
    public SqlQueryBuilder union(SqlQuery query) {
        UnionClause unionClause = new UnionClause(UnionClause.UnionType.UNION, query);
        this.unions.add(unionClause);
        logger.debug("Added UNION query");
        return this;
    }
    
    public SqlQueryBuilder unionAll(SqlQuery query) {
        UnionClause unionClause = new UnionClause(UnionClause.UnionType.UNION_ALL, query);
        this.unions.add(unionClause);
        logger.debug("Added UNION ALL query");
        return this;
    }
    
    // INSERT query methods
    public SqlQueryBuilder insertInto(String table) {
        this.queryType = SqlQuery.QueryType.INSERT;
        this.fromTables.clear();
        this.fromTables.add(table);
        logger.debug("Started INSERT INTO: {}", table);
        return this;
    }
    
    public SqlQueryBuilder columns(String... columns) {
        ValidationUtils.requireNonEmpty(Arrays.asList(columns), "Insert columns cannot be empty");
        this.insertColumns.addAll(Arrays.asList(columns));
        logger.debug("Added INSERT columns: {}", Arrays.toString(columns));
        return this;
    }
    
    public SqlQueryBuilder values(Object... values) {
        ValidationUtils.requireNonEmpty(Arrays.asList(values), "Insert values cannot be empty");
        this.insertValues.addAll(Arrays.asList(values));
        logger.debug("Added INSERT values: {}", Arrays.toString(values));
        return this;
    }
    
    // UPDATE query methods
    public SqlQueryBuilder update(String table) {
        this.queryType = SqlQuery.QueryType.UPDATE;
        this.fromTables.clear();
        this.fromTables.add(table);
        logger.debug("Started UPDATE: {}", table);
        return this;
    }
    
    public SqlQueryBuilder set(String column, Object value) {
        ValidationUtils.requireNonEmpty(column, "Column name cannot be empty");
        this.updateValues.put(column, value);
        logger.debug("Added SET: {} = {}", column, value);
        return this;
    }
    
    public SqlQueryBuilder set(Map<String, Object> values) {
        ValidationUtils.requireNonEmpty(values, "Update values cannot be empty");
        this.updateValues.putAll(values);
        logger.debug("Added SET values: {}", values.keySet());
        return this;
    }
    
    // DELETE query methods
    public SqlQueryBuilder deleteFrom(String table) {
        this.queryType = SqlQuery.QueryType.DELETE;
        this.fromTables.clear();
        this.fromTables.add(table);
        logger.debug("Started DELETE FROM: {}", table);
        return this;
    }
    
    // Parameter methods
    public SqlQueryBuilder parameter(String name, Object value) {
        ValidationUtils.requireNonEmpty(name, "Parameter name cannot be empty");
        this.parameters.put(name, value);
        logger.debug("Added parameter: {} = {}", name, value);
        return this;
    }
    
    public SqlQueryBuilder parameters(Map<String, Object> params) {
        ValidationUtils.requireNonNull(params, "Parameters map cannot be null");
        this.parameters.putAll(params);
        logger.debug("Added parameters: {}", params.keySet());
        return this;
    }
    
    // Raw SQL method
    public SqlQueryBuilder rawSql(String sql) {
        ValidationUtils.requireNonEmpty(sql, "Raw SQL cannot be empty");
        this.rawSql = sql;
        logger.debug("Set raw SQL: {}", sql.substring(0, Math.min(50, sql.length())));
        return this;
    }
    
    // Build method
    public SqlQuery build() {
        if (queryType == null) {
            throw new IllegalStateException("Query type must be specified");
        }
        
        SqlQuery query = new SqlQuery(
            queryType, selectColumns, fromTables, joins, whereConditions,
            groupByColumns, havingConditions, orderByColumns, limit, offset,
            unions, parameters, distinct, insertColumns, insertValues,
            updateValues, rawSql
        );
        
        // Validate the built query
        query.validate();
        
        logger.info("Built {} query: {}", queryType, query.toSql());
        return query;
    }
    
    // Reset method for reusing builder
    public SqlQueryBuilder reset() {
        queryType = null;
        selectColumns.clear();
        fromTables.clear();
        joins.clear();
        whereConditions.clear();
        groupByColumns.clear();
        havingConditions.clear();
        orderByColumns.clear();
        limit = null;
        offset = null;
        unions.clear();
        parameters.clear();
        distinct = false;
        insertColumns.clear();
        insertValues.clear();
        updateValues.clear();
        rawSql = null;
        
        logger.debug("SQL Query Builder reset");
        return this;
    }
    
    // Utility methods for common query patterns
    public SqlQueryBuilder selectFromWhere(String columns, String table, String whereColumn, Object whereValue) {
        return select(columns).from(table).whereEquals(whereColumn, whereValue);
    }
    
    public SqlQueryBuilder selectAllFromWhere(String table, String whereColumn, Object whereValue) {
        return selectAll().from(table).whereEquals(whereColumn, whereValue);
    }
    
    public SqlQueryBuilder countFrom(String table) {
        return selectCount("*").from(table);
    }
    
    public SqlQueryBuilder existsQuery(String table, String whereColumn, Object whereValue) {
        return select("1").from(table).whereEquals(whereColumn, whereValue).limit(1);
    }
    
    // Method to get current state for debugging
    public String getCurrentState() {
        StringBuilder state = new StringBuilder();
        state.append("SqlQueryBuilder State:\n");
        state.append("  Query Type: ").append(queryType).append("\n");
        state.append("  SELECT: ").append(selectColumns).append("\n");
        state.append("  FROM: ").append(fromTables).append("\n");
        state.append("  JOINs: ").append(joins.size()).append("\n");
        state.append("  WHERE: ").append(whereConditions.size()).append(" conditions\n");
        state.append("  GROUP BY: ").append(groupByColumns).append("\n");
        state.append("  HAVING: ").append(havingConditions.size()).append(" conditions\n");
        state.append("  ORDER BY: ").append(orderByColumns.size()).append(" clauses\n");
        state.append("  LIMIT: ").append(limit).append("\n");
        state.append("  OFFSET: ").append(offset).append("\n");
        state.append("  DISTINCT: ").append(distinct).append("\n");
        return state.toString();
    }
}

