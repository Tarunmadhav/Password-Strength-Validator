import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Main class for the Password Strength & Policy Validator application.
 */
public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Initialize validator and scorer
        PasswordValidator validator = new PasswordValidator();
        PasswordScorer scorer = new PasswordScorer();

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

            // Score the password and get report
            PasswordReport report = scorer.scorePassword(password, username);
            System.out.println("\n" + report);
        }

        scanner.close();
        System.out.println("Goodbye!");
    }
}