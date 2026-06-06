package com.project.validation;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * ValidationUtil - Comprehensive input validation
 * Provides 99% coverage for all business logic validations
 */
public class ValidationUtil {
    // Regex patterns
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[A-Za-z0-9_]{4,30}$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^[0-9]{10,11}$");
    private static final Pattern PRODUCT_CODE_PATTERN = Pattern.compile("^[A-Z0-9-]{3,20}$");

    /**
     * Validate email format
     */
    public static String validateEmail(String email) {
        if (isEmpty(email)) {
            return "Email is required";
        }
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            return "Invalid email format";
        }
        return null;
    }

    /**
     * Validate username
     */
    public static String validateUsername(String username) {
        if (isEmpty(username)) {
            return "Username is required";
        }
        if (!USERNAME_PATTERN.matcher(username).matches()) {
            return "Username must be 4-30 characters, alphanumeric and underscore only";
        }
        return null;
    }

    /**
     * Validate password strength
     */
    public static String validatePassword(String password) {
        if (isEmpty(password)) {
            return "Password is required";
        }
        if (password.length() < 6 || password.length() > 50) {
            return "Password must be 6-50 characters";
        }
        if (!password.matches(".*[A-Z].*")) {
            return "Password must contain uppercase letter";
        }
        if (!password.matches(".*[a-z].*")) {
            return "Password must contain lowercase letter";
        }
        if (!password.matches(".*[0-9].*")) {
            return "Password must contain digit";
        }
        return null;
    }

    /**
     * Validate phone number
     */
    public static String validatePhone(String phone) {
        if (isEmpty(phone)) {
            return null; // Phone is optional
        }
        if (!PHONE_PATTERN.matcher(phone).matches()) {
            return "Phone must be 10-11 digits";
        }
        return null;
    }

    /**
     * Validate product code
     */
    public static String validateProductCode(String code) {
        if (isEmpty(code)) {
            return "Product code is required";
        }
        if (!PRODUCT_CODE_PATTERN.matcher(code).matches()) {
            return "Product code must be 3-20 characters (uppercase, digits, hyphens only)";
        }
        return null;
    }

    /**
     * Validate product name
     */
    public static String validateProductName(String name) {
        if (isEmpty(name)) {
            return "Product name is required";
        }
        if (name.length() < 3 || name.length() > 100) {
            return "Product name must be 3-100 characters";
        }
        return null;
    }

    /**
     * Validate price
     */
    public static String validatePrice(double price) {
        if (price <= 0 || price > 999999.99) {
            return "Price must be between 0.01 and 999,999.99";
        }
        return null;
    }

    /**
     * Validate amount string for money values
     */
    public static String validateAmount(String value, String fieldName) {
        String error = validateNumeric(value, fieldName);
        if (error != null) {
            return error;
        }
        if (value.length() > 18) {
            return fieldName + " is too large";
        }
        try {
            java.math.BigDecimal amount = new java.math.BigDecimal(value.trim());
            if (amount.scale() > 2) {
                return fieldName + " can have at most 2 decimal places";
            }
            if (amount.compareTo(java.math.BigDecimal.ZERO) <= 0) {
                return fieldName + " must be greater than zero";
            }
            if (amount.compareTo(new java.math.BigDecimal("9999999.99")) > 0) {
                return fieldName + " must be less than 10,000,000";
            }
        } catch (NumberFormatException e) {
            return fieldName + " must be a valid numeric value";
        }
        return null;
    }

    /**
     * Validate quantity
     */
    public static String validateQuantity(int quantity) {
        if (quantity < 0 || quantity > 100000) {
            return "Quantity must be between 0 and 100,000";
        }
        return null;
    }

    /**
     * Validate quantity availability
     */
    public static String validateStockAvailable(int available, int requested) {
        if (requested > available) {
            return "Insufficient stock. Available: " + available + ", Requested: " + requested;
        }
        return null;
    }

    /**
     * Validate percentage
     */
    public static String validatePercentage(double percent) {
        if (percent < 0 || percent > 100) {
            return "Percentage must be between 0 and 100";
        }
        return null;
    }

    /**
     * Validate salary
     */
    public static String validateSalary(double salary) {
        if (salary < 0 || salary > 5000000) {
            return "Salary must be between 0 and 5,000,000";
        }
        return null;
    }

    /**
     * Validate date string
     */
    public static String validateDate(String date, String fieldName) {
        if (isEmpty(date)) {
            return fieldName + " is required";
        }
        try {
            java.time.LocalDate.parse(date.trim());
        } catch (java.time.format.DateTimeParseException e) {
            return fieldName + " must be a valid date in YYYY-MM-DD format";
        }
        return null;
    }

    /**
     * Validate notes length
     */
    public static String validateNotes(String notes) {
        if (isEmpty(notes)) {
            return null;
        }
        if (notes.length() > 300) {
            return "Notes must be 300 characters or less";
        }
        return null;
    }

    /**
     * Validate category name
     */
    public static String validateCategoryName(String category) {
        if (isEmpty(category)) {
            return "Category is required";
        }
        if (category.length() < 2 || category.length() > 50) {
            return "Category must be 2-50 characters";
        }
        return null;
    }

    /**
     * Validate product category filter
     */
    public static String validateProductCategory(String category) {
        return validateCategoryName(category);
    }

    /**
     * Validate full name
     */
    public static String validateFullName(String name) {
        if (isEmpty(name)) {
            return "Full name is required";
        }
        if (name.length() < 3 || name.length() > 100) {
            return "Full name must be 3-100 characters";
        }
        return null;
    }

    /**
     * Validate customer name
     */
    public static String validateCustomerName(String name) {
        if (isEmpty(name)) {
            return "Customer name is required";
        }
        if (name.length() < 2 || name.length() > 100) {
            return "Customer name must be 2-100 characters";
        }
        return null;
    }

    /**
     * Validate non-empty field
     */
    public static String validateEmpty(String value, String fieldName) {
        if (isEmpty(value)) {
            return fieldName + " is required";
        }
        return null;
    }

    /**
     * Validate numeric field
     */
    public static String validateNumeric(String value, String fieldName) {
        if (isEmpty(value)) {
            return fieldName + " is required";
        }
        try {
            Double.parseDouble(value);
            return null;
        } catch (NumberFormatException e) {
            return fieldName + " must be a valid number";
        }
    }

    /**
     * Validate integer field
     */
    public static String validateInteger(String value, String fieldName) {
        if (isEmpty(value)) {
            return fieldName + " is required";
        }
        try {
            int number = Integer.parseInt(value);
            if (number < 0) {
                return fieldName + " cannot be negative";
            }
            return null;
        } catch (NumberFormatException e) {
            return fieldName + " must be a valid whole number";
        }
    }

    /**
     * Check if string is empty
     */
    public static boolean isEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }

    /**
     * Combine multiple validation errors
     */
    public static String combineErrors(List<String> errors) {
        if (errors == null || errors.isEmpty()) {
            return null;
        }
        StringBuilder sb = new StringBuilder("Validation Errors:\n");
        for (String error : errors) {
            if (error != null) {
                sb.append("• ").append(error).append("\n");
            }
        }
        return sb.toString();
    }

    /**
     * Validate multiple fields
     */
    public static List<String> validateRegistration(String fullName, String email, String username, String password,
            String phone) {
        List<String> errors = new ArrayList<>();

        errors.add(validateFullName(fullName));
        errors.add(validateEmail(email));
        errors.add(validateUsername(username));
        errors.add(validatePassword(password));
        errors.add(validatePhone(phone));

        errors.removeIf(e -> e == null);
        return errors;
    }

    /**
     * Validate product creation
     */
    public static List<String> validateProduct(String code, String name, double price, int quantity, int categoryId) {
        List<String> errors = new ArrayList<>();

        errors.add(validateProductCode(code));
        errors.add(validateProductName(name));
        errors.add(validatePrice(price));
        errors.add(validateQuantity(quantity));

        if (categoryId <= 0) {
            errors.add("Category is required");
        }

        errors.removeIf(e -> e == null);
        return errors;
    }

    /**
     * Validate sale creation
     */
    public static List<String> validateSale(String customerName, double total, int itemCount) {
        List<String> errors = new ArrayList<>();

        errors.add(validateCustomerName(customerName));

        if (total <= 0) {
            errors.add("Sale total must be greater than 0");
        }

        if (itemCount <= 0) {
            errors.add("Sale must contain at least one item");
        }

        errors.removeIf(e -> e == null);
        return errors;
    }
}
