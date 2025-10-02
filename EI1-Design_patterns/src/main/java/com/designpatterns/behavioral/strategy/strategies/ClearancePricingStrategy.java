package com.designpatterns.behavioral.strategy.strategies;

import com.designpatterns.behavioral.strategy.PricingContext;
import com.designpatterns.behavioral.strategy.PricingStrategy;
import com.designpatterns.behavioral.strategy.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Clearance pricing strategy for products that need to be moved quickly
 * Handles perishable items, overstocked products, and end-of-lifecycle items
 */
public class ClearancePricingStrategy implements PricingStrategy {
    private static final Logger logger = LoggerFactory.getLogger(ClearancePricingStrategy.class);
    
    private static final BigDecimal MIN_CLEARANCE_PRICE_RATIO = new BigDecimal("0.1"); // 10% of base price
    
    @Override
    public BigDecimal calculatePrice(Product product, PricingContext context) {
        logger.debug("Calculating clearance price for product: {}", product.getProductId());
        
        BigDecimal basePrice = product.getBasePrice();
        BigDecimal clearancePrice = basePrice;
        
        // Apply base clearance discount
        BigDecimal baseClearanceDiscount = calculateBaseClearanceDiscount(product);
        clearancePrice = clearancePrice.multiply(BigDecimal.ONE.subtract(baseClearanceDiscount));
        
        // Apply urgency-based discounts
        clearancePrice = applyUrgencyDiscounts(clearancePrice, product);
        
        // Apply volume-based incentives
        clearancePrice = applyVolumeIncentives(clearancePrice, product, context);
        
        // Apply time-sensitive discounts
        clearancePrice = applyTimeSensitiveDiscounts(clearancePrice, product);
        
        // Ensure minimum price threshold
        BigDecimal minPrice = basePrice.multiply(MIN_CLEARANCE_PRICE_RATIO);
        if (clearancePrice.compareTo(minPrice) < 0) {
            clearancePrice = minPrice;
            logger.debug("Price adjusted to minimum clearance threshold: {}", minPrice);
        }
        
        // Round to 2 decimal places
        clearancePrice = clearancePrice.setScale(2, RoundingMode.HALF_UP);
        
        logger.info("Clearance pricing: {} -> {} (discount: {:.1f}%)",
                   basePrice, clearancePrice, 
                   (basePrice.subtract(clearancePrice).divide(basePrice, 4, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal("100"))).doubleValue());
        
        return clearancePrice;
    }
    
    /**
     * Calculates base clearance discount based on product characteristics
     */
    private BigDecimal calculateBaseClearanceDiscount(Product product) {
        BigDecimal discount = BigDecimal.ZERO;
        
        // Product tier-based discounts
        switch (product.getTier()) {
            case CLEARANCE -> discount = new BigDecimal("0.60"); // 60% off clearance items
            case BUDGET -> discount = new BigDecimal("0.40");    // 40% off budget items
            case STANDARD -> discount = new BigDecimal("0.30");  // 30% off standard items
            case PREMIUM -> discount = new BigDecimal("0.20");   // 20% off premium items
        }
        
        // Age-based additional discounts
        long ageInDays = product.getAgeInDays();
        if (ageInDays > 365) {
            discount = discount.add(new BigDecimal("0.15")); // Additional 15% for old products
        } else if (ageInDays > 180) {
            discount = discount.add(new BigDecimal("0.10")); // Additional 10% for 6+ month old products
        } else if (ageInDays > 90) {
            discount = discount.add(new BigDecimal("0.05")); // Additional 5% for 3+ month old products
        }
        
        return discount;
    }
    
    /**
     * Applies urgency-based discounts for items that need immediate clearance
     */
    private BigDecimal applyUrgencyDiscounts(BigDecimal price, Product product) {
        BigDecimal urgencyMultiplier = BigDecimal.ONE;
        
        // Perishable items get aggressive discounts based on expiry
        if (product.isPerishable()) {
            int daysUntilExpiry = product.getDaysUntilExpiry();
            
            if (daysUntilExpiry <= 1) {
                urgencyMultiplier = new BigDecimal("0.3"); // 70% off - expires tomorrow
                logger.debug("Critical expiry discount applied: expires in {} days", daysUntilExpiry);
            } else if (daysUntilExpiry <= 3) {
                urgencyMultiplier = new BigDecimal("0.5"); // 50% off - expires soon
                logger.debug("Urgent expiry discount applied: expires in {} days", daysUntilExpiry);
            } else if (daysUntilExpiry <= 7) {
                urgencyMultiplier = new BigDecimal("0.7"); // 30% off - expires this week
                logger.debug("Weekly expiry discount applied: expires in {} days", daysUntilExpiry);
            }
        }
        
        // Overstocked items (high quantity) get additional discounts
        if (product.getStockQuantity() > 100) {
            BigDecimal overstockDiscount = calculateOverstockDiscount(product.getStockQuantity());
            urgencyMultiplier = urgencyMultiplier.multiply(BigDecimal.ONE.subtract(overstockDiscount));
            logger.debug("Overstock discount applied: {} units in stock", product.getStockQuantity());
        }
        
        return price.multiply(urgencyMultiplier);
    }
    
    /**
     * Calculates overstock discount based on quantity
     */
    private BigDecimal calculateOverstockDiscount(int stockQuantity) {
        if (stockQuantity > 500) return new BigDecimal("0.25"); // 25% additional discount
        if (stockQuantity > 300) return new BigDecimal("0.20"); // 20% additional discount
        if (stockQuantity > 200) return new BigDecimal("0.15"); // 15% additional discount
        if (stockQuantity > 100) return new BigDecimal("0.10"); // 10% additional discount
        return BigDecimal.ZERO;
    }
    
    /**
     * Applies volume incentives to encourage bulk purchases
     */
    private BigDecimal applyVolumeIncentives(BigDecimal price, Product product, PricingContext context) {
        if (!context.isBulkOrder()) {
            return price;
        }
        
        int quantity = context.getQuantity();
        BigDecimal volumeDiscount = BigDecimal.ZERO;
        
        // Aggressive bulk discounts for clearance items
        if (quantity >= 100) {
            volumeDiscount = new BigDecimal("0.30"); // 30% additional discount
        } else if (quantity >= 50) {
            volumeDiscount = new BigDecimal("0.25"); // 25% additional discount
        } else if (quantity >= 25) {
            volumeDiscount = new BigDecimal("0.20"); // 20% additional discount
        } else if (quantity >= 10) {
            volumeDiscount = new BigDecimal("0.15"); // 15% additional discount
        }
        
        if (volumeDiscount.compareTo(BigDecimal.ZERO) > 0) {
            logger.debug("Volume incentive applied: {}% for {} units", 
                        volumeDiscount.multiply(new BigDecimal("100")), quantity);
        }
        
        return price.multiply(BigDecimal.ONE.subtract(volumeDiscount));
    }
    
    /**
     * Applies time-sensitive discounts (flash sales, end-of-day, etc.)
     */
    private BigDecimal applyTimeSensitiveDiscounts(BigDecimal price, Product product) {
        BigDecimal timeMultiplier = BigDecimal.ONE;
        
        // End-of-day discounts for perishables (after 6 PM)
        if (product.isPerishable()) {
            int currentHour = java.time.LocalDateTime.now().getHour();
            if (currentHour >= 18) {
                timeMultiplier = new BigDecimal("0.8"); // 20% end-of-day discount
                logger.debug("End-of-day discount applied for perishable item");
            }
        }
        
        // Weekend clearance events
        java.time.DayOfWeek dayOfWeek = java.time.LocalDateTime.now().getDayOfWeek();
        if (dayOfWeek == java.time.DayOfWeek.SATURDAY || dayOfWeek == java.time.DayOfWeek.SUNDAY) {
            timeMultiplier = timeMultiplier.multiply(new BigDecimal("0.9")); // Additional 10% weekend discount
            logger.debug("Weekend clearance discount applied");
        }
        
        return price.multiply(timeMultiplier);
    }
    
    @Override
    public String getStrategyName() {
        return "Clearance Pricing";
    }
    
    @Override
    public String getDescription() {
        return "Aggressive pricing strategy for products that need to be moved quickly. " +
               "Handles perishable items, overstocked products, end-of-lifecycle items, " +
               "and applies time-sensitive and volume-based incentives to maximize clearance.";
    }
    
    @Override
    public boolean isApplicable(Product product, PricingContext context) {
        // This strategy is applicable for:
        // 1. Clearance tier products
        // 2. Perishable items expiring soon
        // 3. Overstocked items
        // 4. Old products (6+ months)
        return product.getTier() == Product.ProductTier.CLEARANCE ||
               product.isExpiringSoon() ||
               product.getStockQuantity() > 100 ||
               product.getAgeInDays() > 180;
    }
    
    @Override
    public int getPriority() {
        return 9; // Highest priority for items that need immediate clearance
    }
}

