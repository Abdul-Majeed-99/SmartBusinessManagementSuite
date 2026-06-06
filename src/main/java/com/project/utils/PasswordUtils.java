package com.project.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * PasswordUtils - Secure password hashing and validation
 */
public class PasswordUtils {
    private static final String ALGORITHM = "SHA-256";

    /**
     * Hash password using SHA-256
     * @param password Plain text password
     * @return Hashed password in hexadecimal format
     */
    public static String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance(ALGORITHM);
            byte[] hash = digest.digest(password.getBytes());
            return bytesToHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not found", e);
        }
    }

    /**
     * Verify password against hash
     * @param password Plain text password
     * @param hash Stored hash
     * @return true if password matches hash
     */
    public static boolean verifyPassword(String password, String hash) {
        String hashOfPassword = hashPassword(password);
        return hashOfPassword.equals(hash);
    }

    /**
     * Convert bytes to hexadecimal string
     */
    private static String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    /**
     * Generate random token
     */
    public static String generateToken(int length) {
        byte[] randomBytes = new byte[length];
        new SecureRandom().nextBytes(randomBytes);
        return Base64.getEncoder().encodeToString(randomBytes);
    }
}
