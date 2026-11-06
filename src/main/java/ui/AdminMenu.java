
package ui;

import dao.UserDao;
import model.User;

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
            System.out.println("          ADMINISTRATOR MENU");
            System.out.println("====================================");
            System.out.println("Welcome, " + admin.username());
            System.out.println("1. View Accounts");
            System.out.println("2. Approve Account");
            System.out.println("3. Logout");
            System.out.print("Enter choice: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1" -> viewAccounts();
                case "2" -> approveAccount();
                case "3" -> {
                    System.out.println("Administrator logged out.");
                    running = false;
                }
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    private void viewAccounts() {

        System.out.println("\n========== REGISTERED ACCOUNTS ==========");

        List<User> users = userDao.getAllUsers();

        if (users.isEmpty()) {
            System.out.println("No registered accounts found.");
            return;
        }

        System.out.printf("%-5s %-20s %-22s %-15s%n",
                "ID", "USERNAME", "ROLE", "STATUS");

        System.out.println("--------------------------------------------------------------");

        for (User user : users) {
            System.out.printf("%-5d %-20s %-22s %-15s%n",
                    user.id(),
                    user.username(),
                    user.role(),
                    user.status());
        }
    }

    private void approveAccount() {

        System.out.println("\n========== APPROVE ACCOUNT ==========");

        List<User> users = userDao.getAllUsers();

        boolean hasPending = false;

        for (User user : users) {
            if (user.status().equalsIgnoreCase("PENDING")) {

                System.out.println("ID: " + user.id());
                System.out.println("Username: " + user.username());
                System.out.println("Role: " + user.role());
                System.out.println("Status: " + user.status());
                System.out.println("-----------------------------");

                hasPending = true;
            }
        }

        if (!hasPending) {
            System.out.println("No pending accounts.");
            return;
        }

        System.out.print("Enter account ID to approve: ");

        try {
            int id = Integer.parseInt(scanner.nextLine());

            User selectedUser = null;

            for (User user : users) {
                if (user.id() == id &&
                        user.status().equalsIgnoreCase("PENDING")) {
                    selectedUser = user;
                    break;
                }
            }

            if (selectedUser == null) {
                System.out.println("Invalid account ID.");
                return;
            }

            if (selectedUser.role().equalsIgnoreCase("ADMINISTRATOR")) {
                System.out.println("Cannot approve another administrator here.");
                return;
            }

            if (userDao.updateStatus(id, "APPROVED")) {
                System.out.println("Account approved successfully!");
            } else {
                System.out.println("Failed to approve account.");
            }

        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid ID.");
        }
    }
}
