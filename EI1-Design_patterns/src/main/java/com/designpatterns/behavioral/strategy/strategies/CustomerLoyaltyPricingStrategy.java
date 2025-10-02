package com.designpatterns.behavioral.strategy.strategies;

import com.designpatterns.behavioral.strategy.PricingContext;
import com.designpatterns.behavioral.strategy.PricingStrategy;
import com.designpatterns.behavioral.strategy.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Customer loyalty-based pricing strategy
 * Provides personalized pricing based on customer tier and loyalty history
 */
public class CustomerLoyaltyPricingStrategy implements PricingStrategy {
    private static final Logger logger = LoggerFactory.getLogger(CustomerLoyaltyPricingStrategy.class);
    
    @Override
    public BigDecimal calculatePrice(Product product, PricingContext context) {
        logger.debug("Calculating loyalty-based price for customer tier: {}", context.getCustomerTier());
        
        BigDecimal basePrice = product.getBasePrice();
        BigDecimal adjustedPrice = basePrice;
        
        // Apply customer tier discount
        BigDecimal tierDiscount = BigDecimal.valueOf(context.getTierDiscount());
        adjustedPrice = adjustedPrice.multiply(BigDecimal.ONE.subtract(tierDiscount));
        
        // Apply loyalty years discount
        BigDecimal loyaltyDiscount = BigDecimal.valueOf(context.getLoyaltyMultiplier());
        adjustedPrice = adjustedPrice.multiply(BigDecimal.ONE.subtract(loyaltyDiscount));
        
        // Apply product tier adjustments
        adjustedPrice = applyProductTierAdjustment(adjustedPrice, product, context);
        
        // Apply special customer benefits
        adjustedPrice = applySpecialBenefits(adjustedPrice, product, context);
        
        // Apply regional pricing adjustments
        adjustedPrice = applyRegionalAdjustment(adjustedPrice, context);
        
        // Round to 2 decimal places
        adjustedPrice = adjustedPrice.setScale(2, RoundingMode.HALF_UP);
        
        logger.info("Loyalty-based pricing: {} -> {} (tier: {}, loyalty: {} years)",
                   basePrice, adjustedPrice, context.getCustomerTier(), context.getCustomerLoyaltyYears());
        
        return adjustedPrice;
    }
    
    /**
     * Applies product tier-specific adjustments for loyal customers
     */
    private BigDecimal applyProductTierAdjustment(BigDecimal price, Product product, PricingContext context) {
        BigDecimal adjustment = BigDecimal.ONE;
        
        switch (product.getTier()) {
            case PREMIUM -> {
                // VIP and Premium customers get better deals on premium products
                if (context.getCustomerTier() == PricingContext.CustomerTier.VIP) {
                    adjustment = new BigDecimal("0.85"); // Additional 15% off premium products
                } else if (context.getCustomerTier() == PricingContext.CustomerTier.PREMIUM) {
                    adjustment = new BigDecimal("0.90"); // Additional 10% off premium products
                }
            }
            case STANDARD -> {
                // Standard pricing for standard products
                adjustment = BigDecimal.ONE;
            }
            case BUDGET -> {
                // Minimal additional discounts on budget products
                if (context.getCustomerTier() == PricingContext.CustomerTier.VIP) {
                    adjustment = new BigDecimal("0.95"); // Additional 5% off budget products
                }
            }
            case CLEARANCE -> {
                // No additional discounts on clearance items
                adjustment = BigDecimal.ONE;
            }
        }
        
        return price.multiply(adjustment);
    }
    
    /**
     * Applies special benefits for high-tier customers
     */
    private BigDecimal applySpecialBenefits(BigDecimal price, Product product, PricingContext context) {
        BigDecimal finalPrice = price;
        
        // VIP customers get free shipping equivalent discount
        if (context.getCustomerTier() == PricingContext.CustomerTier.VIP) {
            BigDecimal shippingDiscount = calculateShippingDiscount(product);
            finalPrice = finalPrice.subtract(shippingDiscount);
            logger.debug("VIP shipping discount applied: {}", shippingDiscount);
        }
        
        // Long-term customers (10+ years) get birthday month discount
        if (context.getCustomerLoyaltyYears() >= 10) {
            // Simulate birthday month (for demo, apply randomly)
            if (isBirthdayMonth(context)) {
                finalPrice = finalPrice.multiply(new BigDecimal("0.9")); // 10% birthday discount
                logger.debug("Birthday month discount applied");
            }
        }
        
        // Premium customers get early access pricing on new products
        if (context.getCustomerTier() == PricingContext.CustomerTier.PREMIUM && 
            product.getAgeInDays() <= 30) {
            finalPrice = finalPrice.multiply(new BigDecimal("0.95")); // 5% early access discount
            logger.debug("Early access discount applied for new product");
        }
        
        return finalPrice;
    }
    
    /**
     * Calculates shipping discount equivalent based on product weight
     */
    private BigDecimal calculateShippingDiscount(Product product) {
        // Simplified shipping cost calculation
        double weight = product.getWeight();
        if (weight <= 1.0) return new BigDecimal("5.99");
        if (weight <= 5.0) return new BigDecimal("9.99");
        if (weight <= 10.0) return new BigDecimal("15.99");
        return new BigDecimal("25.99");
    }
    
    /**
     * Simulates birthday month check (for demo purposes)
     */
    private boolean isBirthdayMonth(PricingContext context) {
        // In a real system, this would check against customer profile
        // For demo, we'll use customer ID hash to simulate
        return context.getCustomerId() != null && 
               context.getCustomerId().hashCode() % 12 == context.getRequestTime().getMonthValue() % 12;
    }
    
    /**
     * Applies regional pricing adjustments
     */
    private BigDecimal applyRegionalAdjustment(BigDecimal price, PricingContext context) {
        String region = context.getRegion().toLowerCase();
        
        // Regional cost of living adjustments
        BigDecimal regionalMultiplier = switch (region) {
            case "north_america", "europe" -> new BigDecimal("1.0"); // Base pricing
            case "asia_pacific" -> new BigDecimal("0.85"); // 15% lower for emerging markets
            case "latin_america" -> new BigDecimal("0.80"); // 20% lower
            case "africa" -> new BigDecimal("0.75"); // 25% lower
            default -> new BigDecimal("1.0");
        };
        
        if (!regionalMultiplier.equals(BigDecimal.ONE)) {
            logger.debug("Regional adjustment applied for {}: {}", region, regionalMultiplier);
        }
        
        return price.multiply(regionalMultiplier);
    }
    
    @Override
    public String getStrategyName() {
        return "Customer Loyalty Pricing";
    }
    
    @Override
    public String getDescription() {
        return "Provides personalized pricing based on customer tier, loyalty years, and special benefits. " +
               "Includes tier-based discounts, loyalty rewards, regional adjustments, and exclusive offers " +
               "for high-value customers.";
    }
    
    @Override
    public boolean isApplicable(Product product, PricingContext context) {
        // This strategy is most effective for known customers with loyalty history
        return context.getCustomerId() != null && 
               (context.getCustomerTier() != PricingContext.CustomerTier.NEW_CUSTOMER ||
                context.getCustomerLoyaltyYears() > 0);
    }
    
    @Override
    public int getPriority() {
        return 7; // High priority for loyal customers
    }
}

