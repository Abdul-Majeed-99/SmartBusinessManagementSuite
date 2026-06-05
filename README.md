# Smart Business Management Suite

Professional Java Swing desktop application with Maven build system and MySQL database.

## Project Structure

```
SmartBusinessManagementSuite/
├── src/
│   ├── main/
│   │   ├── java/com/project/
│   │   │   ├── main/          - Application launcher
│   │   │   ├── gui/           - GUI components (Swing)
│   │   │   ├── models/        - Data models
│   │   │   ├── services/      - Business logic
│   │   │   ├── database/      - Database connection
│   │   │   ├── validation/    - Input validation
│   │   │   └── utils/         - Utility functions
│   │   └── resources/
│   └── test/java/
├── pom.xml                    - Maven configuration
└── database_schema.sql        - MySQL schema
```

## Prerequisites

1. **Java Development Kit (JDK) 11+**
   - Download: https://www.oracle.com/java/technologies/downloads/
   - Install and set JAVA_HOME environment variable

2. **Apache Maven 4.0.0+**
   - Download: https://maven.apache.org/download.cgi
   - Extract and add to PATH

3. **MySQL 8.0+ (via XAMPP)**
   - Download: https://www.apachefriends.org/
   - Start MySQL service from XAMPP Control Panel

## Setup Instructions

### 1. Database Setup

1. Open XAMPP Control Panel and start MySQL
2. Open phpMyAdmin: http://localhost/phpmyadmin
3. Create new database or import `database_schema.sql`:
   ```sql
   source /path/to/database_schema.sql
   ```
4. Verify database connection in `DatabaseConnection.java`

### 2. Maven Build

```bash
# Navigate to project directory
cd SmartBusinessManagementSuite

# Clean and compile
mvn clean compile

# Package application
mvn package

# Run tests
mvn test
```

### 3. Running the Application

**Option A: From command line**
```bash
# After packaging
java -jar target/SmartBusinessManagementSuite.jar
```

**Option B: In VS Code**
- Press F5 to debug
- Or use Ctrl+Shift+B to compile, then Task: Run Application

**Option C: In NetBeans**
- File → Open Project → Select SmartBusinessManagementSuite folder
- Run → Run Project (F6)

## Default Login Credentials

**Admin User**
- Username: admin
- Password: admin123

**Database Configuration**
- Host: localhost
- Port: 3306
- Database: smart_business_suite
- User: root
- Password: (empty)

## Key Features

### Modules
1. **Inventory Management**
   - Add/Edit/Delete products
   - Track stock levels
   - Manage categories
   - Low stock alerts

2. **Sales Management**
   - Create sales transactions
   - Manage shopping cart
   - Generate invoices
   - Process payments

3. **User Authentication**
   - Secure login with SHA-256 hashing
   - User registration
   - Role-based access (Admin, Manager, Employee)

4. **Reports & Analytics**
   - Sales reports
   - Inventory reports
   - Profit analysis
   - PDF export

## Dependencies

### Core
- MySQL JDBC Driver 8.0.33
- SLF4J Logging 1.7.36

### UI & Features
- JFreeChart 1.5.3 (Charts)
- iText 5.5.13.3 (PDF Export)
- Apache Commons IO 2.11.0

### Build & Testing
- Maven Compiler Plugin 3.11.0
- Maven Assembly Plugin 3.3.0
- JUnit 4.13.2

## Troubleshooting

### Database Connection Error
- Verify MySQL is running in XAMPP
- Check `DatabaseConnection.java` configuration
- Ensure `smart_business_suite` database exists
- Run `database_schema.sql` to initialize schema

### Maven Build Error
- Verify Java 11+ installed: `java -version`
- Verify Maven installed: `mvn -version`
- Check internet connection for dependency download
- Run `mvn clean install -U` to update dependencies

### Login Fails
- Verify admin user exists: Check phpMyAdmin users table
- Check password hash: Should be '8c6976e5b5410415bde908bd4dee15dfb167a9c873fc4bb8a81f6f2ab448a918'
- Review application logs in terminal

## Development

### Adding New Module
1. Create new panel class extending JPanel
2. Add panel to DashboardPanel via CardLayout
3. Create corresponding service class
4. Create model classes for data

### Database Operations
All database access through `DatabaseConnection.getInstance()`:
```java
// Query
ResultSet rs = db.executeQuery("SELECT * FROM products WHERE product_id = ?", productId);

// Update
db.executeUpdate("UPDATE products SET quantity = ? WHERE product_id = ?", newQty, productId);
```

### Validation
Use `ValidationUtil` for input validation:
```java
String error = ValidationUtil.validateEmail(email);
if (error != null) {
    JOptionPane.showMessageDialog(null, error);
}
```

## Architecture

**MVC Pattern**
- **Models**: Data classes (User, Product, Sale, etc.)
- **Views**: GUI panels (LoginPanel, DashboardPanel, etc.)
- **Controllers**: Service classes (AuthService, ProductService, etc.)

**Database Layer**
- Singleton DatabaseConnection for centralized access
- Prepared statements for SQL injection prevention
- Auto-reconnect on connection loss

**Security**
- SHA-256 password hashing
- PreparedStatement for all queries
- Role-based access control
- Input validation on all fields

## Contributing

Follow these conventions:
- Package naming: `com.project.{module}`
- Class naming: PascalCase
- Method naming: camelCase
- Database queries: UPPERCASE
- Comments: Clear, concise English

## Testing

```bash
# Run unit tests
mvn test

# Run specific test class
mvn test -Dtest=UserModelTest

# Run with coverage
mvn test jacoco:report
```

## Deployment

1. Build JAR: `mvn clean package`
2. Deploy JAR to target server
3. Ensure MySQL database accessible
4. Run: `java -jar SmartBusinessManagementSuite.jar`

## License

This project is for educational purposes.

## Support

For issues or questions, review logs in console output or create GitHub issue.

---

**Last Updated**: 2024
**Version**: 1.0.0
**Author**: Smart Business Management Suite Team
