
package com.schoollaboratory;

import com.schoollaboratory.dao.UserDao;
import com.schoollaboratory.dao.DatabaseInitializer;
import com.schoollaboratory.ui.Authentication;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        DatabaseInitializer.initialize();

        UserDao userDao = new UserDao();

        Scanner scanner = new Scanner(System.in);

        Authentication authentication =
                new Authentication(userDao, scanner);

        authentication.start();

        scanner.close();
    }
}
