package com.tarun.passwordvalidator.exception;

/**
 * Exception thrown when password input is invalid (e.g., blank).
 */
public class InvalidPasswordInputException extends RuntimeException {

    public InvalidPasswordInputException(String message) {
        super(message);
    }
}