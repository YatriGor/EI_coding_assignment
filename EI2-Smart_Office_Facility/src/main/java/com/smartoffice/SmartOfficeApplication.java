package com.smartoffice;

import com.smartoffice.config.OfficeConfiguration;
import com.smartoffice.ui.ConsoleInterface;

/**
 * Main application class for the Smart Office Facility Management System
 * 
 * This application demonstrates the implementation of:
 * - Singleton Pattern: For office configuration management
 * - Observer Pattern: For occupancy sensors and control systems
 * - Command Pattern: For booking operations
 * 
 * @author Smart Office Team
 * @version 1.0
 */
public class SmartOfficeApplication {
    
    public static void main(String[] args) {
        System.out.println("=== Smart Office Facility Management System ===");
        System.out.println("Welcome to the Smart Office Management Console!");
        System.out.println();
        
        // Initialize the office configuration (Singleton)
        OfficeConfiguration office = OfficeConfiguration.getInstance();
        
        // Start the console interface
        ConsoleInterface console = new ConsoleInterface();
        console.start();
    }
}
