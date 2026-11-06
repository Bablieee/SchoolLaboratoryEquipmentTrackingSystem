
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

    public void start(User admin) {

        boolean running = true;

        while (running) {

            System.out.println("\n====================================");
            System.out.println("       ADMINISTRATOR MENU");
            System.out.println("====================================");
            System.out.println("Welcome, " + admin.username());
            System.out.println("1. View All Accounts");
            System.out.println("2. Approve Account");
            System.out.println("3. Logout");
            System.out.print("Enter choice: ");

            String choice = scanner.nextLine();

            switch (choice) {

                case "1" -> viewAllAccounts();

                case "2" -> approveAccount();

                case "3" -> {
                    System.out.println("Logging out...");
                    running = false;
                }

                default -> System.out.println("Invalid choice. Try again.");
            }
        }
    }

    private void viewAllAccounts() {

        System.out.println("\n========== ALL REGISTERED ACCOUNTS ==========");

        List<User> users = userDao.getAllUsers();

        if (users.isEmpty()) {
            System.out.println("No registered accounts found.");
            return;
        }

        for (User user : users) {

            System.out.println("------------------------------------");
            System.out.println("Account ID: " + user.id());
            System.out.println("Username: " + user.username());
            System.out.println("Role: " + user.role());
            System.out.println("Status: " + user.status());
        }

        System.out.println("------------------------------------");
    }

    private void approveAccount() {

        System.out.println("\n========== APPROVE ACCOUNT ==========");

        List<User> users = userDao.getAllUsers();

        boolean hasPending = false;

        for (User user : users) {

            if (user.status().equalsIgnoreCase("PENDING")) {

                hasPending = true;

                System.out.println("------------------------------------");
                System.out.println("Account ID: " + user.id());
                System.out.println("Username: " + user.username());
                System.out.println("Role: " + user.role());
                System.out.println("Status: " + user.status());
            }
        }

        if (!hasPending) {
            System.out.println("No pending accounts to approve.");
            return;
        }

        System.out.print("\nEnter Account ID to approve: ");

        try {

            int id = Integer.parseInt(scanner.nextLine());

            boolean updated = userDao.updateStatus(id, "APPROVED");

            if (updated) {
                System.out.println("\nAccount approved successfully!");
            } else {
                System.out.println("\nAccount not found. Please try again.");
            }

        } catch (NumberFormatException e) {
            System.out.println("Invalid ID. Please enter a number.");
        }
    }
}
