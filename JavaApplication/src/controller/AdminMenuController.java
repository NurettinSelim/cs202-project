package controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import util.DatabaseConnection;

public class AdminMenuController extends BaseControlller {
    private static List<String> menuItems = Arrays.asList(
        "1. Add Room",
        "2. Delete Room",
        "3. Manage Room Status",
        "4. Add User Account",
        "5. View User Accounts",
        "6. Generate Revenue Report",
        "7. View All Booking Records",
        "8. View All Housekeeping Records",
        "9. View Most Booked Room Types",
        "10. View All Employees",
        "11. Return to Main Menu"
    );

    public void addRoom(int hotelId, int roomNumber, int typeId, int statusId) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "INSERT INTO rooms (hotel_id, room_number, type_id, status_id) VALUES (?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, hotelId);
            stmt.setInt(2, roomNumber);
            stmt.setInt(3, typeId);
            stmt.setInt(4, statusId);
            stmt.executeUpdate();
        }
    }

    public void deleteRoom(int hotelId, int roomNumber) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "DELETE FROM rooms WHERE hotel_id = ? AND room_number = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, hotelId);
            stmt.setInt(2, roomNumber);
            stmt.executeUpdate();
        }
    }

    public void manageRoomStatus(int hotelId, int roomNumber, int statusId) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "UPDATE rooms SET status_id = ? WHERE hotel_id = ? AND room_number = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, statusId);
            stmt.setInt(2, hotelId);
            stmt.setInt(3, roomNumber);
            stmt.executeUpdate();
        }
    }

    public void addUser(String firstName, String lastName, String phone) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "INSERT INTO users (first_name, last_name, phone, created_at) VALUES (?, ?, ?, CURRENT_TIMESTAMP)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, firstName);
            stmt.setString(2, lastName);
            stmt.setString(3, phone);
            stmt.executeUpdate();
        }
    }

    public void viewUserAccounts() throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT * FROM users";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                System.out.println(rs.getString("first_name") + " " + rs.getString("last_name"));
            }
        }
    }

    public void generateRevenueReport(int hotelId) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = """
                    SELECT
                        COUNT(DISTINCT b.booking_id) as total_bookings,
                        SUM(p.amount) as total_revenue,
                        AVG(p.amount) as average_revenue_per_booking
                    FROM hotels h
                    JOIN rooms r ON h.hotel_id = r.hotel_id
                    JOIN room_types rt ON r.type_id = rt.type_id
                    JOIN booking_rooms br ON r.hotel_id = br.hotel_id AND r.room_number = br.room_number
                    JOIN bookings b ON br.booking_id = b.booking_id
                    JOIN payments p ON b.booking_id = p.booking_id
                    WHERE b.status_id = 4 -- CHECKED_OUT
                    AND h.hotel_id = ?;""";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, hotelId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                System.out.println(rs.getString("month") + ": " + rs.getDouble("revenue"));
            }
        }
    }

    public void viewBookingRecords() throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT * FROM bookings";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                System.out.println(rs.getString("booking_id") + ": " + rs.getString("room_number"));
            }
        }
    }

    public void viewHousekeepingRecords() throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT * FROM housekeeping_schedule";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                System.out.println(rs.getString("booking_id") + ": " + rs.getString("room_number"));
            }
        }
    }

    public void viewMostBookedRoomTypes(int hotelId, String startDate, String endDate) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = """
                    SELECT rt.type_name, COUNT(*) as booking_count
                    FROM bookings b
                    JOIN booking_rooms br ON b.booking_id = br.booking_id
                    JOIN rooms r ON br.hotel_id = r.hotel_id AND br.room_number = r.room_number
                    JOIN room_types rt ON r.type_id = rt.type_id
                    WHERE b.check_in_date BETWEEN ? AND ?
                    AND h.hotel_id = ?
                    GROUP BY rt.type_id
                    ORDER BY booking_count DESC;""";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, startDate);
            stmt.setString(2, endDate);
            stmt.setInt(3, hotelId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                System.out.println(rs.getString("type_name") + ": " + rs.getInt("booking_count"));
            }
        }
    }

    public void viewAllEmployees() throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = """
                    SELECT u.*, 'ADMINISTRATOR' as role
                    FROM users u
                    JOIN administrator_staff a ON u.user_id = a.user_id

                    UNION

                    SELECT u.*, 'RECEPTIONIST' as role
                    FROM users u
                    JOIN receptionist_staff r ON u.user_id = r.user_id

                    UNION

                    SELECT u.*, 'HOUSEKEEPING' as role
                    FROM users u
                    JOIN housekeeping_staff h ON u.user_id = h.user_id;""";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                System.out.println(rs.getString("first_name") + " " + rs.getString("last_name"));
            }
        }
    }

    public void displayMenu() {
        super.displayMenu(menuItems);
    }
}
