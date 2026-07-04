package com.tarun.passwordvalidator.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.tarun.passwordvalidator.dto.PasswordReportDto;
import com.tarun.passwordvalidator.exception.InvalidPasswordInputException;
import com.tarun.passwordvalidator.util.PasswordValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class PasswordServiceImplTest {

    @Mock
    private PasswordValidator passwordValidator;

    @InjectMocks
    private PasswordServiceImpl passwordService;

    @BeforeEach
    void setUp() {
        // Additional setup if needed
    }

    @Test
    void testStrongPassword() {
        // Arrange
        String password = "Str0ngP@ssw0rd!";
        String username = "john";

        // Mock validator responses for a strong password (all pass)
        when(passwordValidator.validateLength(password)).thenReturn(null);
        when(passwordValidator.validateUppercase(password)).thenReturn(null);
        when(passwordValidator.validateLowercase(password)).thenReturn(null);
        when(passwordValidator.validateDigit(password)).thenReturn(null);
        when(passwordValidator.validateSpecialChar(password)).thenReturn(null);
        when(passwordValidator.validateCommonPassword(password)).thenReturn(null);
        when(passwordValidator.validateRepeatedChars(password)).thenReturn(null);
        when(passwordValidator.validateSequentialChars(password)).thenReturn(null);
        when(passwordValidator.validateUsernameSimilarity(password, username)).thenReturn(null);
        when(passwordValidator.validateWhitespace(password)).thenReturn(null);
        // Note: lengthBonus is not used in the new scoring, so we don't mock it

        // Act
        PasswordReportDto result = passwordService.validate(password, username);

        // Assert
        assertNotNull(result);
        // Expected score: 100 (all pass, length>=12 -> no deduction for length)
        assertEquals(100, result.getScore());
        assertEquals("VERY STRONG", result.getStrengthLevel());
        assertTrue(result.getSuggestions().isEmpty());
    }

    @Test
    void testWeakPassword() {
        // Arrange
        String password = "weak";
        String username = "john";

        // Mock validator responses for a weak password
        when(passwordValidator.validateLength(password)).thenReturn("Password must be at least 8 characters long");
        when(passwordValidator.validateUppercase(password)).thenReturn("Add at least one uppercase letter");
        when(passwordValidator.validateLowercase(password)).thenReturn(null); // has lowercase
        when(passwordValidator.validateDigit(password)).thenReturn("Add at least one digit");
        when(passwordValidator.validateSpecialChar(password)).thenReturn("Add at least one special character (e.g., !@#$%^&*)");
        when(passwordValidator.validateCommonPassword(password)).thenReturn(null); // not common
        when(passwordValidator.validateRepeatedChars(password)).thenReturn(null);
        when(passwordValidator.validateSequentialChars(password)).thenReturn(null);
        when(passwordValidator.validateUsernameSimilarity(password, username)).thenReturn(null);
        when(passwordValidator.validateWhitespace(password)).thenReturn(null); // no whitespace

        // Act
        PasswordReportDto result = passwordService.validate(password, username);

        // Assert
        assertNotNull(result);
        // Expected score:
        //   Failed rules: Length, Uppercase, Digit, Special
        //   Weights: Length=20, Uppercase=10, Digit=10, Special=15 -> total failed weight = 55
        //   Raw weight total = 135 -> scale = 100/135
        //   Deduction = 55 * (100/135) = 40.7407 -> score = 100 - 40.7407 = 59.2593 -> rounded to 59
        //   Strength level: 59 -> WEAK (41-60)
        assertEquals(59, result.getScore());
        assertEquals("WEAK", result.getStrengthLevel());
        assertFalse(result.getSuggestions().isEmpty());
        assertTrue(result.getSuggestions().contains("Password must be at least 8 characters long"));
        assertTrue(result.getSuggestions().contains("Add at least one uppercase letter"));
        assertTrue(result.getSuggestions().contains("Add at least one digit"));
        assertTrue(result.getSuggestions().contains("Add at least one special character (e.g., !@#$%^&*)"));
    }

    @Test
    void testUsernameSimilarityFails() {
        // Arrange
        String password = "tarun123"; // similar to username "tarun"
        String username = "tarun";

        // Mock validator responses - all pass except username similarity
        when(passwordValidator.validateLength(password)).thenReturn(null);
        when(passwordValidator.validateUppercase(password)).thenReturn(null);
        when(passwordValidator.validateLowercase(password)).thenReturn(null);
        when(passwordValidator.validateDigit(password)).thenReturn(null);
        when(passwordValidator.validateSpecialChar(password)).thenReturn(null);
        when(passwordValidator.validateCommonPassword(password)).thenReturn(null);
        when(passwordValidator.validateRepeatedChars(password)).thenReturn(null);
        when(passwordValidator.validateSequentialChars(password)).thenReturn(null);
        when(passwordValidator.validateWhitespace(password)).thenReturn(null);
        when(passwordValidator.validateUsernameSimilarity(password, username)).thenReturn("Avoid using your username or a variant of it within your password");

        // Act
        PasswordReportDto result = passwordService.validate(password, username);

        // Assert
        assertNotNull(result);
        // Expected score without username override:
        //   All rules pass except username -> failed weight = 10
        //   Deduction = 10 * (100/135) = 7.4074 -> score = 92.5926
        //   Then apply username override: score = 92.5926 * 0.3 = 27.7778 -> capped at 20
        //   Final score = 20
        //   Strength level: 20 -> CRITICAL (0-20)
        assertEquals(20, result.getScore());
        assertEquals("CRITICAL", result.getStrengthLevel());
        assertTrue(result.getSuggestions().contains("Avoid using your username or a variant of it within your password"));
        // Check that the username override warning is present in the controller?
        // But note: the DTO does not have a field for the warning. The warning is added in the controller based on the score.
        // So we don't check it here.
    }

    @Test
    void testBlankPasswordThrowsException() {
        // Arrange
        String password = "";
        String username = "john";

        // Act & Assert
        assertThrows(InvalidPasswordInputException.class, () -> {
            passwordService.validate(password, username);
        });
    }

    @Test
    void testBlankUsernameThrowsException() {
        // Arrange
        String password = "ValidPass1!";
        String username = "";

        // Act & Assert
        assertThrows(InvalidPasswordInputException.class, () -> {
            passwordService.validate(password, username);
        });
    }

    @Test
    void testNullPasswordThrowsException() {
        // Arrange
        String password = null;
        String username = "john";

        // Act & Assert
        assertThrows(InvalidPasswordInputException.class, () -> {
            passwordService.validate(password, username);
        });
    }

    @Test
    void testNullUsernameThrowsException() {
        // Arrange
        String password = "ValidPass1!";
        String username = null;

        // Act & Assert
        assertThrows(InvalidPasswordInputException.class, () -> {
            passwordService.validate(password, username);
        });
    }
}