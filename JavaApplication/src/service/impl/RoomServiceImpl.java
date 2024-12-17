package service.impl;

import model.*;
import service.RoomService;
import util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class RoomServiceImpl extends BaseServiceImpl<Room, String> implements RoomService {

    @Override
    protected String getTableName() {
        return "rooms";
    }

    @Override
    protected String getIdColumnName() {
        return "room_number"; // Combined with hotel_id for actual primary key
    }

    @Override
    protected Room mapRow(ResultSet rs) throws SQLException {
        Room room = new Room();
        
        // Map the hotel
        Hotel hotel = new Hotel();
        hotel.setHotelId(rs.getInt("hotel_id"));
        room.setHotel(hotel);

        room.setRoomNumber(rs.getString("room_number"));

        // Map the room type
        RoomType roomType = new RoomType();
        roomType.setTypeId(rs.getInt("type_id"));
        room.setRoomType(roomType);

        // Map the room status
        RoomStatus status = new RoomStatus();
        status.setStatusId(rs.getInt("status_id"));
        room.setStatus(status);

        return room;
    }

    @Override
    protected String getCreateSQL() {
        return String.format("INSERT INTO %s (hotel_id, room_number, type_id, status_id) VALUES (?, ?, ?, ?)", getTableName());
    }

    @Override
    protected void setCreateStatement(PreparedStatement stmt, Room room) throws SQLException {
        stmt.setInt(1, room.getHotel().getHotelId());
        stmt.setString(2, room.getRoomNumber());
        stmt.setInt(3, room.getRoomType().getTypeId());
        stmt.setInt(4, room.getStatus().getStatusId());
    }

    @Override
    protected void setUpdateStatement(PreparedStatement stmt, Room room) throws SQLException {
        stmt.setInt(1, room.getRoomType().getTypeId());
        stmt.setInt(2, room.getStatus().getStatusId());
        stmt.setInt(3, room.getHotel().getHotelId());
        stmt.setString(4, room.getRoomNumber());
    }

    @Override
    public List<Room> findByHotel(Hotel hotel) {
        String sql = "SELECT r.*, rt.type_name, rt.base_price, rt.capacity, rt.bed_count, " +
                "rs.status_name FROM rooms r " +
                "JOIN room_types rt ON r.type_id = rt.type_id " +
                "JOIN room_statuses rs ON r.status_id = rs.status_id " +
                "WHERE r.hotel_id = ?";
        List<Room> rooms = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, hotel.getHotelId());
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Room room = mapRow(rs);
                // Set additional properties from joins
                room.getRoomType().setTypeName(rs.getString("type_name"));
                room.getRoomType().setBasePrice(rs.getBigDecimal("base_price"));
                room.getRoomType().setCapacity(rs.getInt("capacity"));
                room.getRoomType().setBedCount(rs.getInt("bed_count"));
                room.getStatus().setStatusName(rs.getString("status_name"));
                rooms.add(room);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding rooms by hotel", e);
        }
        return rooms;
    }

    @Override
    public List<Room> findByType(Hotel hotel, RoomType type) {
        String sql = "SELECT * FROM rooms WHERE hotel_id = ? AND type_id = ?";
        List<Room> rooms = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, hotel.getHotelId());
            stmt.setInt(2, type.getTypeId());
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                rooms.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding rooms by type", e);
        }
        return rooms;
    }

    @Override
    public List<Room> findByStatus(Hotel hotel, RoomStatus status) {
        String sql = "SELECT * FROM rooms WHERE hotel_id = ? AND status_id = ?";
        List<Room> rooms = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, hotel.getHotelId());
            stmt.setInt(2, status.getStatusId());
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                rooms.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding rooms by status", e);
        }
        return rooms;
    }

    @Override
    public List<Room> findAvailableRooms(Hotel hotel, Date checkIn, Date checkOut) {
        String sql = "SELECT r.* FROM rooms r " +
                "WHERE r.hotel_id = ? AND r.status_id = 1 " + // 1 = AVAILABLE
                "AND NOT EXISTS (" +
                "   SELECT 1 FROM booking_rooms br " +
                "   JOIN bookings b ON br.booking_id = b.booking_id " +
                "   WHERE br.hotel_id = r.hotel_id " +
                "   AND br.room_number = r.room_number " +
                "   AND b.status_id IN (2, 3) " + // 2 = CONFIRMED, 3 = CHECKED_IN
                "   AND NOT (b.check_out_date <= ? OR b.check_in_date >= ?)" +
                ")";

        List<Room> rooms = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, hotel.getHotelId());
            stmt.setDate(2, checkIn);
            stmt.setDate(3, checkOut);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                rooms.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding available rooms", e);
        }
        return rooms;
    }

    @Override
    public List<Room> findAvailableRoomsByType(Hotel hotel, RoomType type, Date checkIn, Date checkOut) {
        String sql = "SELECT r.* FROM rooms r " +
                "WHERE r.hotel_id = ? AND r.type_id = ? AND r.status_id = 1 " +
                "AND NOT EXISTS (" +
                "   SELECT 1 FROM booking_rooms br " +
                "   JOIN bookings b ON br.booking_id = b.booking_id " +
                "   WHERE br.hotel_id = r.hotel_id " +
                "   AND br.room_number = r.room_number " +
                "   AND b.status_id IN (2, 3) " +
                "   AND NOT (b.check_out_date <= ? OR b.check_in_date >= ?)" +
                ")";

        List<Room> rooms = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, hotel.getHotelId());
            stmt.setInt(2, type.getTypeId());
            stmt.setDate(3, checkIn);
            stmt.setDate(4, checkOut);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                rooms.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding available rooms by type", e);
        }
        return rooms;
    }

    @Override
    public boolean isRoomAvailable(Hotel hotel, String roomNumber, Date checkIn, Date checkOut) {
        String sql = "SELECT COUNT(*) FROM rooms r " +
                "WHERE r.hotel_id = ? AND r.room_number = ? AND r.status_id = 1 " +
                "AND NOT EXISTS (" +
                "   SELECT 1 FROM booking_rooms br " +
                "   JOIN bookings b ON br.booking_id = b.booking_id " +
                "   WHERE br.hotel_id = r.hotel_id " +
                "   AND br.room_number = r.room_number " +
                "   AND b.status_id IN (2, 3) " +
                "   AND NOT (b.check_out_date <= ? OR b.check_in_date >= ?)" +
                ")";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, hotel.getHotelId());
            stmt.setString(2, roomNumber);
            stmt.setDate(3, checkIn);
            stmt.setDate(4, checkOut);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            return false;
        } catch (SQLException e) {
            throw new RuntimeException("Error checking room availability", e);
        }
    }

    @Override
    public void updateRoomStatus(Hotel hotel, String roomNumber, RoomStatus newStatus) {
        String sql = "UPDATE rooms SET status_id = ? WHERE hotel_id = ? AND room_number = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, newStatus.getStatusId());
            stmt.setInt(2, hotel.getHotelId());
            stmt.setString(3, roomNumber);

            int affected = stmt.executeUpdate();
            if (affected == 0) {
                throw new RuntimeException("Room not found");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error updating room status", e);
        }
    }

    @Override
    public double getOccupancyRate(Hotel hotel, Date startDate, Date endDate) {
        String sql = "SELECT " +
                "COUNT(DISTINCT br.room_number) as booked_rooms, " +
                "(SELECT COUNT(*) FROM rooms WHERE hotel_id = ?) as total_rooms " +
                "FROM rooms r " +
                "LEFT JOIN booking_rooms br ON r.hotel_id = br.hotel_id AND r.room_number = br.room_number " +
                "LEFT JOIN bookings b ON br.booking_id = b.booking_id " +
                "WHERE r.hotel_id = ? " +
                "AND b.status_id IN (2, 3) " +
                "AND b.check_in_date <= ? AND b.check_out_date >= ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, hotel.getHotelId());
            stmt.setInt(2, hotel.getHotelId());
            stmt.setDate(3, endDate);
            stmt.setDate(4, startDate);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int bookedRooms = rs.getInt("booked_rooms");
                int totalRooms = rs.getInt("total_rooms");
                return totalRooms > 0 ? (double) bookedRooms / totalRooms : 0;
            }
            return 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error calculating occupancy rate", e);
        }
    }

    @Override
    public List<Room> findRoomsNeedingCleaning(Hotel hotel) {
        String sql = "SELECT r.* FROM rooms r " +
                "LEFT JOIN housekeeping_schedule hs ON r.hotel_id = hs.hotel_id " +
                "AND r.room_number = hs.room_number " +
                "WHERE r.hotel_id = ? " +
                "AND (hs.schedule_id IS NULL OR hs.scheduled_date < CURRENT_DATE) " +
                "AND r.status_id = 1";

        List<Room> rooms = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, hotel.getHotelId());
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                rooms.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding rooms needing cleaning", e);
        }
        return rooms;
    }

    @Override
    public ArrayList<Room> getAvailableRooms(String checkInDate, String checkOutDate) {
        String sql = """
                SELECT
                    r.*,
                    rt.type_name,
                    rt.base_price,
                    rt.capacity,
                    rs.status_name
                FROM rooms r
                JOIN room_types rt ON r.type_id = rt.type_id
                JOIN room_statuses rs ON r.status_id = rs.status_id
                WHERE NOT EXISTS (
                    SELECT 1
                    FROM bookings b
                    JOIN booking_rooms br ON b.booking_id = br.booking_id
                    WHERE br.room_number = r.room_number
                    AND b.status_id IN (2, 3)  -- CONFIRMED or CHECKED_IN
                    AND r.status_id = 1 -- AVAILABLE
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
                Room room = mapRow(rs);
                room.getRoomType().setCapacity(rs.getInt("capacity"));
                room.getRoomType().setTypeName(rs.getString("type_name"));
                room.getRoomType().setBasePrice(rs.getBigDecimal("base_price"));
                room.getStatus().setStatusName(rs.getString("status_name"));
                rooms.add(room);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding available rooms", e);
        }
        return rooms;
    }
}