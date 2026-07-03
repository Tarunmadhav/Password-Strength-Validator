package com.tarun.passwordvalidator.service;

import com.tarun.passwordvalidator.model.PasswordReport;

/**
 * Service interface for password validation.
 */
public interface PasswordService {
    /**
     * Validates a password and returns a report.
     * @param password the password to validate
     * @param username the username to check against
     * @return a PasswordReport containing validation results
     */
    PasswordReport validate(String password, String username);
}