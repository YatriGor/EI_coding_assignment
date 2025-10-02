package com.designpatterns.behavioral.observer.observers;

import com.designpatterns.behavioral.observer.SmartHomeEvent;
import com.designpatterns.behavioral.observer.SmartHomeObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Energy Management Observer - optimizes energy consumption
 * Tracks device usage and implements energy-saving strategies
 */
public class EnergyManagementObserver implements SmartHomeObserver {
    private static final Logger logger = LoggerFactory.getLogger(EnergyManagementObserver.class);
    
    private static final Set<SmartHomeEvent.EventType> ENERGY_EVENTS = Set.of(
        SmartHomeEvent.EventType.LIGHT_ON,
        SmartHomeEvent.EventType.LIGHT_OFF,
        SmartHomeEvent.EventType.TEMPERATURE_CHANGE,
        SmartHomeEvent.EventType.DEVICE_ONLINE,
        SmartHomeEvent.EventType.DEVICE_OFFLINE
    );
    
    private final Map<String, Double> deviceEnergyConsumption = new ConcurrentHashMap<>();
    private final Map<String, Long> deviceOnTime = new ConcurrentHashMap<>();
    private final AtomicInteger energySavingActions = new AtomicInteger(0);
    private volatile double dailyEnergyBudget = 100.0; // kWh
    private volatile double currentEnergyUsage = 0.0;
    
    @Override
    public void onEventReceived(SmartHomeEvent event) {
        switch (event.getEventType()) {
            case LIGHT_ON -> handleLightOn(event);
            case LIGHT_OFF -> handleLightOff(event);
            case TEMPERATURE_CHANGE -> handleTemperatureChange(event);
            case DEVICE_ONLINE -> handleDeviceOnline(event);
            case DEVICE_OFFLINE -> handleDeviceOffline(event);
            default -> logger.debug("Received non-energy event: {}", event.getEventType());
        }
        
        // Check if we're approaching energy budget limit
        checkEnergyBudget();
    }
    
    private void handleLightOn(SmartHomeEvent event) {
        String deviceId = event.getDeviceId();
        deviceOnTime.put(deviceId, System.currentTimeMillis());
        
        logger.info("💡 Light ON: {} - Starting energy tracking", deviceId);
        
        // Estimate power consumption (simplified)
        double estimatedWatts = getDevicePowerRating(deviceId, "light");
        updateEnergyConsumption(deviceId, estimatedWatts);
    }
    
    private void handleLightOff(SmartHomeEvent event) {
        String deviceId = event.getDeviceId();
        Long onTime = deviceOnTime.remove(deviceId);
        
        if (onTime != null) {
            long durationMs = System.currentTimeMillis() - onTime;
            double durationHours = durationMs / (1000.0 * 60.0 * 60.0);
            double energyUsed = getDevicePowerRating(deviceId, "light") * durationHours / 1000.0; // kWh
            
            currentEnergyUsage += energyUsed;
            deviceEnergyConsumption.merge(deviceId, energyUsed, Double::sum);
            
            logger.info("💡 Light OFF: {} - Used {:.3f} kWh (on for {:.2f} hours)", 
                       deviceId, energyUsed, durationHours);
        }
    }
    
    private void handleTemperatureChange(SmartHomeEvent event) {
        try {
            double temperature = Double.parseDouble(event.getEventData());
            String deviceId = event.getDeviceId();
            
            logger.info("🌡️ Temperature change: {} - {}°C", deviceId, temperature);
            
            // Implement smart thermostat logic
            if (temperature > 25.0) {
                suggestCoolingOptimization(deviceId, temperature);
            } else if (temperature < 18.0) {
                suggestHeatingOptimization(deviceId, temperature);
            }
            
        } catch (NumberFormatException e) {
            logger.warn("Invalid temperature data: {}", event.getEventData());
        }
    }
    
    private void handleDeviceOnline(SmartHomeEvent event) {
        String deviceId = event.getDeviceId();
        logger.info("🔌 Device ONLINE: {} - Adding to energy monitoring", deviceId);
        
        // Start tracking energy consumption
        double standbyPower = getDevicePowerRating(deviceId, event.getDeviceType());
        updateEnergyConsumption(deviceId, standbyPower);
    }
    
    private void handleDeviceOffline(SmartHomeEvent event) {
        String deviceId = event.getDeviceId();
        logger.info("🔌 Device OFFLINE: {} - Removing from energy monitoring", deviceId);
        
        // Stop tracking and calculate final consumption
        deviceOnTime.remove(deviceId);
    }
    
    private void suggestCoolingOptimization(String deviceId, double temperature) {
        if (temperature > 27.0) {
            logger.warn("🌡️ High temperature detected ({}°C). Suggesting energy-efficient cooling:", temperature);
            logger.info("   • Increase thermostat to 24°C to save energy");
            logger.info("   • Close blinds to reduce solar heat gain");
            logger.info("   • Use ceiling fans to improve air circulation");
            
            energySavingActions.incrementAndGet();
        }
    }
    
    private void suggestHeatingOptimization(String deviceId, double temperature) {
        if (temperature < 16.0) {
            logger.warn("🌡️ Low temperature detected ({}°C). Suggesting energy-efficient heating:", temperature);
            logger.info("   • Lower thermostat to 20°C during day, 18°C at night");
            logger.info("   • Check for drafts around windows and doors");
            logger.info("   • Use zone heating for occupied areas only");
            
            energySavingActions.incrementAndGet();
        }
    }
    
    private void checkEnergyBudget() {
        double usagePercentage = (currentEnergyUsage / dailyEnergyBudget) * 100;
        
        if (usagePercentage > 80) {
            logger.warn("⚡ Energy usage at {:.1f}% of daily budget ({:.2f}/{:.2f} kWh)", 
                       usagePercentage, currentEnergyUsage, dailyEnergyBudget);
            
            if (usagePercentage > 90) {
                triggerEnergySavingMode();
            }
        }
    }
    
    private void triggerEnergySavingMode() {
        logger.error("🚨 ENERGY SAVING MODE ACTIVATED - Usage exceeds 90% of budget!");
        logger.info("   • Dimming non-essential lights");
        logger.info("   • Adjusting thermostat for energy savings");
        logger.info("   • Scheduling high-consumption devices for off-peak hours");
        
        energySavingActions.incrementAndGet();
    }
    
    private double getDevicePowerRating(String deviceId, String deviceType) {
        // Simplified power ratings (watts)
        return switch (deviceType.toLowerCase()) {
            case "light" -> 60.0;
            case "thermostat" -> 200.0;
            case "security_camera" -> 15.0;
            case "smart_lock" -> 5.0;
            case "motion_sensor" -> 2.0;
            default -> 10.0;
        };
    }
    
    private void updateEnergyConsumption(String deviceId, double watts) {
        // This would normally be called periodically
        // For demo purposes, we'll just log the power rating
        logger.debug("Device {} consuming {} watts", deviceId, watts);
    }
    
    public double getCurrentEnergyUsage() {
        return currentEnergyUsage;
    }
    
    public double getDailyEnergyBudget() {
        return dailyEnergyBudget;
    }
    
    public void setDailyEnergyBudget(double budget) {
        this.dailyEnergyBudget = budget;
        logger.info("Daily energy budget set to {:.2f} kWh", budget);
    }
    
    public int getEnergySavingActions() {
        return energySavingActions.get();
    }
    
    public Map<String, Double> getDeviceEnergyConsumption() {
        return Map.copyOf(deviceEnergyConsumption);
    }
    
    public void resetDailyUsage() {
        currentEnergyUsage = 0.0;
        deviceEnergyConsumption.clear();
        deviceOnTime.clear();
        energySavingActions.set(0);
        logger.info("Daily energy usage reset");
    }
    
    @Override
    public String getObserverName() {
        return "EnergyManagementObserver";
    }
    
    @Override
    public boolean isInterestedIn(SmartHomeEvent.EventType eventType) {
        return ENERGY_EVENTS.contains(eventType);
    }
}

