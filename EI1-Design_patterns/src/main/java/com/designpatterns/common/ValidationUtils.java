package com.designpatterns.common;

import java.util.Collection;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * Utility class for comprehensive input validation
 * Implements defensive programming principles
 */
public final class ValidationUtils {
    
    private ValidationUtils() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }
    
    /**
     * Validates that an object is not null
     */
    public static <T> T requireNonNull(T obj, String message) {
        if (obj == null) {
            throw new IllegalArgumentException(message != null ? message : "Object cannot be null");
        }
        return obj;
    }
    
    /**
     * Validates that a string is not null or empty
     */
    public static String requireNonEmpty(String str, String message) {
        requireNonNull(str, message);
        if (str.trim().isEmpty()) {
            throw new IllegalArgumentException(message != null ? message : "String cannot be empty");
        }
        return str;
    }
    
    /**
     * Validates that a collection is not null or empty
     */
    public static <T extends Collection<?>> T requireNonEmpty(T collection, String message) {
        requireNonNull(collection, message);
        if (collection.isEmpty()) {
            throw new IllegalArgumentException(message != null ? message : "Collection cannot be empty");
        }
        return collection;
    }
    
    /**
     * Validates that a number is within a specified range
     */
    public static <T extends Comparable<T>> T requireInRange(T value, T min, T max, String message) {
        requireNonNull(value, "Value cannot be null");
        requireNonNull(min, "Min value cannot be null");
        requireNonNull(max, "Max value cannot be null");
        
        if (value.compareTo(min) < 0 || value.compareTo(max) > 0) {
            throw new IllegalArgumentException(
                message != null ? message : 
                String.format("Value %s must be between %s and %s", value, min, max)
            );
        }
        return value;
    }
    
    /**
     * Validates that a condition is true
     */
    public static void requireTrue(boolean condition, String message) {
        if (!condition) {
            throw new IllegalArgumentException(message != null ? message : "Condition must be true");
        }
    }
    
    /**
     * Validates using a custom predicate
     */
    public static <T> T requireValid(T value, Predicate<T> validator, String message) {
        requireNonNull(value, "Value cannot be null");
        requireNonNull(validator, "Validator cannot be null");
        
        if (!validator.test(value)) {
            throw new IllegalArgumentException(message != null ? message : "Value failed validation");
        }
        return value;
    }
    
    /**
     * Safe string comparison that handles nulls
     */
    public static boolean safeEquals(String str1, String str2) {
        return Objects.equals(str1, str2);
    }
    
    /**
     * Validates email format (basic validation)
     */
    public static String requireValidEmail(String email, String message) {
        requireNonEmpty(email, message);
        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            throw new IllegalArgumentException(message != null ? message : "Invalid email format");
        }
        return email;
    }
}

