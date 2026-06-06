package com.project.services;

import com.project.database.DatabaseConnection;
import com.project.models.User;
import com.project.utils.PasswordUtils;
import com.project.validation.ValidationUtil;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

/**
 * AuthService - Authentication and user registration
 */
public class AuthService {
    private static final DatabaseConnection db = DatabaseConnection.getInstance();

    /**
     * Register new user
     * 
     * @param user User object with registration data
     * @return User object if successful, null if failed
     * @throws SQLException if database error
     */
    public static User registerUser(User user, String password) throws SQLException {
        try {
            // Validate input
            String error = ValidationUtil.validateEmail(user.getEmail());
            if (error != null)
                throw new SQLException(error);

            error = ValidationUtil.validateUsername(user.getUsername());
            if (error != null)
                throw new SQLException(error);

            error = ValidationUtil.validatePassword(password);
            if (error != null)
                throw new SQLException(error);

            // Check if username exists
            if (usernameExists(user.getUsername())) {
                throw new SQLException("Username already exists");
            }

            // Check if email exists
            if (emailExists(user.getEmail())) {
                throw new SQLException("Email already exists");
            }

            // Hash password
            String passwordHash = PasswordUtils.hashPassword(password);

            // Insert user
            String query = "INSERT INTO users (username, email, password_hash, full_name, phone, role, status) VALUES (?, ?, ?, ?, ?, ?, ?)";
            int result = db.executeUpdate(query,
                    user.getUsername(),
                    user.getEmail(),
                    passwordHash,
                    user.getFullName(),
                    user.getPhone(),
                    user.getRole(),
                    "ACTIVE");

            if (result > 0) {
                System.out.println("✓ User registered: " + user.getUsername());
                return user;
            }

            return null;
        } catch (SQLException e) {
            System.err.println("Registration error: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Authenticate user with username and password
     * 
     * @param username Username
     * @param password Plain text password
     * @return User object if authentication successful, null if failed
     * @throws SQLException if database error
     */
    public static User authenticateUser(String username, String password) throws SQLException {
        try {
            String query = "SELECT * FROM users WHERE username = ? AND status = 'ACTIVE'";
            ResultSet rs = db.executeQuery(query, username);

            if (rs.next()) {
                String storedHash = rs.getString("password_hash");

                // Verify password
                if (PasswordUtils.verifyPassword(password, storedHash)) {
                    // Update last login
                    String updateQuery = "UPDATE users SET last_login = NOW() WHERE user_id = ?";
                    db.executeUpdate(updateQuery, rs.getInt("user_id"));

                    // Create User object
                    User user = new User();
                    user.setUserId(rs.getInt("user_id"));
                    user.setUsername(rs.getString("username"));
                    user.setEmail(rs.getString("email"));
                    user.setFullName(rs.getString("full_name"));
                    user.setPhone(rs.getString("phone"));
                    user.setRole(rs.getString("role"));
                    user.setStatus(rs.getString("status"));

                    System.out.println("✓ User authenticated: " + username);
                    return user;
                } else {
                    System.err.println("✗ Invalid password for user: " + username);
                    return null;
                }
            }

            System.err.println("✗ User not found: " + username);
            return null;
        } catch (SQLException e) {
            System.err.println("Authentication error: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Check if username exists
     */
    public static boolean usernameExists(String username) throws SQLException {
        try {
            String query = "SELECT COUNT(*) as count FROM users WHERE username = ?";
            ResultSet rs = db.executeQuery(query, username);
            if (rs.next()) {
                return rs.getInt("count") > 0;
            }
            return false;
        } catch (SQLException e) {
            System.err.println("Error checking username: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Check if email exists
     */
    public static boolean emailExists(String email) throws SQLException {
        try {
            String query = "SELECT COUNT(*) as count FROM users WHERE email = ?";
            ResultSet rs = db.executeQuery(query, email);
            if (rs.next()) {
                return rs.getInt("count") > 0;
            }
            return false;
        } catch (SQLException e) {
            System.err.println("Error checking email: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Get user by ID
     */
    public static User getUserById(int userId) throws SQLException {
        try {
            String query = "SELECT * FROM users WHERE user_id = ?";
            ResultSet rs = db.executeQuery(query, userId);

            if (rs.next()) {
                User user = new User();
                user.setUserId(rs.getInt("user_id"));
                user.setUsername(rs.getString("username"));
                user.setEmail(rs.getString("email"));
                user.setFullName(rs.getString("full_name"));
                user.setPhone(rs.getString("phone"));
                user.setRole(rs.getString("role"));
                user.setStatus(rs.getString("status"));
                return user;
            }
            return null;
        } catch (SQLException e) {
            System.err.println("Error retrieving user: " + e.getMessage());
            throw e;
        }
    }
}
