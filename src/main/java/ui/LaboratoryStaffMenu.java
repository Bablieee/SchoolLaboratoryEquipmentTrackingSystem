package com.schoollaboratory.ui;

import com.schoollaboratory.dao.UserDao;
import com.schoollaboratory.model.User;

import java.util.Scanner;

public class LaboratoryStaffMenu {

    private final UserDao userDao;
    private final Scanner scanner;

    public LaboratoryStaffMenu(
            UserDao userDao,
            Scanner scanner) {

        this.userDao = userDao;
        this.scanner = scanner;
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

                case "1" -> {
                    System.out.println(
                            "\nEquipment viewing section."
                    );
                    System.out.println(
                            "Equipment records will be displayed here."
                    );
                }

                case "2" -> {
                    System.out.println(
                            "\nEquipment management section."
                    );
                    System.out.println(
                            "Equipment management will be handled here."
                    );
                }

                case "3" -> {
                    System.out.println("Logging out...");
                    running = false;
                }

                default ->
                        System.out.println("Invalid choice. Try again.");
            }
        }
    }
}