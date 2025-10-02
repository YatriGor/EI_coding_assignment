package com.designpatterns.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Manages the application state and lifecycle
 * Implements defensive programming and proper state management
 */
public class ApplicationState {
    private static final Logger logger = LoggerFactory.getLogger(ApplicationState.class);
    
    private volatile boolean running;
    private volatile boolean shutdownRequested;
    
    public ApplicationState() {
        this.running = false;
        this.shutdownRequested = false;
        logger.info("Application state initialized");
    }
    
    public synchronized void start() {
        if (!running && !shutdownRequested) {
            running = true;
            logger.info("Application state set to running");
        }
    }
    
    public synchronized void requestShutdown() {
        if (running) {
            shutdownRequested = true;
            logger.info("Shutdown requested");
        }
    }
    
    public synchronized void stop() {
        running = false;
        shutdownRequested = false;
        logger.info("Application stopped");
    }
    
    public boolean isRunning() {
        return running && !shutdownRequested;
    }
    
    public boolean isShutdownRequested() {
        return shutdownRequested;
    }
}

