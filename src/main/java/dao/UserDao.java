
package com.schoollaboratory.dao;

import com.schoollaboratory.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserDao {

    // REGISTER ACCOUNT
    public boolean register(User user) {

        String sql = """
                INSERT INTO users
                (username, password, role, status)
                VALUES (?, ?, ?, 'PENDING')
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, user.username());
            statement.setString(2, user.password());
            statement.setString(3, user.role());

            return statement.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Registration failed: " + e.getMessage());
            return false;
        }
    }

    // LOGIN ACCOUNT
    public User login(String username, String password) {

        String sql = """
                SELECT * FROM users
                WHERE username = ? AND password = ?
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, username);
            statement.setString(2, password);

            try (ResultSet result = statement.executeQuery()) {

                if (result.next()) {
                    return new User(
                            result.getInt("id"),
                            result.getString("username"),
                            result.getString("password"),
                            result.getString("role"),
                            result.getString("status")
                    );
                }
            }

        } catch (SQLException e) {
            System.out.println("Login error: " + e.getMessage());
        }

        return null;
    }

    // VIEW ALL REGISTERED ACCOUNTS
    public List<User> getAllUsers() {

        List<User> users = new ArrayList<>();

        String sql = "SELECT * FROM users ORDER BY id";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {

            while (result.next()) {

                users.add(new User(
                        result.getInt("id"),
                        result.getString("username"),
                        result.getString("password"),
                        result.getString("role"),
                        result.getString("status")
                ));
            }

        } catch (SQLException e) {
            System.out.println("Error viewing accounts: " + e.getMessage());
        }

        return users;
    }

    // VIEW PENDING REGISTRATIONS
    public List<User> getPendingUsers() {

        List<User> users = new ArrayList<>();

        String sql = """
                SELECT * FROM users
                WHERE status = 'PENDING'
                ORDER BY id
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {

            while (result.next()) {

                users.add(new User(
                        result.getInt("id"),
                        result.getString("username"),
                        result.getString("password"),
                        result.getString("role"),
                        result.getString("status")
                ));
            }

        } catch (SQLException e) {
            System.out.println("Error viewing pending accounts: "
                    + e.getMessage());
        }

        return users;
    }

    // APPROVE PENDING USER ACCOUNT
    public boolean approveUser(int userId, int adminId) {

        String sql = """
                UPDATE users
                SET status = 'APPROVED'
                WHERE id = ? AND status = 'PENDING'
                """;

        try (Connection connection = DatabaseConnection.getConnection()) {

            connection.setAutoCommit(false);

            try (PreparedStatement statement =
                         connection.prepareStatement(sql)) {

                statement.setInt(1, userId);

                int rowsUpdated = statement.executeUpdate();

                if (rowsUpdated == 0) {
                    connection.rollback();
                    return false;
                }

                String description =
                        "Administrator ID: " + adminId
                                + " approved user account ID: " + userId + ".";

                recordActivity(
                        connection,
                        adminId,
                        "USER_APPROVED",
                        description
                );

                connection.commit();
                return true;

            } catch (SQLException e) {
                connection.rollback();
                throw e;
            }

        } catch (SQLException e) {
            System.out.println("Error approving account: " + e.getMessage());
            return false;
        }
    }

    // UPDATE USER ROLE AND STATUS
    public boolean updateUserRoleAndStatus(
            int userId,
            String newRole,
            String newStatus,
            int adminId) {

        String sql = """
                UPDATE users
                SET role = ?, status = ?
                WHERE id = ?
                """;

        try (Connection connection = DatabaseConnection.getConnection()) {

            connection.setAutoCommit(false);

            try (PreparedStatement statement =
                         connection.prepareStatement(sql)) {

                statement.setString(1, newRole.toUpperCase());
                statement.setString(2, newStatus.toUpperCase());
                statement.setInt(3, userId);

                int rowsUpdated = statement.executeUpdate();

                if (rowsUpdated == 0) {
                    connection.rollback();
                    return false;
                }

                String description =
                        "Administrator ID: " + adminId
                                + " updated user ID: " + userId
                                + " to Role: " + newRole.toUpperCase()
                                + ", Status: " + newStatus.toUpperCase() + ".";

                recordActivity(
                        connection,
                        adminId,
                        "USER_UPDATED",
                        description
                );

                connection.commit();
                return true;

            } catch (SQLException e) {
                connection.rollback();
                throw e;
            }

        } catch (SQLException e) {
            System.out.println("Error updating user account: "
                    + e.getMessage());
            return false;
        }
    }

    // RECORD ACTIVITY USING THE SAME DATABASE CONNECTION
    private void recordActivity(
            Connection connection,
            int userId,
            String actionType,
            String description) throws SQLException {

        String sql = """
                INSERT INTO activity_logs
                (user_id, action_type, description)
                VALUES (?, ?, ?)
                """;

        try (PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, userId);
            statement.setString(2, actionType);
            statement.setString(3, description);

            statement.executeUpdate();
        }
    }
}
