@echo off
REM Smart Office Facility Management System - Run Script

echo === Starting Smart Office Facility Management System ===

REM Check if compiled classes exist
if not exist out (
    echo Compiled classes not found. Running build script...
    call build.bat
)

REM Run the application
echo Launching application...
java -cp out com.smartoffice.SmartOfficeApplication
