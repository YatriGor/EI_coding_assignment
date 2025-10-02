package com.smartoffice.config;

import com.smartoffice.model.Room;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Singleton class to manage office configuration and room instances
 * Ensures only one instance of office configuration exists throughout the application
 * 
 * Design Pattern: Singleton Pattern
 * - Thread-safe implementation using double-checked locking
 * - Manages global state of office configuration
 * - Provides centralized access to room management
 */
public class OfficeConfiguration {
    
    private static volatile OfficeConfiguration instance;
    private final Map<Integer, Room> rooms;
    private int roomCount;
    private boolean isConfigured;
    
    /**
     * Private constructor to prevent direct instantiation
     */
    private OfficeConfiguration() {
        this.rooms = new ConcurrentHashMap<>();
        this.roomCount = 0;
        this.isConfigured = false;
    }
    
    /**
     * Thread-safe singleton instance getter using double-checked locking
     * @return The single instance of OfficeConfiguration
     */
    public static OfficeConfiguration getInstance() {
        if (instance == null) {
            synchronized (OfficeConfiguration.class) {
                if (instance == null) {
                    instance = new OfficeConfiguration();
                }
            }
        }
        return instance;
    }
    
    /**
     * Configure the office with specified number of meeting rooms
     * @param count Number of meeting rooms to create
     * @return Success message
     * @throws IllegalArgumentException if count is invalid
     */
    public synchronized String configureRooms(int count) {
        if (count <= 0) {
            throw new IllegalArgumentException("Room count must be positive");
        }
        
        // Clear existing rooms if reconfiguring
        rooms.clear();
        
        // Create new rooms
        for (int i = 1; i <= count; i++) {
            rooms.put(i, new Room(i));
        }
        
        this.roomCount = count;
        this.isConfigured = true;
        
        StringBuilder message = new StringBuilder("Office configured with " + count + " meeting rooms: ");
        for (int i = 1; i <= count; i++) {
            message.append("Room ").append(i);
            if (i < count) {
                message.append(", ");
            }
        }
        message.append(".");
        
        return message.toString();
    }
    
    /**
     * Set maximum capacity for a specific room
     * @param roomNumber Room number to configure
     * @param capacity Maximum capacity for the room
     * @return Success message
     * @throws IllegalArgumentException if room doesn't exist or capacity is invalid
     */
    public synchronized String setRoomMaxCapacity(int roomNumber, int capacity) {
        if (!isConfigured) {
            throw new IllegalStateException("Office not configured. Please configure rooms first.");
        }
        
        if (capacity <= 0) {
            throw new IllegalArgumentException("Invalid capacity. Please enter a valid positive number.");
        }
        
        Room room = rooms.get(roomNumber);
        if (room == null) {
            throw new IllegalArgumentException("Room " + roomNumber + " does not exist.");
        }
        
        room.setMaxCapacity(capacity);
        return "Room " + roomNumber + " maximum capacity set to " + capacity + ".";
    }
    
    /**
     * Get a room by its number
     * @param roomNumber Room number to retrieve
     * @return Room instance or null if not found
     */
    public Room getRoom(int roomNumber) {
        return rooms.get(roomNumber);
    }
    
    /**
     * Get all rooms
     * @return Map of all rooms
     */
    public Map<Integer, Room> getAllRooms() {
        return new HashMap<>(rooms);
    }
    
    /**
     * Check if office is configured
     * @return true if office is configured, false otherwise
     */
    public boolean isConfigured() {
        return isConfigured;
    }
    
    /**
     * Get the number of configured rooms
     * @return Number of rooms
     */
    public int getRoomCount() {
        return roomCount;
    }
    
    /**
     * Validate room number
     * @param roomNumber Room number to validate
     * @return true if valid, false otherwise
     */
    public boolean isValidRoomNumber(int roomNumber) {
        return roomNumber > 0 && roomNumber <= roomCount && rooms.containsKey(roomNumber);
    }
}
