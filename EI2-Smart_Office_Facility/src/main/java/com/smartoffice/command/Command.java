package com.smartoffice.command;

/**
 * Command interface for implementing the Command Pattern
 * 
 * Design Pattern: Command Pattern
 * - Encapsulates requests as objects
 * - Allows for flexible and extendable operations
 * - Supports undo operations and command queuing
 */
public interface Command {
    
    /**
     * Execute the command
     * @return Result message from command execution
     */
    String execute();
    
    /**
     * Undo the command (optional implementation)
     * @return Result message from command undo
     */
    default String undo() {
        return "Undo not supported for this command";
    }
    
    /**
     * Get command description
     * @return Description of what this command does
     */
    String getDescription();
}
