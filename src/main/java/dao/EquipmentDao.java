
package com.schoollaboratory.dao;

import com.schoollaboratory.model.Equipment;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EquipmentDao {

    private final ActivityLogDao activityLogDao;

    public EquipmentDao() {
        this.activityLogDao = new ActivityLogDao();
    }

    public boolean addEquipment(
            String name,
            String category,
            String location,
            String conditionStatus,
            int staffId) {

        String sql = """
                INSERT INTO equipment
                (equipment_name, category, location, condition_status)
                VALUES (?, ?, ?, ?)
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, name);
            statement.setString(2, category);
            statement.setString(3, location);
            statement.setString(4, conditionStatus);

            int rows = statement.executeUpdate();

            if (rows > 0) {
                try (ResultSet keys = statement.getGeneratedKeys()) {
                    if (keys.next()) {
                        int equipmentId = keys.getInt(1);

                        activityLogDao.logAction(
                                staffId,
                                "EQUIPMENT_CREATED",
                                "Staff ID: " + staffId
                                        + " added equipment ID: " + equipmentId
                                        + " (" + name + ")."
                        );
                    }
                }
                return true;
            }

        } catch (SQLException e) {
            System.out.println("Error adding equipment: " + e.getMessage());
        }

        return false;
    }

    public List<Equipment> getAllEquipment() {

        List<Equipment> equipmentList = new ArrayList<>();

        String sql = """
                SELECT equipment_id, equipment_name, category,
                       location, condition_status
                FROM equipment
                ORDER BY equipment_id
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {

            while (result.next()) {
                equipmentList.add(new Equipment(
                        result.getInt("equipment_id"),
                        result.getString("equipment_name"),
                        result.getString("category"),
                        result.getString("location"),
                        result.getString("condition_status")
                ));
            }

        } catch (SQLException e) {
            System.out.println("Error retrieving equipment: " + e.getMessage());
        }

        return equipmentList;
    }

    public boolean updateEquipmentCondition(
            int equipmentId,
            String newCondition,
            int staffId) {

        String sql = """
                UPDATE equipment
                SET condition_status = ?
                WHERE equipment_id = ?
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, newCondition);
            statement.setInt(2, equipmentId);

            int rows = statement.executeUpdate();

            if (rows > 0) {
                activityLogDao.logAction(
                        staffId,
                        "EQUIPMENT_UPDATED",
                        "Staff ID: " + staffId
                                + " updated condition of equipment ID: "
                                + equipmentId + " to " + newCondition + "."
                );

                return true;
            }

        } catch (SQLException e) {
            System.out.println("Error updating equipment: " + e.getMessage());
        }

        return false;
    }
}
