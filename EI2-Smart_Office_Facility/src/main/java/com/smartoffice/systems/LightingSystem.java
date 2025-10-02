package com.smartoffice.systems;

import com.smartoffice.observer.OccupancyObserver;

/**
 * Lighting control system that observes room occupancy
 * 
 * Design Pattern: Observer Pattern
 * - Implements OccupancyObserver to respond to occupancy changes
 * - Automatically controls lights based on room occupancy status
 */
public class LightingSystem implements OccupancyObserver {
    
    private final String systemName;
    
    public LightingSystem() {
        this.systemName = "Lighting System";
    }
    
    @Override
    public void onOccupancyChanged(int roomNumber, boolean isOccupied, int occupantCount) {
        if (isOccupied) {
            turnOnLights(roomNumber);
        } else {
            turnOffLights(roomNumber);
        }
    }
    
    /**
     * Turn on lights for the specified room
     * @param roomNumber Room number to turn on lights
     */
    private void turnOnLights(int roomNumber) {
        System.out.println("[" + systemName + "] Lights turned on for Room " + roomNumber);
    }
    
    /**
     * Turn off lights for the specified room
     * @param roomNumber Room number to turn off lights
     */
    private void turnOffLights(int roomNumber) {
        System.out.println("[" + systemName + "] Lights turned off for Room " + roomNumber);
    }
    
    /**
     * Get the system name
     * @return System name
     */
    public String getSystemName() {
        return systemName;
    }
}
