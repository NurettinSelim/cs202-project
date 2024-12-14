package service.impl;

import model.*;
import service.BookingService;
import util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class BookingServiceImpl extends BaseServiceImpl<Booking, Integer> implements BookingService {

    @Override
    protected String getTableName() {
        return "bookings";
    }

    @Override
    protected String getIdColumnName() {
        return "booking_id";
    }

    @Override
    protected Booking mapRow(ResultSet rs) throws SQLException {
        Booking booking = new Booking();
        booking.setBookingId(rs.getInt("booking_id"));
        
        // Map guest
        Guest guest = new Guest();
        guest.setUserId(rs.getInt("guest_id"));
        booking.setGuest(guest);
        
        booking.setCheckInDate(rs.getDate("check_in_date"));
        booking.setCheckOutDate(rs.getDate("check_out_date"));
        
        // Map status
        BookingStatus status = new BookingStatus();
        status.setStatusId(rs.getInt("status_id"));
        booking.setStatus(status);
        
        booking.setTotalGuests(rs.getInt("total_guests"));
        booking.setCreatedAt(rs.getTimestamp("created_at"));
        
        // Map confirmed by staff if exists
        if (rs.getObject("confirmed_by") != null) {
            Staff confirmedBy = new Staff();
            confirmedBy.setUserId(rs.getInt("confirmed_by"));
            booking.setConfirmedBy(confirmedBy);
        }
        
        return booking;
    }

    @Override
    protected void setCreateStatement(PreparedStatement stmt, Booking booking) throws SQLException {
        stmt.setInt(1, booking.getGuest().getUserId());
        stmt.setDate(2, booking.getCheckInDate());
        stmt.setDate(3, booking.getCheckOutDate());
        stmt.setInt(4, booking.getStatus().getStatusId());
        stmt.setInt(5, booking.getTotalGuests());
        stmt.setTimestamp(6, booking.getCreatedAt());
        if (booking.getConfirmedBy() != null) {
            stmt.setInt(7, booking.getConfirmedBy().getUserId());
        } else {
            stmt.setNull(7, java.sql.Types.INTEGER);
        }
    }

    @Override
    protected void setUpdateStatement(PreparedStatement stmt, Booking booking) throws SQLException {
        stmt.setDate(1, booking.getCheckInDate());
        stmt.setDate(2, booking.getCheckOutDate());
        stmt.setInt(3, booking.getStatus().getStatusId());
        stmt.setInt(4, booking.getTotalGuests());
        if (booking.getConfirmedBy() != null) {
            stmt.setInt(5, booking.getConfirmedBy().getUserId());
        } else {
            stmt.setNull(5, java.sql.Types.INTEGER);
        }
        stmt.setInt(6, booking.getBookingId());
    }

    @Override
    public List<Booking> findByGuest(Guest guest) {
        String sql = "SELECT * FROM bookings WHERE guest_id = ? ORDER BY check_in_date DESC";
        List<Booking> bookings = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, guest.getUserId());
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                bookings.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding bookings by guest", e);
        }
        return bookings;
    }

    @Override
    public List<Booking> findByHotel(Hotel hotel) {
        String sql = "SELECT DISTINCT b.* FROM bookings b " +
                    "JOIN booking_rooms br ON b.booking_id = br.booking_id " +
                    "WHERE br.hotel_id = ? " +
                    "ORDER BY b.check_in_date DESC";
        List<Booking> bookings = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, hotel.getHotelId());
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                bookings.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding bookings by hotel", e);
        }
        return bookings;
    }

    @Override
    public List<Booking> findByStatus(Hotel hotel, BookingStatus status) {
        String sql = "SELECT DISTINCT b.* FROM bookings b " +
                    "JOIN booking_rooms br ON b.booking_id = br.booking_id " +
                    "WHERE br.hotel_id = ? AND b.status_id = ? " +
                    "ORDER BY b.check_in_date DESC";
        List<Booking> bookings = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, hotel.getHotelId());
            stmt.setInt(2, status.getStatusId());
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                bookings.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding bookings by status", e);
        }
        return bookings;
    }

    @Override
    public List<Booking> findByDateRange(Hotel hotel, Date startDate, Date endDate) {
        String sql = "SELECT DISTINCT b.* FROM bookings b " +
                    "JOIN booking_rooms br ON b.booking_id = br.booking_id " +
                    "WHERE br.hotel_id = ? " +
                    "AND b.check_in_date <= ? AND b.check_out_date >= ? " +
                    "ORDER BY b.check_in_date";
        List<Booking> bookings = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, hotel.getHotelId());
            stmt.setDate(2, endDate);
            stmt.setDate(3, startDate);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                bookings.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding bookings by date range", e);
        }
        return bookings;
    }

    @Override
    public List<Booking> findCurrentBookings(Hotel hotel) {
        String sql = "SELECT DISTINCT b.* FROM bookings b " +
                    "JOIN booking_rooms br ON b.booking_id = br.booking_id " +
                    "WHERE br.hotel_id = ? " +
                    "AND b.check_in_date <= CURRENT_DATE " +
                    "AND b.check_out_date >= CURRENT_DATE " +
                    "AND b.status_id IN (2, 3)"; // CONFIRMED or CHECKED_IN
        List<Booking> bookings = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, hotel.getHotelId());
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                bookings.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding current bookings", e);
        }
        return bookings;
    }

    @Override
    public void assignRoom(Integer bookingId, Room room, int guestsInRoom) {
        String sql = "INSERT INTO booking_rooms (booking_id, hotel_id, room_number, guests_in_room) " +
                    "VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, bookingId);
            stmt.setInt(2, room.getHotel().getHotelId());
            stmt.setString(3, room.getRoomNumber());
            stmt.setInt(4, guestsInRoom);
            
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error assigning room to booking", e);
        }
    }

    @Override
    public void updateStatus(Integer bookingId, BookingStatus newStatus) {
        String sql = "UPDATE bookings SET status_id = ? WHERE booking_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, newStatus.getStatusId());
            stmt.setInt(2, bookingId);
            
            int affected = stmt.executeUpdate();
            if (affected == 0) {
                throw new RuntimeException("Booking not found");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error updating booking status", e);
        }
    }

    @Override
    public void checkIn(Integer bookingId) {
        BookingStatus checkInStatus = new BookingStatus();
        checkInStatus.setStatusId(3); // CHECKED_IN
        updateStatus(bookingId, checkInStatus);
    }

    @Override
    public void checkOut(Integer bookingId) {
        BookingStatus checkOutStatus = new BookingStatus();
        checkOutStatus.setStatusId(4); // COMPLETED
        updateStatus(bookingId, checkOutStatus);
    }

    @Override
    public void cancel(Integer bookingId) {
        BookingStatus cancelStatus = new BookingStatus();
        cancelStatus.setStatusId(5); // CANCELLED
        updateStatus(bookingId, cancelStatus);
    }

    @Override
    public boolean canBeCancelled(Integer bookingId) {
        String sql = "SELECT COUNT(*) FROM payments WHERE booking_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, bookingId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) == 0;
            }
            return true;
        } catch (SQLException e) {
            throw new RuntimeException("Error checking if booking can be cancelled", e);
        }
    }

    @Override
    public double calculateTotalPrice(Integer bookingId) {
        String sql = "SELECT SUM(rt.base_price * DATEDIFF(b.check_out_date, b.check_in_date)) as total_price " +
                    "FROM bookings b " +
                    "JOIN booking_rooms br ON b.booking_id = br.booking_id " +
                    "JOIN rooms r ON br.hotel_id = r.hotel_id AND br.room_number = r.room_number " +
                    "JOIN room_types rt ON r.type_id = rt.type_id " +
                    "WHERE b.booking_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, bookingId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getDouble("total_price");
            }
            return 0.0;
        } catch (SQLException e) {
            throw new RuntimeException("Error calculating total price", e);
        }
    }

    @Override
    public List<Room> getAssignedRooms(Integer bookingId) {
        String sql = "SELECT r.* FROM rooms r " +
                    "JOIN booking_rooms br ON r.hotel_id = br.hotel_id " +
                    "AND r.room_number = br.room_number " +
                    "WHERE br.booking_id = ?";
        List<Room> rooms = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, bookingId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Room room = new Room();
                room.setHotel(new Hotel(rs.getInt("hotel_id")));
                room.setRoomNumber(rs.getString("room_number"));
                room.setRoomType(new RoomType(rs.getInt("type_id")));
                room.setStatus(new RoomStatus(rs.getInt("status_id")));
                rooms.add(room);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error getting assigned rooms", e);
        }
        return rooms;
    }
} 