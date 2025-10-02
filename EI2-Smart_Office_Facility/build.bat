@echo off
REM Smart Office Facility Management System - Build Script

echo === Smart Office Facility Management System - Build Script ===

REM Create output directory
if not exist out mkdir out

REM Compile Java source files
echo Compiling Java source files...
javac -d out -cp src\main\java src\main\java\com\smartoffice\*.java src\main\java\com\smartoffice\command\*.java src\main\java\com\smartoffice\config\*.java src\main\java\com\smartoffice\model\*.java src\main\java\com\smartoffice\observer\*.java src\main\java\com\smartoffice\systems\*.java src\main\java\com\smartoffice\ui\*.java src\main\java\com\smartoffice\util\*.java src\main\java\com\smartoffice\exception\*.java

REM Check compilation status
if %errorlevel% equ 0 (
    echo ✅ Compilation successful!
    echo.
    echo To run the application:
    echo java -cp out com.smartoffice.SmartOfficeApplication
    echo.
    echo Or use the run script:
    echo run.bat
) else (
    echo ❌ Compilation failed!
    exit /b 1
)
