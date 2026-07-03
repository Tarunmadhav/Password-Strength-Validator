package com.tarun.passwordvalidator.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.ModelAndView;

import com.tarun.passwordvalidator.dto.PasswordRequestDto;

/**
 * Global exception handler for the application.
 * Handles InvalidPasswordInputException by showing form-connected and generic Exception.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    @ModelAttribute
    public PasswordRequestDto passwordRequestDto() {
        return new PasswordRequestDto();
    }

    @ExceptionHandler(InvalidPasswordInputException.class)
    public ModelAndView handleInvalidPasswordInput(InvalidPasswordInputException ex) {
        ModelAndView mav = new ModelAndView("index");
        mav.addObject("error", ex.getMessage());
        return mav;
    }

    @ExceptionHandler(Exception.class)
    public ModelAndView handleGenericException(Exception ex) {
        ModelAndView mav = new ModelAndView("error");
        mav.addObject("message", "An unexpected error occurred: " + ex.getMessage());
        return mav;
    }
}