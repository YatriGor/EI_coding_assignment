#!/bin/bash

# Smart Office Facility Management System - Run Script

echo "=== Starting Smart Office Facility Management System ==="

# Check if compiled classes exist
if [ ! -d "out" ]; then
    echo "Compiled classes not found. Running build script..."
    ./build.sh
fi

# Run the application
echo "Launching application..."
java -cp out com.smartoffice.SmartOfficeApplication
