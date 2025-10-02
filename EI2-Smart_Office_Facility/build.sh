#!/bin/bash

# Smart Office Facility Management System - Build Script

echo "=== Smart Office Facility Management System - Build Script ==="

# Create output directory
mkdir -p out

# Compile Java source files
echo "Compiling Java source files..."
javac -d out -cp src/main/java src/main/java/com/smartoffice/**/*.java

# Check compilation status
if [ $? -eq 0 ]; then
    echo "✅ Compilation successful!"
    echo ""
    echo "To run the application:"
    echo "java -cp out com.smartoffice.SmartOfficeApplication"
    echo ""
    echo "Or use the run script:"
    echo "./run.sh"
else
    echo "❌ Compilation failed!"
    exit 1
fi
