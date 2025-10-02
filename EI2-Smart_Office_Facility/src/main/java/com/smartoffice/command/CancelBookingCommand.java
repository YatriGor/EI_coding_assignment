package com.smartoffice.command;

import com.smartoffice.config.OfficeConfiguration;
import com.smartoffice.model.Room;

/**
 * Command to cancel a room booking
 * 
 * Design Pattern: Command Pattern
 * - Encapsulates booking cancellation request as an object
 * - Handles validation and execution of cancellation operation
 */
public class CancelBookingCommand implements Command {
    
    private final int roomNumber;
    private final OfficeConfiguration office;
    private Room.BookingInfo previousBooking; // For undo functionality
    
    public CancelBookingCommand(int roomNumber) {
        this.roomNumber = roomNumber;
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
            
            Room room = office.getRoom(roomNumber);
            if (room == null) {
                return "Room " + roomNumber + " does not exist.";
            }
            
            // Check if room is booked
            if (!room.isBooked()) {
                return "Room " + roomNumber + " is not booked. Cannot cancel booking.";
            }
            
            // Store booking info for potential undo
            previousBooking = room.getCurrentBooking();
            
            // Cancel the booking
            room.cancelBooking();
            return "Booking for Room " + roomNumber + " cancelled successfully.";
            
        } catch (Exception e) {
            return "Error cancelling booking: " + e.getMessage();
        }
    }
    
    @Override
    public String undo() {
        try {
            if (previousBooking == null) {
                return "No previous booking to restore for Room " + roomNumber + ".";
            }
            
            Room room = office.getRoom(roomNumber);
            if (room != null) {
                boolean restored = room.bookRoom(previousBooking.getStartTime(), 
                                               previousBooking.getDurationMinutes());
                if (restored) {
                    return "Booking for Room " + roomNumber + " restored (undo cancellation).";
                } else {
                    return "Could not restore booking for Room " + roomNumber + " - time slot may be unavailable.";
                }
            }
            return "Room " + roomNumber + " not found for undo operation.";
        } catch (Exception e) {
            return "Error undoing cancellation: " + e.getMessage();
        }
    }
    
    @Override
    public String getDescription() {
        return "Cancel booking for Room " + roomNumber;
    }
}
