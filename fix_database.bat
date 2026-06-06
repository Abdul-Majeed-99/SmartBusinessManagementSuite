@echo off
REM This script fixes the salary_records table in the MySQL database
REM Make sure MySQL is running and you have the correct credentials

setlocal enabledelayedexpansion

REM MySQL connection details (adjust if needed)
set MYSQL_PATH=C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe
set MYSQL_USER=root
set MYSQL_PASS=root
set MYSQL_HOST=localhost

echo.
echo ======================================
echo Fixing salary_records table...
echo ======================================
echo.

REM Execute the SQL fix script
if exist "%MYSQL_PATH%" (
    "%MYSQL_PATH%" -h %MYSQL_HOST% -u %MYSQL_USER% -p%MYSQL_PASS% < fix_salary_table.sql
    if errorlevel 1 (
        echo Error executing SQL script!
        pause
        exit /b 1
    ) else (
        echo.
        echo ======================================
        echo Database fixed successfully!
        echo ======================================
        echo.
        pause
    )
) else (
    echo MySQL not found at: %MYSQL_PATH%
    echo Please adjust the MYSQL_PATH in this script
    pause
    exit /b 1
)
