package com.schoollaboratory.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInitializer {

    public static boolean initialize() {

        String usersSQL = """
                CREATE TABLE IF NOT EXISTS users (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    username TEXT NOT NULL UNIQUE,
                    password TEXT NOT NULL,
                    role TEXT NOT NULL,
                    status TEXT NOT NULL DEFAULT 'PENDING'
                )
                """;

        String equipmentSQL = """
                CREATE TABLE IF NOT EXISTS equipment (
                    equipment_id INTEGER PRIMARY KEY AUTOINCREMENT,
                    equipment_name TEXT NOT NULL,
                    category TEXT NOT NULL,
                    location TEXT NOT NULL,
                    condition_status TEXT NOT NULL
                )
                """;

        String logsSQL = """
                CREATE TABLE IF NOT EXISTS activity_logs (
                    log_id INTEGER PRIMARY KEY AUTOINCREMENT,
                    user_id INTEGER NOT NULL,
                    equipment_id INTEGER,
                    action TEXT NOT NULL,
                    date_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

                    FOREIGN KEY (user_id)
                    REFERENCES users(id),

                    FOREIGN KEY (equipment_id)
                    REFERENCES equipment(equipment_id)
                )
                """;

        try (Connection connection =
                     DatabaseConnection.getConnection();

             Statement statement = connection.createStatement()) {

            statement.execute(usersSQL);

            statement.execute(equipmentSQL);

            statement.execute(logsSQL);

            createDefaultAdmin(connection);

            System.out.println("Database initialized successfully!");

            return true;

        } catch (SQLException e) {

            System.out.println(
                    "Database initialization failed: "
                            + e.getMessage()
            );

            return false;
        }
    }

    private static void createDefaultAdmin(Connection connection)
            throws SQLException {

        String sql = """
                INSERT OR IGNORE INTO users
                (username, password, role, status)
                VALUES (?, ?, ?, ?)
                """;

        try (PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, "admin");
            statement.setString(2, "admin123");
            statement.setString(3, "ADMINISTRATOR");
            statement.setString(4, "APPROVED");

            statement.executeUpdate();
        }
    }
}