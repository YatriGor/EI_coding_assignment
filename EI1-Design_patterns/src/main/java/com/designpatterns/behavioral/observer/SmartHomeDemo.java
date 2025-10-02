package com.designpatterns.behavioral.observer;

import com.designpatterns.behavioral.observer.observers.EnergyManagementObserver;
import com.designpatterns.behavioral.observer.observers.SecuritySystemObserver;
import com.designpatterns.common.ValidationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Demonstration of Observer Pattern with Smart Home IoT System
 * Shows real-time event processing and observer notifications
 */
public class SmartHomeDemo {
    private static final Logger logger = LoggerFactory.getLogger(SmartHomeDemo.class);
    
    private final SmartHomeHub hub;
    private final SecuritySystemObserver securityObserver;
    private final EnergyManagementObserver energyObserver;
    
    public SmartHomeDemo() {
        this.hub = new SmartHomeHub();
        this.securityObserver = new SecuritySystemObserver();
        this.energyObserver = new EnergyManagementObserver();
        
        // Register observers
        hub.registerObserver(securityObserver);
        hub.registerObserver(energyObserver);
        
        logger.info("Smart Home Demo initialized with {} observers", hub.getObserverCount());
    }
    
    public void runDemo(Scanner scanner) {
        logger.info("=== Smart Home IoT Observer Pattern Demo ===");
        
        boolean running = true;
        while (running) {
            displayMenu();
            
            try {
                String choice = scanner.nextLine().trim();
                running = handleMenuChoice(choice, scanner);
            } catch (Exception e) {
                logger.error("Error processing menu choice: {}", e.getMessage());
                System.out.println("❌ Error: " + e.getMessage());
            }
        }
        
        cleanup();
    }
    
    private void displayMenu() {
        System.out.println("\n🏠 Smart Home Control Panel:");
        System.out.println("1. Simulate Motion Detection");
        System.out.println("2. Simulate Door Events");
        System.out.println("3. Simulate Light Control");
        System.out.println("4. Simulate Temperature Change");
        System.out.println("5. Simulate Security Breach");
        System.out.println("6. Toggle Security System");
        System.out.println("7. View Energy Usage");
        System.out.println("8. Set Energy Budget");
        System.out.println("9. Run Automated Simulation");
        System.out.println("0. Back to Main Menu");
        System.out.print("Choose an option: ");
    }
    
    private boolean handleMenuChoice(String choice, Scanner scanner) {
        switch (choice) {
            case "1" -> simulateMotionDetection();
            case "2" -> simulateDoorEvents(scanner);
            case "3" -> simulateLightControl(scanner);
            case "4" -> simulateTemperatureChange(scanner);
            case "5" -> simulateSecurityBreach();
            case "6" -> toggleSecuritySystem();
            case "7" -> viewEnergyUsage();
            case "8" -> setEnergyBudget(scanner);
            case "9" -> runAutomatedSimulation();
            case "0" -> {
                return false;
            }
            default -> System.out.println("❌ Invalid option. Please try again.");
        }
        return true;
    }
    
    private void simulateMotionDetection() {
        String[] sensors = {"motion_sensor_living_room", "motion_sensor_kitchen", "motion_sensor_bedroom"};
        String sensorId = sensors[ThreadLocalRandom.current().nextInt(sensors.length)];
        
        SmartHomeEvent event = new SmartHomeEvent(
            sensorId,
            "motion_sensor",
            SmartHomeEvent.EventType.MOTION_DETECTED,
            "Motion detected in zone",
            Map.of("confidence", 0.95, "zone", sensorId.split("_")[2])
        );
        
        hub.publishEvent(event);
        System.out.println("✅ Motion detection event published");
    }
    
    private void simulateDoorEvents(Scanner scanner) {
        System.out.print("Enter door ID (e.g., front_door, back_door): ");
        String doorId = ValidationUtils.requireNonEmpty(scanner.nextLine().trim(), "Door ID cannot be empty");
        
        System.out.print("Is this authorized access? (y/n): ");
        boolean authorized = scanner.nextLine().trim().toLowerCase().startsWith("y");
        
        // Simulate door opening
        SmartHomeEvent openEvent = new SmartHomeEvent(
            doorId,
            "smart_lock",
            SmartHomeEvent.EventType.DOOR_OPENED,
            "Door opened",
            Map.of("authorized", authorized, "method", authorized ? "keypad" : "unknown")
        );
        hub.publishEvent(openEvent);
        
        // Simulate door closing after a delay
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        SmartHomeEvent closeEvent = new SmartHomeEvent(
            doorId,
            "smart_lock",
            SmartHomeEvent.EventType.DOOR_CLOSED,
            "Door closed",
            Map.of("auto_lock", true)
        );
        hub.publishEvent(closeEvent);
        
        System.out.println("✅ Door events published");
    }
    
    private void simulateLightControl(Scanner scanner) {
        System.out.print("Enter light ID (e.g., living_room_light, bedroom_light): ");
        String lightId = ValidationUtils.requireNonEmpty(scanner.nextLine().trim(), "Light ID cannot be empty");
        
        System.out.print("Turn light on or off? (on/off): ");
        String action = scanner.nextLine().trim().toLowerCase();
        
        SmartHomeEvent.EventType eventType = action.equals("on") ? 
            SmartHomeEvent.EventType.LIGHT_ON : SmartHomeEvent.EventType.LIGHT_OFF;
        
        SmartHomeEvent event = new SmartHomeEvent(
            lightId,
            "light",
            eventType,
            "Light " + action,
            Map.of("brightness", action.equals("on") ? 100 : 0, "energy_efficient", true)
        );
        
        hub.publishEvent(event);
        System.out.println("✅ Light control event published");
    }
    
    private void simulateTemperatureChange(Scanner scanner) {
        System.out.print("Enter thermostat ID (e.g., main_thermostat): ");
        String thermostatId = ValidationUtils.requireNonEmpty(scanner.nextLine().trim(), "Thermostat ID cannot be empty");
        
        System.out.print("Enter temperature (°C): ");
        String temperature = ValidationUtils.requireNonEmpty(scanner.nextLine().trim(), "Temperature cannot be empty");
        
        try {
            double temp = Double.parseDouble(temperature);
            ValidationUtils.requireInRange(temp, -10.0, 50.0, "Temperature must be between -10°C and 50°C");
            
            SmartHomeEvent event = new SmartHomeEvent(
                thermostatId,
                "thermostat",
                SmartHomeEvent.EventType.TEMPERATURE_CHANGE,
                String.valueOf(temp),
                Map.of("unit", "celsius", "sensor_accuracy", 0.1)
            );
            
            hub.publishEvent(event);
            System.out.println("✅ Temperature change event published");
            
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid temperature format");
        }
    }
    
    private void simulateSecurityBreach() {
        String[] devices = {"window_sensor_1", "door_sensor_main", "glass_break_detector"};
        String deviceId = devices[ThreadLocalRandom.current().nextInt(devices.length)];
        
        SmartHomeEvent event = new SmartHomeEvent(
            deviceId,
            "security_sensor",
            SmartHomeEvent.EventType.SECURITY_BREACH,
            "Unauthorized access detected",
            Map.of("severity", "HIGH", "location", "perimeter", "confidence", 0.98)
        );
        
        hub.publishEvent(event);
        System.out.println("✅ Security breach event published");
    }
    
    private void toggleSecuritySystem() {
        if (securityObserver.isArmed()) {
            securityObserver.disarmSystem();
            System.out.println("🔓 Security system DISARMED");
        } else {
            securityObserver.armSystem();
            System.out.println("🛡️ Security system ARMED");
        }
    }
    
    private void viewEnergyUsage() {
        System.out.println("\n⚡ Energy Usage Report:");
        System.out.printf("Current Usage: %.2f kWh%n", energyObserver.getCurrentEnergyUsage());
        System.out.printf("Daily Budget: %.2f kWh%n", energyObserver.getDailyEnergyBudget());
        System.out.printf("Budget Used: %.1f%%%n", 
                         (energyObserver.getCurrentEnergyUsage() / energyObserver.getDailyEnergyBudget()) * 100);
        System.out.printf("Energy Saving Actions: %d%n", energyObserver.getEnergySavingActions());
        
        System.out.println("\nDevice Consumption:");
        energyObserver.getDeviceEnergyConsumption().forEach((device, consumption) ->
            System.out.printf("  %s: %.3f kWh%n", device, consumption));
    }
    
    private void setEnergyBudget(Scanner scanner) {
        System.out.print("Enter daily energy budget (kWh): ");
        try {
            double budget = Double.parseDouble(scanner.nextLine().trim());
            ValidationUtils.requireInRange(budget, 1.0, 1000.0, "Budget must be between 1 and 1000 kWh");
            
            energyObserver.setDailyEnergyBudget(budget);
            System.out.println("✅ Energy budget updated");
            
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid budget format");
        }
    }
    
    private void runAutomatedSimulation() {
        System.out.println("🤖 Running automated simulation for 10 seconds...");
        
        long endTime = System.currentTimeMillis() + 10000;
        int eventCount = 0;
        
        while (System.currentTimeMillis() < endTime) {
            try {
                // Generate random events
                generateRandomEvent();
                eventCount++;
                
                Thread.sleep(1000 + ThreadLocalRandom.current().nextInt(2000));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        
        System.out.printf("✅ Simulation completed. Generated %d events%n", eventCount);
    }
    
    private void generateRandomEvent() {
        SmartHomeEvent.EventType[] eventTypes = SmartHomeEvent.EventType.values();
        SmartHomeEvent.EventType eventType = eventTypes[ThreadLocalRandom.current().nextInt(eventTypes.length)];
        
        String deviceId = "device_" + ThreadLocalRandom.current().nextInt(1, 6);
        String deviceType = getDeviceTypeForEvent(eventType);
        String eventData = generateEventData(eventType);
        
        SmartHomeEvent event = new SmartHomeEvent(
            deviceId,
            deviceType,
            eventType,
            eventData,
            Map.of("automated", true, "simulation", true)
        );
        
        hub.publishEvent(event);
    }
    
    private String getDeviceTypeForEvent(SmartHomeEvent.EventType eventType) {
        return switch (eventType) {
            case TEMPERATURE_CHANGE -> "thermostat";
            case MOTION_DETECTED -> "motion_sensor";
            case DOOR_OPENED, DOOR_CLOSED -> "smart_lock";
            case LIGHT_ON, LIGHT_OFF -> "light";
            case SECURITY_BREACH -> "security_sensor";
            case DEVICE_OFFLINE, DEVICE_ONLINE -> "generic_device";
            case BATTERY_LOW, MAINTENANCE_REQUIRED -> "sensor";
        };
    }
    
    private String generateEventData(SmartHomeEvent.EventType eventType) {
        return switch (eventType) {
            case TEMPERATURE_CHANGE -> String.valueOf(15 + ThreadLocalRandom.current().nextInt(20));
            case MOTION_DETECTED -> "Motion in zone " + ThreadLocalRandom.current().nextInt(1, 5);
            case DOOR_OPENED -> "Door opened";
            case DOOR_CLOSED -> "Door closed";
            case LIGHT_ON -> "Light turned on";
            case LIGHT_OFF -> "Light turned off";
            case SECURITY_BREACH -> "Unauthorized access attempt";
            case DEVICE_OFFLINE -> "Device went offline";
            case DEVICE_ONLINE -> "Device came online";
            case BATTERY_LOW -> "Battery level: " + ThreadLocalRandom.current().nextInt(5, 20) + "%";
            case MAINTENANCE_REQUIRED -> "Maintenance due";
        };
    }
    
    private void cleanup() {
        logger.info("Cleaning up Smart Home Demo...");
        hub.shutdown();
    }
}

