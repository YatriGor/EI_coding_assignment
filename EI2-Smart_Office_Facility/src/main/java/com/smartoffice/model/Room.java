package com.smartoffice.model;

import com.smartoffice.observer.OccupancyObserver;
import com.smartoffice.observer.OccupancySubject;
import com.smartoffice.systems.AirConditioningSystem;
import com.smartoffice.systems.LightingSystem;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Room class representing a conference room with occupancy detection and booking capabilities
 * 
 * Implements Observer Pattern as Subject for occupancy notifications
 * Handles automatic booking release after 5 minutes of non-occupancy
 */
public class Room implements OccupancySubject {
    
    private final int roomNumber;
    private final AtomicInteger maxCapacity;
    private final AtomicInteger currentOccupantCount;
    private final AtomicBoolean isOccupied;
    private final AtomicBoolean isBooked;
    private final List<OccupancyObserver> observers;
    
    // Booking information
    private BookingInfo currentBooking;
    private Timer autoReleaseTimer;
    private final Object bookingLock = new Object();
    
    // Constants
    private static final int MIN_OCCUPANTS_FOR_OCCUPIED = 2;
    private static final int AUTO_RELEASE_DELAY_MINUTES = 5;
    
    /**
     * Constructor for Room
     * @param roomNumber The room number
     */
    public Room(int roomNumber) {
        this.roomNumber = roomNumber;
        this.maxCapacity = new AtomicInteger(10); // Default capacity
        this.currentOccupantCount = new AtomicInteger(0);
        this.isOccupied = new AtomicBoolean(false);
        this.isBooked = new AtomicBoolean(false);
        this.observers = new ArrayList<>();
        
        // Register default observers (AC and Lighting systems)
        addObserver(new AirConditioningSystem());
        addObserver(new LightingSystem());
    }
    
    /**
     * Book the room for a specific time and duration
     * @param startTime Start time of booking
     * @param durationMinutes Duration in minutes
     * @return true if booking successful, false if already booked
     */
    public boolean bookRoom(LocalTime startTime, int durationMinutes) {
        synchronized (bookingLock) {
            if (isBooked.get()) {
                return false; // Already booked
            }
            
            currentBooking = new BookingInfo(startTime, durationMinutes);
            isBooked.set(true);
            
            // Start auto-release timer if room is not occupied
            if (!isOccupied.get()) {
                startAutoReleaseTimer();
            }
            
            return true;
        }
    }
    
    /**
     * Cancel the current booking
     */
    public void cancelBooking() {
        synchronized (bookingLock) {
            if (autoReleaseTimer != null) {
                autoReleaseTimer.cancel();
                autoReleaseTimer = null;
            }
            
            currentBooking = null;
            isBooked.set(false);
        }
    }
    
    /**
     * Update room occupancy
     * @param occupantCount New occupant count
     * @return Status message
     */
    public String updateOccupancy(int occupantCount) {
        int previousCount = currentOccupantCount.getAndSet(occupantCount);
        boolean wasOccupied = isOccupied.get();
        boolean nowOccupied = occupantCount >= MIN_OCCUPANTS_FOR_OCCUPIED;
        
        isOccupied.set(nowOccupied);
        
        // Handle occupancy change
        if (wasOccupied != nowOccupied) {
            handleOccupancyChange(nowOccupied);
        }
        
        // Notify observers
        notifyObservers(roomNumber, nowOccupied, occupantCount);
        
        // Generate appropriate message
        if (occupantCount == 0) {
            return generateUnoccupiedMessage();
        } else if (nowOccupied) {
            return "Room " + roomNumber + " is now occupied by " + occupantCount + " persons. AC and lights turned on.";
        } else {
            return "Room " + roomNumber + " has " + occupantCount + " person(s) but is not considered occupied (minimum " + MIN_OCCUPANTS_FOR_OCCUPIED + " required).";
        }
    }
    
    /**
     * Handle occupancy change logic
     * @param nowOccupied Current occupancy status
     */
    private void handleOccupancyChange(boolean nowOccupied) {
        synchronized (bookingLock) {
            if (nowOccupied) {
                // Room became occupied - cancel auto-release timer
                if (autoReleaseTimer != null) {
                    autoReleaseTimer.cancel();
                    autoReleaseTimer = null;
                }
            } else {
                // Room became unoccupied - start auto-release timer if booked
                if (isBooked.get()) {
                    startAutoReleaseTimer();
                }
            }
        }
    }
    
    /**
     * Generate message for unoccupied room
     * @return Appropriate message based on booking status
     */
    private String generateUnoccupiedMessage() {
        synchronized (bookingLock) {
            if (isBooked.get()) {
                return "Room " + roomNumber + " is now unoccupied. AC and lights turned off.";
            } else {
                return "Room " + roomNumber + " is now unoccupied. AC and lights turned off.";
            }
        }
    }
    
    /**
     * Start the auto-release timer for unoccupied booked rooms
     */
    private void startAutoReleaseTimer() {
        synchronized (bookingLock) {
            if (autoReleaseTimer != null) {
                autoReleaseTimer.cancel();
            }
            
            autoReleaseTimer = new Timer("AutoRelease-Room" + roomNumber, true);
            autoReleaseTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    synchronized (bookingLock) {
                        if (isBooked.get() && !isOccupied.get()) {
                            cancelBooking();
                            System.out.println("Room " + roomNumber + " is now unoccupied. Booking released. AC and lights off.");
                        }
                    }
                }
            }, AUTO_RELEASE_DELAY_MINUTES * 60 * 1000L); // Convert minutes to milliseconds
        }
    }
    
    // Observer Pattern Implementation
    @Override
    public void addObserver(OccupancyObserver observer) {
        synchronized (observers) {
            if (!observers.contains(observer)) {
                observers.add(observer);
            }
        }
    }
    
    @Override
    public void removeObserver(OccupancyObserver observer) {
        synchronized (observers) {
            observers.remove(observer);
        }
    }
    
    @Override
    public void notifyObservers(int roomNumber, boolean isOccupied, int occupantCount) {
        List<OccupancyObserver> observersCopy;
        synchronized (observers) {
            observersCopy = new ArrayList<>(observers);
        }
        
        for (OccupancyObserver observer : observersCopy) {
            try {
                observer.onOccupancyChanged(roomNumber, isOccupied, occupantCount);
            } catch (Exception e) {
                System.err.println("Error notifying observer: " + e.getMessage());
            }
        }
    }
    
    // Getters and Setters
    public int getRoomNumber() {
        return roomNumber;
    }
    
    public int getMaxCapacity() {
        return maxCapacity.get();
    }
    
    public void setMaxCapacity(int capacity) {
        this.maxCapacity.set(capacity);
    }
    
    public int getCurrentOccupantCount() {
        return currentOccupantCount.get();
    }
    
    public boolean isOccupied() {
        return isOccupied.get();
    }
    
    public boolean isBooked() {
        return isBooked.get();
    }
    
    public BookingInfo getCurrentBooking() {
        synchronized (bookingLock) {
            return currentBooking;
        }
    }
    
    /**
     * Get room status information
     * @return Room status string
     */
    public String getStatus() {
        StringBuilder status = new StringBuilder();
        status.append("Room ").append(roomNumber).append(": ");
        status.append("Occupants: ").append(currentOccupantCount.get()).append("/").append(maxCapacity.get());
        status.append(", Occupied: ").append(isOccupied.get() ? "Yes" : "No");
        status.append(", Booked: ").append(isBooked.get() ? "Yes" : "No");
        
        synchronized (bookingLock) {
            if (currentBooking != null) {
                status.append(" (").append(currentBooking.toString()).append(")");
            }
        }
        
        return status.toString();
    }
    
    /**
     * Clean up resources when room is no longer needed
     */
    public void cleanup() {
        synchronized (bookingLock) {
            if (autoReleaseTimer != null) {
                autoReleaseTimer.cancel();
                autoReleaseTimer = null;
            }
        }
        
        synchronized (observers) {
            observers.clear();
        }
    }
    
    /**
     * Inner class to hold booking information
     */
    public static class BookingInfo {
        private final LocalTime startTime;
        private final int durationMinutes;
        private final LocalTime endTime;
        
        public BookingInfo(LocalTime startTime, int durationMinutes) {
            this.startTime = startTime;
            this.durationMinutes = durationMinutes;
            this.endTime = startTime.plusMinutes(durationMinutes);
        }
        
        public LocalTime getStartTime() {
            return startTime;
        }
        
        public int getDurationMinutes() {
            return durationMinutes;
        }
        
        public LocalTime getEndTime() {
            return endTime;
        }
        
        @Override
        public String toString() {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
            return startTime.format(formatter) + "-" + endTime.format(formatter) + 
                   " (" + durationMinutes + " min)";
        }
    }
}
