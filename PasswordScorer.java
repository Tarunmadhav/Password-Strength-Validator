import java.util.ArrayList;
import java.util.List;

/**
 * Scores password strength based on validation results.
 */
public class PasswordScorer {

    /**
     * Strength levels for passwords.
     */
    public enum StrengthLevel {
        WEAK, MEDIUM, STRONG
    }

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

    public PasswordScorer() {
        this.validator = new PasswordValidator();
    }

    /**
     * Determines the strength level based on the score.
     * @param score the score (0-100)
     * @return the strength level (WEAK, MEDIUM, or STRONG)
     */
    public StrengthLevel getStrengthLevel(int score) {
        if (score < 60) {
            return StrengthLevel.WEAK;
        } else if (score < 80) {
            return StrengthLevel.MEDIUM;
        } else {
            return StrengthLevel.STRONG;
        }
    }

    /**
     * Scores a password based on various strength criteria.
     *
     * @param password the password to score
     * @param username the username to check against (for username-in-password rule)
     * @return a PasswordReport containing the score, strength level, and suggestions
     */
    public PasswordReport scorePassword(String password, String username) {
        if (password == null) {
            password = "";
        }

        int score = 0;
        List<String> suggestions = new ArrayList<>();

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