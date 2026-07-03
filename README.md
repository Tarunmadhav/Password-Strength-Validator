# Password Strength & Policy Validator

A console-based Java 17 application that validates password strength against a configurable policy and provides a strength score with feedback.

## Features

- Validates passwords against multiple criteria:
  - Minimum length
  - Uppercase and lowercase letters
  - Digits
  - Special characters
  - Username inclusion
  - Common passwords
  - Repeated characters (3+ identical in a row)
  - Sequential characters (3+ consecutive like abc or 123)
- Scores passwords on a scale of 0-100
- Rates password strength as WEAK, MEDIUM, or STRONG
- Provides specific suggestions for improvement
- Simple console interface for continuous testing

## Requirements

- Java Development Kit (JDK) 17 or higher

## Compilation and Execution

Since this project uses plain Java 17 with no external dependencies or build tools, you can compile and run it directly with `javac` and `java`.

### Step 1: Compile all Java files

From the project root directory:

```bash
javac *.java
```

### Step 2: Run the application

```bash
java Main
```

### Usage

1. The program will prompt you to enter a username.
2. Then it will prompt for a password.
3. It will display a password strength report including:
   - Score (out of 100)
   - Strength level (WEAK/MEDIUM/STRONG)
   - Specific suggestions for improvement (if any)
4. To exit, type `quit` at either the username or password prompt.

## Example

```
=== Password Strength & Policy Validator ===
Enter 'quit' to exit.

Enter username: john
Enter password: Password123!

==== Password Strength Report ====
Score : 80 / 100
Strength : STRONG
Suggestions :
  - (none - password is strong!)
===================================
```

## Code Structure

- `PasswordValidator.java`: Contains validation methods for each password rule.
- `PasswordScorer.java`: Combines validation results into a 0-100 score and strength rating.
- `PasswordReport.java`: Holds the score, strength level, and suggestions, and formats the output.
- `Main.java`: Contains the main method with the console loop.

## Notes

- The password policy used in `Main.java` requires:
  - Minimum length of 8 characters
  - At least one uppercase letter
  - At least one lowercase letter
  - At least one digit
  - At least one special character
  - Username not allowed in password
  - Common passwords not allowed
  - No repeated characters (3+ identical in a row)
  - No sequential characters (3+ consecutive like abc or 123)
- You can adjust the policy requirements by modifying the `PasswordPolicy` constructor in `Main.java`.

## License

This project is for educational purposes.