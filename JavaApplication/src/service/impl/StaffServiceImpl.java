package service.impl;

import model.Staff;
import service.StaffService;
import util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public class StaffServiceImpl implements StaffService {

    /**
     * Find all staff with their role
     * @param hotelId the hotel id
     * @return a hashmap of staff with their role
     */
    public HashMap<Staff, String> findAllWithRole(int hotelId) {
        String sql = """
                SELECT u.*, 'ADMINISTRATOR' as role
                FROM users u
                JOIN administrator_staff a ON u.user_id = a.user_id
                WHERE a.hotel_id = ?

                UNION

                SELECT u.*, 'RECEPTIONIST' as role
                FROM users u
                JOIN receptionist_staff r ON u.user_id = r.user_id
                WHERE r.hotel_id = ?

                UNION

                SELECT u.*, 'HOUSEKEEPING' as role
                FROM users u
                JOIN housekeeping_staff h ON u.user_id = h.user_id
                WHERE h.hotel_id = ?;
                """;

        HashMap<Staff, String> users = new HashMap<>();
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, hotelId);
            stmt.setInt(2, hotelId);
            stmt.setInt(3, hotelId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Staff staff = new Staff();
                staff.setUserId(rs.getInt("user_id"));
                staff.setFirstName(rs.getString("first_name"));
                staff.setLastName(rs.getString("last_name"));
                staff.setPhone(rs.getString("phone"));
                staff.setCreatedAt(rs.getTimestamp("created_at"));
                staff.setSalary(rs.getBigDecimal("salary"));
                users.put(staff, rs.getString("role"));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error getting all users", e);
        }
        return users;
    }
}