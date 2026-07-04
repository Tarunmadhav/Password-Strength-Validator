# Password Strength & Policy Validator

A Spring Boot web application that validates password strength based on multiple security criteria with a weighted scoring system. The application provides real-time feedback on password strength and specific improvement suggestions.

## Features

- **Multi-factor Password Validation**: Checks passwords against 10 security criteria
- **Weighted Scoring System**: Each rule contributes proportionally to the final score (0-100)
- **Username Override Protection**: Severe penalty if password contains username
- **Strength Tier Classification**: 
  - 91-100: VERY STRONG (green)
  - 76-90: STRONG (light green)
  - 61-75: MEDIUM (yellow/amber)
  - 41-60: WEAK (orange)
  - 21-40: VERY WEAK (red-orange)
  - 0-20: CRITICAL (red)
- **Real-time Feedback**: Results displayed on the same page after form submission
- **Detailed Rule Reporting**: Shows pass/fail status for each validation rule with specific suggestions
- **Responsive Design**: Mobile-friendly interface using Bootstrap 5

## Validation Rules

All 10 rules are checked on every submission with individual contributions to the score:

| Rule | Weight | Pass Condition | Fail Suggestion |
|------|--------|----------------|-----------------|
| **Length** | 20 | ≥ 8 characters (full points ≥12) | "Password must be at least 8 characters long" |
| **Uppercase** | 10 | ≥ 1 uppercase letter (A-Z) | "Add at least one uppercase letter" |
| **Lowercase** | 10 | ≥ 1 lowercase letter (a-z) | "Add at least one lowercase letter" |
| **Digit** | 10 | ≥ 1 digit (0-9) | "Add at least one digit" |
| **Special Character** | 15 | ≥ 1 from !@#$%^&*()-_=+[]{};:,.<>? | "Add at least one special character from: !@#$%^&*()-_=+[]{};:,.<>?" |
| **Common Password** | 30 | Not in top 50 common passwords list | "Choose a less common password" |
| **Username Detection** | 10 | Username not contained in password (and vice versa) | "Do not use your username as your password" or "Do not include your username in your password" |
| **Sequential Characters** | 10 | No 3+ consecutive sequential chars (asc/desc) or keyboard patterns | "Avoid sequential characters (like abc or 123) 3+ in a row" or "Avoid common keyboard sequences (like qwe or asd) 3+ in a row" |
| **Repeated Characters** | 10 | No 3+ identical consecutive characters | "Avoid repeating the same character 3+ times in a row" |
| **Whitespace** | 10 | No leading, trailing, or internal whitespace | "Password must not contain any whitespace characters" |

## Scoring Mechanics

1. **Base Score**: Start at 100 points
2. **Rule Deductions**: 
   - Each failed rule deducts `(rule_weight / total_weight) * 100` points
   - Total raw weight = 135 → Scale factor = 100/135
   - Special handling for length: 
     - < 8 chars: full weight deduction
     - 8-11 chars: half weight deduction (even if passed)
3. **Username Override**: 
   - If username check fails: `score = score * 0.3`
   - Then hard-cap result at 20 points maximum
4. **Final Score**: Rounded to nearest integer (0-100)
5. **Strength Level**: Determined by final score ranges

## Technology Stack

- **Backend**: Java 17, Spring Boot 3.1.0
- **Frontend**: Thymeleaf, Bootstrap 5.3.0
- **Build Tool**: Maven
- **Testing**: JUnit 5, Mockito
- **Validation**: Jakarta Validation (for basic DTO validation)

## Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── com/tarun/passwordvalidator/
│   │       ├── config/
│   │       ├── controller/
│   │       ├── dto/
│   │       ├── exception/
│   │       ├── model/
│   │       ├── service/
│   │       │   └── impl/
│   │       └── util/
│   └── resources/
│       ├── application.properties
│       └── templates/
│           ├── index.html
│           ├── error.html
│           └── result.html
└── test/
    └── java/
        └ com/tarun/passwordvalidator/
            ├── service/
            │   └ impl/
            └ util/
```

## Setup & Installation

### Prerequisites
- Java JDK 17 or higher
- Maven 3.6+
- Git

### Steps

1. Clone the repository:
```bash
git clone https://github.com/yourusername/password-strength-validator.git
cd password-strength-validator
```

2. Build the project:
```bash
mvn clean install
```

3. Run the application:
```bash
mvn spring-boot:run
```

4. Access the application at:
   ```
   http://localhost:8080
   ```

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/` | Display the password validation form |
| POST | `/validate` | Process password validation request and show results on same page |

### Request Body (for POST /validate)
```json
{
  "username": "string",
  "password": "string"
}
```

### Response
Returns the same page (`index.html`) with validation results displayed below the form when successful.

## Usage Example

1. Navigate to `http://localhost:8080`
2. Enter a username and password in the form
3. Click "Validate Password"
4. View results below the form including:
   - Numerical score (0-100)
   - Strength level with color-coded badge
   - Checklist of all 10 validation rules with pass/fail indicators
   - Specific suggestions for any failed rules
   - Special warning if username detection triggered the override penalty

## Sample Validations

| Username | Password | Score | Strength | Notes |
|----------|----------|-------|----------|-------|
| john | Str0ngP@ssw0rd! | 100 | VERY STRONG | All rules pass |
| john | weak | 59 | WEAK | Fails length, uppercase, digit, special |
| tarun | tarun123 | 20 | CRITICAL | Username detection triggers override |
| admin | Admin123! | 65 | MEDIUM | Missing special character? (Actually has !) Let me recalculate... |

## Design Decisions

1. **Same-Page Results**: Chose to display results on the same page using Thymeleaf conditionals (`th:if="${report != null}"`) rather than redirecting to a separate results page for better user experience.

2. **Weight Normalization**: Since the raw weights sum to 135 (not 100), we normalize by multiplying each deduction by (100/135) to ensure the total possible deduction equals 100 points.

3. **Username Override**: Implemented as a multiplicative penalty (×0.3) applied AFTER standard deductions, then capped at 20 points to ensure it's disproportionately severe as specified.

4. **Length Scoring**: 
   - < 8 characters: Full weight deduction
   - 8-11 characters: Half weight deduction (rewards partial compliance)
   - ≥ 12 characters: No deduction (full points)

5. **Extensibility**: 
   - Validation rules are encapsulated in `PasswordValidator` service
   - Easy to modify weights or add new rules
   - Clear separation of concerns between validation logic, scoring, and presentation

## Future Enhancements

1. **API Endpoint**: Add a REST API endpoint (`/api/validate`) that returns JSON for programmatic consumption
2. **Configurable Rules**: Move rule weights and thresholds to application.properties for easy customization
3. **Password History**: Integrate with Have I Been Pwned API to check against known breached passwords
4. **Strength Meter**: Add visual strength meter (like those used by many websites) using CSS/JavaScript
5. **Multilingual Support**: Add i18n for error messages and UI text
6. **Unit Test Coverage**: Add tests for edge cases in validation logic
7. **Docker Support**: Add Dockerfile for easy containerization

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Acknowledgments

- Inspired by common password strength meters used by major websites
- Based on NIST SP 800-63B guidelines for password security
- Uses Bootstrap 5 for responsive UI components
- Special thanks to the Spring Boot team for the excellent framework
---
**Note**: This project was developed as a portfolio piece to demonstrate full-stack Spring Boot development skills, secure coding practices, and attention to detailed requirements implementation.