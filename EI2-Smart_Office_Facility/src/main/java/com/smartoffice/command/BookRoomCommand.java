package com.smartoffice.command;

import com.smartoffice.config.OfficeConfiguration;
import com.smartoffice.model.Room;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Command to book a conference room
 * 
 * Design Pattern: Command Pattern
 * - Encapsulates room booking request as an object
 * - Handles validation and execution of booking operation
 */
public class BookRoomCommand implements Command {
    
    private final int roomNumber;
    private final String startTime;
    private final int durationMinutes;
    private final OfficeConfiguration office;
    
    public BookRoomCommand(int roomNumber, String startTime, int durationMinutes) {
        this.roomNumber = roomNumber;
        this.startTime = startTime;
        this.durationMinutes = durationMinutes;
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
            
            // Validate time format
            LocalTime parsedTime;
            try {
                parsedTime = LocalTime.parse(startTime, DateTimeFormatter.ofPattern("HH:mm"));
            } catch (DateTimeParseException e) {
                return "Invalid time format. Please use HH:mm format (e.g., 09:00).";
            }
            
            // Validate duration
            if (durationMinutes <= 0) {
                return "Invalid duration. Duration must be positive.";
            }
            
            Room room = office.getRoom(roomNumber);
            if (room == null) {
                return "Room " + roomNumber + " does not exist.";
            }
            
            // Attempt to book the room
            boolean booked = room.bookRoom(parsedTime, durationMinutes);
            if (booked) {
                return "Room " + roomNumber + " booked from " + startTime + " for " + durationMinutes + " minutes.";
            } else {
                return "Room " + roomNumber + " is already booked during this time. Cannot book.";
            }
            
        } catch (Exception e) {
            return "Error booking room: " + e.getMessage();
        }
    }
    
    @Override
    public String undo() {
        try {
            Room room = office.getRoom(roomNumber);
            if (room != null && room.isBooked()) {
                room.cancelBooking();
                return "Booking for Room " + roomNumber + " cancelled (undo).";
            }
            return "No booking to undo for Room " + roomNumber + ".";
        } catch (Exception e) {
            return "Error undoing booking: " + e.getMessage();
        }
    }
    
    @Override
    public String getDescription() {
        return "Book Room " + roomNumber + " from " + startTime + " for " + durationMinutes + " minutes";
    }
}
