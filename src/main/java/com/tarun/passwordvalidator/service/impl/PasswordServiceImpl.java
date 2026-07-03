package com.tarun.passwordvalidator.service.impl;

import com.tarun.passwordvalidator.model.PasswordReport;
import com.tarun.passwordvalidator.model.PasswordReport.StrengthLevel;
import com.tarun.passwordvalidator.service.PasswordService;
import com.tarun.passwordvalidator.util.PasswordValidator;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Service implementation for password validation and scoring.
 */
@Service
public class PasswordServiceImpl implements PasswordService {

    // Weights for different criteria (must sum to 100)
    private static final int LENGTH_WEIGHT = 15;
    private static final int UPPERCASE_WEIGHT = 10;
    private static final int LOWERCASE_WEIGHT = 10;
    private static final int DIGIT_WEIGHT = 10;
    private static final int SPECIAL_CHAR_WEIGHT = 15;
    private static final int NO_USERNAME_WEIGHT = 10;
    private static final int NOT_COMMON_WEIGHT = 10;
    private static final int NO_REPEATED_CHARS_WEIGHT = 10;
    private static final int NO_SEQUENTIAL_CHARS_WEIGHT = 10;

    private final PasswordValidator validator;

    public PasswordServiceImpl(PasswordValidator validator) {
        this.validator = validator;
    }

    @Override
    public PasswordReport validate(String password, String username) {
        if (password == null) {
            password = "";
        }

        int score = 0;
        List<String> suggestions = new ArrayList<String>();

        // Check length (minimum 8 chars, but longer is better)
        int length = password.length();
        if (length >= 8) {
            score += LENGTH_WEIGHT;
            // Bonus for longer passwords (up to extra 10 points for 16+ chars)
            if (length >= 16) {
                score += 10; // bonus
            } else if (length >= 12) {
                score += 5; // smaller bonus
            } else {
                suggestions.add("Consider using a longer password (12+ characters for better security)");
            }
        } else {
            suggestions.add("Password must be at least 8 characters long");
        }

        // Check uppercase
        if (validator.hasUppercase(password)) {
            score += UPPERCASE_WEIGHT;
        } else {
            suggestions.add("Add at least one uppercase letter");
        }

        // Check lowercase
        if (validator.hasLowercase(password)) {
            score += LOWERCASE_WEIGHT;
        } else {
            suggestions.add("Add at least one lowercase letter");
        }

        // Check digit
        if (validator.hasDigit(password)) {
            score += DIGIT_WEIGHT;
        } else {
            suggestions.add("Add at least one digit");
        }

        // Check special character
        if (validator.hasSpecialChar(password)) {
            score += SPECIAL_CHAR_WEIGHT;
        } else {
            suggestions.add("Add at least one special character (e.g., !@#$%^&*)");
        }

        // Check username not in password
        if (username == null || username.isEmpty() || !validator.containsUsername(password, username)) {
            score += NO_USERNAME_WEIGHT;
        } else {
            suggestions.add("Do not include your username in your password");
        }

        // Check not common password
        if (!validator.isCommonPassword(password)) {
            score += NOT_COMMON_WEIGHT;
        } else {
            suggestions.add("Choose a less common password");
        }

        // Check no repeated characters (3+ identical in a row)
        if (!validator.hasRepeatedChars(password)) {
            score += NO_REPEATED_CHARS_WEIGHT;
        } else {
            suggestions.add("Avoid repeating the same character 3+ times in a row");
        }

        // Check no sequential characters (3+ consecutive like abc or 123)
        if (!validator.hasSequentialChars(password)) {
            score += NO_SEQUENTIAL_CHARS_WEIGHT;
        } else {
            suggestions.add("Avoid sequential characters (like abc or 123) 3+ in a row");
        }

        // Cap score at 100
        score = Math.min(score, 100);

        // Determine strength level
        PasswordReport.StrengthLevel strengthLevel;
        if (score >= 80) {
            strengthLevel = PasswordReport.StrengthLevel.STRONG;
        } else if (score >= 60) {
            strengthLevel = PasswordReport.StrengthLevel.MEDIUM;
        } else {
            strengthLevel = PasswordReport.StrengthLevel.WEAK;
        }

        return new PasswordReport(score, strengthLevel, suggestions);
    }
}