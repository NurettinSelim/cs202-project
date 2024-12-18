package service.impl;

import model.Hotel;
import model.HousekeepingStaff;
import model.Staff;
import service.StaffService;
import util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class StaffServiceImpl implements StaffService {

    /**
     * Find all staff with their role
     * 
     * @param hotelId the hotel id
     * @return a hashmap of staff with their role
     */
    public HashMap<Staff, String> findAllWithRole(int hotelId) {
        String sql = """
                SELECT u.*, s.hotel_id, s.salary, s.hire_date, 'ADMINISTRATOR' as role
                FROM users u
                JOIN administrator_staff a ON u.user_id = a.user_id
                JOIN staff s ON u.user_id = s.user_id
                WHERE s.hotel_id = ?

                UNION

                SELECT u.*, s.hotel_id, s.salary, s.hire_date, 'RECEPTIONIST' as role
                FROM users u
                JOIN receptionist_staff r ON u.user_id = r.user_id
                JOIN staff s ON u.user_id = s.user_id
                WHERE s.hotel_id = ?

                UNION

                SELECT u.*, s.hotel_id, s.salary, s.hire_date, 'HOUSEKEEPING' as role
                FROM users u
                JOIN housekeeping_staff h ON u.user_id = h.user_id
                JOIN staff s ON u.user_id = s.user_id
                WHERE s.hotel_id = ?;
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
                staff.setHireDate(rs.getDate("hire_date"));

                Hotel hotel = new Hotel();
                hotel.setHotelId(rs.getInt("hotel_id"));
                staff.setHotel(hotel);

                users.put(staff, rs.getString("role"));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error getting all users", e);
        }
        return users;
    }

    @Override
    public List<HousekeepingStaff> findAvailableHousekeepers(Hotel hotel, String date) {
        String sql = """
                SELECT
                    hs_staff.user_id,
                    u.first_name,
                    u.last_name,
                    u.phone,
                    h.hotel_name,
                    hs.room_number,
                    hs.scheduled_date,
                    rs.status_name
                FROM housekeeping_staff hs_staff
                JOIN users u ON hs_staff.user_id = u.user_id
                JOIN staff s ON hs_staff.user_id = s.user_id
                JOIN hotels h ON s.hotel_id = h.hotel_id
                LEFT JOIN housekeeping_schedule hs ON h.hotel_id = hs.hotel_id
                    AND hs.scheduled_date = ?
                LEFT JOIN room_statuses rs ON hs.status_id = rs.status_id
                WHERE h.hotel_id = ?;
                """;

        List<HousekeepingStaff> staff = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, date);
            stmt.setInt(2, hotel.getHotelId());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                HousekeepingStaff housekeepingStaff = new HousekeepingStaff();
                housekeepingStaff.setUserId(rs.getInt("user_id"));
                housekeepingStaff.setFirstName(rs.getString("first_name"));
                housekeepingStaff.setLastName(rs.getString("last_name"));
                housekeepingStaff.setPhone(rs.getString("phone"));
                staff.add(housekeepingStaff);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error getting available housekeepers", e);
        }
        return staff;
    }
}