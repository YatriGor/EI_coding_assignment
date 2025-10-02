package com.designpatterns.behavioral.strategy;

import com.designpatterns.behavioral.strategy.strategies.ClearancePricingStrategy;
import com.designpatterns.behavioral.strategy.strategies.CustomerLoyaltyPricingStrategy;
import com.designpatterns.behavioral.strategy.strategies.DemandBasedPricingStrategy;
import com.designpatterns.common.ValidationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Demonstration of Strategy Pattern with Dynamic Pricing Engine
 * Shows how different pricing strategies are selected and applied
 */
public class PricingDemo {
    private static final Logger logger = LoggerFactory.getLogger(PricingDemo.class);
    
    private final DynamicPricingEngine pricingEngine;
    
    public PricingDemo() {
        this.pricingEngine = new DynamicPricingEngine();
        initializeStrategies();
        logger.info("Pricing Demo initialized with {} strategies", pricingEngine.getStrategyCount());
    }
    
    private void initializeStrategies() {
        // Register all pricing strategies
        pricingEngine.registerStrategy(new DemandBasedPricingStrategy());
        pricingEngine.registerStrategy(new CustomerLoyaltyPricingStrategy());
        pricingEngine.registerStrategy(new ClearancePricingStrategy());
        
        // Set default strategy
        pricingEngine.setDefaultStrategy(new DemandBasedPricingStrategy());
    }
    
    public void runDemo(Scanner scanner) {
        logger.info("=== Dynamic Pricing Engine Strategy Pattern Demo ===");
        
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
        System.out.println("\n💰 Dynamic Pricing Engine Control Panel:");
        System.out.println("1. Calculate Price for Sample Product");
        System.out.println("2. Compare All Pricing Strategies");
        System.out.println("3. Create Custom Product and Price");
        System.out.println("4. Run Pricing Scenarios");
        System.out.println("5. View Strategy Information");
        System.out.println("6. Clear Price Cache");
        System.out.println("7. Run Automated Pricing Tests");
        System.out.println("0. Back to Main Menu");
        System.out.print("Choose an option: ");
    }
    
    private boolean handleMenuChoice(String choice, Scanner scanner) {
        switch (choice) {
            case "1" -> calculateSamplePrice(scanner);
            case "2" -> compareAllStrategies(scanner);
            case "3" -> createCustomProductAndPrice(scanner);
            case "4" -> runPricingScenarios();
            case "5" -> viewStrategyInformation();
            case "6" -> clearPriceCache();
            case "7" -> runAutomatedTests();
            case "0" -> {
                return false;
            }
            default -> System.out.println("❌ Invalid option. Please try again.");
        }
        return true;
    }
    
    private void calculateSamplePrice(Scanner scanner) {
        // Create sample products
        Product[] sampleProducts = createSampleProducts();
        
        System.out.println("\nAvailable Sample Products:");
        for (int i = 0; i < sampleProducts.length; i++) {
            Product p = sampleProducts[i];
            System.out.printf("%d. %s - %s (Base: $%s, Stock: %d, Tier: %s)%n",
                             i + 1, p.getName(), p.getCategory(), p.getBasePrice(), 
                             p.getStockQuantity(), p.getTier());
        }
        
        System.out.print("Select a product (1-" + sampleProducts.length + "): ");
        int productIndex = Integer.parseInt(scanner.nextLine().trim()) - 1;
        ValidationUtils.requireInRange(productIndex, 0, sampleProducts.length - 1, "Invalid product selection");
        
        Product selectedProduct = sampleProducts[productIndex];
        PricingContext context = createPricingContext(scanner);
        
        BigDecimal finalPrice = pricingEngine.calculatePrice(selectedProduct, context);
        
        System.out.println("\n💰 Pricing Result:");
        System.out.printf("Product: %s%n", selectedProduct.getName());
        System.out.printf("Base Price: $%s%n", selectedProduct.getBasePrice());
        System.out.printf("Final Price: $%s%n", finalPrice);
        System.out.printf("Discount: $%s (%.1f%%)%n", 
                         selectedProduct.getBasePrice().subtract(finalPrice),
                         selectedProduct.getBasePrice().subtract(finalPrice)
                             .divide(selectedProduct.getBasePrice(), 4, java.math.RoundingMode.HALF_UP)
                             .multiply(new BigDecimal("100")).doubleValue());
    }
    
    private void compareAllStrategies(Scanner scanner) {
        Product[] sampleProducts = createSampleProducts();
        
        System.out.println("\nSelect a product for strategy comparison:");
        for (int i = 0; i < sampleProducts.length; i++) {
            System.out.printf("%d. %s%n", i + 1, sampleProducts[i].getName());
        }
        
        System.out.print("Select a product (1-" + sampleProducts.length + "): ");
        int productIndex = Integer.parseInt(scanner.nextLine().trim()) - 1;
        ValidationUtils.requireInRange(productIndex, 0, sampleProducts.length - 1, "Invalid product selection");
        
        Product selectedProduct = sampleProducts[productIndex];
        PricingContext context = createPricingContext(scanner);
        
        DynamicPricingEngine.PricingComparison comparison = pricingEngine.compareStrategies(selectedProduct, context);
        
        System.out.println("\n📊 Strategy Comparison Results:");
        System.out.println(comparison.toString());
    }
    
    private void createCustomProductAndPrice(Scanner scanner) {
        System.out.println("\n🛍️ Create Custom Product:");
        
        System.out.print("Product Name: ");
        String name = ValidationUtils.requireNonEmpty(scanner.nextLine().trim(), "Product name cannot be empty");
        
        System.out.print("Category: ");
        String category = ValidationUtils.requireNonEmpty(scanner.nextLine().trim(), "Category cannot be empty");
        
        System.out.print("Base Price ($): ");
        BigDecimal basePrice = new BigDecimal(scanner.nextLine().trim());
        ValidationUtils.requireTrue(basePrice.compareTo(BigDecimal.ZERO) > 0, "Price must be positive");
        
        System.out.print("Stock Quantity: ");
        int stock = Integer.parseInt(scanner.nextLine().trim());
        ValidationUtils.requireInRange(stock, 0, 10000, "Invalid stock quantity");
        
        System.out.print("Weight (kg): ");
        double weight = Double.parseDouble(scanner.nextLine().trim());
        
        System.out.print("Brand: ");
        String brand = ValidationUtils.requireNonEmpty(scanner.nextLine().trim(), "Brand cannot be empty");
        
        System.out.println("Product Tier (1=PREMIUM, 2=STANDARD, 3=BUDGET, 4=CLEARANCE): ");
        int tierChoice = Integer.parseInt(scanner.nextLine().trim());
        Product.ProductTier tier = switch (tierChoice) {
            case 1 -> Product.ProductTier.PREMIUM;
            case 2 -> Product.ProductTier.STANDARD;
            case 3 -> Product.ProductTier.BUDGET;
            case 4 -> Product.ProductTier.CLEARANCE;
            default -> Product.ProductTier.STANDARD;
        };
        
        System.out.print("Is Perishable? (y/n): ");
        boolean isPerishable = scanner.nextLine().trim().toLowerCase().startsWith("y");
        
        int daysUntilExpiry = 0;
        if (isPerishable) {
            System.out.print("Days until expiry: ");
            daysUntilExpiry = Integer.parseInt(scanner.nextLine().trim());
        }
        
        Product customProduct = new Product(
            "CUSTOM_" + System.currentTimeMillis(),
            name, category, basePrice, stock, weight, brand, tier,
            isPerishable, daysUntilExpiry, Map.of("custom", true)
        );
        
        PricingContext context = createPricingContext(scanner);
        BigDecimal finalPrice = pricingEngine.calculatePrice(customProduct, context);
        
        System.out.println("\n💰 Custom Product Pricing:");
        System.out.printf("Product: %s%n", customProduct.getName());
        System.out.printf("Base Price: $%s%n", customProduct.getBasePrice());
        System.out.printf("Final Price: $%s%n", finalPrice);
    }
    
    private PricingContext createPricingContext(Scanner scanner) {
        System.out.println("\n📋 Create Pricing Context:");
        
        System.out.print("Customer ID (or press Enter for anonymous): ");
        String customerId = scanner.nextLine().trim();
        if (customerId.isEmpty()) customerId = null;
        
        System.out.println("Customer Tier (1=VIP, 2=PREMIUM, 3=STANDARD, 4=NEW): ");
        int tierChoice = Integer.parseInt(scanner.nextLine().trim());
        PricingContext.CustomerTier customerTier = switch (tierChoice) {
            case 1 -> PricingContext.CustomerTier.VIP;
            case 2 -> PricingContext.CustomerTier.PREMIUM;
            case 3 -> PricingContext.CustomerTier.STANDARD;
            case 4 -> PricingContext.CustomerTier.NEW_CUSTOMER;
            default -> PricingContext.CustomerTier.STANDARD;
        };
        
        System.out.print("Customer Loyalty Years: ");
        int loyaltyYears = Integer.parseInt(scanner.nextLine().trim());
        
        System.out.print("Region (e.g., north_america, europe, asia_pacific): ");
        String region = ValidationUtils.requireNonEmpty(scanner.nextLine().trim(), "Region cannot be empty");
        
        System.out.print("Competitor Price ($): ");
        double competitorPrice = Double.parseDouble(scanner.nextLine().trim());
        
        System.out.print("Demand Level (1-10): ");
        int demandLevel = Integer.parseInt(scanner.nextLine().trim());
        ValidationUtils.requireInRange(demandLevel, 1, 10, "Demand level must be 1-10");
        
        System.out.print("Is Special Event? (y/n): ");
        boolean isSpecialEvent = scanner.nextLine().trim().toLowerCase().startsWith("y");
        
        String eventType = null;
        if (isSpecialEvent) {
            System.out.print("Event Type (e.g., black_friday, christmas): ");
            eventType = scanner.nextLine().trim();
        }
        
        System.out.print("Quantity: ");
        int quantity = Integer.parseInt(scanner.nextLine().trim());
        ValidationUtils.requireInRange(quantity, 1, 1000, "Quantity must be 1-1000");
        
        return new PricingContext(customerId, customerTier, loyaltyYears, region,
                                 competitorPrice, demandLevel, isSpecialEvent, eventType,
                                 quantity, Map.of("demo", true));
    }
    
    private void runPricingScenarios() {
        System.out.println("\n🎯 Running Predefined Pricing Scenarios...");
        
        Product[] products = createSampleProducts();
        PricingContext[] scenarios = createPricingScenarios();
        
        for (int i = 0; i < Math.min(products.length, scenarios.length); i++) {
            Product product = products[i];
            PricingContext context = scenarios[i];
            
            BigDecimal price = pricingEngine.calculatePrice(product, context);
            
            System.out.printf("\n📦 Scenario %d:%n", i + 1);
            System.out.printf("Product: %s (Base: $%s)%n", product.getName(), product.getBasePrice());
            System.out.printf("Context: %s%n", context.toString());
            System.out.printf("Final Price: $%s%n", price);
        }
        
        System.out.println("\n✅ All scenarios completed");
    }
    
    private void viewStrategyInformation() {
        System.out.println("\n📋 Strategy Information:");
        System.out.println(pricingEngine.getStrategyInfo());
    }
    
    private void clearPriceCache() {
        pricingEngine.clearPriceCache();
        System.out.println("✅ Price cache cleared");
    }
    
    private void runAutomatedTests() {
        System.out.println("\n🤖 Running Automated Pricing Tests...");
        
        Product[] products = createSampleProducts();
        int testCount = 20;
        
        for (int i = 0; i < testCount; i++) {
            Product randomProduct = products[ThreadLocalRandom.current().nextInt(products.length)];
            PricingContext randomContext = createRandomPricingContext();
            
            BigDecimal price = pricingEngine.calculatePrice(randomProduct, randomContext);
            
            System.out.printf("Test %d: %s -> $%s%n", i + 1, randomProduct.getName(), price);
        }
        
        System.out.printf("\n✅ Completed %d automated tests%n", testCount);
        System.out.printf("Total requests processed: %d%n", pricingEngine.getPricingRequestCount());
        System.out.printf("Cache size: %d%n", pricingEngine.getCacheSize());
    }
    
    private Product[] createSampleProducts() {
        return new Product[] {
            new Product("LAPTOP_001", "Gaming Laptop", "Electronics", new BigDecimal("1299.99"),
                       15, 2.5, "TechBrand", Product.ProductTier.PREMIUM, false, 0,
                       Map.of("warranty", "2 years")),
            
            new Product("MILK_001", "Organic Milk", "Dairy", new BigDecimal("4.99"),
                       50, 1.0, "FarmFresh", Product.ProductTier.STANDARD, true, 5,
                       Map.of("organic", true)),
            
            new Product("SHIRT_001", "Cotton T-Shirt", "Clothing", new BigDecimal("19.99"),
                       200, 0.2, "FashionCo", Product.ProductTier.BUDGET, false, 0,
                       Map.of("material", "100% cotton")),
            
            new Product("PHONE_001", "Smartphone", "Electronics", new BigDecimal("899.99"),
                       8, 0.18, "MobileTech", Product.ProductTier.PREMIUM, false, 0,
                       Map.of("storage", "128GB")),
            
            new Product("BREAD_001", "Artisan Bread", "Bakery", new BigDecimal("6.99"),
                       25, 0.5, "LocalBakery", Product.ProductTier.STANDARD, true, 2,
                       Map.of("handmade", true)),
            
            new Product("CLEARANCE_001", "Last Season Jacket", "Clothing", new BigDecimal("79.99"),
                       150, 0.8, "OutdoorGear", Product.ProductTier.CLEARANCE, false, 0,
                       Map.of("season", "last_winter"))
        };
    }
    
    private PricingContext[] createPricingScenarios() {
        return new PricingContext[] {
            new PricingContext("VIP_001", PricingContext.CustomerTier.VIP, 8, "north_america",
                             1199.99, 9, false, null, 1, Map.of("scenario", "vip_high_demand")),
            
            new PricingContext("CUST_002", PricingContext.CustomerTier.STANDARD, 2, "europe",
                             4.50, 3, false, null, 5, Map.of("scenario", "bulk_low_demand")),
            
            new PricingContext(null, PricingContext.CustomerTier.NEW_CUSTOMER, 0, "asia_pacific",
                             18.99, 5, true, "flash_sale", 10, Map.of("scenario", "new_customer_event")),
            
            new PricingContext("PREMIUM_003", PricingContext.CustomerTier.PREMIUM, 5, "north_america",
                             849.99, 8, false, null, 2, Map.of("scenario", "premium_high_demand")),
            
            new PricingContext("LOYAL_004", PricingContext.CustomerTier.STANDARD, 12, "europe",
                             6.50, 2, false, null, 1, Map.of("scenario", "loyal_customer")),
            
            new PricingContext("BULK_005", PricingContext.CustomerTier.STANDARD, 3, "north_america",
                             75.99, 1, true, "clearance_event", 50, Map.of("scenario", "bulk_clearance"))
        };
    }
    
    private PricingContext createRandomPricingContext() {
        String[] regions = {"north_america", "europe", "asia_pacific", "latin_america"};
        PricingContext.CustomerTier[] tiers = PricingContext.CustomerTier.values();
        String[] events = {"black_friday", "christmas", "summer_sale", "flash_sale"};
        
        ThreadLocalRandom random = ThreadLocalRandom.current();
        
        return new PricingContext(
            "CUST_" + random.nextInt(1000),
            tiers[random.nextInt(tiers.length)],
            random.nextInt(15),
            regions[random.nextInt(regions.length)],
            random.nextDouble(10.0, 2000.0),
            random.nextInt(1, 11),
            random.nextBoolean(),
            random.nextBoolean() ? events[random.nextInt(events.length)] : null,
            random.nextInt(1, 21),
            Map.of("automated", true)
        );
    }
}

