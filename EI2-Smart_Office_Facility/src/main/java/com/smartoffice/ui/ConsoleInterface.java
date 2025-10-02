package com.smartoffice.ui;

import com.smartoffice.command.*;
import com.smartoffice.config.OfficeConfiguration;
import com.smartoffice.model.Room;

import java.util.Map;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * Console-based user interface for the Smart Office Management System
 * 
 * Handles user input, command parsing, and output formatting
 * Provides comprehensive input validation and error handling
 */
public class ConsoleInterface {
    
    private final Scanner scanner;
    private final CommandInvoker commandInvoker;
    private final OfficeConfiguration office;
    private boolean running;
    
    // Input validation patterns
    private static final Pattern TIME_PATTERN = Pattern.compile("^([0-1]?[0-9]|2[0-3]):[0-5][0-9]$");
    private static final Pattern POSITIVE_INTEGER_PATTERN = Pattern.compile("^[1-9]\\d*$");
    private static final Pattern NON_NEGATIVE_INTEGER_PATTERN = Pattern.compile("^\\d+$");
    
    public ConsoleInterface() {
        this.scanner = new Scanner(System.in);
        this.commandInvoker = new CommandInvoker();
        this.office = OfficeConfiguration.getInstance();
        this.running = true;
    }
    
    /**
     * Start the console interface
     */
    public void start() {
        printWelcomeMessage();
        
        while (running) {
            try {
                System.out.print("\nEnter command: ");
                String input = scanner.nextLine().trim();
                
                if (input.isEmpty()) {
                    continue;
                }
                
                String result = processCommand(input);
                System.out.println(result);
                
            } catch (Exception e) {
                System.out.println("Error processing command: " + e.getMessage());
            }
        }
        
        cleanup();
    }
    
    /**
     * Process user command input
     * @param input User input string
     * @return Result message
     */
    private String processCommand(String input) {
        String[] parts = input.split("\\s+");
        if (parts.length == 0) {
            return "Invalid command. Type 'help' for available commands.";
        }
        
        String command = parts[0].toLowerCase();
        
        switch (command) {
            case "config":
                return handleConfigCommand(parts);
            case "block":
                return handleBlockCommand(parts);
            case "cancel":
                return handleCancelCommand(parts);
            case "add":
                return handleAddCommand(parts);
            case "status":
                return handleStatusCommand(parts);
            case "help":
                return getHelpMessage();
            case "exit":
            case "quit":
                running = false;
                return "Goodbye!";
            case "undo":
                return commandInvoker.undoLastCommand();
            case "history":
                return getCommandHistory();
            default:
                return "Unknown command: " + command + ". Type 'help' for available commands.";
        }
    }
    
    /**
     * Handle config command
     * @param parts Command parts
     * @return Result message
     */
    private String handleConfigCommand(String[] parts) {
        if (parts.length < 3) {
            return "Invalid config command. Usage: config room count <number> OR config room max capacity <room> <capacity>";
        }
        
        if (!"room".equals(parts[1])) {
            return "Invalid config command. Expected 'room' after 'config'.";
        }
        
        if ("count".equals(parts[2])) {
            return handleConfigRoomCount(parts);
        } else if ("max".equals(parts[2]) && parts.length >= 4 && "capacity".equals(parts[3])) {
            return handleConfigRoomCapacity(parts);
        } else {
            return "Invalid config command. Usage: config room count <number> OR config room max capacity <room> <capacity>";
        }
    }
    
    /**
     * Handle config room count command
     * @param parts Command parts
     * @return Result message
     */
    private String handleConfigRoomCount(String[] parts) {
        if (parts.length != 4) {
            return "Invalid command. Usage: config room count <number>";
        }
        
        try {
            int count = Integer.parseInt(parts[3]);
            return office.configureRooms(count);
        } catch (NumberFormatException e) {
            return "Invalid room count. Please enter a valid positive number.";
        } catch (IllegalArgumentException e) {
            return e.getMessage();
        }
    }
    
    /**
     * Handle config room max capacity command
     * @param parts Command parts
     * @return Result message
     */
    private String handleConfigRoomCapacity(String[] parts) {
        if (parts.length != 6) {
            return "Invalid command. Usage: config room max capacity <room> <capacity>";
        }
        
        try {
            int roomNumber = Integer.parseInt(parts[4]);
            int capacity = Integer.parseInt(parts[5]);
            return office.setRoomMaxCapacity(roomNumber, capacity);
        } catch (NumberFormatException e) {
            return "Invalid room number or capacity. Please enter valid numbers.";
        } catch (IllegalArgumentException | IllegalStateException e) {
            return e.getMessage();
        }
    }
    
    /**
     * Handle block room command
     * @param parts Command parts
     * @return Result message
     */
    private String handleBlockCommand(String[] parts) {
        if (parts.length != 5 || !"room".equals(parts[1])) {
            return "Invalid command. Usage: block room <number> <start_time> <duration>";
        }
        
        try {
            // Validate room number
            String roomStr = parts[2];
            if (!NON_NEGATIVE_INTEGER_PATTERN.matcher(roomStr).matches()) {
                return "Invalid room number. Please enter a valid room number.";
            }
            int roomNumber = Integer.parseInt(roomStr);
            
            // Validate time format
            String startTime = parts[3];
            if (!TIME_PATTERN.matcher(startTime).matches()) {
                return "Invalid time format. Please use HH:mm format (e.g., 09:00).";
            }
            
            // Validate duration
            String durationStr = parts[4];
            if (!POSITIVE_INTEGER_PATTERN.matcher(durationStr).matches()) {
                return "Invalid duration. Please enter a valid positive number.";
            }
            int duration = Integer.parseInt(durationStr);
            
            Command bookCommand = new BookRoomCommand(roomNumber, startTime, duration);
            return commandInvoker.executeCommand(bookCommand);
            
        } catch (NumberFormatException e) {
            return "Invalid input format. Please check room number and duration.";
        }
    }
    
    /**
     * Handle cancel room command
     * @param parts Command parts
     * @return Result message
     */
    private String handleCancelCommand(String[] parts) {
        if (parts.length != 3 || !"room".equals(parts[1])) {
            return "Invalid command. Usage: cancel room <number>";
        }
        
        try {
            String roomStr = parts[2];
            if (!NON_NEGATIVE_INTEGER_PATTERN.matcher(roomStr).matches()) {
                return "Invalid room number. Please enter a valid room number.";
            }
            int roomNumber = Integer.parseInt(roomStr);
            
            Command cancelCommand = new CancelBookingCommand(roomNumber);
            return commandInvoker.executeCommand(cancelCommand);
            
        } catch (NumberFormatException e) {
            return "Invalid room number. Please enter a valid number.";
        }
    }
    
    /**
     * Handle add occupant command
     * @param parts Command parts
     * @return Result message
     */
    private String handleAddCommand(String[] parts) {
        if (parts.length != 4 || !"occupant".equals(parts[1])) {
            return "Invalid command. Usage: add occupant <room> <count>";
        }
        
        try {
            String roomStr = parts[2];
            if (!NON_NEGATIVE_INTEGER_PATTERN.matcher(roomStr).matches()) {
                return "Invalid room number. Please enter a valid room number.";
            }
            int roomNumber = Integer.parseInt(roomStr);
            
            String countStr = parts[3];
            if (!NON_NEGATIVE_INTEGER_PATTERN.matcher(countStr).matches()) {
                return "Invalid occupant count. Please enter a valid non-negative number.";
            }
            int occupantCount = Integer.parseInt(countStr);
            
            Command addOccupantCommand = new AddOccupantCommand(roomNumber, occupantCount);
            return commandInvoker.executeCommand(addOccupantCommand);
            
        } catch (NumberFormatException e) {
            return "Invalid input format. Please check room number and occupant count.";
        }
    }
    
    /**
     * Handle room status command
     * @param parts Command parts
     * @return Result message
     */
    private String handleStatusCommand(String[] parts) {
        if (parts.length == 1) {
            // Show all rooms status
            return getAllRoomsStatus();
        } else if (parts.length == 2) {
            // Show specific room status
            try {
                int roomNumber = Integer.parseInt(parts[1]);
                return getRoomStatus(roomNumber);
            } catch (NumberFormatException e) {
                return "Invalid room number. Please enter a valid number.";
            }
        } else {
            return "Invalid command. Usage: status OR status <room_number>";
        }
    }
    
    /**
     * Get status of all rooms
     * @return Status message
     */
    private String getAllRoomsStatus() {
        if (!office.isConfigured()) {
            return "Office not configured. Please configure rooms first.";
        }
        
        StringBuilder status = new StringBuilder("=== Room Status Summary ===\\n");
        Map<Integer, Room> rooms = office.getAllRooms();
        
        for (Room room : rooms.values()) {
            status.append(room.getStatus()).append("\\n");
        }
        
        return status.toString();
    }
    
    /**
     * Get status of specific room
     * @param roomNumber Room number
     * @return Status message
     */
    private String getRoomStatus(int roomNumber) {
        if (!office.isConfigured()) {
            return "Office not configured. Please configure rooms first.";
        }
        
        if (!office.isValidRoomNumber(roomNumber)) {
            return "Room " + roomNumber + " does not exist.";
        }
        
        Room room = office.getRoom(roomNumber);
        return room != null ? room.getStatus() : "Room " + roomNumber + " not found.";
    }
    
    /**
     * Get command history
     * @return History message
     */
    private String getCommandHistory() {
        StringBuilder history = new StringBuilder("=== Command History ===\\n");
        var commands = commandInvoker.getCommandHistory();
        
        if (commands.isEmpty()) {
            history.append("No commands executed yet.\\n");
        } else {
            for (int i = 0; i < commands.size(); i++) {
                history.append((i + 1)).append(". ").append(commands.get(i).getDescription()).append("\\n");
            }
        }
        
        return history.toString();
    }
    
    /**
     * Print welcome message
     */
    private void printWelcomeMessage() {
        System.out.println("\\n=== Smart Office Facility Management System ===");
        System.out.println("Available commands:");
        System.out.println(getHelpMessage());
    }
    
    /**
     * Get help message
     * @return Help message
     */
    private String getHelpMessage() {
        return """
                Available Commands:
                
                Configuration:
                  config room count <number>                    - Configure office with N meeting rooms
                  config room max capacity <room> <capacity>    - Set maximum capacity for a room
                
                Booking Operations:
                  block room <number> <start_time> <duration>   - Book a room (time: HH:mm, duration: minutes)
                  cancel room <number>                          - Cancel room booking
                
                Occupancy Management:
                  add occupant <room> <count>                   - Set room occupancy (simulates sensor)
                
                Information:
                  status                                        - Show all rooms status
                  status <room_number>                          - Show specific room status
                  history                                       - Show command history
                  undo                                          - Undo last command
                
                System:
                  help                                          - Show this help message
                  exit/quit                                     - Exit the application
                
                Examples:
                  config room count 3
                  config room max capacity 1 10
                  block room 1 09:00 60
                  add occupant 1 2
                  cancel room 1
                """;
    }
    
    /**
     * Cleanup resources
     */
    private void cleanup() {
        if (office.isConfigured()) {
            Map<Integer, Room> rooms = office.getAllRooms();
            for (Room room : rooms.values()) {
                room.cleanup();
            }
        }
        
        scanner.close();
        System.out.println("Smart Office Management System shut down successfully.");
    }
}
