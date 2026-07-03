package com.tarun.passwordvalidator.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Holds the results of a password strength evaluation.
 */
public class PasswordReport {

    private int score;
    private StrengthLevel strengthLevel;
    private List<String> suggestions;

    /**
     * Constructs a password report.
     * @param score the score (0-100)
     * @param strengthLevel the strength level (WEAK, MEDIUM, STRONG)
     * @param suggestions list of suggestions for improvement
     */
    public PasswordReport(int score, StrengthLevel strengthLevel, List<String> suggestions) {
        this.score = score;
        this.strengthLevel = strengthLevel;
        this.suggestions = suggestions;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public StrengthLevel getStrengthLevel() {
        return strengthLevel;
    }

    public void setStrengthLevel(StrengthLevel strengthLevel) {
        this.strengthLevel = strengthLevel;
    }

    public List<String> getSuggestions() {
        return suggestions;
    }

    public void setSuggestions(List<String> suggestions) {
        this.suggestions = suggestions;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("==== Password Strength Report ====\n");
        sb.append(String.format("Score : %d / 100%n", score));
        sb.append(String.format("Strength : %s%n", strengthLevel));
        sb.append("Suggestions :\n");
        if (suggestions.isEmpty()) {
            sb.append("  - (none - password is strong!)\n");
        } else {
            for (String suggestion : suggestions) {
                sb.append("  - ").append(suggestion).append('\n');
            }
        }
        sb.append("===================================\n");
        return sb.toString();
    }

    /**
     * Enum for password strength levels.
     */
    public enum StrengthLevel {
        WEAK, MEDIUM, STRONG
    }
}