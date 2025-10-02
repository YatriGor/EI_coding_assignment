package com.smartoffice.observer;

/**
 * Observer interface for occupancy changes
 * 
 * Design Pattern: Observer Pattern
 * - Defines the contract for objects that need to be notified of occupancy changes
 * - Allows loose coupling between room occupancy and control systems
 */
public interface OccupancyObserver {
    
    /**
     * Called when room occupancy changes
     * @param roomNumber The room number where occupancy changed
     * @param isOccupied Whether the room is now occupied
     * @param occupantCount Current number of occupants
     */
    void onOccupancyChanged(int roomNumber, boolean isOccupied, int occupantCount);
}
