package com.designpatterns.behavioral.observer;

import com.designpatterns.common.ValidationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.List;
import java.util.Map;

/**
 * Smart Home Hub - Subject in Observer pattern
 * Manages IoT devices and notifies observers of events
 * Thread-safe implementation with async notification
 */
public class SmartHomeHub {
    private static final Logger logger = LoggerFactory.getLogger(SmartHomeHub.class);
    
    private final List<SmartHomeObserver> observers;
    private final Map<String, Object> deviceStates;
    private final ExecutorService notificationExecutor;
    private volatile boolean isActive;
    
    public SmartHomeHub() {
        this.observers = new CopyOnWriteArrayList<>();
        this.deviceStates = new ConcurrentHashMap<>();
        this.notificationExecutor = Executors.newFixedThreadPool(3, r -> {
            Thread t = new Thread(r, "SmartHome-Notification-Thread");
            t.setDaemon(true);
            return t;
        });
        this.isActive = true;
        logger.info("Smart Home Hub initialized");
    }
    
    /**
     * Registers an observer to receive events
     */
    public synchronized void registerObserver(SmartHomeObserver observer) {
        ValidationUtils.requireNonNull(observer, "Observer cannot be null");
        
        if (!observers.contains(observer)) {
            observers.add(observer);
            logger.info("Observer '{}' registered successfully", observer.getObserverName());
        } else {
            logger.warn("Observer '{}' is already registered", observer.getObserverName());
        }
    }
    
    /**
     * Unregisters an observer
     */
    public synchronized void unregisterObserver(SmartHomeObserver observer) {
        ValidationUtils.requireNonNull(observer, "Observer cannot be null");
        
        if (observers.remove(observer)) {
            logger.info("Observer '{}' unregistered successfully", observer.getObserverName());
        } else {
            logger.warn("Observer '{}' was not registered", observer.getObserverName());
        }
    }
    
    /**
     * Publishes an event to all interested observers
     */
    public void publishEvent(SmartHomeEvent event) {
        ValidationUtils.requireNonNull(event, "Event cannot be null");
        
        if (!isActive) {
            logger.warn("Hub is not active, ignoring event: {}", event);
            return;
        }
        
        logger.debug("Publishing event: {}", event);
        
        // Update device state
        updateDeviceState(event);
        
        // Notify observers asynchronously
        List<SmartHomeObserver> interestedObservers = observers.stream()
            .filter(observer -> observer.isInterestedIn(event.getEventType()))
            .toList();
        
        if (interestedObservers.isEmpty()) {
            logger.debug("No observers interested in event type: {}", event.getEventType());
            return;
        }
        
        for (SmartHomeObserver observer : interestedObservers) {
            notificationExecutor.submit(() -> {
                try {
                    observer.onEventReceived(event);
                    logger.debug("Event delivered to observer: {}", observer.getObserverName());
                } catch (Exception e) {
                    logger.error("Error notifying observer '{}': {}", 
                               observer.getObserverName(), e.getMessage(), e);
                }
            });
        }
    }
    
    /**
     * Updates the internal state based on the event
     */
    private void updateDeviceState(SmartHomeEvent event) {
        String stateKey = event.getDeviceId() + "_" + event.getEventType().name();
        deviceStates.put(stateKey, event.getEventData());
        
        // Store last seen timestamp
        deviceStates.put(event.getDeviceId() + "_LAST_SEEN", event.getTimestamp());
    }
    
    /**
     * Gets the current state of a device
     */
    public Object getDeviceState(String deviceId, SmartHomeEvent.EventType eventType) {
        ValidationUtils.requireNonEmpty(deviceId, "Device ID cannot be empty");
        ValidationUtils.requireNonNull(eventType, "Event type cannot be null");
        
        String stateKey = deviceId + "_" + eventType.name();
        return deviceStates.get(stateKey);
    }
    
    /**
     * Gets all registered observers
     */
    public List<SmartHomeObserver> getObservers() {
        return List.copyOf(observers);
    }
    
    /**
     * Gets the number of registered observers
     */
    public int getObserverCount() {
        return observers.size();
    }
    
    /**
     * Checks if the hub is active
     */
    public boolean isActive() {
        return isActive;
    }
    
    /**
     * Shuts down the hub gracefully
     */
    public void shutdown() {
        logger.info("Shutting down Smart Home Hub...");
        isActive = false;
        
        notificationExecutor.shutdown();
        try {
            if (!notificationExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                notificationExecutor.shutdownNow();
                logger.warn("Forced shutdown of notification executor");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            notificationExecutor.shutdownNow();
        }
        
        observers.clear();
        deviceStates.clear();
        logger.info("Smart Home Hub shutdown completed");
    }
}

