package com.tarun.passwordvalidator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * Spring Boot application class that runs the password validator as a web application.
 *
 * IoC Note: Objects are now created and managed by the Spring container.
 * PasswordService is injected via constructor injection,
 * eliminating the need for manual 'new' calls.
 */
@SpringBootApplication
@ComponentScan(basePackages = "com.tarun.passwordvalidator")
public class PasswordvalidatorApplication {

    public static void main(String[] args) {
        SpringApplication.run(PasswordvalidatorApplication.class, args);
    }
}