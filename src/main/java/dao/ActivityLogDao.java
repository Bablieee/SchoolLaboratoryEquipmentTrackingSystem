package com.schoollaboratory.dao;

import com.schoollaboratory.model.ActivityLog;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ActivityLogDao {

    // RECORD ACTIVITY
    public boolean logAction(int userId, String actionType, String description) {

        String sql = """
                INSERT INTO activity_logs
                (user_id, action_type, description)
                VALUES (?, ?, ?)
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, userId);
            statement.setString(2, actionType);
            statement.setString(3, description);

            return statement.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error recording activity: " + e.getMessage());
            return false;
        }
    }

    // GET ALL ACTIVITY LOGS
    public List<ActivityLog> getAllLogs() {

        List<ActivityLog> logs = new ArrayList<>();

        String sql = """
                SELECT id, user_id, action_type, description, timestamp
                FROM activity_logs
                ORDER BY timestamp DESC, id DESC
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {

            while (result.next()) {
                logs.add(new ActivityLog(
                        result.getInt("id"),
                        result.getInt("user_id"),
                        result.getString("action_type"),
                        result.getString("description"),
                        result.getString("timestamp")
                ));
            }

        } catch (SQLException e) {
            System.out.println("Error retrieving activity logs: " + e.getMessage());
        }

        return logs;
    }

    // GET LOGS BY USER ID
    public List<ActivityLog> getLogsByUserId(int userId) {

        List<ActivityLog> logs = new ArrayList<>();

        String sql = """
                SELECT id, user_id, action_type, description, timestamp
                FROM activity_logs
                WHERE user_id = ?
                ORDER BY timestamp DESC, id DESC
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, userId);

            try (ResultSet result = statement.executeQuery()) {

                while (result.next()) {
                    logs.add(new ActivityLog(
                            result.getInt("id"),
                            result.getInt("user_id"),
                            result.getString("action_type"),
                            result.getString("description"),
                            result.getString("timestamp")
                    ));
                }
            }

        } catch (SQLException e) {
            System.out.println("Error retrieving user activity logs: " + e.getMessage());
        }

        return logs;
    }

    // CLEAR ALL ACTIVITY LOGS
    public boolean clearAllLogs() {

        String sql = "DELETE FROM activity_logs";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.out.println("Error clearing activity logs: " + e.getMessage());
            return false;
        }
    }
}
