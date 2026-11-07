
package com.schoollaboratory.ui;

import com.schoollaboratory.dao.UserDao;
import com.schoollaboratory.model.User;

import java.util.List;
import java.util.Scanner;

public class AdminMenu {

    private final UserDao userDao;
    private final Scanner scanner;

    public AdminMenu(UserDao userDao, Scanner scanner) {
        this.userDao = userDao;
        this.scanner = scanner;
    }

    // ADMIN MENU
    public void start(User admin) {

        int choice;

        do {
            System.out.println("\n==================================");
            System.out.println("          ADMIN MENU");
            System.out.println("==================================");
            System.out.println("1. View All System Users");
            System.out.println("2. View Pending Registrations");
            System.out.println("3. Approve User Account");
            System.out.println("4. Logout");
            System.out.println("==================================");
            System.out.print("Enter choice: ");

            choice = readChoice();

            switch (choice) {

                case 1:
                    viewAllUsers();
                    break;

                case 2:
                    viewPendingUsers();
                    break;

                case 3:
                    approveUser();
                    break;

                case 4:
                    System.out.println("\nAdmin logged out successfully!");
                    break;

                default:
                    System.out.println("\nInvalid choice. Please try again.");
            }

        } while (choice != 4);
    }

    // VIEW ALL SYSTEM USERS
    private void viewAllUsers() {

        System.out.println("\n========== ALL SYSTEM USERS ==========");

        List<User> users = userDao.getAllUsers();

        displayUserTable(users);
    }

    // VIEW PENDING REGISTRATIONS
    private void viewPendingUsers() {

        System.out.println("\n========== PENDING REGISTRATIONS ==========");

        List<User> users = userDao.getPendingUsers();

        displayUserTable(users);
    }

    // APPROVE USER ACCOUNT
    private void approveUser() {

        System.out.println("\n========== APPROVE USER ACCOUNT ==========");

        List<User> pendingUsers = userDao.getPendingUsers();

        if (pendingUsers.isEmpty()) {
            System.out.println("No pending registrations found.");
            return;
        }

        displayUserTable(pendingUsers);

        System.out.println("\nEnter 0 to cancel approval.");
        System.out.print("Enter User ID to approve: ");

        int userId = readChoice();

        if (userId == 0) {
            System.out.println("Approval cancelled.");
            return;
        }

        // VERIFY USER ID IN PENDING LIST
        boolean userExists = pendingUsers.stream()
                .anyMatch(user -> user.id() == userId);

        if (!userExists) {
            System.out.println(
                    "User ID not found in pending registrations."
            );
            return;
        }

        // APPROVE ACCOUNT
        boolean approved = userDao.approveUser(userId);

        if (approved) {
            System.out.println(
                    "\nUser account approved successfully!"
            );
        } else {
            System.out.println(
                    "\nUnable to approve user account."
            );
        }
    }

    // FORMATTED USER TABLE
    private void displayUserTable(List<User> users) {

        if (users.isEmpty()) {
            System.out.println("No user records found.");
            return;
        }

        String line =
                "---------------------------------------------------------------";

        String format = "%-5s | %-15s | %-20s | %-10s%n";

        System.out.println(line);

        System.out.printf(
                format,
                "ID",
                "USERNAME",
                "ROLE",
                "STATUS"
        );

        System.out.println(line);

        for (User user : users) {

            System.out.printf(
                    format,
                    user.id(),
                    user.username(),
                    user.role(),
                    user.status()
            );
        }

        System.out.println(line);
    }

    // SAFE INTEGER INPUT
    private int readChoice() {

        while (true) {

            String input = scanner.nextLine().trim();

            try {
                return Integer.parseInt(input);

            } catch (NumberFormatException e) {
                System.out.print("Invalid input. Enter a number: ");
            }
        }
    }
}
