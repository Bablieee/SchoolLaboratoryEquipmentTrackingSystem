
package com.schoollaboratory.ui;

import com.schoollaboratory.dao.ActivityLogDao;
import com.schoollaboratory.dao.UserDao;
import com.schoollaboratory.model.ActivityLog;
import com.schoollaboratory.model.User;

import java.util.List;
import java.util.Scanner;

public class AdminMenu {

    private final UserDao userDao;
    private final ActivityLogDao activityLogDao;
    private final Scanner scanner;

    public AdminMenu(UserDao userDao, Scanner scanner) {
        this.userDao = userDao;
        this.activityLogDao = new ActivityLogDao();
        this.scanner = scanner;
    }

    public void start(User admin) {
        boolean running = true;

        while (running) {
            System.out.println("\n========================================");
            System.out.println("       SCHOOL LABORATORY SYSTEM");
            System.out.println("           ADMINISTRATOR MENU");
            System.out.println("========================================");
            System.out.println("Logged in as: " + admin.username());
            System.out.println("----------------------------------------");
            System.out.println("1. View All System Users");
            System.out.println("2. View Pending Registrations");
            System.out.println("3. Approve User Account");
            System.out.println("4. Update User Role and Status");
            System.out.println("5. View System Audit Logs");
            System.out.println("6. Logout");
            System.out.println("----------------------------------------");

            int choice = readIntInput("Enter your choice: ");

            switch (choice) {
                case 1:
                    viewAllUsers();
                    break;

                case 2:
                    viewPendingUsers();
                    break;

                case 3:
                    approveUser(admin);
                    break;

                case 4:
                    updateUser(admin);
                    break;

                case 5:
                    auditLogMenu();
                    break;

                case 6:
                    System.out.println("\nAdmin logged out successfully!");
                    running = false;
                    break;

                default:
                    System.out.println("\nInvalid choice. Please try again.");
            }
        }
    }

    private void viewAllUsers() {
        System.out.println("\n========== ALL SYSTEM USERS ==========");

        List<User> users = userDao.getAllUsers();

        displayUserTable(users);
    }

    private void viewPendingUsers() {
        System.out.println("\n========== PENDING REGISTRATIONS ==========");

        List<User> users = userDao.getPendingUsers();

        displayUserTable(users);
    }

    private void approveUser(User admin) {
        System.out.println("\n========== APPROVE USER ACCOUNT ==========");

        List<User> pendingUsers = userDao.getPendingUsers();

        if (pendingUsers.isEmpty()) {
            System.out.println("No pending user accounts found.");
            return;
        }

        displayUserTable(pendingUsers);

        int userId = readIntInput(
                "\nEnter User ID to approve (0 to cancel): "
        );

        if (userId == 0) {
            System.out.println("Approval cancelled.");
            return;
        }

        boolean approved = userDao.approveUser(userId, admin.id());

        if (approved) {
            System.out.println("User account approved successfully!");
        } else {
            System.out.println("User ID not found or account is not pending.");
        }
    }

    private void updateUser(User admin) {
        System.out.println("\n========== UPDATE USER ROLE AND STATUS ==========");

        List<User> users = userDao.getAllUsers();

        displayUserTable(users);

        int userId = readIntInput(
                "\nEnter User ID to update (0 to cancel): "
        );

        if (userId == 0) {
            System.out.println("Update cancelled.");
            return;
        }

        if (userId == admin.id()) {
            System.out.println(
                    "WARNING: Modifying the currently active admin account is restricted."
            );
            return;
        }

        String newRole = readValidRole();
        if (newRole == null) {
            System.out.println("Update cancelled.");
            return;
        }

        String newStatus = readValidStatus();
        if (newStatus == null) {
            System.out.println("Update cancelled.");
            return;
        }

        boolean updated = userDao.updateUserRoleAndStatus(
                userId,
                newRole,
                newStatus,
                admin.id()
        );

        if (updated) {
            System.out.println("User role and status updated successfully!");
        } else {
            System.out.println("User ID not found. No changes were made.");
        }
    }

    private void auditLogMenu() {
        boolean running = true;

        while (running) {
            System.out.println("\n========================================");
            System.out.println("           SYSTEM AUDIT LOGS");
            System.out.println("========================================");
            System.out.println("1. View All System Logs");
            System.out.println("2. Filter Logs by User ID");
            System.out.println("3. Clear Historical Logs");
            System.out.println("4. Back to Admin Menu");
            System.out.println("----------------------------------------");

            int choice = readIntInput("Enter your choice: ");

            switch (choice) {
                case 1:
                    viewAllLogs();
                    break;

                case 2:
                    filterLogsByUserId();
                    break;

                case 3:
                    clearHistoricalLogs();
                    break;

                case 4:
                    running = false;
                    break;

                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private void viewAllLogs() {
        System.out.println("\n========== ALL SYSTEM AUDIT LOGS ==========");

        List<ActivityLog> logs = activityLogDao.getAllLogs();

        displayLogTable(logs);
    }

    private void filterLogsByUserId() {
        System.out.println("\n========== FILTER LOGS BY USER ID ==========");

        int userId = readIntInput(
                "Enter User ID (0 to cancel): "
        );

        if (userId == 0) {
            System.out.println("Filtering cancelled.");
            return;
        }

        List<ActivityLog> logs = activityLogDao.getLogsByUserId(userId);

        displayLogTable(logs);
    }

    private void clearHistoricalLogs() {
        System.out.println("\n========== CLEAR HISTORICAL LOGS ==========");

        System.out.print(
                "Are you sure you want to delete all historical logs? (Y/N): "
        );

        String confirmation = scanner.nextLine().trim();

        if (confirmation.equalsIgnoreCase("Y")) {
            activityLogDao.clearAllLogs();
            System.out.println("Historical logs cleared successfully!");
        } else {
            System.out.println("Clear operation cancelled.");
        }
    }

    private void displayLogTable(List<ActivityLog> logs) {
        if (logs.isEmpty()) {
            System.out.println("No activity logs found.");
            return;
        }

        System.out.println();
        System.out.printf(
                "%-5s | %-8s | %-18s | %-35s | %-20s%n",
                "ID",
                "USER ID",
                "ACTION TYPE",
                "DESCRIPTION",
                "TIMESTAMP"
        );

        System.out.println(
                "---------------------------------------------------------------------------------------------------------------"
        );

        for (ActivityLog log : logs) {
            System.out.printf(
                    "%-5d | %-8d | %-18s | %-35s | %-20s%n",
                    log.id(),
                    log.userId(),
                    log.actionType(),
                    log.description(),
                    log.timestamp()
            );
        }
    }

    private void displayUserTable(List<User> users) {
        if (users.isEmpty()) {
            System.out.println("No user records found.");
            return;
        }

        System.out.println();
        System.out.printf(
                "%-5s | %-20s | %-22s | %-12s%n",
                "ID",
                "USERNAME",
                "ROLE",
                "STATUS"
        );

        System.out.println(
                "------------------------------------------------------------------"
        );

        for (User user : users) {
            System.out.printf(
                    "%-5d | %-20s | %-22s | %-12s%n",
                    user.id(),
                    user.username(),
                    user.role(),
                    user.status()
            );
        }
    }

    private int readIntInput(String message) {
        while (true) {
            System.out.print(message);

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

    private String readValidRole() {
        while (true) {
            System.out.println("\nAvailable Roles:");
            System.out.println("1. ADMINISTRATOR");
            System.out.println("2. LABORATORY STAFF");
            System.out.println("3. EQUIPMENT VIEWER");
            System.out.println("0. Cancel");

            int choice = readIntInput("Select new role: ");

            switch (choice) {
                case 1:
                    return "ADMINISTRATOR";

                case 2:
                    return "LABORATORY STAFF";

                case 3:
                    return "EQUIPMENT VIEWER";

                case 0:
                    return null;

                default:
                    System.out.println("Invalid role selection.");
            }
        }
    }

    private String readValidStatus() {
        while (true) {
            System.out.println("\nAvailable Statuses:");
            System.out.println("1. APPROVED");
            System.out.println("2. PENDING");
            System.out.println("0. Cancel");

            int choice = readIntInput("Select new status: ");

            switch (choice) {
                case 1:
                    return "APPROVED";

                case 2:
                    return "PENDING";

                case 0:
                    return null;

                default:
                    System.out.println("Invalid status selection.");
            }
        }
    }
}
