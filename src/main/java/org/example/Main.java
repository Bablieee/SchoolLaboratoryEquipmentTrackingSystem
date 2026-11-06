
import dao.UserDao;
import ui.Menu;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        UserDao userDao = new UserDao();

        // Initialize database table
        userDao.createTable();

        Scanner scanner = new Scanner(System.in);

        // Start application menu
        Menu menu = new Menu(userDao, scanner);
        menu.start();

        scanner.close();
    }
}
