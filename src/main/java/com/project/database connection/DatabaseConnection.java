package com.project.database;

import java.sql.*;

/**
 * DatabaseConnection - Singleton pattern for centralized database management
 * Handles all JDBC connections, statements, and result sets
 * 
 * @author Smart Business Management Suite
 * @version 1.0
 */
public class DatabaseConnection {
    private static DatabaseConnection instance;
    private Connection connection;

    // Configuration - can be loaded from config file
    private static final String HOST = "localhost";
    private static final int PORT = 3306;
    private static final String DATABASE = "smart_business_suite";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String URL = String.format(
            "jdbc:mysql://%s:%d/%s?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true&autoReconnect=true",
            HOST, PORT, DATABASE);

    /**
     * Private constructor - Singleton pattern
     */
    private DatabaseConnection() {
        try {
            Class.forName(DRIVER);
        } catch (ClassNotFoundException e) {
            System.err.println("JDBC Driver not found: " + e.getMessage());
            throw new ExceptionInInitializerError(e);
        }
    }

    /**
     * Get singleton instance
     * 
     * @return DatabaseConnection instance
     */
    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    /**
     * Get database connection
     * 
     * @return Connection object
     * @throws SQLException if connection fails
     */
    public Connection getConnection() throws SQLException {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                connection.setAutoCommit(true);
                System.out.println("✓ Database connection established");
                ensureSchema();
            }
            return connection;
        } catch (SQLException e) {
            System.err.println("Database connection error: " + e.getMessage());
            throw new SQLException("Failed to connect to database: " + e.getMessage());
        }
    }

    /**
     * Ensure required tables exist for the application.
     * This allows new installations to create missing persistence tables
     * automatically.
     * 
     * @throws SQLException if schema creation fails
     */
    private void ensureSchema() throws SQLException {
        executeUpdate("CREATE TABLE IF NOT EXISTS salary_records (" +
                "salary_id INT AUTO_INCREMENT PRIMARY KEY, " +
                "salary_date DATE NOT NULL, " +
                "type VARCHAR(100) NOT NULL, " +
                "amount DECIMAL(10, 2) NOT NULL, " +
                "notes TEXT, " +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4");

        // Expenses table
        executeUpdate("CREATE TABLE IF NOT EXISTS expenses (" +
                "expense_id INT AUTO_INCREMENT PRIMARY KEY, " +
                "expense_date DATE NOT NULL, " +
                "category VARCHAR(100) NOT NULL, " +
                "amount DECIMAL(10, 2) NOT NULL, " +
                "notes TEXT, " +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4");

        // Categories table
        executeUpdate("CREATE TABLE IF NOT EXISTS categories (" +
                "category_id INT AUTO_INCREMENT PRIMARY KEY, " +
                "category_name VARCHAR(150) NOT NULL UNIQUE, " +
                "description TEXT, " +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4");

        // Products table
        executeUpdate("CREATE TABLE IF NOT EXISTS products (" +
                "product_id INT AUTO_INCREMENT PRIMARY KEY, " +
                "product_code VARCHAR(50) NOT NULL UNIQUE, " +
                "product_name VARCHAR(255) NOT NULL, " +
                "category_id INT DEFAULT NULL, " +
                "unit_price DECIMAL(12,2) DEFAULT 0.00, " +
                "quantity_in_stock INT DEFAULT 0, " +
                "description TEXT, " +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY (category_id) REFERENCES categories(category_id) ON DELETE SET NULL" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4");

        // Sales by category/history table
        executeUpdate("CREATE TABLE IF NOT EXISTS sales_by_category (" +
                "sale_id INT AUTO_INCREMENT PRIMARY KEY, " +
                "sale_date DATE NOT NULL, " +
                "category VARCHAR(150), " +
                "product_name VARCHAR(255), " +
                "quantity INT, " +
                "unit_price DECIMAL(12,2), " +
                "discount DECIMAL(5,2), " +
                "total_amount DECIMAL(12,2), " +
                "notes TEXT, " +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4");

        // Users table (authentication)
        executeUpdate("CREATE TABLE IF NOT EXISTS users (" +
                "user_id INT AUTO_INCREMENT PRIMARY KEY, " +
                "username VARCHAR(100) NOT NULL UNIQUE, " +
                "email VARCHAR(150), " +
                "password_hash VARCHAR(255), " +
                "full_name VARCHAR(255), " +
                "phone VARCHAR(50), " +
                "role VARCHAR(50), " +
                "status VARCHAR(50) DEFAULT 'ACTIVE', " +
                "last_login TIMESTAMP NULL, " +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4");
    }

    /**
     * Close database connection
     */
    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("✓ Database connection closed");
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }
    }

    /**
     * Test database connection
     * 
     * @return true if connection successful
     */
    public static boolean testConnection() {
        try {
            Connection conn = getInstance().getConnection();
            DatabaseMetaData metadata = conn.getMetaData();
            System.out.println("✓ Database: " + metadata.getDatabaseProductName());
            System.out.println("✓ Version: " + metadata.getDatabaseProductVersion());
            return true;
        } catch (SQLException e) {
            System.err.println("✗ Connection test failed: " + e.getMessage());
            return false;
        }
    }

    /**
     * Execute SELECT query
     * 
     * @param query SQL query string
     * @return ResultSet with query results
     * @throws SQLException if query fails
     */
    public ResultSet executeQuery(String query) throws SQLException {
        try {
            PreparedStatement pst = getConnection().prepareStatement(query);
            return pst.executeQuery();
        } catch (SQLException e) {
            System.err.println("Query execution error: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Execute PreparedStatement query
     * 
     * @param query  SQL query with parameters
     * @param params Query parameters
     * @return ResultSet with results
     * @throws SQLException if query fails
     */
    public ResultSet executeQuery(String query, Object... params) throws SQLException {
        try {
            PreparedStatement pst = getConnection().prepareStatement(query);
            setParameters(pst, params);
            return pst.executeQuery();
        } catch (SQLException e) {
            System.err.println("Query execution error: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Execute UPDATE/INSERT/DELETE query
     * 
     * @param query SQL query string
     * @return number of rows affected
     * @throws SQLException if query fails
     */
    public int executeUpdate(String query) throws SQLException {
        try {
            Statement stmt = getConnection().createStatement();
            return stmt.executeUpdate(query);
        } catch (SQLException e) {
            System.err.println("Update execution error: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Execute PreparedStatement update
     * 
     * @param query  SQL query with parameters
     * @param params Query parameters
     * @return number of rows affected
     * @throws SQLException if query fails
     */
    public int executeUpdate(String query, Object... params) throws SQLException {
        try {
            PreparedStatement pst = getConnection().prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            setParameters(pst, params);
            return pst.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Update execution error: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Execute update and return generated keys
     * 
     * @param query  SQL query string
     * @param params Query parameters
     * @return ResultSet with generated keys
     * @throws SQLException if query fails
     */
    public ResultSet executeUpdateReturnKeys(String query, Object... params) throws SQLException {
        try {
            PreparedStatement pst = getConnection().prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            setParameters(pst, params);
            pst.executeUpdate();
            return pst.getGeneratedKeys();
        } catch (SQLException e) {
            System.err.println("Update with keys error: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Set parameters in PreparedStatement
     * 
     * @param pst    PreparedStatement object
     * @param params Parameters to set
     * @throws SQLException if setting parameters fails
     */
    private static void setParameters(PreparedStatement pst, Object... params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            Object param = params[i];
            if (param == null) {
                pst.setNull(i + 1, java.sql.Types.NULL);
            } else if (param instanceof String) {
                pst.setString(i + 1, (String) param);
            } else if (param instanceof Integer) {
                pst.setInt(i + 1, (Integer) param);
            } else if (param instanceof Double) {
                pst.setDouble(i + 1, (Double) param);
            } else if (param instanceof Boolean) {
                pst.setBoolean(i + 1, (Boolean) param);
            } else if (param instanceof java.util.Date) {
                pst.setTimestamp(i + 1, new java.sql.Timestamp(((java.util.Date) param).getTime()));
            } else {
                pst.setObject(i + 1, param);
            }
        }
    }

    /**
     * Get database configuration
     * 
     * @return configuration string
     */
    public static String getConfiguration() {
        return String.format("Database: %s (User: %s, Host: %s:%d)", DATABASE, USER, HOST, PORT);
    }
}
