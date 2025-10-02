package com.designpatterns.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;

/**
 * Utility class for handling transient errors with retry mechanisms
 * Implements exponential backoff with jitter
 */
public final class RetryUtils {
    private static final Logger logger = LoggerFactory.getLogger(RetryUtils.class);
    
    private RetryUtils() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }
    
    /**
     * Executes an operation with retry logic and exponential backoff
     */
    public static <T> T executeWithRetry(
            Supplier<T> operation,
            int maxAttempts,
            Duration initialDelay,
            Class<? extends Exception> retryableException,
            String operationName) {
        
        ValidationUtils.requireNonNull(operation, "Operation cannot be null");
        ValidationUtils.requireInRange(maxAttempts, 1, 10, "Max attempts must be between 1 and 10");
        ValidationUtils.requireNonNull(initialDelay, "Initial delay cannot be null");
        ValidationUtils.requireNonNull(retryableException, "Retryable exception class cannot be null");
        ValidationUtils.requireNonEmpty(operationName, "Operation name cannot be empty");
        
        Exception lastException = null;
        
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                logger.debug("Executing {} - attempt {}/{}", operationName, attempt, maxAttempts);
                T result = operation.get();
                
                if (attempt > 1) {
                    logger.info("Operation {} succeeded on attempt {}/{}", operationName, attempt, maxAttempts);
                }
                
                return result;
                
            } catch (Exception e) {
                lastException = e;
                
                if (!retryableException.isAssignableFrom(e.getClass())) {
                    logger.error("Non-retryable exception occurred in {}: {}", operationName, e.getMessage());
                    throw new RuntimeException("Non-retryable exception in " + operationName, e);
                }
                
                if (attempt == maxAttempts) {
                    logger.error("Operation {} failed after {} attempts", operationName, maxAttempts);
                    break;
                }
                
                long delayMs = calculateBackoffDelay(attempt, initialDelay);
                logger.warn("Operation {} failed on attempt {}/{}, retrying in {}ms. Error: {}", 
                           operationName, attempt, maxAttempts, delayMs, e.getMessage());
                
                try {
                    Thread.sleep(delayMs);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Interrupted during retry delay", ie);
                }
            }
        }
        
        throw new RuntimeException(
            String.format("Operation %s failed after %d attempts", operationName, maxAttempts), 
            lastException
        );
    }
    
    /**
     * Calculates exponential backoff delay with jitter
     */
    private static long calculateBackoffDelay(int attempt, Duration initialDelay) {
        // Exponential backoff: delay = initialDelay * 2^(attempt-1)
        long baseDelay = initialDelay.toMillis() * (1L << (attempt - 1));
        
        // Add jitter (±25% random variation)
        double jitterFactor = 0.75 + (ThreadLocalRandom.current().nextDouble() * 0.5);
        
        return Math.round(baseDelay * jitterFactor);
    }
    
    /**
     * Simple retry without exponential backoff
     */
    public static <T> T executeWithSimpleRetry(
            Supplier<T> operation,
            int maxAttempts,
            String operationName) {
        
        return executeWithRetry(
            operation,
            maxAttempts,
            Duration.ofMillis(100),
            RuntimeException.class,
            operationName
        );
    }
}

