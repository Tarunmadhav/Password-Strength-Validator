package com.tarun.passwordvalidator.dto;

import java.util.List;

/**
 * DTO for password validation response.
 */
public class PasswordReportDto {

    private int score;
    private String strengthLevel;
    private java.util.List<String> suggestions;
    private java.util.List<RuleResult> ruleResults;

    // Getters and setters
    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getStrengthLevel() {
        return strengthLevel;
    }

    public void setStrengthLevel(String strengthLevel) {
        this.strengthLevel = strengthLevel;
    }

    public java.util.List<String> getSuggestions() {
        return suggestions;
    }

    public void setSuggestions(java.util.List<String> suggestions) {
        this.suggestions = suggestions;
    }

    public java.util.List<RuleResult> getRuleResults() {
        return ruleResults;
    }

    public void setRuleResults(java.util.List<RuleResult> ruleResults) {
        this.ruleResults = ruleResults;
    }
}