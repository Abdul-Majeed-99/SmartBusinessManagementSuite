@echo off
REM Run script for Smart Business Management Suite
REM This script runs the application

echo.
echo ========================================
echo Smart Business Management Suite
echo Run Script
echo ========================================
echo.

REM Check if JAR file exists
if not exist "target\SmartBusinessManagementSuite.jar" (
    echo ERROR: JAR file not found at target\SmartBusinessManagementSuite.jar
    echo.
    echo Please build the project first:
    echo   .\build.bat
    echo.
    pause
    exit /b 1
)

REM Check if Java is installed
java -version >nul 2>nul
if errorlevel 1 (
    echo ERROR: Java is not installed or not in PATH
    echo Please install Java 11 or newer
    pause
    exit /b 1
)

echo.
echo Starting application...
echo.
echo ========================================
echo Database Configuration:
echo   Host: localhost
echo   Port: 3306
echo   Database: smart_business_suite
echo ========================================
echo.
echo Login with:
echo   Username: admin
echo   Password: admin123
echo.
echo ========================================
echo.

REM Run the application
java -jar target\SmartBusinessManagementSuite.jar

REM Pause to see any error messages
if errorlevel 1 (
    echo.
    echo ERROR: Application failed to run
    pause
    exit /b 1
)
