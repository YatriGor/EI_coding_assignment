package com.smartoffice.command;

import com.smartoffice.config.OfficeConfiguration;
import com.smartoffice.model.Room;

/**
 * Command to add occupants to a room (simulate sensor detection)
 * 
 * Design Pattern: Command Pattern
 * - Encapsulates occupancy update request as an object
 * - Handles validation and execution of occupancy changes
 */
public class AddOccupantCommand implements Command {
    
    private final int roomNumber;
    private final int occupantCount;
    private final OfficeConfiguration office;
    private int previousOccupantCount; // For undo functionality
    
    public AddOccupantCommand(int roomNumber, int occupantCount) {
        this.roomNumber = roomNumber;
        this.occupantCount = occupantCount;
        this.office = OfficeConfiguration.getInstance();
    }
    
    @Override
    public String execute() {
        try {
            // Validate office configuration
            if (!office.isConfigured()) {
                return "Office not configured. Please configure rooms first.";
            }
            
            // Validate room number
            if (!office.isValidRoomNumber(roomNumber)) {
                return "Room " + roomNumber + " does not exist.";
            }
            
            // Validate occupant count
            if (occupantCount < 0) {
                return "Invalid occupant count. Count cannot be negative.";
            }
            
            Room room = office.getRoom(roomNumber);
            if (room == null) {
                return "Room " + roomNumber + " does not exist.";
            }
            
            // Store previous count for undo
            previousOccupantCount = room.getCurrentOccupantCount();
            
            // Update occupancy
            String result = room.updateOccupancy(occupantCount);
            
            // Check if occupancy is sufficient (at least 2 people for occupied status)
            if (occupantCount > 0 && occupantCount < 2) {
                return "Room " + roomNumber + " occupancy insufficient to mark as occupied.";
            }
            
            return result;
            
        } catch (Exception e) {
            return "Error updating occupancy: " + e.getMessage();
        }
    }
    
    @Override
    public String undo() {
        try {
            Room room = office.getRoom(roomNumber);
            if (room != null) {
                room.updateOccupancy(previousOccupantCount);
                return "Occupancy for Room " + roomNumber + " restored to " + previousOccupantCount + " (undo).";
            }
            return "Room " + roomNumber + " not found for undo operation.";
        } catch (Exception e) {
            return "Error undoing occupancy change: " + e.getMessage();
        }
    }
    
    @Override
    public String getDescription() {
        return "Set Room " + roomNumber + " occupancy to " + occupantCount + " persons";
    }
}
