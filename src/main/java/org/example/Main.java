package com.schoollaboratory;

import com.schoollaboratory.dao.DatabaseInitializer;
import com.schoollaboratory.dao.UserDao;
import com.schoollaboratory.ui.Authentication;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        // STEP 1: INITIALIZE DATABASE

        if (!DatabaseInitializer.initialize()) {

            System.out.println(
                    "Application cannot start because database "
                            + "initialization failed."
            );

            return;
        }

        // STEP 2: INSTANTIATE DAO AND SCANNER

        UserDao userDao = new UserDao();

        try (Scanner scanner = new Scanner(System.in)) {

            // STEP 3: INSTANTIATE AUTHENTICATION

            Authentication authentication =
                    new Authentication(userDao, scanner);

            // STEP 4: DELEGATE EXECUTION

            authentication.start();
        }

        System.out.println("Application closed successfully.");
    }
}