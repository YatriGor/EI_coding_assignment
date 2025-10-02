package com.designpatterns.behavioral.observer;

import com.designpatterns.common.ValidationUtils;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;

/**
 * Represents an event in the smart home system
 * Immutable event object with comprehensive data
 */
public final class SmartHomeEvent {
    private final String deviceId;
    private final String deviceType;
    private final EventType eventType;
    private final String eventData;
    private final LocalDateTime timestamp;
    private final Map<String, Object> metadata;
    
    public enum EventType {
        TEMPERATURE_CHANGE,
        MOTION_DETECTED,
        DOOR_OPENED,
        DOOR_CLOSED,
        LIGHT_ON,
        LIGHT_OFF,
        SECURITY_BREACH,
        DEVICE_OFFLINE,
        DEVICE_ONLINE,
        BATTERY_LOW,
        MAINTENANCE_REQUIRED
    }
    
    public SmartHomeEvent(String deviceId, String deviceType, EventType eventType, 
                         String eventData, Map<String, Object> metadata) {
        this.deviceId = ValidationUtils.requireNonEmpty(deviceId, "Device ID cannot be empty");
        this.deviceType = ValidationUtils.requireNonEmpty(deviceType, "Device type cannot be empty");
        this.eventType = ValidationUtils.requireNonNull(eventType, "Event type cannot be null");
        this.eventData = eventData != null ? eventData : "";
        this.timestamp = LocalDateTime.now();
        this.metadata = metadata != null ? Map.copyOf(metadata) : Map.of();
    }
    
    // Getters
    public String getDeviceId() { return deviceId; }
    public String getDeviceType() { return deviceType; }
    public EventType getEventType() { return eventType; }
    public String getEventData() { return eventData; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public Map<String, Object> getMetadata() { return metadata; }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        SmartHomeEvent that = (SmartHomeEvent) obj;
        return Objects.equals(deviceId, that.deviceId) &&
               Objects.equals(deviceType, that.deviceType) &&
               eventType == that.eventType &&
               Objects.equals(eventData, that.eventData) &&
               Objects.equals(timestamp, that.timestamp);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(deviceId, deviceType, eventType, eventData, timestamp);
    }
    
    @Override
    public String toString() {
        return String.format("SmartHomeEvent{deviceId='%s', deviceType='%s', eventType=%s, " +
                           "eventData='%s', timestamp=%s}", 
                           deviceId, deviceType, eventType, eventData, timestamp);
    }
}

