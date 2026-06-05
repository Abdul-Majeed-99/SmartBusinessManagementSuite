# 🚀 QUICK START - 5 Minutes to Running Application

## Prerequisites Check (2 minutes)

### 1. Java Installation
```bash
java -version
# Should show: openjdk version "11" or higher (or Oracle JDK 11+)
```

**Don't have Java?** → Download from: https://www.oracle.com/java/technologies/downloads/

### 2. MySQL/MariaDB Running
```bash
# Ensure MySQL is running in XAMPP
# Control Panel → Start MySQL button
```

**Don't have XAMPP?** → Download from: https://www.apachefriends.org/

### 3. Database Setup (1 minute)
```bash
# Import database schema
mysql -u root < database_schema.sql
```

## Build & Run (2 minutes)

### Option A: Using build script (Easiest)

**Windows:**
```bash
.\build.bat
```

**Linux/Mac:**
```bash
chmod +x build.sh
./build.sh
```

### Option B: Using Maven directly
```bash
mvn clean package
```

### Option C: Using VS Code (if Extension Pack for Java installed)
1. Press `Ctrl+Shift+B`
2. Select `Maven: Compile`

## Run Application (30 seconds)

### Option A: Using run script

**Windows:**
```bash
.\run.bat
```

**Linux/Mac:**
```bash
chmod +x run.sh
./run.sh
```

### Option B: Using Java directly
```bash
java -jar target/SmartBusinessManagementSuite.jar
```

### Option C: Using NetBeans
1. File → Open Project → Select this folder
2. Run → Run Project (F6)

## Login

```
Username: admin
Password: admin123
```

---

## Troubleshooting

### ❌ "mvn: command not found"
**Solution**: Add Maven to PATH
- Download Maven: https://maven.apache.org/download.cgi
- Add `C:\Maven\apache-maven-4.0.0\bin` to system PATH

### ❌ "Database connection refused"
**Solution**: 
1. Start MySQL in XAMPP
2. Open phpMyAdmin: http://localhost/phpmyadmin
3. Import `database_schema.sql`
4. Verify admin user exists

### ❌ "Cannot find jar file"
**Solution**: Build first
```bash
mvn clean package
```

### ❌ "Login fails"
**Solution**: 
1. Check database: `mysql -u root -e "USE smart_business_suite; SELECT * FROM users;"`
2. Verify admin user exists
3. Check password hash in database

---

## Next Steps

- Read **README.md** for complete documentation
- Read **SETUP.md** for detailed setup instructions
- Read **PROJECT_SUMMARY.md** for project status

---

## Full Build Output Example

```
$ mvn clean package

[INFO] Scanning for projects...
[INFO] 
[INFO] --------< com.project:smart-business-management-suite >--------
[INFO] Building Smart Business Management Suite 1.0.0
[INFO] --------------------------------[ jar ]---------------------------------
[INFO] 
[INFO] --- maven-clean-plugin:2.6.1:clean (default-clean) @ smart-business-management-suite ---
[INFO] Deleting C:\...\target
[INFO] 
[INFO] --- maven-compiler-plugin:3.11.0:compile (default-compile) @ smart-business-management-suite ---
[INFO] Compiling 28 source files to target/classes
[INFO] 
[INFO] --- maven-jar-plugin:2.6:jar (default-jar) @ smart-business-management-suite ---
[INFO] Building jar: target/SmartBusinessManagementSuite.jar
[INFO] 
[INFO] BUILD SUCCESS
[INFO] Total time: 15.234 s
[INFO] Finished at: 2024-01-15T10:30:45+00:00

$ java -jar target/SmartBusinessManagementSuite.jar
✓ Database connection successful
✓ Loading main application...
✓ Application started successfully
```

---

## System Requirements

| Component | Requirement |
|-----------|-------------|
| **Java** | 11 or higher |
| **RAM** | 512 MB minimum |
| **Disk** | 100 MB free space |
| **MySQL** | 5.7 or 8.0+ |
| **OS** | Windows, Linux, macOS |

---

## Success Criteria

✅ Build completes with "BUILD SUCCESS"  
✅ JAR file appears in `target/` folder  
✅ Application launches with splash screen  
✅ Login panel appears  
✅ Can login with admin/admin123  

---

## Get Help

- **Database issues?** → See SETUP.md
- **Compilation errors?** → See README.md
- **Project status?** → See PROJECT_SUMMARY.md
- **Architecture details?** → Check code comments

---

**Estimated Time**: 5-10 minutes to full working application  
**Last Updated**: 2024  
**Version**: 1.0.0
