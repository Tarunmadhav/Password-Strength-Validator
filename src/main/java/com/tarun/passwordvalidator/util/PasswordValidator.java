package com.tarun.passwordvalidator.util;

import org.springframework.stereotype.Component;
import java.util.HashSet;
import java.util.Set;

/**
 * Validates password strength based on various criteria.
 */
@Component
public class PasswordValidator {

    private static final Set<String> COMMON_PASSWORDS = new HashSet<>();
    static {
        // Common passwords (approx 20)
        COMMON_PASSWORDS.add("password");
        COMMON_PASSWORDS.add("123456");
        COMMON_PASSWORDS.add("12345678");
        COMMON_PASSWORDS.add("qwerty");
        COMMON_PASSWORDS.add("abc123");
        COMMON_PASSWORDS.add("monkey");
        COMMON_PASSWORDS.add("letmein");
        COMMON_PASSWORDS.add("dragon");
        COMMON_PASSWORDS.add("111111");
        COMMON_PASSWORDS.add("baseball");
        COMMON_PASSWORDS.add("iloveyou");
        COMMON_PASSWORDS.add("trustno1");
        COMMON_PASSWORDS.add("sunshine");
        COMMON_PASSWORDS.add("master");
        COMMON_PASSWORDS.add("123123");
        COMMON_PASSWORDS.add("welcome");
        COMMON_PASSWORDS.add("shadow");
        COMMON_PASSWORDS.add("ashley");
        COMMON_PASSWORDS.add("football");
        COMMON_PASSWORDS.add("jesus");
        COMMON_PASSWORDS.add("michael");
        COMMON_PASSWORDS.add("ninja");
        COMMON_PASSWORDS.add("mustang");
        COMMON_PASSWORDS.add("password1");
    }

    /**
     * Checks if password meets minimum length requirement.
     * @param password the password to check
     * @param minLength the minimum required length
     * @return true if password length >= minLength
     */
    public boolean hasMinLength(String password, int minLength) {
        return password != null && password.length() >= minLength;
    }

    /**
     * Checks if password contains at least one uppercase letter.
     * @param password the password to check
     * @return true if password contains at least one uppercase letter
     */
    public boolean hasUppercase(String password) {
        if (password == null) {
            return false;
        }
        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if password contains at least one lowercase letter.
     * @param password the password to check
     * @return true if password contains at least one lowercase letter
     */
    public boolean hasLowercase(String password) {
        if (password == null) {
            return false;
        }
        for (char c : password.toCharArray()) {
            if (Character.isLowerCase(c)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if password contains at least one digit.
     * @param password the password to check
     * @return true if password contains at least one digit
     */
    public boolean hasDigit(String password) {
        if (password == null) {
            return false;
        }
        for (char c : password.toCharArray()) {
            if (Character.isDigit(c)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if password contains at least one special character.
     * Special characters are defined as non-alphanumeric.
     * @param password the password to check
     * @return true if password contains at least one special character
     */
    public boolean hasSpecialChar(String password) {
        if (password == null) {
            return false;
        }
        for (char c : password.toCharArray()) {
            if (!Character.isLetterOrDigit(c)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if password contains the username (case-insensitive).
     * @param password the password to check
     * @param username the username to check against
     * @return true if password contains username as a substring
     */
    public boolean containsUsername(String password, String username) {
        if (password == null || username == null || username.isEmpty()) {
            return false;
        }
        return password.toLowerCase().contains(username.toLowerCase());
    }

    /**
     * Checks if password is a common password.
     * @param password the password to check
     * @return true if password is in the common passwords set
     */
    public boolean isCommonPassword(String password) {
        if (password == null) {
            return false;
        }
        return COMMON_PASSWORDS.contains(password.toLowerCase());
    }

    /**
     * Checks if password has three or more identical consecutive characters.
     * @param password the password to check
     * @return true if password contains 3+ identical consecutive characters
     */
    public boolean hasRepeatedChars(String password) {
        if (password == null || password.length() < 3) {
            return false;
        }
        char prev = password.charAt(0);
        int count = 1;
        for (int i = 1; i < password.length(); i++) {
            char c = password.charAt(i);
            if (c == prev) {
                count++;
                if (count >= 3) {
                    return true;
                }
            } else {
                prev = c;
                count = 1;
            }
        }
        return false;
    }

    /**
     * Checks if password has three or more consecutive sequential characters
     * (either increasing or decreasing by 1 in ASCII).
     * Only considers alphanumeric sequences for simplicity.
     * @param password the password to check
     * @return true if password contains 3+ consecutive sequential characters
     */
    public boolean hasSequentialChars(String password) {
        if (password == null || password.length() < 3) {
            return false;
        }
        for (int i = 0; i < password.length() - 2; i++) {
            char c1 = password.charAt(i);
            char c2 = password.charAt(i + 1);
            char c3 = password.charAt(i + 2);
            // Check for increasing sequence (e.g., abc, 123)
            if ((c2 == c1 + 1) && (c3 == c2 + 1)) {
                return true;
            }
            // Check for decreasing sequence (e.g., cba, 321)
            if ((c2 == c1 - 1) && (c3 == c2 - 1)) {
                return true;
            }
        }
        return false;
    }
}