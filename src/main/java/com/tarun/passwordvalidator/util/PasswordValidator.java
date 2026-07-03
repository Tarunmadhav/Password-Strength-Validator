package com.tarun.passwordvalidator.util;

import java.util.HashSet;
import java.util.Set;
import org.springframework.stereotype.Component;
/**
 * Validates password strength based on various criteria.
 * Each rule returns a suggestion string if the rule fails, or null if passes.
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

    // Keyboard rows for sequential check
    private static final String[] KEYBOARD_ROWS = {
            "`1234567890-=",  // top row
            " qwertyuiop[]\\",  // home row (note leading space for pinky)
            " asdfghjkl;''",   // home row (note: we'll adjust)
            " zxcvbnm,./"
    };
    // Actually, let's define common sequences for simplicity
    private static final String[] SEQUENCES = {
            "abc", "bcd", "cde", "def", "efg", "fgh", "ghi", "hij", "ijk", "jkl", "klm", "lmn", "mno", "nop", "opq", "pqr", "qrs", "rst", "stu", "tuv", "uvw", "vwx", "wxy", "xyz",
            "012", "123", "234", "345", "456", "567", "678", "789", "890",
            "qwe", "wrt", "yui", "iop", "asd", "sdf", "dfg", "fgh", "ghj", "hjk", "jkl",
            "zxc", "xcv", "cvb", "vbn", "bnm",
            "qaz", "wsx", "edc", "rfv", "tgb", "yhn", "ujm", "ik,", "ol.", "p;/"
    };

    /**
     * Checks if password meets minimum length requirement (>=8).
     * @param password the password to check
     * @return null if length >= 8, otherwise suggestion to increase length
     */
    public String validateLength(String password) {
        if (password != null && password.length() >= 8) {
            return null;
        }
        return "Password must be at least 8 characters long";
    }

    /**
     * Checks if password contains at least one uppercase letter.
     * @param password the password to check
     * @return null if contains uppercase, otherwise suggestion
     */
    public String validateUppercase(String password) {
        if (password != null) {
            for (char c : password.toCharArray()) {
                if (Character.isUpperCase(c)) {
                    return null;
                }
            }
        }
        return "Add at least one uppercase letter";
    }

    /**
     * Checks if password contains at least one lowercase letter.
     * @param password the password to check
     * @return null if contains lowercase, otherwise suggestion
     */
    public String validateLowercase(String password) {
        if (password != null) {
            for (char c : password.toCharArray()) {
                if (Character.isLowerCase(c)) {
                    return null;
                }
            }
        }
        return "Add at least one lowercase letter";
    }

    /**
     * Checks if password contains at least one digit.
     * @param password the password to check
     * @return null if contains digit, otherwise suggestion
     */
    public String validateDigit(String password) {
        if (password != null) {
            for (char c : password.toCharArray()) {
                if (Character.isDigit(c)) {
                    return null;
                }
            }
        }
        return "Add at least one digit";
    }

    /**
     * Checks if password contains at least one special character.
     * Special characters are defined as non-alphanumeric.
     * @param password the password to check
     * @return null if contains special char, otherwise suggestion
     */
    public String validateSpecialChar(String password) {
        if (password != null) {
            for (char c : password.toCharArray()) {
                if (!Character.isLetterOrDigit(c)) {
                    return null;
                }
            }
        }
        return "Add at least one special character (e.g., !@#$%^&*)";
    }

    /**
     * Checks if password is a common password.
     * @param password the password to check
     * @return null if not common, otherwise suggestion
     */
    public String validateCommonPassword(String password) {
        if (password != null && !COMMON_PASSWORDS.contains(password.toLowerCase())) {
            return null;
        }
        return "Choose a less common password";
    }

    /**
     * Checks if password has three or more identical consecutive characters.
     * @param password the password to check
     * @return null if no 3+ identical consecutive chars, otherwise suggestion
     */
    public String validateRepeatedChars(String password) {
        if (password == null || password.length() < 3) {
            return null;
        }
        char prev = password.charAt(0);
        int count = 1;
        for (int i = 1; i < password.length(); i++) {
            char c = password.charAt(i);
            if (c == prev) {
                count++;
                if (count >= 3) {
                    return "Avoid repeating the same character 3+ times in a row";
                }
            } else {
                prev = c;
                count = 1;
            }
        }
        return null;
    }

    /**
     * Checks if password has three or more consecutive sequential characters
     * (either increasing or decreasing by 1 in ASCII) or common keyboard sequences.
     * @param password the password to check
     * @return null if no 3+ sequential chars, otherwise suggestion
     */
    public String validateSequentialChars(String password) {
        if (password == null || password.length() < 3) {
            return null;
        }
        String lower = password.toLowerCase();
        // Check ASCII sequences (e.g., abc, 123)
        for (int i = 0; i < password.length() - 2; i++) {
            char c1 = password.charAt(i);
            char c2 = password.charAt(i + 1);
            char c3 = password.charAt(i + 2);
            // Check for increasing sequence (e.g., abc, 123)
            if ((c2 == c1 + 1) && (c3 == c2 + 1)) {
                return "Avoid sequential characters (like abc or 123) 3+ in a row";
            }
            // Check for decreasing sequence (e.g., cba, 321)
            if ((c2 == c1 - 1) && (c3 == c2 - 1)) {
                return "Avoid sequential characters (like abc or 123) 3+ in a row";
            }
        }
        // Check common keyboard sequences
        for (String seq : SEQUENCES) {
            if (lower.contains(seq)) {
                return "Avoid common keyboard sequences (like qwe or asd) 3+ in a row";
            }
        }
        return null;
    }

    /**
     * Checks if password contains the username (case-insensitive) or is too similar.
     * Uses a simple length-based similarity: if the longest common substring is at least 50% of password length.
     * @param password the password to check
     * @param username the username to check against
     * @return null if username not contained or similarity low, otherwise suggestion
     */
    public String validateUsernameSimilarity(String password, String username) {
        if (password == null || username == null || username.isEmpty()) {
            return null;
        }
        String lowerPass = password.toLowerCase();
        String lowerUser = username.toLowerCase();
        // Exact match
        if (lowerPass.equals(lowerUser)) {
            return "Do not use your username as your password";
        }
        // Contains username as substring
        if (lowerPass.contains(lowerUser) || lowerUser.contains(lowerPass)) {
            return "Do not include your username in your password";
        }
        // Similarity check: longest common substring length relative to password length
        int lcs = longestCommonSubstringLength(lowerPass, lowerUser);
        double similarity = (double) lcs / password.length();
        if (similarity >= 0.5) { // 50% or more of password matches username in order
            return "Avoid using your username or a variant of it within your password";
        }
        return null;
    }

    /**
     * Returns the length of the longest common substring between two strings.
     * @param s1 first string
     * @param s2 second string
     * @return length of longest common substring
     */
    private int longestCommonSubstringLength(String s1, String s2) {
        int max = 0;
        for (int i = 0; i < s1.length(); i++) {
            for (int j = 0; j < s2.length(); j++) {
                int k = 0;
                while (i + k < s1.length() && j + k < s2.length() && s1.charAt(i + k) == s2.charAt(j + k)) {
                    k++;
                }
                if (k > max) {
                    max = k;
                }
            }
        }
        return max;
    }

    /**
     * Returns bonus points for length >=12 and >=16.
     * @param password the password to check
     * @return bonus points (0, 5, or 10)
     */
    public int lengthBonus(String password) {
        if (password == null) {
            return 0;
        }
        int len = password.length();
        if (len >= 16) {
            return 10;
        } else if (len >= 12) {
            return 5;
        }
        return 0;
    }
}