package com.tarun.passwordvalidator.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.tarun.passwordvalidator.exception.InvalidPasswordInputException;
import com.tarun.passwordvalidator.model.PasswordReport;
import com.tarun.passwordvalidator.model.PasswordReport.StrengthLevel;
import com.tarun.passwordvalidator.service.PasswordService;
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

        // Mock validator responses for a strong password
        when(passwordValidator.validateLength(password)).thenReturn(null);
        when(passwordValidator.validateUppercase(password)).thenReturn(null);
        when(passwordValidator.validateLowercase(password)).thenReturn(null);
        when(passwordValidator.validateDigit(password)).thenReturn(null);
        when(passwordValidator.validateSpecialChar(password)).thenReturn(null);
        when(passwordValidator.validateCommonPassword(password)).thenReturn(null);
        when(passwordValidator.validateRepeatedChars(password)).thenReturn(null);
        when(passwordValidator.validateSequentialChars(password)).thenReturn(null);
        when(passwordValidator.validateUsernameSimilarity(password, username)).thenReturn(null);
        when(passwordValidator.lengthBonus(password)).thenReturn(5); // length 15 -> bonus 5

        // Act
        PasswordReport result = passwordService.validate(password, username);

        // Assert
        assertNotNull(result);
        assertEquals(StrengthLevel.STRONG, result.getStrengthLevel());
        assertTrue(result.getScore() >= 80); // Should be strong
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
        when(passwordValidator.lengthBonus(password)).thenReturn(0);

        // Act
        PasswordReport result = passwordService.validate(password, username);

        // Assert
        assertNotNull(result);
        assertEquals(StrengthLevel.WEAK, result.getStrengthLevel());
        assertTrue(result.getScore() < 60); // Should be weak
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
        when(passwordValidator.validateUsernameSimilarity(password, username)).thenReturn("Avoid using your username or a variant of it within your password");
        when(passwordValidator.lengthBonus(password)).thenReturn(0); // length 8 -> no bonus

        // Act
        PasswordReport result = passwordService.validate(password, username);

        // Assert
        assertNotNull(result);
        // Should still be strong or moderate depending on score, but should have the username suggestion
        assertTrue(result.getSuggestions().contains("Avoid using your username or a variant of it within your password"));
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