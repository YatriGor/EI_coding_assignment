package com.designpatterns.behavioral.strategy;

import com.designpatterns.common.ValidationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Dynamic Pricing Engine - Context class in Strategy pattern
 * Manages multiple pricing strategies and selects the most appropriate one
 */
public class DynamicPricingEngine {
    private static final Logger logger = LoggerFactory.getLogger(DynamicPricingEngine.class);
    
    private final List<PricingStrategy> strategies;
    private final ConcurrentMap<String, BigDecimal> priceCache;
    private final AtomicLong pricingRequestCount;
    private PricingStrategy defaultStrategy;
    
    public DynamicPricingEngine() {
        this.strategies = new ArrayList<>();
        this.priceCache = new ConcurrentHashMap<>();
        this.pricingRequestCount = new AtomicLong(0);
        logger.info("Dynamic Pricing Engine initialized");
    }
    
    /**
     * Registers a pricing strategy
     */
    public synchronized void registerStrategy(PricingStrategy strategy) {
        ValidationUtils.requireNonNull(strategy, "Strategy cannot be null");
        
        if (!strategies.contains(strategy)) {
            strategies.add(strategy);
            // Sort strategies by priority (highest first)
            strategies.sort(Comparator.comparingInt(PricingStrategy::getPriority).reversed());
            logger.info("Strategy '{}' registered with priority {}", 
                       strategy.getStrategyName(), strategy.getPriority());
        } else {
            logger.warn("Strategy '{}' is already registered", strategy.getStrategyName());
        }
    }
    
    /**
     * Unregisters a pricing strategy
     */
    public synchronized void unregisterStrategy(PricingStrategy strategy) {
        ValidationUtils.requireNonNull(strategy, "Strategy cannot be null");
        
        if (strategies.remove(strategy)) {
            logger.info("Strategy '{}' unregistered", strategy.getStrategyName());
            clearPriceCache(); // Clear cache as pricing logic has changed
        } else {
            logger.warn("Strategy '{}' was not registered", strategy.getStrategyName());
        }
    }
    
    /**
     * Sets the default strategy to use when no specific strategy is applicable
     */
    public void setDefaultStrategy(PricingStrategy defaultStrategy) {
        this.defaultStrategy = ValidationUtils.requireNonNull(defaultStrategy, "Default strategy cannot be null");
        logger.info("Default strategy set to: {}", defaultStrategy.getStrategyName());
    }
    
    /**
     * Calculates the price for a product using the most appropriate strategy
     */
    public BigDecimal calculatePrice(Product product, PricingContext context) {
        ValidationUtils.requireNonNull(product, "Product cannot be null");
        ValidationUtils.requireNonNull(context, "Pricing context cannot be null");
        
        long requestId = pricingRequestCount.incrementAndGet();
        logger.debug("Processing pricing request #{} for product: {}", requestId, product.getProductId());
        
        // Check cache first
        String cacheKey = generateCacheKey(product, context);
        BigDecimal cachedPrice = priceCache.get(cacheKey);
        if (cachedPrice != null) {
            logger.debug("Returning cached price for product {}: {}", product.getProductId(), cachedPrice);
            return cachedPrice;
        }
        
        // Find the most appropriate strategy
        PricingStrategy selectedStrategy = selectStrategy(product, context);
        
        if (selectedStrategy == null) {
            if (defaultStrategy != null) {
                selectedStrategy = defaultStrategy;
                logger.debug("Using default strategy: {}", defaultStrategy.getStrategyName());
            } else {
                logger.error("No applicable strategy found and no default strategy set");
                throw new IllegalStateException("No pricing strategy available for product: " + product.getProductId());
            }
        }
        
        // Calculate price using selected strategy
        BigDecimal calculatedPrice;
        try {
            calculatedPrice = selectedStrategy.calculatePrice(product, context);
            ValidationUtils.requireNonNull(calculatedPrice, "Strategy returned null price");
            ValidationUtils.requireTrue(calculatedPrice.compareTo(BigDecimal.ZERO) >= 0, 
                                       "Strategy returned negative price");
            
            logger.info("Price calculated for product {} using strategy '{}': {}", 
                       product.getProductId(), selectedStrategy.getStrategyName(), calculatedPrice);
            
        } catch (Exception e) {
            logger.error("Error calculating price with strategy '{}': {}", 
                        selectedStrategy.getStrategyName(), e.getMessage(), e);
            
            // Fallback to base price
            calculatedPrice = product.getBasePrice();
            logger.warn("Falling back to base price: {}", calculatedPrice);
        }
        
        // Cache the result
        priceCache.put(cacheKey, calculatedPrice);
        
        return calculatedPrice;
    }
    
    /**
     * Selects the most appropriate strategy based on product and context
     */
    private PricingStrategy selectStrategy(Product product, PricingContext context) {
        for (PricingStrategy strategy : strategies) {
            if (strategy.isApplicable(product, context)) {
                logger.debug("Selected strategy: {} (priority: {})", 
                           strategy.getStrategyName(), strategy.getPriority());
                return strategy;
            }
        }
        
        logger.debug("No specific strategy applicable, will use default if available");
        return null;
    }
    
    /**
     * Generates a cache key for price caching
     */
    private String generateCacheKey(Product product, PricingContext context) {
        // Simple cache key - in production, this would be more sophisticated
        return String.format("%s_%s_%d_%d_%s_%b", 
                           product.getProductId(),
                           context.getCustomerTier(),
                           context.getDemandLevel(),
                           context.getQuantity(),
                           context.getRegion(),
                           context.isSpecialEvent());
    }
    
    /**
     * Gets all registered strategies
     */
    public List<PricingStrategy> getRegisteredStrategies() {
        return List.copyOf(strategies);
    }
    
    /**
     * Gets the number of registered strategies
     */
    public int getStrategyCount() {
        return strategies.size();
    }
    
    /**
     * Gets the total number of pricing requests processed
     */
    public long getPricingRequestCount() {
        return pricingRequestCount.get();
    }
    
    /**
     * Gets the current cache size
     */
    public int getCacheSize() {
        return priceCache.size();
    }
    
    /**
     * Clears the price cache
     */
    public void clearPriceCache() {
        priceCache.clear();
        logger.info("Price cache cleared");
    }
    
    /**
     * Gets strategy information for debugging/monitoring
     */
    public String getStrategyInfo() {
        StringBuilder info = new StringBuilder();
        info.append("Registered Strategies (").append(strategies.size()).append("):\n");
        
        for (int i = 0; i < strategies.size(); i++) {
            PricingStrategy strategy = strategies.get(i);
            info.append(String.format("%d. %s (Priority: %d)\n   %s\n",
                                    i + 1,
                                    strategy.getStrategyName(),
                                    strategy.getPriority(),
                                    strategy.getDescription()));
        }
        
        if (defaultStrategy != null) {
            info.append("\nDefault Strategy: ").append(defaultStrategy.getStrategyName());
        }
        
        info.append("\nStatistics:\n");
        info.append("  Total Requests: ").append(pricingRequestCount.get()).append("\n");
        info.append("  Cache Size: ").append(priceCache.size()).append("\n");
        
        return info.toString();
    }
    
    /**
     * Performs a pricing comparison across all applicable strategies
     */
    public PricingComparison compareStrategies(Product product, PricingContext context) {
        ValidationUtils.requireNonNull(product, "Product cannot be null");
        ValidationUtils.requireNonNull(context, "Pricing context cannot be null");
        
        PricingComparison comparison = new PricingComparison(product, context);
        
        for (PricingStrategy strategy : strategies) {
            if (strategy.isApplicable(product, context)) {
                try {
                    BigDecimal price = strategy.calculatePrice(product, context);
                    comparison.addResult(strategy.getStrategyName(), price);
                } catch (Exception e) {
                    logger.warn("Strategy '{}' failed during comparison: {}", 
                               strategy.getStrategyName(), e.getMessage());
                    comparison.addError(strategy.getStrategyName(), e.getMessage());
                }
            }
        }
        
        return comparison;
    }
    
    /**
     * Inner class to hold pricing comparison results
     */
    public static class PricingComparison {
        private final Product product;
        private final PricingContext context;
        private final ConcurrentMap<String, BigDecimal> results;
        private final ConcurrentMap<String, String> errors;
        
        public PricingComparison(Product product, PricingContext context) {
            this.product = product;
            this.context = context;
            this.results = new ConcurrentHashMap<>();
            this.errors = new ConcurrentHashMap<>();
        }
        
        public void addResult(String strategyName, BigDecimal price) {
            results.put(strategyName, price);
        }
        
        public void addError(String strategyName, String error) {
            errors.put(strategyName, error);
        }
        
        public Product getProduct() { return product; }
        public PricingContext getContext() { return context; }
        public ConcurrentMap<String, BigDecimal> getResults() { return results; }
        public ConcurrentMap<String, String> getErrors() { return errors; }
        
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("Pricing Comparison for Product: ").append(product.getName()).append("\n");
            sb.append("Base Price: ").append(product.getBasePrice()).append("\n\n");
            
            results.forEach((strategy, price) -> 
                sb.append(String.format("%-25s: %s\n", strategy, price)));
            
            if (!errors.isEmpty()) {
                sb.append("\nErrors:\n");
                errors.forEach((strategy, error) -> 
                    sb.append(String.format("%-25s: %s\n", strategy, error)));
            }
            
            return sb.toString();
        }
    }
}

