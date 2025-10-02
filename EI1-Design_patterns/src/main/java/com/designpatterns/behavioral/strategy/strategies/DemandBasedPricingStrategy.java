package com.designpatterns.behavioral.strategy.strategies;

import com.designpatterns.behavioral.strategy.PricingContext;
import com.designpatterns.behavioral.strategy.PricingStrategy;
import com.designpatterns.behavioral.strategy.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Demand-based pricing strategy that adjusts prices based on market demand
 * Implements surge pricing similar to ride-sharing services
 */
public class DemandBasedPricingStrategy implements PricingStrategy {
    private static final Logger logger = LoggerFactory.getLogger(DemandBasedPricingStrategy.class);
    
    private static final BigDecimal MAX_SURGE_MULTIPLIER = new BigDecimal("2.5");
    private static final BigDecimal MIN_DISCOUNT_MULTIPLIER = new BigDecimal("0.7");
    
    @Override
    public BigDecimal calculatePrice(Product product, PricingContext context) {
        logger.debug("Calculating demand-based price for product: {}", product.getProductId());
        
        BigDecimal basePrice = product.getBasePrice();
        BigDecimal adjustedPrice = basePrice;
        
        // Apply demand-based multiplier
        BigDecimal demandMultiplier = calculateDemandMultiplier(context);
        adjustedPrice = adjustedPrice.multiply(demandMultiplier);
        
        // Apply stock-based adjustments
        BigDecimal stockMultiplier = calculateStockMultiplier(product);
        adjustedPrice = adjustedPrice.multiply(stockMultiplier);
        
        // Apply time-based adjustments
        BigDecimal timeMultiplier = calculateTimeMultiplier(context);
        adjustedPrice = adjustedPrice.multiply(timeMultiplier);
        
        // Apply competitor price considerations
        adjustedPrice = adjustCompetitorPrice(adjustedPrice, context);
        
        // Apply quantity discounts for bulk orders
        if (context.isBulkOrder()) {
            BigDecimal bulkDiscount = calculateBulkDiscount(context.getQuantity());
            adjustedPrice = adjustedPrice.multiply(BigDecimal.ONE.subtract(bulkDiscount));
        }
        
        // Ensure price doesn't go below minimum threshold (30% of base price)
        BigDecimal minPrice = basePrice.multiply(new BigDecimal("0.3"));
        if (adjustedPrice.compareTo(minPrice) < 0) {
            adjustedPrice = minPrice;
            logger.debug("Price adjusted to minimum threshold: {}", minPrice);
        }
        
        // Round to 2 decimal places
        adjustedPrice = adjustedPrice.setScale(2, RoundingMode.HALF_UP);
        
        logger.info("Demand-based pricing: {} -> {} (demand level: {}, stock: {})",
                   basePrice, adjustedPrice, context.getDemandLevel(), product.getStockQuantity());
        
        return adjustedPrice;
    }
    
    /**
     * Calculates demand multiplier based on demand level (1-10)
     */
    private BigDecimal calculateDemandMultiplier(PricingContext context) {
        int demandLevel = context.getDemandLevel();
        
        // Linear scaling: demand 1 = 0.7x, demand 5 = 1.0x, demand 10 = 2.5x
        double multiplier = 0.7 + (demandLevel - 1) * 0.2;
        
        BigDecimal demandMultiplier = BigDecimal.valueOf(multiplier);
        
        // Cap the multiplier
        if (demandMultiplier.compareTo(MAX_SURGE_MULTIPLIER) > 0) {
            demandMultiplier = MAX_SURGE_MULTIPLIER;
        }
        if (demandMultiplier.compareTo(MIN_DISCOUNT_MULTIPLIER) < 0) {
            demandMultiplier = MIN_DISCOUNT_MULTIPLIER;
        }
        
        logger.debug("Demand multiplier for level {}: {}", demandLevel, demandMultiplier);
        return demandMultiplier;
    }
    
    /**
     * Calculates stock-based multiplier
     */
    private BigDecimal calculateStockMultiplier(Product product) {
        if (product.isOutOfStock()) {
            return BigDecimal.ZERO; // Can't sell what we don't have
        }
        
        if (product.isLowStock()) {
            // Low stock increases price by up to 20%
            double scarcityMultiplier = 1.0 + (0.2 * (10 - product.getStockQuantity()) / 10.0);
            return BigDecimal.valueOf(scarcityMultiplier);
        }
        
        if (product.getStockQuantity() > 100) {
            // Excess stock reduces price by up to 10%
            return new BigDecimal("0.9");
        }
        
        return BigDecimal.ONE; // Normal stock level
    }
    
    /**
     * Calculates time-based multiplier
     */
    private BigDecimal calculateTimeMultiplier(PricingContext context) {
        BigDecimal multiplier = BigDecimal.ONE;
        
        // Peak hours increase price by 15%
        if (context.isPeakHours()) {
            multiplier = multiplier.multiply(new BigDecimal("1.15"));
        }
        
        // Weekend shopping increases price by 10%
        if (context.isWeekend()) {
            multiplier = multiplier.multiply(new BigDecimal("1.10"));
        }
        
        // Special events increase price by 25%
        if (context.isSpecialEvent()) {
            multiplier = multiplier.multiply(new BigDecimal("1.25"));
            logger.debug("Special event pricing applied: {}", context.getEventType());
        }
        
        return multiplier;
    }
    
    /**
     * Adjusts price based on competitor pricing
     */
    private BigDecimal adjustCompetitorPrice(BigDecimal currentPrice, PricingContext context) {
        BigDecimal competitorPrice = BigDecimal.valueOf(context.getCompetitorPrice());
        
        // If our price is more than 20% higher than competitor, reduce it
        BigDecimal maxAcceptablePrice = competitorPrice.multiply(new BigDecimal("1.20"));
        
        if (currentPrice.compareTo(maxAcceptablePrice) > 0) {
            // Price match with 5% premium
            BigDecimal adjustedPrice = competitorPrice.multiply(new BigDecimal("1.05"));
            logger.debug("Price adjusted due to competitor pricing: {} -> {}", 
                        currentPrice, adjustedPrice);
            return adjustedPrice;
        }
        
        return currentPrice;
    }
    
    /**
     * Calculates bulk discount based on quantity
     */
    private BigDecimal calculateBulkDiscount(int quantity) {
        if (quantity >= 100) return new BigDecimal("0.15"); // 15% discount
        if (quantity >= 50) return new BigDecimal("0.10");  // 10% discount
        if (quantity >= 20) return new BigDecimal("0.05");  // 5% discount
        return BigDecimal.ZERO; // No bulk discount
    }
    
    @Override
    public String getStrategyName() {
        return "Demand-Based Pricing";
    }
    
    @Override
    public String getDescription() {
        return "Adjusts prices dynamically based on market demand, stock levels, time factors, " +
               "and competitor pricing. Implements surge pricing for high-demand periods and " +
               "discounts for low-demand periods.";
    }
    
    @Override
    public boolean isApplicable(Product product, PricingContext context) {
        // This strategy is most effective for products with variable demand
        return !product.isOutOfStock() && 
               (context.isHighDemand() || context.isLowDemand() || product.isLowStock());
    }
    
    @Override
    public int getPriority() {
        return 8; // High priority for demand-sensitive scenarios
    }
}

