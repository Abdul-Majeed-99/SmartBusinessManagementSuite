#!/bin/bash
# Run script for Smart Business Management Suite - Linux/Mac version

echo ""
echo "========================================"
echo "Smart Business Management Suite"
echo "Run Script"
echo "========================================"
echo ""

# Check if JAR file exists
if [ ! -f "target/SmartBusinessManagementSuite.jar" ]; then
    echo "ERROR: JAR file not found at target/SmartBusinessManagementSuite.jar"
    echo ""
    echo "Please build the project first:"
    echo "  ./build.sh"
    echo ""
    exit 1
fi

# Check if Java is installed
if ! command -v java &> /dev/null; then
    echo "ERROR: Java is not installed or not in PATH"
    echo "Please install Java 11 or newer"
    exit 1
fi

echo ""
echo "Starting application..."
echo ""
echo "========================================"
echo "Database Configuration:"
echo "  Host: localhost"
echo "  Port: 3306"
echo "  Database: smart_business_suite"
echo "========================================"
echo ""
echo "Login with:"
echo "  Username: admin"
echo "  Password: admin123"
echo ""
echo "========================================"
echo ""

# Run the application
java -jar target/SmartBusinessManagementSuite.jar

# Check for errors
if [ $? -ne 0 ]; then
    echo ""
    echo "ERROR: Application failed to run"
    exit 1
fi
