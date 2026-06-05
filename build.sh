#!/bin/bash
# Build script for Smart Business Management Suite - Linux/Mac version

echo ""
echo "========================================"
echo "Smart Business Management Suite"
echo "Build Script"
echo "========================================"
echo ""

# Check if Maven is installed
if ! command -v mvn &> /dev/null; then
    echo "ERROR: Maven is not installed or not in PATH"
    echo ""
    echo "Please install Maven:"
    echo "1. Download from: https://maven.apache.org/download.cgi"
    echo "2. Extract to a folder (e.g., ~/maven)"
    echo "3. Add to PATH in ~/.bashrc or ~/.zshrc:"
    echo "   export PATH=\$PATH:~/maven/apache-maven-4.0.0/bin"
    echo "4. Restart terminal"
    echo ""
    exit 1
fi

# Check Java version
if ! command -v java &> /dev/null; then
    echo "ERROR: Java is not installed or not in PATH"
    echo "Please install Java 11 or newer"
    exit 1
fi

echo ""
echo "Step 1: Running mvn clean"
mvn clean
if [ $? -ne 0 ]; then
    echo "ERROR: Clean phase failed"
    exit 1
fi

echo ""
echo "Step 2: Running mvn compile"
mvn compile
if [ $? -ne 0 ]; then
    echo "ERROR: Compile phase failed"
    exit 1
fi

echo ""
echo "Step 3: Running mvn package"
mvn package
if [ $? -ne 0 ]; then
    echo "ERROR: Package phase failed"
    exit 1
fi

echo ""
echo "========================================"
echo "BUILD SUCCESS!"
echo "========================================"
echo ""
echo "JAR created: target/SmartBusinessManagementSuite.jar"
echo ""
echo "To run the application:"
echo "  java -jar target/SmartBusinessManagementSuite.jar"
echo ""
