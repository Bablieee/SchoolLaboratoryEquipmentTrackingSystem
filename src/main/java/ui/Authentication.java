
package com.schoollaboratory.ui;

import com.schoollaboratory.dao.UserDao;
import com.schoollaboratory.model.User;

import java.util.Scanner;

public class Authentication {

    private final UserDao userDao;
    private final Scanner scanner;

    public Authentication(UserDao userDao, Scanner scanner) {
        this.userDao = userDao;
        this.scanner = scanner;
    }

    public void start() {

        boolean running = true;

        while (running) {

            System.out.println("\n====================================");
            System.out.println(" SCHOOL LABORATORY EQUIPMENT SYSTEM");
            System.out.println("====================================");
            System.out.println("1. Register Account");
            System.out.println("2. Login");
            System.out.println("3. Exit");
            System.out.print("Enter choice: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1" -> registerAccount();
                case "2" -> loginAccount();
                case "3" -> {
                    System.out.println("Thank you for using the system!");
                    running = false;
                }
                default -> System.out.println("Invalid choice. Try again.");
            }
        }
    }

    private void registerAccount() {

        System.out.println("\n========== REGISTER ACCOUNT ==========");

        System.out.print("Enter username: ");
        String username = scanner.nextLine().trim();

        if (username.isEmpty()) {
            System.out.println("Username cannot be empty.");
            return;
        }

        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        if (password.isEmpty()) {
            System.out.println("Password cannot be empty.");
            return;
        }

        System.out.println("\nSelect Role:");
        System.out.println("1. ADMINISTRATOR");
        System.out.println("2. LABORATORY STAFF");
        System.out.println("3. EQUIPMENT VIEWER");
        System.out.print("Enter choice: ");

        String roleChoice = scanner.nextLine();

        String role;

        switch (roleChoice) {
            case "1" -> role = "ADMINISTRATOR";
            case "2" -> role = "LABORATORY STAFF";
            case "3" -> role = "EQUIPMENT VIEWER";
            default -> {
                System.out.println("Invalid role selection.");
                return;
            }
        }

        User user = new User(
                0,
                username,
                password,
                role,
                "PENDING"
        );

        if (userDao.register(user)) {
            System.out.println("\nAccount registered successfully!");
            System.out.println("Username: " + username);
            System.out.println("Role: " + role);
            System.out.println("Status: PENDING");
            System.out.println("Please wait for Administrator approval.");
        } else {
            System.out.println("Unable to register account.");
        }
    }

    private void loginAccount() {

        System.out.println("\n========== LOGIN ==========");

        System.out.print("Enter username: ");
        String username = scanner.nextLine();

        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        User user = userDao.login(username, password);

        if (user == null) {
            System.out.println("Invalid username or password.");
            return;
        }

        if (!user.status().equalsIgnoreCase("APPROVED")) {
            System.out.println("\nYour account is still PENDING.");
            System.out.println("Please wait for Administrator approval.");
            return;
        }

        System.out.println("\nLogin successful!");
        System.out.println("Welcome, " + user.username());
        System.out.println("Role: " + user.role());

        switch (user.role().toUpperCase()) {

            case "ADMINISTRATOR" -> {
                AdminMenu adminMenu = new AdminMenu(userDao, scanner);
                adminMenu.start(user);
            }

            case "LABORATORY STAFF" -> {
                System.out.println("\n===== LABORATORY STAFF =====");
                System.out.println("Welcome, " + user.username());
                System.out.println("Staff access is not yet implemented.");
            }

            case "EQUIPMENT VIEWER" -> {
                System.out.println("\n===== EQUIPMENT VIEWER =====");
                System.out.println("Welcome, " + user.username());
                System.out.println("Equipment viewing is not yet implemented.");
            }

            default -> System.out.println("Unknown user role.");
        }
    }
}
