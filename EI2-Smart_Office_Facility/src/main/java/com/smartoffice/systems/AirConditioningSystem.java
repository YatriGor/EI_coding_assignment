package com.smartoffice.systems;

import com.smartoffice.observer.OccupancyObserver;

/**
 * Air Conditioning control system that observes room occupancy
 * 
 * Design Pattern: Observer Pattern
 * - Implements OccupancyObserver to respond to occupancy changes
 * - Automatically controls AC based on room occupancy status
 */
public class AirConditioningSystem implements OccupancyObserver {
    
    private final String systemName;
    
    public AirConditioningSystem() {
        this.systemName = "Air Conditioning System";
    }
    
    @Override
    public void onOccupancyChanged(int roomNumber, boolean isOccupied, int occupantCount) {
        if (isOccupied) {
            turnOnAC(roomNumber);
        } else {
            turnOffAC(roomNumber);
        }
    }
    
    /**
     * Turn on air conditioning for the specified room
     * @param roomNumber Room number to turn on AC
     */
    private void turnOnAC(int roomNumber) {
        System.out.println("[" + systemName + "] AC turned on for Room " + roomNumber);
    }
    
    /**
     * Turn off air conditioning for the specified room
     * @param roomNumber Room number to turn off AC
     */
    private void turnOffAC(int roomNumber) {
        System.out.println("[" + systemName + "] AC turned off for Room " + roomNumber);
    }
    
    /**
     * Get the system name
     * @return System name
     */
    public String getSystemName() {
        return systemName;
    }
}
