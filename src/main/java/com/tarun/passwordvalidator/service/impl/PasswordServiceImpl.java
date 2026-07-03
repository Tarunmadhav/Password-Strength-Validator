package com.tarun.passwordvalidator.service.impl;

import com.tarun.passwordvalidator.exception.InvalidPasswordInputException;
import com.tarun.passwordvalidator.model.PasswordReport;
import com.tarun.passwordvalidator.model.PasswordReport.StrengthLevel;
import com.tarun.passwordvalidator.service.PasswordService;
import com.tarun.passwordvalidator.util.PasswordValidator;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Service implementation for password validation.
 * <p>
 * IoC/DI Wiring (for interview reference):
 * - This class is annotated with @Service, making it a Spring bean.
 * - The PasswordValidator is constructor-injected (see constructor below).
 * - Spring's dependency injection container will automatically provide an instance of PasswordValidator
 *   when creating an instance of PasswordServiceImpl.
 * - This follows constructor injection best practice, ensuring immutability and testability.
 */
@Service
public class PasswordServiceImpl implements PasswordService {

    private final PasswordValidator passwordValidator;

    public PasswordServiceImpl(PasswordValidator passwordValidator) {
        this.passwordValidator = passwordValidator;
    }

    @Override
    public PasswordReport validate(String password, String username) {
        // Validate input (blank check) - this could also be done via DTO validation, but we do it here for clarity
        if (password == null || password.isBlank()) {
            throw new InvalidPasswordInputException("Password cannot be blank");
        }
        if (username == null || username.isBlank()) {
            throw new InvalidPasswordInputException("Username cannot be blank");
        }

        List<String> suggestions = new ArrayList<>();
        int score = 0;

        // Length check (base points: up to 30 for length >=8, plus bonus)
        String lengthSuggestion = passwordValidator.validateLength(password);
        if (lengthSuggestion == null) {
            score += 30; // base length score for >=8
            // Bonus for length >=12 and >=16
            score += passwordValidator.lengthBonus(password);
        } else {
            suggestions.add(lengthSuggestion);
        }

        // Uppercase
        String upperSuggestion = passwordValidator.validateUppercase(password);
        if (upperSuggestion == null) {
            score += 15;
        } else {
            suggestions.add(upperSuggestion);
        }

        // Lowercase
        String lowerSuggestion = passwordValidator.validateLowercase(password);
        if (lowerSuggestion == null) {
            score += 15;
        } else {
            suggestions.add(lowerSuggestion);
        }

        // Digit
        String digitSuggestion = passwordValidator.validateDigit(password);
        if (digitSuggestion == null) {
            score += 15;
        } else {
            suggestions.add(digitSuggestion);
        }

        // Special character
        String specialSuggestion = passwordValidator.validateSpecialChar(password);
        if (specialSuggestion == null) {
            score += 15;
        } else {
            suggestions.add(specialSuggestion);
        }

        // Common password
        String commonSuggestion = passwordValidator.validateCommonPassword(password);
        if (commonSuggestion == null) {
            score += 10; // bonus for not being common
        } else {
            suggestions.add(commonSuggestion);
        }

        // Repeated chars
        String repeatSuggestion = passwordValidator.validateRepeatedChars(password);
        if (repeatSuggestion == null) {
            score += 10; // bonus for no repeats
        } else {
            suggestions.add(repeatSuggestion);
        }

        // Sequential chars
        String seqSuggestion = passwordValidator.validateSequentialChars(password);
        if (seqSuggestion == null) {
            score += 5; // bonus for no sequences
        } else {
            suggestions.add(seqSuggestion);
        }

        // Username similarity
        String userSuggestion = passwordValidator.validateUsernameSimilarity(password, username);
        if (userSuggestion == null) {
            score += 5; // bonus for username uniqueness
        } else {
            suggestions.add(userSuggestion);
        }

        // Cap score at 100
        if (score > 100) {
            score = 100;
        }

        // Determine strength level
        StrengthLevel strengthLevel;
        if (score >= 80) {
            strengthLevel = StrengthLevel.STRONG;
        } else if (score >= 60) {
            strengthLevel = StrengthLevel.MEDIUM;
        } else {
            strengthLevel = StrengthLevel.WEAK;
        }

        return new PasswordReport(score, strengthLevel, suggestions);
    }
}