package com.smartoffice.util;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.regex.Pattern;

/**
 * Utility class for input validation
 * 
 * Provides centralized validation methods for various input types
 */
public class ValidationUtils {
    
    // Validation patterns
    private static final Pattern TIME_PATTERN = Pattern.compile("^([0-1]?[0-9]|2[0-3]):[0-5][0-9]$");
    private static final Pattern POSITIVE_INTEGER_PATTERN = Pattern.compile("^[1-9]\\d*$");
    private static final Pattern NON_NEGATIVE_INTEGER_PATTERN = Pattern.compile("^\\d+$");
    
    private ValidationUtils() {
        // Utility class - prevent instantiation
    }
    
    /**
     * Validate time format (HH:mm)
     * @param timeString Time string to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidTimeFormat(String timeString) {
        if (timeString == null || timeString.trim().isEmpty()) {
            return false;
        }
        
        return TIME_PATTERN.matcher(timeString.trim()).matches();
    }
    
    /**
     * Parse time string to LocalTime
     * @param timeString Time string in HH:mm format
     * @return LocalTime object
     * @throws DateTimeParseException if format is invalid
     */
    public static LocalTime parseTime(String timeString) throws DateTimeParseException {
        return LocalTime.parse(timeString.trim(), DateTimeFormatter.ofPattern("HH:mm"));
    }
    
    /**
     * Validate positive integer
     * @param numberString Number string to validate
     * @return true if valid positive integer, false otherwise
     */
    public static boolean isValidPositiveInteger(String numberString) {
        if (numberString == null || numberString.trim().isEmpty()) {
            return false;
        }
        
        return POSITIVE_INTEGER_PATTERN.matcher(numberString.trim()).matches();
    }
    
    /**
     * Validate non-negative integer
     * @param numberString Number string to validate
     * @return true if valid non-negative integer, false otherwise
     */
    public static boolean isValidNonNegativeInteger(String numberString) {
        if (numberString == null || numberString.trim().isEmpty()) {
            return false;
        }
        
        return NON_NEGATIVE_INTEGER_PATTERN.matcher(numberString.trim()).matches();
    }
    
    /**
     * Parse integer safely
     * @param numberString Number string to parse
     * @return Integer value
     * @throws NumberFormatException if parsing fails
     */
    public static int parseInteger(String numberString) throws NumberFormatException {
        if (numberString == null || numberString.trim().isEmpty()) {
            throw new NumberFormatException("Empty or null number string");
        }
        
        return Integer.parseInt(numberString.trim());
    }
    
    /**
     * Validate room number range
     * @param roomNumber Room number to validate
     * @param maxRooms Maximum number of rooms
     * @return true if valid, false otherwise
     */
    public static boolean isValidRoomNumber(int roomNumber, int maxRooms) {
        return roomNumber > 0 && roomNumber <= maxRooms;
    }
    
    /**
     * Validate capacity value
     * @param capacity Capacity to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidCapacity(int capacity) {
        return capacity > 0 && capacity <= 1000; // Reasonable upper limit
    }
    
    /**
     * Validate duration in minutes
     * @param duration Duration to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidDuration(int duration) {
        return duration > 0 && duration <= 1440; // Max 24 hours
    }
    
    /**
     * Validate occupant count
     * @param count Occupant count to validate
     * @param maxCapacity Maximum room capacity
     * @return true if valid, false otherwise
     */
    public static boolean isValidOccupantCount(int count, int maxCapacity) {
        return count >= 0 && count <= maxCapacity;
    }
    
    /**
     * Sanitize string input
     * @param input Input string
     * @return Sanitized string
     */
    public static String sanitizeInput(String input) {
        if (input == null) {
            return "";
        }
        
        return input.trim().replaceAll("[\\r\\n\\t]", " ").replaceAll("\\s+", " ");
    }
}
