
package com.schoollaboratory.ui;

import com.schoollaboratory.dao.EquipmentDao;
import com.schoollaboratory.dao.UserDao;
import com.schoollaboratory.model.Equipment;
import com.schoollaboratory.model.User;

import java.util.List;
import java.util.Scanner;

public class LaboratoryStaffMenu {

    private final UserDao userDao;
    private final Scanner scanner;
    private final EquipmentDao equipmentDao;

    public LaboratoryStaffMenu(
            UserDao userDao,
            Scanner scanner) {

        this.userDao = userDao;
        this.scanner = scanner;
        this.equipmentDao = new EquipmentDao();
    }

    public void start(User staff) {

        boolean running = true;

        while (running) {

            System.out.println("\n====================================");
            System.out.println("       LABORATORY STAFF MENU");
            System.out.println("====================================");
            System.out.println("Welcome, " + staff.username());

            System.out.println("1. View Equipment");
            System.out.println("2. Manage Equipment");
            System.out.println("3. Logout");

            System.out.print("Enter choice: ");

            String choice = scanner.nextLine();

            switch (choice) {

                case "1" -> viewEquipment();

                case "2" -> manageEquipment(staff);

                case "3" -> {
                    System.out.println("Logging out...");
                    running = false;
                }

                default ->
                        System.out.println("Invalid choice. Try again.");
            }
        }
    }

    private void viewEquipment() {

        System.out.println("\n========== EQUIPMENT RECORDS ==========");

        List<Equipment> equipmentList =
                equipmentDao.getAllEquipment();

        if (equipmentList.isEmpty()) {
            System.out.println("No equipment records found.");
            return;
        }

        System.out.printf(
                "%-5s | %-25s | %-18s | %-20s | %-18s%n",
                "ID", "EQUIPMENT NAME", "CATEGORY",
                "LOCATION", "CONDITION"
        );

        System.out.println("------------------------------------------------------------------------------------------");

        for (Equipment equipment : equipmentList) {

            System.out.printf(
                    "%-5d | %-25s | %-18s | %-20s | %-18s%n",
                    equipment.equipmentId(),
                    equipment.equipmentName(),
                    equipment.category(),
                    equipment.location(),
                    equipment.conditionStatus()
            );
        }
    }

    private void manageEquipment(User staff) {

        boolean managing = true;

        while (managing) {

            System.out.println("\n========== MANAGE EQUIPMENT ==========");
            System.out.println("1. Add Equipment");
            System.out.println("2. Update Equipment Condition");
            System.out.println("3. Back");
            System.out.print("Enter choice: ");

            String choice = scanner.nextLine();

            switch (choice) {

                case "1" -> addEquipment(staff);

                case "2" -> updateEquipmentCondition(staff);

                case "3" -> managing = false;

                default ->
                        System.out.println("Invalid choice. Try again.");
            }
        }
    }

    private void addEquipment(User staff) {

        System.out.println("\n========== ADD EQUIPMENT ==========");

        System.out.print("Equipment Name: ");
        String name = scanner.nextLine().trim();

        System.out.print("Category: ");
        String category = scanner.nextLine().trim();

        System.out.print("Location: ");
        String location = scanner.nextLine().trim();

        System.out.print("Condition (Good/Needs Repair/Damaged): ");
        String condition = scanner.nextLine().trim();

        if (name.isEmpty() || category.isEmpty()
                || location.isEmpty() || condition.isEmpty()) {

            System.out.println("All fields are required.");
            return;
        }

        boolean added = equipmentDao.addEquipment(
                name,
                category,
                location,
                condition,
                staff.id()
        );

        if (added) {
            System.out.println("Equipment added successfully!");
            System.out.println("Equipment creation activity recorded.");
        } else {
            System.out.println("Failed to add equipment.");
        }
    }

    private void updateEquipmentCondition(User staff) {

        System.out.println("\n========== UPDATE EQUIPMENT CONDITION ==========");

        viewEquipment();

        System.out.print("\nEnter Equipment ID: ");
        String input = scanner.nextLine();

        int equipmentId;

        try {
            equipmentId = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            System.out.println("Invalid equipment ID.");
            return;
        }

        System.out.print("Enter New Condition: ");
        String newCondition = scanner.nextLine().trim();

        if (newCondition.isEmpty()) {
            System.out.println("Condition is required.");
            return;
        }

        boolean updated = equipmentDao.updateEquipmentCondition(
                equipmentId,
                newCondition,
                staff.id()
        );

        if (updated) {
            System.out.println("Equipment condition updated successfully!");
            System.out.println("Equipment update activity recorded.");
        } else {
            System.out.println("Equipment not found or update failed.");
        }
    }
}
