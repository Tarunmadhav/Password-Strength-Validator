package com.tarun.passwordvalidator.service.impl;

import com.tarun.passwordvalidator.dto.PasswordReportDto;
import com.tarun.passwordvalidator.dto.RuleResult;
import com.tarun.passwordvalidator.exception.InvalidPasswordInputException;
import com.tarun.passwordvalidator.service.PasswordService;
import com.tarun.passwordvalidator.util.PasswordValidator;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public PasswordReportDto validate(String password, String username) {
        // Validate input (blank check) - this could also be done via DTO validation, but we do it here for clarity
        if (password == null || password.isBlank()) {
            throw new InvalidPasswordInputException("Password cannot be blank");
        }
        if (username == null || username.isBlank()) {
            throw new InvalidPasswordInputException("Username cannot be blank");
        }

        // Define raw weights (as per spec example, plus username=10)
        Map<String, Integer> rawWeights = new HashMap<>();
        rawWeights.put("Length", 20);
        rawWeights.put("Uppercase", 10);
        rawWeights.put("Lowercase", 10);
        rawWeights.put("Digit", 10);
        rawWeights.put("Special Character", 15);
        rawWeights.put("Common Password", 30);
        rawWeights.put("Username Detection", 10);
        rawWeights.put("Sequential Characters", 10);
        rawWeights.put("Repeated Characters", 10);
        rawWeights.put("Whitespace", 10);

        int totalRawWeight = rawWeights.values().stream().mapToInt(Integer::intValue).sum(); // 135
        double scale = 100.0 / totalRawWeight;

        // Track pass/fail and suggestions for each rule
        Map<String, Boolean> passedMap = new HashMap<>();
        Map<String, String> suggestionsMap = new HashMap<>();

        // Length check
        String lengthSuggestion = passwordValidator.validateLength(password);
        boolean lengthPassed = (lengthSuggestion == null);
        passedMap.put("Length", lengthPassed);
        suggestionsMap.put("Length", lengthSuggestion);

        // Uppercase
        String upperSuggestion = passwordValidator.validateUppercase(password);
        boolean upperPassed = (upperSuggestion == null);
        passedMap.put("Uppercase", upperPassed);
        suggestionsMap.put("Uppercase", upperSuggestion);

        // Lowercase
        String lowerSuggestion = passwordValidator.validateLowercase(password);
        boolean lowerPassed = (lowerSuggestion == null);
        passedMap.put("Lowercase", lowerPassed);
        suggestionsMap.put("Lowercase", lowerSuggestion);

        // Digit
        String digitSuggestion = passwordValidator.validateDigit(password);
        boolean digitPassed = (digitSuggestion == null);
        passedMap.put("Digit", digitPassed);
        suggestionsMap.put("Digit", digitSuggestion);

        // Special character
        String specialSuggestion = passwordValidator.validateSpecialChar(password);
        boolean specialPassed = (specialSuggestion == null);
        passedMap.put("Special Character", specialPassed);
        suggestionsMap.put("Special Character", specialSuggestion);

        // Common password
        String commonSuggestion = passwordValidator.validateCommonPassword(password);
        boolean commonPassed = (commonSuggestion == null);
        passedMap.put("Common Password", commonPassed);
        suggestionsMap.put("Common Password", commonSuggestion);

        // Username detection
        String userSuggestion = passwordValidator.validateUsernameSimilarity(password, username);
        boolean userPassed = (userSuggestion == null);
        passedMap.put("Username Detection", userPassed);
        suggestionsMap.put("Username Detection", userSuggestion);

        // Sequential characters
        String seqSuggestion = passwordValidator.validateSequentialChars(password);
        boolean seqPassed = (seqSuggestion == null);
        passedMap.put("Sequential Characters", seqPassed);
        suggestionsMap.put("Sequential Characters", seqSuggestion);

        // Repeated characters
        String repeatSuggestion = passwordValidator.validateRepeatedChars(password);
        boolean repeatPassed = (repeatSuggestion == null);
        passedMap.put("Repeated Characters", repeatPassed);
        suggestionsMap.put("Repeated Characters", repeatSuggestion);

        // Whitespace
        String whitespaceSuggestion = passwordValidator.validateWhitespace(password);
        boolean whitespacePassed = (whitespaceSuggestion == null);
        passedMap.put("Whitespace", whitespacePassed);
        suggestionsMap.put("Whitespace", whitespaceSuggestion);

        // Start with 100 points
        double score = 100.0;

        // Apply deductions for each failed rule (with special handling for length)
        for (Map.Entry<String, Integer> entry : rawWeights.entrySet()) {
            String rule = entry.getKey();
            int weight = entry.getValue();
            boolean passed = passedMap.get(rule);

            if (!passed) {
                if (rule.equals("Length")) {
                    // Length rule: if failed (length < 8) -> full weight deduction
                    // Note: if passed, we handle below in the else block
                    score -= weight * scale;
                } else {
                    score -= weight * scale;
                }
            } else {
                // Rule passed, but for length we may have a partial deduction if length is between 8 and 12
                if (rule.equals("Length")) {
                    // Check if length is at least 12 for full points, otherwise half deduction
                    if (password != null && password.length() >= 12) {
                        // Full points for length -> no deduction
                    } else {
                        // Length is between 8 and 11 -> deduct half the weight
                        score -= (weight / 2.0) * scale;
                    }
                }
                // For all other rules, if passed, no deduction
            }
        }

        // Apply username override penalty if username check failed
        if (!userPassed) {
            score = score * 0.3;
            // Hard-cap the final result at 20
            if (score > 20.0) {
                score = 20.0;
            }
        }

        // Ensure score is not negative (shouldn't happen with our weights, but just in case)
        if (score < 0) {
            score = 0.0;
        }

        // Round to nearest integer for display
        int finalScore = (int) Math.round(score);

        // Determine strength level based on final score
        String strengthLevel;
        if (finalScore >= 91) {
            strengthLevel = "VERY STRONG";
        } else if (finalScore >= 76) {
            strengthLevel = "STRONG";
        } else if (finalScore >= 61) {
            strengthLevel = "MEDIUM";
        } else if (finalScore >= 41) {
            strengthLevel = "WEAK";
        } else if (finalScore >= 21) {
            strengthLevel = "VERY WEAK";
        } else {
            strengthLevel = "CRITICAL";
        }

        // Build list of suggestions for failed rules
        List<String> failedSuggestions = new ArrayList<>();
        for (Map.Entry<String, String> entry : suggestionsMap.entrySet()) {
            if (!passedMap.get(entry.getKey())) {
                failedSuggestions.add(entry.getValue());
            }
        }

        // Build list of RuleResult for the view
        List<RuleResult> ruleResults = new ArrayList<>();
        for (Map.Entry<String, Boolean> entry : passedMap.entrySet()) {
            String ruleName = entry.getKey();
            boolean passed = entry.getValue();
            String suggestion = suggestionsMap.get(ruleName);
            ruleResults.add(new RuleResult(ruleName, passed, suggestion));
        }

        // Create and return the DTO
        PasswordReportDto dto = new PasswordReportDto();
        dto.setScore(finalScore);
        dto.setStrengthLevel(strengthLevel);
        dto.setSuggestions(failedSuggestions);
        dto.setRuleResults(ruleResults);
        return dto;
    }
}