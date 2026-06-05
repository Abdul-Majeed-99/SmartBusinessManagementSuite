# Database Fix Instructions

## Problem
The `salary_records` table is missing the `employee_id` column, causing the error:
```
Unable to load salary records: Unknown column 'sr.employee_id' in 'on clause'
```

## Solution - Choose ONE method:

### Method 1: Using MySQL Workbench (EASIEST)
1. Open **MySQL Workbench**
2. Connect to your MySQL server
3. Go to **File → Open SQL Script**
4. Select `fix_salary_table.sql` from the SmartBusinessManagementSuite folder
5. Click the **Execute** button (⚡ icon) or press `Ctrl+Shift+Enter`
6. Wait for the script to complete successfully
7. Close the application and restart it

### Method 2: Using Command Line
1. Open **Command Prompt** (Windows) or **Terminal** (Mac/Linux)
2. Navigate to: `C:\Users\majee\Documents\NetBeansProjects\SmartBusinessManagementSuite`
3. Run this command (replace "root" and "root" with your actual MySQL username and password):
   ```
   mysql -h localhost -u root -proot < fix_salary_table.sql
   ```
4. Wait for it to complete (should show success message)
5. Restart the application

### Method 3: Using Windows Batch File
1. Double-click `fix_database.bat` in the SmartBusinessManagementSuite folder
2. The script will automatically run the SQL fix
3. Close the window when done
4. Restart the application

### Method 4: Manual Fix (if above methods don't work)
1. Open MySQL Workbench or MySQL command line
2. Run these commands one by one:
   ```sql
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
   ```

## After Fixing:
1. Close the Java application completely
2. Reopen it
3. Go to Admin Dashboard → Salary Management
4. Try adding or viewing salary records - it should work now!

## Still having issues?
- Make sure MySQL server is running
- Verify your database name is `smart_business_suite` (lowercase)
- Check that your MySQL credentials (username/password) are correct
