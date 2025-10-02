package com.designpatterns.behavioral.observer.observers;

import com.designpatterns.behavioral.observer.SmartHomeEvent;
import com.designpatterns.behavioral.observer.SmartHomeObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Security System Observer - monitors security-related events
 * Implements specific logic for security event handling
 */
public class SecuritySystemObserver implements SmartHomeObserver {
    private static final Logger logger = LoggerFactory.getLogger(SecuritySystemObserver.class);
    
    private static final Set<SmartHomeEvent.EventType> SECURITY_EVENTS = Set.of(
        SmartHomeEvent.EventType.MOTION_DETECTED,
        SmartHomeEvent.EventType.DOOR_OPENED,
        SmartHomeEvent.EventType.DOOR_CLOSED,
        SmartHomeEvent.EventType.SECURITY_BREACH
    );
    
    private final AtomicInteger alertCount = new AtomicInteger(0);
    private volatile boolean armed = true;
    
    @Override
    public void onEventReceived(SmartHomeEvent event) {
        if (!armed) {
            logger.debug("Security system is disarmed, ignoring event: {}", event.getEventType());
            return;
        }
        
        switch (event.getEventType()) {
            case MOTION_DETECTED -> handleMotionDetected(event);
            case DOOR_OPENED -> handleDoorOpened(event);
            case DOOR_CLOSED -> handleDoorClosed(event);
            case SECURITY_BREACH -> handleSecurityBreach(event);
            default -> logger.debug("Received non-security event: {}", event.getEventType());
        }
    }
    
    private void handleMotionDetected(SmartHomeEvent event) {
        logger.warn("🚨 MOTION DETECTED: Device {} detected motion at {}", 
                   event.getDeviceId(), event.getTimestamp());
        
        // Check if it's during night hours (simplified logic)
        int hour = event.getTimestamp().getHour();
        if (hour >= 22 || hour <= 6) {
            triggerAlert("Night-time motion detected", event);
        } else {
            logger.info("Daytime motion detected - logging for review");
        }
    }
    
    private void handleDoorOpened(SmartHomeEvent event) {
        logger.warn("🚪 DOOR OPENED: {} opened at {}", 
                   event.getDeviceId(), event.getTimestamp());
        
        // Check metadata for authorization
        Object authorized = event.getMetadata().get("authorized");
        if (authorized == null || !Boolean.parseBoolean(authorized.toString())) {
            triggerAlert("Unauthorized door access", event);
        } else {
            logger.info("Authorized door access logged");
        }
    }
    
    private void handleDoorClosed(SmartHomeEvent event) {
        logger.info("🔒 DOOR CLOSED: {} closed at {}", 
                   event.getDeviceId(), event.getTimestamp());
    }
    
    private void handleSecurityBreach(SmartHomeEvent event) {
        logger.error("🚨🚨 SECURITY BREACH DETECTED: {} - {}", 
                    event.getDeviceId(), event.getEventData());
        triggerAlert("CRITICAL: Security breach detected", event);
    }
    
    private void triggerAlert(String alertMessage, SmartHomeEvent event) {
        int currentAlerts = alertCount.incrementAndGet();
        
        logger.error("🚨 SECURITY ALERT #{}: {} - Device: {}, Time: {}", 
                    currentAlerts, alertMessage, event.getDeviceId(), event.getTimestamp());
        
        // In a real system, this would:
        // - Send notifications to mobile app
        // - Contact security company
        // - Trigger sirens/lights
        // - Record video footage
        
        simulateSecurityResponse(alertMessage, event);
    }
    
    private void simulateSecurityResponse(String alertMessage, SmartHomeEvent event) {
        logger.info("📱 Sending push notification to homeowner");
        logger.info("📞 Contacting security monitoring service");
        logger.info("📹 Starting video recording from nearby cameras");
        
        // Simulate response time
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    public void armSystem() {
        armed = true;
        logger.info("🛡️ Security system ARMED");
    }
    
    public void disarmSystem() {
        armed = false;
        logger.info("🔓 Security system DISARMED");
    }
    
    public boolean isArmed() {
        return armed;
    }
    
    public int getAlertCount() {
        return alertCount.get();
    }
    
    public void resetAlertCount() {
        alertCount.set(0);
        logger.info("Security alert count reset");
    }
    
    @Override
    public String getObserverName() {
        return "SecuritySystemObserver";
    }
    
    @Override
    public boolean isInterestedIn(SmartHomeEvent.EventType eventType) {
        return SECURITY_EVENTS.contains(eventType);
    }
}

