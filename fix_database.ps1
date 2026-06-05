# PowerShell Script to Fix salary_records Table
# This script will fix the database salary_records table structure

# Configuration
$MySQLUser = "root"
$MySQLPassword = "root"
$MySQLHost = "localhost"
$Database = "smart_business_suite"
$ScriptPath = Split-Path -Parent $MyInvocation.MyCommand.Path

Write-Host "======================================" -ForegroundColor Cyan
Write-Host "Fixing salary_records table..." -ForegroundColor Cyan
Write-Host "======================================" -ForegroundColor Cyan
Write-Host ""

# SQL commands to execute
$SqlCommands = @"
USE smart_business_suite;
DROP TABLE IF EXISTS salary_records;
CREATE TABLE salary_records (
    salary_id INT AUTO_INCREMENT PRIMARY KEY,
    employee_id INT NOT NULL,
    salary_date DATE NOT NULL,
    type VARCHAR(50) NOT NULL DEFAULT 'Salary',
    amount DECIMAL(10, 2) NOT NULL,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (employee_id) REFERENCES users(user_id) ON DELETE RESTRICT,
    INDEX idx_employee_id (employee_id),
    INDEX idx_salary_date (salary_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
SHOW CREATE TABLE salary_records;
"@

try {
    # Try to use mysql.exe directly
    $mysqlPath = "mysql.exe"
    $process = New-Object System.Diagnostics.ProcessStartInfo
    $process.FileName = $mysqlPath
    $process.Arguments = "-h $MySQLHost -u $MySQLUser -p$MySQLPassword"
    $process.UseShellExecute = $false
    $process.RedirectStandardInput = $true
    $process.RedirectStandardOutput = $true
    $process.RedirectStandardError = $true
    
    $proc = [System.Diagnostics.Process]::Start($process)
    
    # Write SQL commands to stdin
    $proc.StandardInput.WriteLine($SqlCommands)
    $proc.StandardInput.Close()
    
    # Read output
    $output = $proc.StandardOutput.ReadToEnd()
    $error = $proc.StandardError.ReadToEnd()
    
    $proc.WaitForExit()
    
    if ($proc.ExitCode -eq 0) {
        Write-Host "✓ Database fixed successfully!" -ForegroundColor Green
        Write-Host ""
        Write-Host "Output:" -ForegroundColor Yellow
        Write-Host $output -ForegroundColor Green
        Write-Host ""
        Write-Host "======================================" -ForegroundColor Cyan
        Write-Host "You can now restart the application." -ForegroundColor Cyan
        Write-Host "======================================" -ForegroundColor Cyan
    } else {
        Write-Host "✗ Error occurred:" -ForegroundColor Red
        Write-Host $error -ForegroundColor Red
    }
    
} catch {
    Write-Host "Error: MySQL command line tool not found." -ForegroundColor Red
    Write-Host "Please use Method 1 (MySQL Workbench) from DATABASE_FIX_README.txt" -ForegroundColor Yellow
    Write-Host ""
    Write-Host "Instructions:" -ForegroundColor Cyan
    Write-Host "1. Open MySQL Workbench"
    Write-Host "2. Open File → Open SQL Script"
    Write-Host "3. Select: fix_salary_table.sql"
    Write-Host "4. Click Execute (⚡ icon)"
}

Read-Host "Press Enter to exit"
