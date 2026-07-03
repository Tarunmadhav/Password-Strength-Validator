# Password Strength & Policy Validator (Spring Boot Version)

This is a Spring Boot 3.x console application that validates password strength and provides feedback.

## Running the Application

To run the application, use the following Maven command:

```bash
mvn spring-boot:run
```

The application will start and prompt you to enter a username and password. Enter 'quit' to exit.

## Project Structure

- `com.tarun.passwordvalidator.PasswordvalidatorApplication`: The main Spring Boot application class that implements `CommandLineRunner`.
- `com.tarun.passwordvalidator.model.PasswordReport`: Model class representing the password validation report.
- `com.tarun.passwordvalidator.service.PasswordService`: Service interface for password validation.
- `com.tarun.passwordvalidator.service.impl.PasswordServiceImpl`: Implementation of the password validation service.
- `com.tarun.passwordvalidator.util.PasswordValidator`: Utility class containing password validation rules (marked as `@Component`).

## Inversion of Control (IoC) and Dependency Injection (DI)

In this Spring Boot version, objects are created and managed by the Spring container. The `PasswordvalidatorApplication` class receives its dependencies (`PasswordService`) via constructor injection, eliminating the need for manual `new` calls. This demonstrates the Inversion of Control principle, where the framework (Spring) controls the lifecycle and wiring of objects.

## Notes

- Ensure you have Maven and Java 17 installed.
- The first run will download dependencies from Maven Central, which requires an internet connection.
- The console output format is identical to the Phase 1 console application, except for the Spring Boot startup banner.

## Acceptance Criteria

- `mvn spring-boot:run` boots cleanly and reproduces the same Score/Strength/Suggestions output as Phase 1 for the same inputs.
- No manual `new PasswordServiceImpl(...)` anywhere in the code — everything is container-managed.