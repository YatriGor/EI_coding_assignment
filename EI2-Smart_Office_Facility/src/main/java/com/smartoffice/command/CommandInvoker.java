package com.smartoffice.command;

import java.util.ArrayList;
import java.util.List;

/**
 * Command invoker that executes commands and maintains command history
 * 
 * Design Pattern: Command Pattern
 * - Manages command execution and history
 * - Supports undo operations
 * - Decouples command execution from command creation
 */
public class CommandInvoker {
    
    private final List<Command> commandHistory;
    private int currentCommandIndex;
    
    public CommandInvoker() {
        this.commandHistory = new ArrayList<>();
        this.currentCommandIndex = -1;
    }
    
    /**
     * Execute a command and add it to history
     * @param command Command to execute
     * @return Result of command execution
     */
    public String executeCommand(Command command) {
        try {
            String result = command.execute();
            
            // Add to history only if execution was successful (no error messages)
            if (!result.toLowerCase().contains("error") && 
                !result.toLowerCase().contains("invalid") &&
                !result.toLowerCase().contains("does not exist") &&
                !result.toLowerCase().contains("cannot")) {
                
                // Remove any commands after current index (for redo functionality)
                if (currentCommandIndex < commandHistory.size() - 1) {
                    commandHistory.subList(currentCommandIndex + 1, commandHistory.size()).clear();
                }
                
                commandHistory.add(command);
                currentCommandIndex++;
            }
            
            return result;
        } catch (Exception e) {
            return "Error executing command: " + e.getMessage();
        }
    }
    
    /**
     * Undo the last executed command
     * @return Result of undo operation
     */
    public String undoLastCommand() {
        if (currentCommandIndex >= 0 && currentCommandIndex < commandHistory.size()) {
            Command lastCommand = commandHistory.get(currentCommandIndex);
            String result = lastCommand.undo();
            currentCommandIndex--;
            return result;
        }
        return "No command to undo.";
    }
    
    /**
     * Get command history
     * @return List of executed commands
     */
    public List<Command> getCommandHistory() {
        return new ArrayList<>(commandHistory);
    }
    
    /**
     * Clear command history
     */
    public void clearHistory() {
        commandHistory.clear();
        currentCommandIndex = -1;
    }
    
    /**
     * Get the number of commands in history
     * @return Number of commands
     */
    public int getHistorySize() {
        return commandHistory.size();
    }
}
