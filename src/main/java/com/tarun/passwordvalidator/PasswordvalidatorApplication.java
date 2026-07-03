package com.tarun.passwordvalidator;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * Spring Boot application class that runs the password validator as a command-line application.
 *
 * IoC Note: Objects are now created and managed by the Spring container.
 * PasswordService is injected via constructor injection,
 * eliminating the need for manual 'new' calls.
 */
@SpringBootApplication
@ComponentScan(basePackages = "com.tarun.passwordvalidator")
public class PasswordvalidatorApplication implements CommandLineRunner {

    private final com.tarun.passwordvalidator.service.PasswordService passwordService;

    public PasswordvalidatorApplication(com.tarun.passwordvalidator.service.PasswordService passwordService) {
        this.passwordService = passwordService;
    }

    public static void main(String[] args) {
        SpringApplication.run(PasswordvalidatorApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        java.util.Scanner scanner = new java.util.Scanner(System.in);

        System.out.println("=== Password Strength & Policy Validator ===");
        System.out.println("Enter 'quit' to exit.");
        System.out.println();

        while (true) {
            System.out.print("Enter username: ");
            String username = scanner.nextLine().trim();
            if (username.equalsIgnoreCase("quit")) {
                break;
            }

            System.out.print("Enter password: ");
            String password = scanner.nextLine();
            if (password.equalsIgnoreCase("quit")) {
                break;
            }

            // Validate the password and get report
            com.tarun.passwordvalidator.model.PasswordReport report = passwordService.validate(password, username);
            System.out.println("\n" + report);
        }

        scanner.close();
        System.out.println("Goodbye!");
    }
}