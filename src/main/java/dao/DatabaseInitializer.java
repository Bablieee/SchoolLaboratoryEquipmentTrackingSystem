
package com.schoollaboratory.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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

        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement()) {

            statement.execute(usersSQL);
            statement.execute(equipmentSQL);

            migrateActivityLogs(connection);

            createDefaultAdmin(connection);

            System.out.println("Database initialized successfully!");

            return true;

        } catch (SQLException e) {

            System.out.println(
                    "Database initialization failed: " + e.getMessage()
            );

            return false;
        }
    }

    private static void migrateActivityLogs(Connection connection)
            throws SQLException {

        boolean tableExists;

        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT name FROM sqlite_master " +
                        "WHERE type = 'table' AND name = ?")) {

            statement.setString(1, "activity_logs");

            try (ResultSet result = statement.executeQuery()) {
                tableExists = result.next();
            }
        }

        if (tableExists && !hasColumn(connection, "action_type")) {

            try (Statement statement = connection.createStatement()) {

                statement.execute(
                        "ALTER TABLE activity_logs " +
                                "RENAME TO activity_logs_legacy"
                );
            }

            createActivityLogsTable(connection);

            String migrateSQL = """
                    INSERT INTO activity_logs
                    (id, user_id, action_type, description, timestamp)
                    SELECT log_id, user_id, action, action, date_time
                    FROM activity_logs_legacy
                    """;

            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate(migrateSQL);

                statement.execute("DROP TABLE activity_logs_legacy");
            }

        } else {

            createActivityLogsTable(connection);
        }
    }

    private static boolean hasColumn(Connection connection, String columnName)
            throws SQLException {

        try (Statement statement = connection.createStatement();
             ResultSet result = statement.executeQuery(
                     "PRAGMA table_info(activity_logs)")) {

            while (result.next()) {

                if (result.getString("name").equalsIgnoreCase(columnName)) {
                    return true;
                }
            }
        }

        return false;
    }

    private static void createActivityLogsTable(Connection connection)
            throws SQLException {

        String logsSQL = """
                CREATE TABLE IF NOT EXISTS activity_logs (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    user_id INTEGER NOT NULL,
                    action_type TEXT NOT NULL,
                    description TEXT NOT NULL,
                    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

                    FOREIGN KEY (user_id)
                    REFERENCES users(id)
                    ON DELETE CASCADE
                )
                """;

        try (Statement statement = connection.createStatement()) {
            statement.execute(logsSQL);
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
