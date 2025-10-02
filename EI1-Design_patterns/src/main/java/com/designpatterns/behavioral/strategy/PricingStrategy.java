package com.designpatterns.behavioral.strategy;

import java.math.BigDecimal;

/**
 * Strategy interface for different pricing algorithms
 * Defines the contract for dynamic pricing strategies
 */
public interface PricingStrategy {
    
    /**
     * Calculates the final price for a product given the pricing context
     * 
     * @param product The product to price
     * @param context The pricing context with customer and market information
     * @return The calculated price with all adjustments applied
     */
    BigDecimal calculatePrice(Product product, PricingContext context);
    
    /**
     * Returns the name of this pricing strategy
     * 
     * @return Strategy name for identification and logging
     */
    String getStrategyName();
    
    /**
     * Provides a description of how this strategy works
     * 
     * @return Strategy description
     */
    String getDescription();
    
    /**
     * Indicates if this strategy is suitable for the given product and context
     * 
     * @param product The product to evaluate
     * @param context The pricing context
     * @return true if this strategy should be used, false otherwise
     */
    default boolean isApplicable(Product product, PricingContext context) {
        return true; // By default, all strategies are applicable
    }
    
    /**
     * Gets the priority of this strategy (higher number = higher priority)
     * Used when multiple strategies are applicable
     * 
     * @return Strategy priority (1-10 scale)
     */
    default int getPriority() {
        return 5; // Default medium priority
    }
}

