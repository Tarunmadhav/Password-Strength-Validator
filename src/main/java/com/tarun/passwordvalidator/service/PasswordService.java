package com.tarun.passwordvalidator.service;

import com.tarun.passwordvalidator.dto.PasswordReportDto;

/**
 * Service interface for password validation.
 */
public interface PasswordService {
    /**
     * Validates a password and returns a report.
     * @param password the password to validate
     * @param username the username to check against
     * @return a PasswordReportDto containing validation results
     */
    PasswordReportDto validate(String password, String username);
}