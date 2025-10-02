package com.designpatterns.behavioral.strategy;

import com.designpatterns.common.ValidationUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;

/**
 * Represents a product in the e-commerce system
 * Immutable product with comprehensive attributes for pricing decisions
 */
public final class Product {
    private final String productId;
    private final String name;
    private final String category;
    private final BigDecimal basePrice;
    private final int stockQuantity;
    private final double weight;
    private final String brand;
    private final LocalDateTime createdDate;
    private final Map<String, Object> attributes;
    private final ProductTier tier;
    private final boolean isPerishable;
    private final int daysUntilExpiry;
    
    public enum ProductTier {
        PREMIUM,
        STANDARD,
        BUDGET,
        CLEARANCE
    }
    
    public Product(String productId, String name, String category, BigDecimal basePrice,
                   int stockQuantity, double weight, String brand, ProductTier tier,
                   boolean isPerishable, int daysUntilExpiry, Map<String, Object> attributes) {
        this.productId = ValidationUtils.requireNonEmpty(productId, "Product ID cannot be empty");
        this.name = ValidationUtils.requireNonEmpty(name, "Product name cannot be empty");
        this.category = ValidationUtils.requireNonEmpty(category, "Category cannot be empty");
        this.basePrice = ValidationUtils.requireNonNull(basePrice, "Base price cannot be null");
        ValidationUtils.requireTrue(basePrice.compareTo(BigDecimal.ZERO) > 0, "Base price must be positive");
        this.stockQuantity = ValidationUtils.requireInRange(stockQuantity, 0, 100000, "Stock quantity out of range");
        this.weight = ValidationUtils.requireInRange(weight, 0.0, 1000.0, "Weight must be between 0 and 1000 kg");
        this.brand = ValidationUtils.requireNonEmpty(brand, "Brand cannot be empty");
        this.tier = ValidationUtils.requireNonNull(tier, "Product tier cannot be null");
        this.isPerishable = isPerishable;
        this.daysUntilExpiry = daysUntilExpiry;
        this.createdDate = LocalDateTime.now();
        this.attributes = attributes != null ? Map.copyOf(attributes) : Map.of();
    }
    
    // Getters
    public String getProductId() { return productId; }
    public String getName() { return name; }
    public String getCategory() { return category; }
    public BigDecimal getBasePrice() { return basePrice; }
    public int getStockQuantity() { return stockQuantity; }
    public double getWeight() { return weight; }
    public String getBrand() { return brand; }
    public LocalDateTime getCreatedDate() { return createdDate; }
    public Map<String, Object> getAttributes() { return attributes; }
    public ProductTier getTier() { return tier; }
    public boolean isPerishable() { return isPerishable; }
    public int getDaysUntilExpiry() { return daysUntilExpiry; }
    
    /**
     * Creates a copy of this product with updated stock quantity
     */
    public Product withStockQuantity(int newStockQuantity) {
        return new Product(productId, name, category, basePrice, newStockQuantity, weight,
                          brand, tier, isPerishable, daysUntilExpiry, attributes);
    }
    
    /**
     * Creates a copy of this product with updated base price
     */
    public Product withBasePrice(BigDecimal newBasePrice) {
        return new Product(productId, name, category, newBasePrice, stockQuantity, weight,
                          brand, tier, isPerishable, daysUntilExpiry, attributes);
    }
    
    /**
     * Checks if the product is low in stock (less than 10 units)
     */
    public boolean isLowStock() {
        return stockQuantity < 10;
    }
    
    /**
     * Checks if the product is out of stock
     */
    public boolean isOutOfStock() {
        return stockQuantity == 0;
    }
    
    /**
     * Checks if the product is expiring soon (within 3 days for perishables)
     */
    public boolean isExpiringSoon() {
        return isPerishable && daysUntilExpiry <= 3;
    }
    
    /**
     * Gets the age of the product in days
     */
    public long getAgeInDays() {
        return java.time.temporal.ChronoUnit.DAYS.between(createdDate.toLocalDate(), LocalDateTime.now().toLocalDate());
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Product product = (Product) obj;
        return Objects.equals(productId, product.productId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(productId);
    }
    
    @Override
    public String toString() {
        return String.format("Product{id='%s', name='%s', category='%s', basePrice=%s, stock=%d, tier=%s}",
                           productId, name, category, basePrice, stockQuantity, tier);
    }
}

