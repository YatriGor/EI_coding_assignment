package com.designpatterns.behavioral.observer;

/**
 * Observer interface for smart home system
 * Defines contract for receiving smart home events
 */
public interface SmartHomeObserver {
    
    /**
     * Called when a smart home event occurs
     * @param event The event that occurred
     */
    void onEventReceived(SmartHomeEvent event);
    
    /**
     * Returns the name/identifier of this observer
     * @return Observer name
     */
    String getObserverName();
    
    /**
     * Indicates if this observer is interested in the given event type
     * @param eventType The event type to check
     * @return true if interested, false otherwise
     */
    default boolean isInterestedIn(SmartHomeEvent.EventType eventType) {
        return true; // By default, interested in all events
    }
}

