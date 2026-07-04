package com.tarun.passwordvalidator.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PasswordValidatorTest {

    private PasswordValidator validator;

    @BeforeEach
    void setUp() {
        validator = new PasswordValidator();
    }

    @Test
    void testLengthValid() {
        assertNull(validator.validateLength("abcdefgh")); // exactly 8
        assertNull(validator.validateLength("abcdefghijklmnop")); // 16
    }

    @Test
    void testLengthInvalid() {
        assertEquals("Password must be at least 8 characters long", validator.validateLength("abc"));
        assertEquals("Password must be at least 8 characters long", validator.validateLength(""));
        assertEquals("Password must be at least 8 characters long", validator.validateLength(null));
    }

    @Test
    void testUppercaseValid() {
        assertNull(validator.validateUppercase("Abcdefgh"));
        assertNull(validator.validateUppercase("ABC"));
    }

    @Test
    void testUppercaseInvalid() {
        assertEquals("Add at least one uppercase letter", validator.validateUppercase("abcdefgh"));
        assertEquals("Add at least one uppercase letter", validator.validateUppercase(""));
        assertEquals("Add at least one uppercase letter", validator.validateUppercase(null));
    }

    @Test
    void testLowercaseValid() {
        assertNull(validator.validateLowercase("Abcdefgh"));
        assertNull(validator.validateLowercase("abc"));
    }

    @Test
    void testLowercaseInvalid() {
        assertEquals("Add at least one lowercase letter", validator.validateLowercase("ABCDEFGH"));
        assertEquals("Add at least one lowercase letter", validator.validateLowercase(""));
        assertEquals("Add at least one lowercase letter", validator.validateLowercase(null));
    }

    @Test
    void testDigitValid() {
        assertNull(validator.validateDigit("abcdefg8"));
        assertNull(validator.validateDigit("123"));
    }

    @Test
    void testDigitInvalid() {
        assertEquals("Add at least one digit", validator.validateDigit("abcdefgh"));
        assertEquals("Add at least one digit", validator.validateDigit(""));
        assertEquals("Add at least one digit", validator.validateDigit(null));
    }

    @Test
    void testSpecialCharValid() {
        assertNull(validator.validateSpecialChar("abcdefg!"));
        assertNull(validator.validateSpecialChar("!@#"));
    }

    @Test
    void testSpecialCharInvalid() {
        assertEquals("Add at least one special character from: !@#$%^&*()-_=+[]{};:,.<>?", validator.validateSpecialChar("abcdefgh"));
        assertEquals("Add at least one special character from: !@#$%^&*()-_=+[]{};:,.<>?", validator.validateSpecialChar(""));
        assertEquals("Add at least one special character from: !@#$%^&*()-_=+[]{};:,.<>?", validator.validateSpecialChar(null));
    }

    @Test
    void testCommonPasswordValid() {
        assertNull(validator.validateCommonPassword("Password123!")); // not in list
        // Note: empty string and null are not in the common passwords set, but our method returns a suggestion for null.
        // We'll accept that as per the current implementation.
    }

    @Test
    void testCommonPasswordInvalid() {
        assertEquals("Choose a less common password", validator.validateCommonPassword("password"));
        assertEquals("Choose a less common password", validator.validateCommonPassword("PASSWORD")); // case-insensitive
        assertEquals("Choose a less common password", validator.validateCommonPassword("123456"));
    }

    @Test
    void testRepeatedCharsValid() {
        assertNull(validator.validateRepeatedChars("abcdefgh")); // no repeats
        assertNull(validator.validateRepeatedChars("aabbccdd")); // max 2 repeats
        assertNull(validator.validateRepeatedChars("ab")); // less than 3
        assertNull(validator.validateRepeatedChars(""));
        assertNull(validator.validateRepeatedChars(null));
    }

    @Test
    void testRepeatedCharsInvalid() {
        assertEquals("Avoid repeating the same character 3+ times in a row", validator.validateRepeatedChars("aaab"));
        assertEquals("Avoid repeating the same character 3+ times in a row", validator.validateRepeatedChars("baaab"));
        assertEquals("Avoid repeating the same character 3+ times in a row", validator.validateRepeatedChars("aaa"));
    }

    @Test
    void testSequentialCharsValid() {
        // We need a string without any 3 consecutive increasing/decreasing ASCII or keyboard sequences.
        assertNull(validator.validateSequentialChars("aceg")); // gaps
        assertNull(validator.validateSequentialChars("a1b2c3")); // not consecutive in ASCII
        assertNull(validator.validateSequentialChars(""));
        assertNull(validator.validateSequentialChars(null));
    }

    @Test
    void testSequentialCharsInvalid() {
        // increasing ASCII
        assertEquals("Avoid sequential characters (like abc or 123) 3+ in a row", validator.validateSequentialChars("abc"));
        assertEquals("Avoid sequential characters (like abc or 123) 3+ in a row", validator.validateSequentialChars("123"));
        assertEquals("Avoid sequential characters (like abc or 123) 3+ in a row", validator.validateSequentialChars("xyz"));
        // decreasing ASCII
        assertEquals("Avoid sequential characters (like abc or 123) 3+ in a row", validator.validateSequentialChars("cba"));
        assertEquals("Avoid sequential characters (like abc or 123) 3+ in a row", validator.validateSequentialChars("321"));
        // keyboard sequences
        assertEquals("Avoid common keyboard sequences (like qwe or asd) 3+ in a row", validator.validateSequentialChars("qwe"));
        assertEquals("Avoid common keyboard sequences (like qwe or asd) 3+ in a row", validator.validateSequentialChars("asd"));
    }

    @Test
    void testUsernameSimilarityExactMatch() {
        assertEquals("Do not use your username as your password", validator.validateUsernameSimilarity("password", "password"));
        assertEquals("Do not use your username as your password", validator.validateUsernameSimilarity("Password", "password")); // case-insensitive
    }

    @Test
    void testUsernameSimilaritySubstring() {
        assertEquals("Do not include your username in your password", validator.validateUsernameSimilarity("mypassword123", "password"));
        assertEquals("Do not include your username in your password", validator.validateUsernameSimilarity("password123", "password"));
        assertEquals("Do not include your username in your password", validator.validateUsernameSimilarity("123password", "password"));
        // username containing password
        assertEquals("Do not include your username in your password", validator.validateUsernameSimilarity("pass", "password"));
    }

    @Test
    void testUsernameSimilarityFuzzy() {
        // We'll define fuzzy as having at least 50% longest common substring, but NOT a substring relation.
        // Example: password "abcfgh", username "abczz" -> LCS = "abc" (3), similarity = 3/6 = 0.5
        assertEquals("Avoid using your username or a variant of it within your password", validator.validateUsernameSimilarity("abcfgh", "abczz"));
        // Another: "abcdxy" and "abcdef" -> LCS = "abcd" (4), length 6 -> 4/6≈0.66
        assertEquals("Avoid using your username or a variant of it within your password", validator.validateUsernameSimilarity("abcdxy", "abcdef"));
        // Edge: exactly 50%: password length 6, lcs 3 -> avoid substring case, use "abcfgh", "abczz" already covers.
        // Below 50%: password length 8, lcs 2 -> 2/8=0.25 -> no trigger.
        assertNull(validator.validateUsernameSimilarity("abcdefgh", "abx")); // lcs=2 -> 2/8=0.25
        // No match at all
        assertNull(validator.validateUsernameSimilarity("xxxxxxxx", "yyyy"));
        // Exact match already covered.
    }

    // Note: The lengthBonus method is deprecated and not used in the new scoring, but we keep the test for completeness.
    @Test
    void testLengthBonus() {
        assertEquals(0, validator.lengthBonus(null));
        assertEquals(0, validator.lengthBonus("")); // 0 length
        assertEquals(0, validator.lengthBonus("abc")); // <12
        assertEquals(0, validator.lengthBonus("abcdefgh")); // 8 -> <12
        assertEquals(5, validator.lengthBonus("abcdefghijkl")); // 12 -> exactly 12 -> 5
        assertEquals(5, validator.lengthBonus("abcdefghijklm")); // 13 -> 5
        assertEquals(10, validator.lengthBonus("abcdefghijklmnop")); // 16 -> 10
        assertEquals(10, validator.lengthBonus("abcdefghijklmnopqrst")); // 20 -> 10
    }
}