package com.designpatterns.creational.builder;

import com.designpatterns.common.ValidationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Scanner;

/**
 * Demonstration of Builder Pattern with SQL Query Builder
 * Shows how complex SQL queries are constructed step by step
 */
public class QueryBuilderDemo {
    private static final Logger logger = LoggerFactory.getLogger(QueryBuilderDemo.class);
    
    private final SqlQueryBuilder queryBuilder;
    
    public QueryBuilderDemo() {
        this.queryBuilder = new SqlQueryBuilder();
        logger.info("Query Builder Demo initialized");
    }
    
    public void runDemo(Scanner scanner) {
        logger.info("=== SQL Query Builder Pattern Demo ===");
        
        boolean running = true;
        while (running) {
            displayMenu();
            
            try {
                String choice = scanner.nextLine().trim();
                running = handleMenuChoice(choice, scanner);
            } catch (Exception e) {
                logger.error("Error processing menu choice: {}", e.getMessage());
                System.out.println("❌ Error: " + e.getMessage());
            }
        }
    }
    
    private void displayMenu() {
        System.out.println("\n🔧 SQL Query Builder Control Panel:");
        System.out.println("1. Build Simple SELECT Query");
        System.out.println("2. Build Complex SELECT with JOINs");
        System.out.println("3. Build Aggregate Query with GROUP BY");
        System.out.println("4. Build INSERT Query");
        System.out.println("5. Build UPDATE Query");
        System.out.println("6. Build DELETE Query");
        System.out.println("7. Build Custom Query (Interactive)");
        System.out.println("8. Show Predefined Query Examples");
        System.out.println("9. Query Builder Performance Test");
        System.out.println("0. Back to Main Menu");
        System.out.print("Choose an option: ");
    }
    
    private boolean handleMenuChoice(String choice, Scanner scanner) {
        switch (choice) {
            case "1" -> buildSimpleSelectQuery(scanner);
            case "2" -> buildComplexSelectQuery();
            case "3" -> buildAggregateQuery();
            case "4" -> buildInsertQuery(scanner);
            case "5" -> buildUpdateQuery(scanner);
            case "6" -> buildDeleteQuery(scanner);
            case "7" -> buildCustomQuery(scanner);
            case "8" -> showPredefinedExamples();
            case "9" -> runPerformanceTest();
            case "0" -> {
                return false;
            }
            default -> System.out.println("❌ Invalid option. Please try again.");
        }
        return true;
    }
    
    private void buildSimpleSelectQuery(Scanner scanner) {
        System.out.println("\n📋 Building Simple SELECT Query");
        
        System.out.print("Enter table name: ");
        String table = ValidationUtils.requireNonEmpty(scanner.nextLine().trim(), "Table name cannot be empty");
        
        System.out.print("Enter columns (comma-separated, or * for all): ");
        String columnsInput = scanner.nextLine().trim();
        String[] columns = columnsInput.equals("*") ? new String[]{"*"} : columnsInput.split(",");
        
        System.out.print("Add WHERE condition? (y/n): ");
        boolean addWhere = scanner.nextLine().trim().toLowerCase().startsWith("y");
        
        SqlQuery query = queryBuilder.reset()
            .select(columns)
            .from(table);
        
        if (addWhere) {
            System.out.print("WHERE column: ");
            String whereColumn = scanner.nextLine().trim();
            System.out.print("WHERE value: ");
            String whereValue = scanner.nextLine().trim();
            query = queryBuilder.whereEquals(whereColumn, whereValue).build();
        } else {
            query = queryBuilder.build();
        }
        
        displayQueryResult(query);
    }
    
    private void buildComplexSelectQuery() {
        System.out.println("\n🔗 Building Complex SELECT Query with JOINs");
        
        SqlQuery query = queryBuilder.reset()
            .select("u.username", "u.email", "p.title", "p.content", "c.name AS category")
            .from("users u")
            .innerJoin("posts p", "u.id = p.user_id")
            .leftJoin("categories c", "p.category_id = c.id")
            .where("u.active", WhereCondition.Operator.EQUALS, true)
            .and("p.published_at", WhereCondition.Operator.IS_NOT_NULL, null)
            .orderByDesc("p.published_at")
            .limit(10)
            .build();
        
        System.out.println("Built complex query with multiple JOINs:");
        displayQueryResult(query);
    }
    
    private void buildAggregateQuery() {
        System.out.println("\n📊 Building Aggregate Query with GROUP BY");
        
        SqlQuery query = queryBuilder.reset()
            .select("category_id", "COUNT(*) as post_count", "AVG(view_count) as avg_views")
            .from("posts")
            .where("published_at", WhereCondition.Operator.GREATER_THAN, "2023-01-01")
            .groupBy("category_id")
            .havingCount("*", WhereCondition.Operator.GREATER_THAN, 5)
            .orderByDesc("post_count")
            .build();
        
        System.out.println("Built aggregate query with GROUP BY and HAVING:");
        displayQueryResult(query);
    }
    
    private void buildInsertQuery(Scanner scanner) {
        System.out.println("\n➕ Building INSERT Query");
        
        System.out.print("Enter table name: ");
        String table = ValidationUtils.requireNonEmpty(scanner.nextLine().trim(), "Table name cannot be empty");
        
        System.out.print("Enter columns (comma-separated): ");
        String columnsInput = ValidationUtils.requireNonEmpty(scanner.nextLine().trim(), "Columns cannot be empty");
        String[] columns = columnsInput.split(",");
        
        System.out.print("Enter values (comma-separated): ");
        String valuesInput = ValidationUtils.requireNonEmpty(scanner.nextLine().trim(), "Values cannot be empty");
        String[] values = valuesInput.split(",");
        
        if (columns.length != values.length) {
            throw new IllegalArgumentException("Number of columns must match number of values");
        }
        
        SqlQuery query = queryBuilder.reset()
            .insertInto(table)
            .columns(columns)
            .values((Object[]) values)
            .build();
        
        displayQueryResult(query);
    }
    
    private void buildUpdateQuery(Scanner scanner) {
        System.out.println("\n✏️ Building UPDATE Query");
        
        System.out.print("Enter table name: ");
        String table = ValidationUtils.requireNonEmpty(scanner.nextLine().trim(), "Table name cannot be empty");
        
        System.out.print("Enter column to update: ");
        String column = ValidationUtils.requireNonEmpty(scanner.nextLine().trim(), "Column cannot be empty");
        
        System.out.print("Enter new value: ");
        String value = scanner.nextLine().trim();
        
        System.out.print("Enter WHERE column: ");
        String whereColumn = ValidationUtils.requireNonEmpty(scanner.nextLine().trim(), "WHERE column cannot be empty");
        
        System.out.print("Enter WHERE value: ");
        String whereValue = scanner.nextLine().trim();
        
        SqlQuery query = queryBuilder.reset()
            .update(table)
            .set(column, value)
            .whereEquals(whereColumn, whereValue)
            .build();
        
        displayQueryResult(query);
    }
    
    private void buildDeleteQuery(Scanner scanner) {
        System.out.println("\n🗑️ Building DELETE Query");
        
        System.out.print("Enter table name: ");
        String table = ValidationUtils.requireNonEmpty(scanner.nextLine().trim(), "Table name cannot be empty");
        
        System.out.print("Enter WHERE column: ");
        String whereColumn = ValidationUtils.requireNonEmpty(scanner.nextLine().trim(), "WHERE column cannot be empty");
        
        System.out.print("Enter WHERE value: ");
        String whereValue = scanner.nextLine().trim();
        
        System.out.print("Are you sure you want to delete? (yes/no): ");
        String confirmation = scanner.nextLine().trim().toLowerCase();
        
        if (!confirmation.equals("yes")) {
            System.out.println("❌ DELETE operation cancelled");
            return;
        }
        
        SqlQuery query = queryBuilder.reset()
            .deleteFrom(table)
            .whereEquals(whereColumn, whereValue)
            .build();
        
        displayQueryResult(query);
        System.out.println("⚠️ This is a DELETE query - use with caution!");
    }
    
    private void buildCustomQuery(Scanner scanner) {
        System.out.println("\n🛠️ Interactive Query Builder");
        
        queryBuilder.reset();
        boolean building = true;
        
        while (building) {
            System.out.println("\nCurrent query state:");
            System.out.println(queryBuilder.getCurrentState());
            
            System.out.println("\nAvailable operations:");
            System.out.println("1. SELECT columns");
            System.out.println("2. FROM table");
            System.out.println("3. JOIN table");
            System.out.println("4. WHERE condition");
            System.out.println("5. GROUP BY");
            System.out.println("6. ORDER BY");
            System.out.println("7. LIMIT");
            System.out.println("8. Build and finish");
            System.out.println("9. Reset and start over");
            System.out.print("Choose operation: ");
            
            String operation = scanner.nextLine().trim();
            
            try {
                switch (operation) {
                    case "1" -> {
                        System.out.print("Enter columns (comma-separated): ");
                        String columns = scanner.nextLine().trim();
                        queryBuilder.select(columns.split(","));
                    }
                    case "2" -> {
                        System.out.print("Enter table name: ");
                        String table = scanner.nextLine().trim();
                        queryBuilder.from(table);
                    }
                    case "3" -> {
                        System.out.print("Join type (INNER/LEFT/RIGHT): ");
                        String joinType = scanner.nextLine().trim().toUpperCase();
                        System.out.print("Table to join: ");
                        String table = scanner.nextLine().trim();
                        System.out.print("Join condition: ");
                        String condition = scanner.nextLine().trim();
                        
                        switch (joinType) {
                            case "INNER" -> queryBuilder.innerJoin(table, condition);
                            case "LEFT" -> queryBuilder.leftJoin(table, condition);
                            case "RIGHT" -> queryBuilder.rightJoin(table, condition);
                            default -> System.out.println("Invalid join type");
                        }
                    }
                    case "4" -> {
                        System.out.print("Column name: ");
                        String column = scanner.nextLine().trim();
                        System.out.print("Operator (EQUALS/GREATER_THAN/LIKE/etc.): ");
                        String op = scanner.nextLine().trim().toUpperCase();
                        System.out.print("Value: ");
                        String value = scanner.nextLine().trim();
                        
                        WhereCondition.Operator operator = WhereCondition.Operator.valueOf(op);
                        queryBuilder.where(column, operator, value);
                    }
                    case "5" -> {
                        System.out.print("GROUP BY columns (comma-separated): ");
                        String columns = scanner.nextLine().trim();
                        queryBuilder.groupBy(columns.split(","));
                    }
                    case "6" -> {
                        System.out.print("ORDER BY column: ");
                        String column = scanner.nextLine().trim();
                        System.out.print("Direction (ASC/DESC): ");
                        String direction = scanner.nextLine().trim().toUpperCase();
                        
                        if ("DESC".equals(direction)) {
                            queryBuilder.orderByDesc(column);
                        } else {
                            queryBuilder.orderByAsc(column);
                        }
                    }
                    case "7" -> {
                        System.out.print("LIMIT value: ");
                        int limit = Integer.parseInt(scanner.nextLine().trim());
                        queryBuilder.limit(limit);
                    }
                    case "8" -> {
                        SqlQuery query = queryBuilder.build();
                        displayQueryResult(query);
                        building = false;
                    }
                    case "9" -> {
                        queryBuilder.reset();
                        System.out.println("✅ Builder reset");
                    }
                    default -> System.out.println("❌ Invalid operation");
                }
            } catch (Exception e) {
                System.out.println("❌ Error: " + e.getMessage());
            }
        }
    }
    
    private void showPredefinedExamples() {
        System.out.println("\n📚 Predefined Query Examples");
        
        // Example 1: User posts with categories
        SqlQuery example1 = new SqlQueryBuilder()
            .select("u.username", "p.title", "c.name AS category", "p.created_at")
            .from("users u")
            .innerJoin("posts p", "u.id = p.user_id")
            .leftJoin("categories c", "p.category_id = c.id")
            .where("u.active", WhereCondition.Operator.EQUALS, true)
            .orderByDesc("p.created_at")
            .limit(20)
            .build();
        
        System.out.println("\n1. User Posts with Categories:");
        displayQueryResult(example1);
        
        // Example 2: Sales report with aggregation
        SqlQuery example2 = new SqlQueryBuilder()
            .select("p.name", "SUM(oi.quantity) AS total_sold", "SUM(oi.price * oi.quantity) AS revenue")
            .from("products p")
            .innerJoin("order_items oi", "p.id = oi.product_id")
            .innerJoin("orders o", "oi.order_id = o.id")
            .where("o.status", WhereCondition.Operator.EQUALS, "completed")
            .and("o.created_at", WhereCondition.Operator.GREATER_THAN, "2023-01-01")
            .groupBy("p.id", "p.name")
            .havingSum("oi.quantity", WhereCondition.Operator.GREATER_THAN, 10)
            .orderByDesc("revenue")
            .build();
        
        System.out.println("\n2. Sales Report with Aggregation:");
        displayQueryResult(example2);
        
        // Example 3: Complex search with multiple conditions
        SqlQuery example3 = new SqlQueryBuilder()
            .selectDistinct("p.id", "p.title", "p.price")
            .from("products p")
            .leftJoin("product_tags pt", "p.id = pt.product_id")
            .leftJoin("tags t", "pt.tag_id = t.id")
            .where("p.active", WhereCondition.Operator.EQUALS, true)
            .and("p.price", WhereCondition.Operator.BETWEEN, new Object[]{10.0, 100.0})
            .and("p.title", WhereCondition.Operator.LIKE, "%laptop%")
            .or("t.name", WhereCondition.Operator.IN, new Object[]{"electronics", "computers"})
            .orderBy("p.price")
            .limitOffset(10, 20)
            .build();
        
        System.out.println("\n3. Complex Product Search:");
        displayQueryResult(example3);
        
        // Example 4: Subquery with UNION
        SqlQuery subquery = new SqlQueryBuilder()
            .select("customer_id", "order_date", "total_amount")
            .from("orders")
            .where("status", WhereCondition.Operator.EQUALS, "pending")
            .build();
        
        SqlQuery example4 = new SqlQueryBuilder()
            .select("customer_id", "order_date", "total_amount")
            .from("orders")
            .where("status", WhereCondition.Operator.EQUALS, "completed")
            .union(subquery)
            .orderBy("order_date")
            .build();
        
        System.out.println("\n4. Orders with UNION:");
        displayQueryResult(example4);
        
        // Example 5: INSERT with multiple values
        SqlQuery example5 = new SqlQueryBuilder()
            .insertInto("users")
            .columns("username", "email", "created_at")
            .values("john_doe", "john@example.com", "2023-12-01")
            .build();
        
        System.out.println("\n5. User INSERT:");
        displayQueryResult(example5);
        
        // Example 6: UPDATE with complex WHERE
        SqlQuery example6 = new SqlQueryBuilder()
            .update("products")
            .set("price", 99.99)
            .set("updated_at", "NOW()")
            .where("category_id", WhereCondition.Operator.EQUALS, 1)
            .and("stock_quantity", WhereCondition.Operator.GREATER_THAN, 0)
            .build();
        
        System.out.println("\n6. Product Price UPDATE:");
        displayQueryResult(example6);
    }
    
    private void runPerformanceTest() {
        System.out.println("\n⚡ Running Query Builder Performance Test...");
        
        int iterations = 1000;
        long startTime = System.currentTimeMillis();
        
        for (int i = 0; i < iterations; i++) {
            SqlQuery query = new SqlQueryBuilder()
                .select("id", "name", "email", "created_at")
                .from("users")
                .where("active", WhereCondition.Operator.EQUALS, true)
                .and("created_at", WhereCondition.Operator.GREATER_THAN, "2023-01-01")
                .orderByDesc("created_at")
                .limit(10)
                .build();
            
            // Simulate query execution by calling toSql()
            String sql = query.toSql();
        }
        
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        
        System.out.printf("✅ Performance Test Results:%n");
        System.out.printf("   Iterations: %d%n", iterations);
        System.out.printf("   Total Time: %d ms%n", duration);
        System.out.printf("   Average Time: %.2f ms per query%n", (double) duration / iterations);
        System.out.printf("   Queries per Second: %.0f%n", (double) iterations / duration * 1000);
        
        // Memory usage test
        Runtime runtime = Runtime.getRuntime();
        long memoryBefore = runtime.totalMemory() - runtime.freeMemory();
        
        SqlQueryBuilder[] builders = new SqlQueryBuilder[100];
        for (int i = 0; i < builders.length; i++) {
            builders[i] = new SqlQueryBuilder()
                .select("*")
                .from("test_table_" + i)
                .where("id", WhereCondition.Operator.GREATER_THAN, i);
        }
        
        long memoryAfter = runtime.totalMemory() - runtime.freeMemory();
        long memoryUsed = memoryAfter - memoryBefore;
        
        System.out.printf("   Memory Usage: %d bytes for 100 builders%n", memoryUsed);
        System.out.printf("   Average Memory per Builder: %d bytes%n", memoryUsed / 100);
    }
    
    private void displayQueryResult(SqlQuery query) {
        System.out.println("\n🔍 Generated SQL Query:");
        System.out.println("─".repeat(50));
        System.out.println(query.toSql());
        System.out.println("─".repeat(50));
        
        Object[] parameters = query.getParameterValues();
        if (parameters.length > 0) {
            System.out.println("📋 Parameters:");
            for (int i = 0; i < parameters.length; i++) {
                System.out.printf("  $%d: %s%n", i + 1, parameters[i]);
            }
        }
        
        System.out.println("📊 Query Info:");
        System.out.printf("  Type: %s%n", query.getQueryType());
        System.out.printf("  Tables: %s%n", query.getFromTables());
        System.out.printf("  Joins: %d%n", query.getJoins().size());
        System.out.printf("  WHERE conditions: %d%n", query.getWhereConditions().size());
        System.out.printf("  Parameters: %d%n", parameters.length);
        
        if (query.getLimit() != null) {
            System.out.printf("  Limit: %d%n", query.getLimit());
        }
        if (query.getOffset() != null) {
            System.out.printf("  Offset: %d%n", query.getOffset());
        }
    }
}

