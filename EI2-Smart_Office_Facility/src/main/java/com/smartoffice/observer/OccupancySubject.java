package com.smartoffice.observer;

/**
 * Subject interface for occupancy notifications
 * 
 * Design Pattern: Observer Pattern
 * - Defines the contract for objects that can be observed for occupancy changes
 * - Manages observer registration and notification
 */
public interface OccupancySubject {
    
    /**
     * Add an observer to be notified of occupancy changes
     * @param observer The observer to add
     */
    void addObserver(OccupancyObserver observer);
    
    /**
     * Remove an observer from notifications
     * @param observer The observer to remove
     */
    void removeObserver(OccupancyObserver observer);
    
    /**
     * Notify all observers of occupancy change
     * @param roomNumber The room number where occupancy changed
     * @param isOccupied Whether the room is now occupied
     * @param occupantCount Current number of occupants
     */
    void notifyObservers(int roomNumber, boolean isOccupied, int occupantCount);
}
