@echo off
REM Build script for Smart Business Management Suite
REM This script requires Maven to be in PATH

setlocal enabledelayedexpansion

echo.
echo ========================================
echo Smart Business Management Suite
echo Build Script
echo ========================================
echo.

REM Check if Maven is installed
where mvn >nul 2>nul
if errorlevel 1 (
    echo ERROR: Maven is not installed or not in PATH
    echo.
    echo Please install Maven:
    echo 1. Download from: https://maven.apache.org/download.cgi
    echo 2. Extract to a folder (e.g., C:\Maven)
    echo 3. Add to PATH: setx PATH "%%PATH%%;C:\Maven\apache-maven-4.0.0\bin"
    echo 4. Restart command prompt
    echo.
    pause
    exit /b 1
)

REM Check Java version
java -version >nul 2>nul
if errorlevel 1 (
    echo ERROR: Java is not installed or not in PATH
    echo Please install Java 11 or newer
    pause
    exit /b 1
)

echo.
echo Step 1: Running mvn clean
mvn clean
if errorlevel 1 (
    echo ERROR: Clean phase failed
    pause
    exit /b 1
)

echo.
echo Step 2: Running mvn compile
mvn compile
if errorlevel 1 (
    echo ERROR: Compile phase failed
    pause
    exit /b 1
)

echo.
echo Step 3: Running mvn package
mvn package
if errorlevel 1 (
    echo ERROR: Package phase failed
    pause
    exit /b 1
)

echo.
echo ========================================
echo BUILD SUCCESS!
echo ========================================
echo.
echo JAR created: target\SmartBusinessManagementSuite.jar
echo.
echo To run the application:
echo   java -jar target\SmartBusinessManagementSuite.jar
echo.
pause
