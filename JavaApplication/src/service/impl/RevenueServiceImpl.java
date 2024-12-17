package service.impl;

import service.RevenueService;
import util.DatabaseConnection;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RevenueServiceImpl implements RevenueService {
    @Override
    public RevenueReport generateRevenueReport(int hotelId) {
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
            AND h.hotel_id = ?""";
        
        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, hotelId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return new RevenueReport(
                    rs.getInt("total_bookings"),
                    rs.getBigDecimal("total_revenue"),
                    rs.getBigDecimal("average_revenue_per_booking")
                );
            }
            return new RevenueReport(0, BigDecimal.ZERO, BigDecimal.ZERO);
        } catch (SQLException e) {
            throw new RuntimeException("Error generating revenue report", e);
        }
    }

    @Override
    public List<RoomTypeStats> getMostBookedRoomTypes(int hotelId) {
        String sql = """
            SELECT 
                rt.type_name,
                COUNT(DISTINCT b.booking_id) as total_bookings,
                SUM(p.amount) as total_revenue,
                COUNT(DISTINCT b.booking_id) * 100.0 / 
                    (SELECT COUNT(*) FROM rooms r2 WHERE r2.type_id = rt.type_id) as occupancy_rate
            FROM room_types rt
            JOIN rooms r ON rt.type_id = r.type_id
            JOIN booking_rooms br ON r.hotel_id = br.hotel_id AND r.room_number = br.room_number
            JOIN bookings b ON br.booking_id = b.booking_id
            JOIN payments p ON b.booking_id = p.booking_id
            WHERE r.hotel_id = ?
            GROUP BY rt.type_id, rt.type_name
            ORDER BY total_bookings DESC""";
        
        List<RoomTypeStats> stats = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, hotelId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                stats.add(new RoomTypeStats(
                    rs.getString("type_name"),
                    rs.getInt("total_bookings"),
                    rs.getBigDecimal("total_revenue"),
                    rs.getDouble("occupancy_rate")
                ));
            }
            return stats;
        } catch (SQLException e) {
            throw new RuntimeException("Error getting most booked room types", e);
        }
    }
} 