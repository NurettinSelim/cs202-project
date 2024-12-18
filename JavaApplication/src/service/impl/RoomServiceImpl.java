package service.impl;

import model.*;
import service.RoomService;
import util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RoomServiceImpl implements RoomService {

    @Override
    public void create(Room room) {
        String sql = """
                INSERT INTO rooms (hotel_id, room_number, type_id, status_id) VALUES (?, ?, ?, ?);
                """;
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, room.getHotel().getHotelId());
            stmt.setString(2, room.getRoomNumber());
            stmt.setInt(3, room.getRoomType().getTypeId());
            stmt.setInt(4, room.getStatus().getStatusId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create room", e);
        }
    }

    @Override
    public List<Room> findByHotel(Hotel hotel) {
        String sql = """
                SELECT r.*, rt.*, rs.status_name
                FROM rooms r
                JOIN room_types rt ON r.type_id = rt.type_id
                JOIN room_statuses rs ON r.status_id = rs.status_id
                WHERE r.hotel_id = ?;
                """;
        ArrayList<Room> rooms = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, hotel.getHotelId());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Room room = new Room();
                room.setRoomNumber(rs.getString("room_number"));
                
                // Set room type
                RoomType roomType = new RoomType();
                roomType.setTypeId(rs.getInt("type_id"));
                roomType.setTypeName(rs.getString("type_name"));
                roomType.setBasePrice(rs.getBigDecimal("base_price"));
                roomType.setCapacity(rs.getInt("capacity"));
                roomType.setBedCount(rs.getInt("bed_count"));
                room.setRoomType(roomType);
                
                // Set room status
                RoomStatus status = new RoomStatus();
                status.setStatusId(rs.getInt("status_id"));
                status.setStatusName(rs.getString("status_name"));
                room.setStatus(status);
                
                rooms.add(room);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find rooms by hotel", e);
        }
        return rooms;
    }

    @Override
    public void updateRoomStatus(Hotel hotel, String roomNumber, RoomStatus status) {
        String sql = "UPDATE rooms SET status_id = ? WHERE hotel_id = ? AND room_number = ?;";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, status.getStatusId());
            stmt.setInt(2, hotel.getHotelId());
            stmt.setString(3, roomNumber);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update room status", e);
        }
    }

    @Override
    public ArrayList<Room> getAvailableRooms(String checkInDate, String checkOutDate) {
        String sql = """
                SELECT
                    r.room_number,
                    r.hotel_id,
                    r.status_id,
                    rs.status_name,
                    rt.*
                FROM rooms r
                JOIN room_types rt ON r.type_id = rt.type_id
                JOIN room_statuses rs ON r.status_id = rs.status_id
                WHERE NOT EXISTS (
                    SELECT 1
                    FROM bookings b
                    JOIN booking_rooms br ON b.booking_id = br.booking_id
                    WHERE br.room_number = r.room_number
                    AND b.status_id IN (2, 3)  -- CONFIRMED or CHECKED_IN
                    AND b.check_in_date < ? -- check_out_date
                    AND b.check_out_date > ? -- check_in_date
                );
                """;
        ArrayList<Room> rooms = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, checkOutDate);
            stmt.setString(2, checkInDate);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Room room = new Room();
                room.setRoomNumber(rs.getString("room_number"));
                
                // Set hotel
                Hotel hotel = new Hotel();
                hotel.setHotelId(rs.getInt("hotel_id"));
                room.setHotel(hotel);
                
                // Set room type
                room.setRoomType(new RoomType(
                        rs.getInt("type_id"),
                        null,
                        rs.getString("type_name"),
                        rs.getBigDecimal("base_price"),
                        rs.getInt("capacity"),
                        rs.getInt("bed_count")));
                
                // Set room status
                RoomStatus status = new RoomStatus();
                status.setStatusId(rs.getInt("status_id"));
                status.setStatusName(rs.getString("status_name"));
                room.setStatus(status);
                
                rooms.add(room);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get available rooms", e);
        }
        return rooms;
    }

}