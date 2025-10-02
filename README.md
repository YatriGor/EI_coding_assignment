# EI Coding Assignment

## Overview
This repository contains solutions for two programming exercises designed to demonstrate proficiency in software design patterns, best coding practices, and system design.

---

## Exercise 1: Design Patterns

### Problem Statement
Develop six different use cases to demonstrate understanding of software design patterns by implementing the same. The use cases are divided as follows:

- Two use cases demonstrating two different **behavioral design patterns**.
- Two use cases demonstrating two different **creational design patterns**.
- Two use cases demonstrating two different **structural design patterns**.

Each use case showcases practical applications of these design patterns through coding examples.

---

## Exercise 2: Smart Office Facility Programming Exercise

### Problem Statement
Design a console-based application to manage a smart office facility. The system should handle conference room bookings, occupancy detection, and automate control of air conditioning and lighting based on room occupancy. This exercise evaluates the ability to implement best coding practices, design patterns, and create an efficient, maintainable, and scalable solution.

### Functional Requirements

#### Mandatory Requirements
1. Configure the office facility by specifying the number of meeting rooms.
2. Book and cancel bookings for conference rooms.
3. Detect occupancy using sensors that register when at least two people enter a room.
4. Automatically release bookings for unoccupied rooms if not occupied within 5 minutes.
5. Turn off air conditioning and lights if the room is not occupied.

#### Optional Requirements
1. Provide a summary of room usage statistics.
2. Implement user authentication to restrict access to booking and configuration features.
3. Notify users via email or SMS when their booked room is released automatically.

### Design Patterns Used
- **Singleton Pattern**: Ensures a single instance of the office configuration and room booking system throughout the application.
- **Observer Pattern**: Sensors and control systems (lights, AC) observe room occupancy status and react accordingly.
- **Command Pattern**: Handles booking, cancellation, and room status updates through commands, allowing flexible and extendable operations.

### Possible Inputs and Outputs

#### Positive Cases
- `Config room count 3`  
  *Output:* "Office configured with 3 meeting rooms: Room 1, Room 2, Room 3."
- `Config room max capacity 1 10`  
  *Output:* "Room 1 maximum capacity set to 10."
- `Add occupant 1 2`  
  *Output:* "Room 1 is now occupied by 2 persons. AC and lights turned on."
- `Block room 1 09:00 60`  
  *Output:* "Room 1 booked from 09:00 for 60 minutes."
- `Cancel room 1`  
  *Output:* "Booking for Room 1 cancelled successfully."
- `Add occupant 1 0`  
  *Output:* "Room 1 is now unoccupied. AC and lights turned off."

#### Negative Cases
- `Block room 1 09:00 60` (already booked)  
  *Output:* "Room 1 is already booked during this time. Cannot book."
- `Cancel room 2` (not booked)  
  *Output:* "Room 2 is not booked. Cannot cancel booking."
- `Add occupant 2 1`  
  *Output:* "Room 2 occupancy insufficient to mark as occupied."
- `Add occupant 4 2` (non-existent room)  
  *Output:* "Room 4 does not exist."
- `Block room A 09:00 60`  
  *Output:* "Invalid room number. Please enter a valid room number."
- `Config room max capacity 1-5`  
  *Output:* "Invalid capacity. Please enter a valid positive number."
- `Room status 1` (unoccupied for > 5 mins)  
  *Output:* "Room 1 is now unoccupied. Booking released. AC and lights off."


---

## How to Run
Instructions to build and run the projects can be found in the respective folders.

---

## Author
Yatri Gor
(22BCP176)
