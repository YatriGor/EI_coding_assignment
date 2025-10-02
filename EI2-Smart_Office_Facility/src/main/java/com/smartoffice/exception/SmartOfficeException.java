package com.smartoffice.exception;

/**
 * Custom exception class for Smart Office application
 * 
 * Provides specific error handling for office management operations
 */
public class SmartOfficeException extends Exception {
    
    public SmartOfficeException(String message) {
        super(message);
    }
    
    public SmartOfficeException(String message, Throwable cause) {
        super(message, cause);
    }
    
    /**
     * Exception for invalid room operations
     */
    public static class InvalidRoomException extends SmartOfficeException {
        public InvalidRoomException(String message) {
            super(message);
        }
    }
    
    /**
     * Exception for booking conflicts
     */
    public static class BookingConflictException extends SmartOfficeException {
        public BookingConflictException(String message) {
            super(message);
        }
    }
    
    /**
     * Exception for configuration errors
     */
    public static class ConfigurationException extends SmartOfficeException {
        public ConfigurationException(String message) {
            super(message);
        }
    }
    
    /**
     * Exception for occupancy validation errors
     */
    public static class OccupancyException extends SmartOfficeException {
        public OccupancyException(String message) {
            super(message);
        }
    }
}
