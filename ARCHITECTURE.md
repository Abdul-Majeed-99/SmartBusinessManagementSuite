# Project Architecture & Package Structure

## Application Architecture Overview

```
┌─────────────────────────────────────────────────────────────────┐
│               Smart Business Management Suite v1.0              │
│                      Application Architecture                   │
└─────────────────────────────────────────────────────────────────┘

                              ┌──────────────┐
                              │ User (Client)│
                              └──────┬───────┘
                                     │
                         ┌───────────┴───────────┐
                         │                       │
                    ┌────▼─────┐          ┌─────▼───┐
                    │ VS Code   │          │ NetBeans│
                    │ or IDE    │          │ IDE     │
                    └────┬─────┘          └─────┬───┘
                         │                       │
                         └───────────┬───────────┘
                                     │
                         ┌───────────▼───────────┐
                         │ ApplicationLauncher   │
                         │ (com.project.main)   │
                         └───────────┬───────────┘
                                     │
                    ┌────────────────┼────────────────┐
                    │                │                │
              ┌─────▼─────┐   ┌─────▼──────┐   ┌────▼────────┐
              │   GUI      │   │ Services   │   │ Database    │
              │  (Swing)   │   │ Layer      │   │ Connection  │
              └─────┬─────┘   └─────┬──────┘   └────┬────────┘
                    │                │              │
        ┌───────────┴────────┐      │              │
        │                    │      │              │
    ┌───▼──┐    ┌────▼───┐  │      │              │
    │Login │    │ Main   │  │   ┌──▼───────────┐  │
    │Panel │    │ Window │  │   │  Auth       │  │
    └──────┘    └────┬───┘  │   │  Service    │  │
                     │       │   └──┬──────────┘  │
                ┌────▼────┐  │      │             │
                │Dashboard│  │   ┌──▼────────┐   │
                └────┬────┘  │   │ Product   │   │
                     │       │   │ Service   │   │
        ┌────────────┼────┐  │   └──┬────────┘   │
        │            │    │  │      │            │
    ┌───▼──┐  ┌─────▼─┐ ┌─▼─▼───┐  │         ┌──▼────────────┐
    │Inven-│  │Sales  │ │Reports│  │         │Database-      │
    │tory  │  │Panel  │ │Panel  │  │         │Connection     │
    │Panel │  │       │ │       │  │         │(Singleton)    │
    └──────┘  └───────┘ └───────┘  │         └──┬─────────────┘
                                    │            │
                            ┌───────▼─┐          │
                            │ Models  │          │
                            │ (6)     │          │
                            └────┬────┘          │
                                 │               │
              ┌──────────────────┼───────────────┤
              │                  │               │
          ┌───▼──┐  ┌────▼────┐ ┌▼───┐        │
          │User  │  │ Product │ │Sale│...     │
          │Model │  │ Model   │ │Mdl │        │
          └──────┘  └─────────┘ └────┘        │
                                               │
                            ┌──────────────────▼──┐
                            │  MySQL Database     │
                            │ (smart_business_   │
                            │  suite)             │
                            │                     │
                            │ 6 Tables:           │
                            │ • users             │
                            │ • categories        │
                            │ • products          │
                            │ • sales             │
                            │ • sales_items       │
                            │ • invoices          │
                            └─────────────────────┘
```

## Package Hierarchy

```
com.project/
│
├── main/
│   └── ApplicationLauncher.java ★
│       Entry point for entire application
│       • Splash screen display
│       • Database connection test
│       • Launches MainWindow
│
├── gui/
│   ├── SplashScreen.java
│   │   Welcome screen (3 seconds)
│   │
│   ├── LoginPanel.java
│   │   Username/Password input
│   │   ↓ Authenticates via AuthService
│   │
│   ├── MainWindow.java
│   │   Container with CardLayout
│   │   Switches between Login & Dashboard
│   │
│   ├── DashboardPanel.java
│   │   Main app window after login
│   │   Menu system with module switching
│   │
│   ├── InventoryPanel.java
│   │   Module UI for product management
│   │   Calls ProductService
│   │
│   ├── SalesPanel.java
│   │   Module UI for sales transactions
│   │   Calls SaleService
│   │
│   └── ReportsPanel.java
│       Module UI for reports/analytics
│       Calls ReportService
│
├── models/
│   ├── User.java
│   │   id, username, email, passwordHash,
│   │   fullName, phone, role, status
│   │
│   ├── Product.java
│   │   id, code, name, categoryId, 
│   │   price, quantity, reorderLevel
│   │
│   ├── Category.java
│   │   id, name, description
│   │
│   ├── Sale.java
│   │   id, date, userId, customer,
│   │   amounts (subtotal, tax, discount, total)
│   │
│   ├── SaleItem.java
│   │   id, saleId, productId,
│   │   quantity, unitPrice, totalPrice
│   │
│   └── Invoice.java
│       id, saleId, number, customer info,
│       amounts, tax%, discount%, status
│
├── services/
│   ├── AuthService.java ✓
│   │   • registerUser()
│   │   • authenticateUser()
│   │   • usernameExists()
│   │   • emailExists()
│   │   • getUserById()
│   │   Uses: DatabaseConnection, PasswordUtils
│   │
│   ├── ProductService.java (TODO)
│   │   • getAllProducts()
│   │   • getProductById()
│   │   • addProduct()
│   │   • updateProduct()
│   │   • deleteProduct()
│   │   Uses: DatabaseConnection, ValidationUtil
│   │
│   ├── SaleService.java (TODO)
│   │   • createSale()
│   │   • getSaleById()
│   │   • getAllSales()
│   │   • updatePaymentStatus()
│   │   Uses: DatabaseConnection, ValidationUtil
│   │
│   └── ReportService.java (TODO)
│       • generateSalesReport()
│       • generateInventoryReport()
│       • exportToPDF()
│       Uses: DatabaseConnection, JFreeChart
│
├── database/
│   └── DatabaseConnection.java ✓
│       Singleton pattern
│       • getInstance() → Synchronized
│       • getConnection() → Create/reuse
│       • executeQuery() → SELECT
│       • executeUpdate() → INSERT/UPDATE/DELETE
│       • executeUpdateReturnKeys() → Generated IDs
│       • setParameters() → Safe parameter binding
│       • testConnection() → Verify connectivity
│       • getConfiguration() → Display config
│
├── validation/
│   └── ValidationUtil.java ✓
│       All input validation methods
│       • validateEmail()
│       • validateUsername()
│       • validatePassword()
│       • validatePhone()
│       • validateProductCode()
│       • validatePrice()
│       • validateQuantity()
│       • combineErrors()
│       Returns null if valid, error string if invalid
│
└── utils/
    └── PasswordUtils.java ✓
        • hashPassword() → SHA-256
        • verifyPassword() → Compare hash
        • generateToken() → Random tokens
```

## Data Flow Diagrams

### User Login Flow

```
    User                MainWindow            LoginPanel           AuthService         Database
     │                      │                      │                    │                │
     ├─ Enter Credentials ──┬─ Display ─────────────┤                    │                │
     │                      │                       │                    │                │
     └─ Click Login ────────┼──────────────────────►│                    │                │
                            │                       ├─ Validate Email ───►│                │
                            │                       │                    ├─ Query Email ─►│
                            │                       │                    │◄─ Not Found ───┤
                            │                       │◄─ Proceed ─────────┤                │
                            │                       │                    │                │
                            │                       ├─ Validate Password ►│                │
                            │                       │                    ├─ Query User ──►│
                            │                       │                    │◄─ User Data ───┤
                            │                       │                    │                │
                            │                       │                    ├─ Hash Password │
                            │                       │                    │ & Compare      │
                            │                       │◄─ User Object ─────┤                │
                            │◄──────────────────────┤                    │                │
                            │                       │                    ├─ Update ───────►│
                            │                       │                    │ last_login     │
                            ├─ Show Dashboard ─────┤                    │                │
                            │ (CardLayout.show)    │                    │                │
                            │                      │                    │                │
    User ◄─ Application ────┤                      │                    │                │
```

### Product Query Flow

```
    User                InventoryPanel          ProductService      DatabaseConnection    MySQL
     │                      │                        │                    │               │
     ├─ Click "View"───────┬┤                        │                    │               │
     │                      │                        │                    │               │
     │                      ├─ getAllProducts() ────►│                    │               │
     │                      │                        │                    │               │
     │                      │                        ├─ executeQuery() ───►│               │
     │                      │                        │  "SELECT * FROM     │               │
     │                      │                        │   products..."      │               │
     │                      │                        │                    ├─ Query ──────►│
     │                      │                        │                    │               │
     │                      │                        │                    │◄─ ResultSet ──┤
     │                      │◄────────────────────────┤                    │               │
     │                      │ List<Product>          │                    │               │
     │                      │                        │                    │               │
     │◄─ Display Table ─────┤                        │                    │               │
     │
```

## Module Structure

```
┌────────────────────────────────────────────────────────────────────────┐
│                        Smart Business Management Suite                 │
│                              Main Window                               │
├────────────────────────────────────────────────────────────────────────┤
│ Menu Bar:                                                              │
│ [ File ] [ Modules ] [ View ] [ Tools ] [ Help ]                      │
├────────────────────────────────────────────────────────────────────────┤
│                                                                        │
│ Welcome, John Smith (Admin)                                           │
│                                                                        │
├────────────────────────────────────────────────────────────────────────┤
│                          Main Content Area                             │
│                        (CardLayout switching)                          │
│                                                                        │
│  ┌────────────────────────────────────────────────────────────────┐   │
│  │ Inventory Panel         │ Sales Panel         │ Reports Panel  │   │
│  │                         │                     │                │   │
│  │ ✓ Current             │ ✓ Current           │ ✓ Current      │   │
│  │ • Add Product         │ • New Sale          │ • Sales Report │   │
│  │ • View Products       │ • View Sales        │ • Inventory    │   │
│  │ • Edit Product        │ • Generate Invoice  │ • Profit       │   │
│  │ • Delete Product      │ • Process Payment   │ • Export PDF   │   │
│  │ • Stock Report        │ • View Invoices     │ • Charts       │   │
│  │                       │                     │                │   │
│  └────────────────────────────────────────────────────────────────┘   │
│                                                                        │
├────────────────────────────────────────────────────────────────────────┤
│ Status Bar: Ready | Database: Connected | Last Action: Logged in     │
└────────────────────────────────────────────────────────────────────────┘
```

## Technology Stack

```
┌─────────────────────────────────────────────────────────────┐
│                    Technology Stack                         │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│ User Interface Layer:                                       │
│  └─ Java Swing (JFrame, JPanel, CardLayout)                │
│                                                             │
│ Application Layer:                                          │
│  ├─ MVC Architecture                                        │
│  ├─ Singleton Pattern (Database Connection)                │
│  ├─ PreparedStatement (SQL Safety)                          │
│  ├─ SHA-256 Password Hashing                               │
│  └─ Comprehensive Input Validation                          │
│                                                             │
│ Data Layer:                                                 │
│  ├─ JDBC (Java Database Connectivity)                       │
│  ├─ MySQL 8.0+ (6 normalized tables)                        │
│  ├─ Foreign Key Relationships                               │
│  └─ Indexes on query-heavy columns                          │
│                                                             │
│ Build System:                                               │
│  ├─ Apache Maven 4.0.0                                      │
│  ├─ Java 11 Compiler Target                                 │
│  └─ Assembly Plugin (Executable JAR)                        │
│                                                             │
│ Supporting Libraries:                                       │
│  ├─ JFreeChart (Charts & Graphs)                            │
│  ├─ iText (PDF Export)                                      │
│  ├─ SLF4J (Logging)                                         │
│  └─ Apache Commons (Utilities)                              │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

## Deployment Architecture

```
Developer's Laptop          CI/CD Server           Production Server
         │                         │                       │
         ├─ Source Code ──────────►│                       │
         │ (GitHub)                │                       │
         │                         ├─ Maven Build ─────┐   │
         │                         │ (compile, test,    │   │
         │                         │  package)          │   │
         │                         │                    │   │
         │                         ├─ Artifact ────────►│   │
         │                         │ (JAR)              │   │
         │                         │                    │   │
         │                         ├─ MySQL Setup      │   │
         │                         │ (schema.sql) ──┐   │   │
         │                         │                │   │   │
         │                         │                └──►│   │
         │                         │                    │   │
         │                         └─ Deploy ──────────►│   │
         │                            (java -jar)      │   │
         │                                             │   │
         └─ Test Locally ─┐                           │   │
              (mvn test)   ├─ Production Ready ───────►│   │
                           │                          │   │
                           └──────────────────────────►│   │
```

## Security Architecture

```
┌─────────────────────────────────────────────────────────┐
│                    Security Layers                      │
├─────────────────────────────────────────────────────────┤
│                                                         │
│ Layer 1: Input Validation                              │
│  • ValidationUtil.java                                  │
│  • Email, username, password, phone patterns           │
│  • Price, quantity, percentage bounds                  │
│  Result: Block invalid input at UI level               │
│                                                         │
│ Layer 2: Database Access                               │
│  • PreparedStatement (no string concatenation)         │
│  • Parameter binding (setParameters)                   │
│  • SQL injection prevention                            │
│  Result: Safe queries even with malicious input        │
│                                                         │
│ Layer 3: Password Security                             │
│  • SHA-256 hashing (no plaintext storage)              │
│  • Unique hash for each password                       │
│  • PasswordUtils.verifyPassword() for login            │
│  Result: Compromised password doesn't expose users    │
│                                                         │
│ Layer 4: Access Control                                │
│  • Role-based access (ADMIN, MANAGER, EMPLOYEE)       │
│  • Status tracking (ACTIVE, INACTIVE, BLOCKED)        │
│  • Session management hooks                            │
│  Result: Limited access per user role                  │
│                                                         │
│ Layer 5: Connection Security                           │
│  • Singleton pattern (single managed connection)       │
│  • Connection pooling ready                            │
│  • SSL option available (serverTimezone config)        │
│  Result: Secure, reusable database connections        │
│                                                         │
└─────────────────────────────────────────────────────────┘
```

## Code Organization Best Practices

```
✓ Separation of Concerns
  • GUI (views) separate from Business Logic (services)
  • Business Logic separate from Data Access (database)

✓ Single Responsibility
  • AuthService: Only authentication
  • ProductService: Only product operations
  • DatabaseConnection: Only connection management

✓ DRY (Don't Repeat Yourself)
  • Centralized database connection (DatabaseConnection)
  • Reusable validation methods (ValidationUtil)
  • Singleton pattern (one instance only)

✓ SOLID Principles
  • Single Responsibility: Each class one job
  • Open/Closed: Open for extension, closed for modification
  • Liskov Substitution: Service methods interchangeable
  • Interface Segregation: Models have only needed fields
  • Dependency Inversion: Services depend on abstractions

✓ Design Patterns
  • Singleton: DatabaseConnection
  • MVC: Models/Views/Controllers
  • CardLayout: Module switching
  • DAO: Data Access Objects (future)
```

---

**Project Architecture**: MVC Pattern with Singleton Database Connection  
**Scalability**: Ready for service layer expansion and additional modules  
**Maintainability**: Clear separation of concerns, well-documented code  
**Security**: Multiple layers of protection (validation → PreparedStatement → hashing → RBAC)  
**Performance**: Connection pooling ready, indexed queries, efficient data models
