
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
            System.out.println("4. Update User Role and Status");
            System.out.println("5. Logout");
            System.out.println("==================================");

            choice = readIntInput("Enter choice: ");

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
                    updateUser(admin);
                    break;

                case 5:
                    System.out.println("\nAdmin logged out successfully!");
                    break;

                default:
                    System.out.println("\nInvalid choice. Please try again.");
            }

        } while (choice != 5);
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

        int userId = readIntInput("Enter User ID to approve: ");

        if (userId == 0) {
            System.out.println("Approval cancelled.");
            return;
        }

        boolean userExists = pendingUsers.stream()
                .anyMatch(user -> user.id() == userId);

        if (!userExists) {
            System.out.println(
                    "User ID not found in pending registrations."
            );
            return;
        }

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

    // UPDATE USER ROLE AND STATUS
    private void updateUser(User admin) {

        System.out.println("\n========== UPDATE USER ROLE AND STATUS ==========");

        List<User> users = userDao.getAllUsers();

        if (users.isEmpty()) {
            System.out.println("No user records found.");
            return;
        }

        displayUserTable(users);

        System.out.println("\nEnter 0 to cancel update.");

        // GET TARGET USER ID
        int userId = readIntInput("Enter User ID to update: ");

        if (userId == 0) {
            System.out.println("Update cancelled.");
            return;
        }

        // CHECK IF USER EXISTS
        User targetUser = users.stream()
                .filter(user -> user.id() == userId)
                .findFirst()
                .orElse(null);

        if (targetUser == null) {
            System.out.println("User ID not found.");
            return;
        }

        // SELF-EDIT RESTRICTION
        if (userId == admin.id()) {
            System.out.println(
                    "WARNING: Modifying the currently active admin account is restricted."
            );
            return;
        }

        // DISPLAY SELECTED USER
        System.out.println("\nSelected User:");
        System.out.println("ID: " + targetUser.id());
        System.out.println("Username: " + targetUser.username());
        System.out.println("Current Role: " + targetUser.role());
        System.out.println("Current Status: " + targetUser.status());

        // GET NEW ROLE
        System.out.println("\nAvailable Roles:");
        System.out.println("ADMINISTRATOR");
        System.out.println("LABORATORY STAFF");
        System.out.println("EQUIPMENT VIEWER");
        System.out.println("Enter 0 to cancel.");

        String newRole = readValidRole();

        if (newRole == null) {
            System.out.println("Update cancelled.");
            return;
        }

        // GET NEW STATUS
        System.out.println("\nAvailable Statuses:");
        System.out.println("APPROVED");
        System.out.println("PENDING");
        System.out.println("Enter 0 to cancel.");

        String newStatus = readValidStatus();

        if (newStatus == null) {
            System.out.println("Update cancelled.");
            return;
        }

        // UPDATE DATABASE
        boolean updated = userDao.updateUserRoleAndStatus(
                userId,
                newRole,
                newStatus
        );

        if (updated) {
            System.out.println(
                    "\nUser role and status updated successfully!"
            );
            System.out.println("New Role: " + newRole);
            System.out.println("New Status: " + newStatus);
        } else {
            System.out.println(
                    "\nUnable to update user account."
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
    private int readIntInput(String prompt) {

        while (true) {

            System.out.print(prompt);

            String input = scanner.nextLine().trim();

            try {
                return Integer.parseInt(input);

            } catch (NumberFormatException e) {
                System.out.println(
                        "Invalid input. Please enter a valid number."
                );
            }
        }
    }

    // VALIDATE USER ROLE
    private String readValidRole() {

        while (true) {

            System.out.print("Enter new role: ");

            String role = scanner.nextLine().trim().toUpperCase();

            if (role.equals("0")) {
                return null;
            }

            if (role.equals("ADMINISTRATOR")
                    || role.equals("LABORATORY STAFF")
                    || role.equals("EQUIPMENT VIEWER")) {

                return role;
            }

            System.out.println(
                    "Invalid role. Please enter a valid role."
            );
            System.out.println(
                    "Allowed roles: ADMINISTRATOR, LABORATORY STAFF, EQUIPMENT VIEWER"
            );
            System.out.println("Enter 0 to cancel.");
        }
    }

    // VALIDATE USER STATUS
    private String readValidStatus() {

        while (true) {

            System.out.print("Enter new status: ");

            String status = scanner.nextLine().trim().toUpperCase();

            if (status.equals("0")) {
                return null;
            }

            if (status.equals("APPROVED")
                    || status.equals("PENDING")) {

                return status;
            }

            System.out.println(
                    "Invalid status. Please enter APPROVED or PENDING."
            );
            System.out.println("Enter 0 to cancel.");
        }
    }
}
