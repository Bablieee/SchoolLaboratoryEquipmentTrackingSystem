
package com.schoollaboratory.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInitializer {

    public static void initialize() {

        String sql = """
                CREATE TABLE IF NOT EXISTS users (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    username TEXT NOT NULL UNIQUE,
                    password TEXT NOT NULL,
                    role TEXT NOT NULL,
                    status TEXT NOT NULL DEFAULT 'PENDING'
                )
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement()) {

            statement.execute(sql);

            System.out.println("Database initialized successfully!");

            createDefaultAdmin(connection);

        } catch (SQLException e) {
            System.out.println("Database initialization failed: "
                    + e.getMessage());
        }
    }

    private static void createDefaultAdmin(Connection connection)
            throws SQLException {

        String sql = """
                INSERT OR IGNORE INTO users
                (username, password, role, status)
                VALUES ('admin', 'admin123', 'ADMINISTRATOR', 'APPROVED')
                """;

        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(sql);
        }
    }
}
