package com.tarun.passwordvalidator.dto;

/**
 * DTO for individual password validation rule result.
 */
public class RuleResult {

    private String ruleName;
    private boolean passed;
    private String suggestion;

    public RuleResult(String ruleName, boolean passed, String suggestion) {
        this.ruleName = ruleName;
        this.passed = passed;
        this.suggestion = suggestion;
    }

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public boolean isPassed() {
        return passed;
    }

    public void setPassed(boolean passed) {
        this.passed = passed;
    }

    public String getSuggestion() {
        return suggestion;
    }

    public void setSuggestion(String suggestion) {
        this.suggestion = suggestion;
    }
}