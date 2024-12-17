package service.impl;

import service.HotelService;
import util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class HotelServiceImpl implements HotelService {

    public Revenue getRevenue(Integer hotelId) {
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
                AND h.hotel_id = ?;
                    """;
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, hotelId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Revenue(rs.getInt("total_bookings"), rs.getDouble("total_revenue"),
                        rs.getDouble("average_revenue_per_booking"));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get revenue", e);
        }
        return null;
    }
}
