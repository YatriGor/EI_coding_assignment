package com.designpatterns.behavioral.strategy;

import com.designpatterns.common.ValidationUtils;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;

/**
 * Context information for pricing decisions
 * Contains all relevant data for dynamic pricing calculations
 */
public final class PricingContext {
    private final String customerId;
    private final CustomerTier customerTier;
    private final int customerLoyaltyYears;
    private final LocalDateTime requestTime;
    private final String region;
    private final double competitorPrice;
    private final int demandLevel; // 1-10 scale
    private final boolean isSpecialEvent;
    private final String eventType;
    private final Map<String, Object> marketConditions;
    private final int quantity;
    
    public enum CustomerTier {
        VIP,
        PREMIUM,
        STANDARD,
        NEW_CUSTOMER
    }
    
    public PricingContext(String customerId, CustomerTier customerTier, int customerLoyaltyYears,
                         String region, double competitorPrice, int demandLevel,
                         boolean isSpecialEvent, String eventType, int quantity,
                         Map<String, Object> marketConditions) {
        this.customerId = customerId; // Can be null for anonymous customers
        this.customerTier = ValidationUtils.requireNonNull(customerTier, "Customer tier cannot be null");
        this.customerLoyaltyYears = ValidationUtils.requireInRange(customerLoyaltyYears, 0, 50, 
                                                                   "Loyalty years must be between 0 and 50");
        this.requestTime = LocalDateTime.now();
        this.region = ValidationUtils.requireNonEmpty(region, "Region cannot be empty");
        this.competitorPrice = ValidationUtils.requireInRange(competitorPrice, 0.0, 100000.0, 
                                                              "Competitor price must be positive");
        this.demandLevel = ValidationUtils.requireInRange(demandLevel, 1, 10, 
                                                         "Demand level must be between 1 and 10");
        this.isSpecialEvent = isSpecialEvent;
        this.eventType = eventType;
        this.quantity = ValidationUtils.requireInRange(quantity, 1, 1000, 
                                                      "Quantity must be between 1 and 1000");
        this.marketConditions = marketConditions != null ? Map.copyOf(marketConditions) : Map.of();
    }
    
    // Getters
    public String getCustomerId() { return customerId; }
    public CustomerTier getCustomerTier() { return customerTier; }
    public int getCustomerLoyaltyYears() { return customerLoyaltyYears; }
    public LocalDateTime getRequestTime() { return requestTime; }
    public String getRegion() { return region; }
    public double getCompetitorPrice() { return competitorPrice; }
    public int getDemandLevel() { return demandLevel; }
    public boolean isSpecialEvent() { return isSpecialEvent; }
    public String getEventType() { return eventType; }
    public Map<String, Object> getMarketConditions() { return marketConditions; }
    public int getQuantity() { return quantity; }
    
    /**
     * Checks if the request is during peak hours (9 AM - 6 PM)
     */
    public boolean isPeakHours() {
        int hour = requestTime.getHour();
        return hour >= 9 && hour <= 18;
    }
    
    /**
     * Checks if the request is during weekend
     */
    public boolean isWeekend() {
        DayOfWeek dayOfWeek = requestTime.getDayOfWeek();
        return dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY;
    }
    
    /**
     * Checks if this is a bulk order (quantity > 10)
     */
    public boolean isBulkOrder() {
        return quantity > 10;
    }
    
    /**
     * Gets the customer loyalty multiplier based on years
     */
    public double getLoyaltyMultiplier() {
        if (customerLoyaltyYears >= 10) return 0.15; // 15% discount
        if (customerLoyaltyYears >= 5) return 0.10;  // 10% discount
        if (customerLoyaltyYears >= 2) return 0.05;  // 5% discount
        return 0.0; // No discount
    }
    
    /**
     * Gets the customer tier discount
     */
    public double getTierDiscount() {
        return switch (customerTier) {
            case VIP -> 0.20;      // 20% discount
            case PREMIUM -> 0.10;   // 10% discount
            case STANDARD -> 0.05;  // 5% discount
            case NEW_CUSTOMER -> 0.0; // No discount
        };
    }
    
    /**
     * Checks if demand is high (level 8-10)
     */
    public boolean isHighDemand() {
        return demandLevel >= 8;
    }
    
    /**
     * Checks if demand is low (level 1-3)
     */
    public boolean isLowDemand() {
        return demandLevel <= 3;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        PricingContext that = (PricingContext) obj;
        return customerLoyaltyYears == that.customerLoyaltyYears &&
               Double.compare(that.competitorPrice, competitorPrice) == 0 &&
               demandLevel == that.demandLevel &&
               isSpecialEvent == that.isSpecialEvent &&
               quantity == that.quantity &&
               Objects.equals(customerId, that.customerId) &&
               customerTier == that.customerTier &&
               Objects.equals(region, that.region) &&
               Objects.equals(eventType, that.eventType);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(customerId, customerTier, customerLoyaltyYears, region, 
                           competitorPrice, demandLevel, isSpecialEvent, eventType, quantity);
    }
    
    @Override
    public String toString() {
        return String.format("PricingContext{customerId='%s', tier=%s, loyalty=%d years, " +
                           "region='%s', demand=%d, quantity=%d, specialEvent=%s}",
                           customerId, customerTier, customerLoyaltyYears, region, 
                           demandLevel, quantity, isSpecialEvent);
    }
}

