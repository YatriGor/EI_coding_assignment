# Smart Office Facility Management System

A comprehensive Java console application for managing smart office facilities with conference room bookings, occupancy detection, and automated control systems.

## Features

### Core Functionality
- **Room Configuration**: Configure office with multiple meeting rooms
- **Room Booking**: Book and cancel conference room reservations
- **Occupancy Detection**: Sensor-based occupancy tracking (minimum 2 people for occupied status)
- **Automated Controls**: Automatic AC and lighting control based on occupancy
- **Auto-Release**: Unoccupied rooms automatically release bookings after 5 minutes
- **Real-time Status**: View room status and usage statistics

### Design Patterns Implemented

#### 1. Singleton Pattern
- **OfficeConfiguration**: Ensures single instance of office configuration throughout the application
- Thread-safe implementation using double-checked locking
- Manages global state and room instances

#### 2. Observer Pattern
- **OccupancySubject/OccupancyObserver**: Rooms notify control systems of occupancy changes
- **AirConditioningSystem**: Automatically controls AC based on occupancy
- **LightingSystem**: Automatically controls lights based on occupancy
- Loose coupling between occupancy detection and control systems

#### 3. Command Pattern
- **BookRoomCommand**: Encapsulates room booking operations
- **CancelBookingCommand**: Encapsulates booking cancellation
- **AddOccupantCommand**: Encapsulates occupancy updates
- **CommandInvoker**: Manages command execution and history with undo support

## Project Structure

```
src/main/java/com/smartoffice/
├── SmartOfficeApplication.java          # Main application entry point
├── command/                             # Command Pattern implementation
│   ├── Command.java                     # Command interface
│   ├── BookRoomCommand.java            # Room booking command
│   ├── CancelBookingCommand.java       # Booking cancellation command
│   ├── AddOccupantCommand.java         # Occupancy update command
│   └── CommandInvoker.java             # Command execution manager
├── config/                             # Configuration management
│   └── OfficeConfiguration.java       # Singleton office configuration
├── model/                              # Domain models
│   └── Room.java                       # Room entity with occupancy logic
├── observer/                           # Observer Pattern implementation
│   ├── OccupancyObserver.java         # Observer interface
│   └── OccupancySubject.java          # Subject interface
├── systems/                            # Control systems
│   ├── AirConditioningSystem.java     # AC control observer
│   └── LightingSystem.java            # Lighting control observer
├── ui/                                 # User interface
│   └── ConsoleInterface.java          # Console-based UI
├── util/                               # Utility classes
│   └── ValidationUtils.java           # Input validation utilities
└── exception/                          # Custom exceptions
    └── SmartOfficeException.java      # Application-specific exceptions
```

## Usage Examples

### Basic Configuration
```
config room count 3
# Output: Office configured with 3 meeting rooms: Room 1, Room 2, Room 3.

config room max capacity 1 10
# Output: Room 1 maximum capacity set to 10.
```

### Room Booking
```
block room 1 09:00 60
# Output: Room 1 booked from 09:00 for 60 minutes.

cancel room 1
# Output: Booking for Room 1 cancelled successfully.
```

### Occupancy Management
```
add occupant 1 2
# Output: Room 1 is now occupied by 2 persons. AC and lights turned on.

add occupant 1 0
# Output: Room 1 is now unoccupied. AC and lights turned off.
```

### Status Monitoring
```
status
# Shows all rooms status

status 1
# Shows Room 1 specific status
```

## Command Reference

### Configuration Commands
- `config room count <number>` - Configure office with N meeting rooms
- `config room max capacity <room> <capacity>` - Set maximum capacity for a room

### Booking Commands
- `block room <number> <start_time> <duration>` - Book a room (time: HH:mm, duration: minutes)
- `cancel room <number>` - Cancel room booking

### Occupancy Commands
- `add occupant <room> <count>` - Set room occupancy (simulates sensor input)

### Information Commands
- `status` - Show all rooms status
- `status <room_number>` - Show specific room status
- `history` - Show command history
- `undo` - Undo last command

### System Commands
- `help` - Show help message
- `exit` or `quit` - Exit the application

## Key Features

### Automatic Booking Release
- Rooms booked but not occupied within 5 minutes are automatically released
- Timer-based implementation with proper cleanup

### Thread Safety
- Concurrent access handling using atomic variables and synchronization
- Thread-safe singleton implementation
- Safe observer notification

### Input Validation
- Comprehensive validation for all user inputs
- Proper error messages for invalid inputs
- Regex-based format validation

### Error Handling
- Custom exception hierarchy
- Graceful error recovery
- Detailed error messages

## Technical Highlights

### SOLID Principles
- **Single Responsibility**: Each class has a single, well-defined purpose
- **Open/Closed**: Extensible design through interfaces and abstract classes
- **Liskov Substitution**: Proper inheritance hierarchies
- **Interface Segregation**: Focused, cohesive interfaces
- **Dependency Inversion**: Depends on abstractions, not concretions

### Best Practices
- Comprehensive documentation with JavaDoc
- Consistent naming conventions
- Proper resource management and cleanup
- Defensive programming with input validation
- Separation of concerns

### Performance Considerations
- Efficient data structures (ConcurrentHashMap for thread safety)
- Minimal object creation in hot paths
- Proper timer management for auto-release functionality

## Running the Application

### Prerequisites
- Java 11 or higher
- No external dependencies required

### Compilation and Execution
```bash
# Compile the application
javac -d out src/main/java/com/smartoffice/**/*.java

# Run the application
java -cp out com.smartoffice.SmartOfficeApplication
```

### Sample Session
```
=== Smart Office Facility Management System ===
Welcome to the Smart Office Management Console!

Enter command: config room count 3
Office configured with 3 meeting rooms: Room 1, Room 2, Room 3.

Enter command: block room 1 09:00 60
Room 1 booked from 09:00 for 60 minutes.

Enter command: add occupant 1 2
Room 1 is now occupied by 2 persons. AC and lights turned on.
[Air Conditioning System] AC turned on for Room 1
[Lighting System] Lights turned on for Room 1

Enter command: add occupant 1 0
Room 1 is now unoccupied. AC and lights turned off.
[Air Conditioning System] AC turned off for Room 1
[Lighting System] Lights turned off for Room 1

Enter command: status
=== Room Status Summary ===
Room 1: Occupants: 0/10, Occupied: No, Booked: Yes (09:00-10:00 (60 min))
Room 2: Occupants: 0/10, Occupied: No, Booked: No
Room 3: Occupants: 0/10, Occupied: No, Booked: No

Enter command: exit
Goodbye!
Smart Office Management System shut down successfully.
```

## Testing Scenarios

The application handles all specified test cases:

### Positive Cases ✅
- Room configuration with multiple rooms
- Room capacity setting
- Successful room booking
- Occupancy detection with automatic AC/lights control
- Booking cancellation

### Negative Cases ✅
- Duplicate booking attempts
- Cancelling non-existent bookings
- Insufficient occupancy (< 2 people)
- Invalid room numbers
- Invalid input formats
- Automatic booking release for unoccupied rooms

## Future Enhancements

- User authentication system
- Email/SMS notifications
- Room usage statistics and reporting
- Web-based interface
- Database persistence
- Multi-day booking support
- Recurring bookings
- Integration with calendar systems

## Architecture Benefits

1. **Maintainability**: Clear separation of concerns and modular design
2. **Extensibility**: Easy to add new features through established patterns
3. **Testability**: Loosely coupled components enable comprehensive testing
4. **Scalability**: Thread-safe design supports concurrent operations
5. **Reliability**: Comprehensive error handling and input validation
