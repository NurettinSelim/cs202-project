package service.impl;

import model.*;
import service.BookingService;
import util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

public class BookingServiceImpl implements BookingService {

    public Booking findById(int bookingId) {
        String sql = "SELECT * FROM bookings WHERE booking_id = ?";
        Booking booking = null;
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, bookingId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                booking = new Booking();
                booking.setBookingId(rs.getInt("booking_id"));
                booking.setCheckInDate(rs.getDate("check_in_date"));
                booking.setCheckOutDate(rs.getDate("check_out_date"));
                booking.setTotalGuests(rs.getInt("total_guests"));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding booking by id", e);
        }
        return booking;
    }

    public void updateBooking(Booking booking) {
        String sql = """
                UPDATE bookings
                SET check_in_date = ?,
                    check_out_date = ?,
                    total_guests = ?,
                    status_id = ?
                WHERE booking_id = ?;
                """;
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, booking.getCheckInDate().toString());
            stmt.setString(2, booking.getCheckOutDate().toString());
            stmt.setInt(3, booking.getTotalGuests());
            stmt.setInt(4, booking.getStatus().getStatusId());
            stmt.setInt(5, booking.getBookingId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating booking", e);
        }
    }

    /**
     * Find bookings by guest id
     */
    public ArrayList<Booking> findByGuestId(int guestId) {
        String sql = """
                SELECT b.*,
                    u.first_name, u.last_name,
                    bs.status_name
                FROM bookings b
                        JOIN users u ON b.guest_id = u.user_id
                        JOIN booking_statuses bs ON b.status_id = bs.status_id
                WHERE b.guest_id = ?;
                """;
        ArrayList<Booking> bookings = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, guestId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Booking booking = new Booking();
                booking.setBookingId(rs.getInt("booking_id"));
                booking.setCheckInDate(rs.getDate("check_in_date"));
                booking.setCheckOutDate(rs.getDate("check_out_date"));
                booking.setTotalGuests(rs.getInt("total_guests"));
                BookingStatus status = new BookingStatus();
                status.setStatusId(rs.getInt("status_id"));
                status.setStatusName(rs.getString("status_name"));
                booking.setStatus(status);
                Guest guest = new Guest();
                guest.setUserId(rs.getInt("guest_id"));
                guest.setFirstName(rs.getString("first_name"));
                guest.setLastName(rs.getString("last_name"));
                booking.setGuest(guest);
                bookings.add(booking);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding bookings by guest id", e);
        }
        return bookings;
    }

    /**
     * find all bookings with guest
     */
    public ArrayList<Booking> findAllWithGuest() {
        String sql = """
                SELECT b.*, u.*, bs.status_name
                FROM bookings b
                JOIN users u ON b.guest_id = u.user_id
                JOIN booking_statuses bs ON b.status_id = bs.status_id
                """;
        ArrayList<Booking> bookings = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Booking booking = new Booking();
                booking.setBookingId(rs.getInt("booking_id"));
                booking.setCheckInDate(rs.getDate("check_in_date"));
                booking.setCheckOutDate(rs.getDate("check_out_date"));
                booking.setTotalGuests(rs.getInt("total_guests"));
                BookingStatus status = new BookingStatus();
                status.setStatusId(rs.getInt("status_id"));
                status.setStatusName(rs.getString("status_name"));
                booking.setStatus(status);
                Guest guest = new Guest();
                guest.setUserId(rs.getInt("user_id"));
                guest.setFirstName(rs.getString("first_name"));
                guest.setLastName(rs.getString("last_name"));
                booking.setGuest(guest);
                bookings.add(booking);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding all bookings with guest", e);
        }
        return bookings;
    }

    /**
     * cancel booking
     */
    public void cancelBooking(int bookingId) {
        String sql = "UPDATE bookings SET status_id = 5 WHERE booking_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, bookingId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error cancelling booking", e);
        }
    }

    /**
     * will be called in payment service
     * 
     * @param bookingId
     */
    public void processPayment(int bookingId) {
        String sql = "UPDATE bookings SET status_id = 4 WHERE booking_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, bookingId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error processing payment", e);
        }
    }

    /**
     * get most booked room types
     */
    public ArrayList<RoomTypeStats> getMostBookedRoomTypes(int hotelId, String checkInDate, String checkOutDate) {
        String sql = """
                SELECT rt.type_name, COUNT(*) as booking_count
                FROM bookings b
                JOIN booking_rooms br ON b.booking_id = br.booking_id
                JOIN rooms r ON br.hotel_id = r.hotel_id AND br.room_number = r.room_number
                JOIN room_types rt ON r.type_id = rt.type_id
                WHERE b.check_in_date BETWEEN ? AND ?
                AND h.hotel_id = ?
                GROUP BY rt.type_id
                ORDER BY booking_count DESC;
                """;
        ArrayList<RoomTypeStats> roomTypeStats = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, checkInDate);
            stmt.setString(2, checkOutDate);
            stmt.setInt(3, hotelId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                roomTypeStats.add(new RoomTypeStats(rs.getString("type_name"), rs.getInt("booking_count")));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error getting most booked room types", e);
        }
        return roomTypeStats;
    }

    public void addNewBooking(int guestId, String checkInDate, String checkOutDate, int statusId, int totalGuests,
            HashMap<Room, Integer> rooms) {
        int bookingId = createBooking(guestId, checkInDate, checkOutDate, statusId, totalGuests);

        for (Room room : rooms.keySet()) {
            addRoomToBooking(bookingId, room.getHotel().getHotelId(), room.getRoomNumber(), rooms.get(room));
        }
    }

    private int createBooking(int guestId, String checkInDate, String checkOutDate, int statusId, int totalGuests) {
        String sql = """
                INSERT INTO bookings (
                    guest_id, check_in_date,
                    check_out_date, status_id, total_guests
                ) VALUES (?, ?, ?, ?, ?);
                """;
        int bookingId = 0;
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, guestId);
            stmt.setString(2, checkInDate);
            stmt.setString(3, checkOutDate);
            stmt.setInt(4, statusId);
            stmt.setInt(5, totalGuests);
            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                bookingId = rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error adding new booking", e);
        }
        return bookingId;

    }

    private void addRoomToBooking(int bookingId, int hotelId, String roomNumber, int guestsInRoom) {
        String sql = """
                INSERT INTO booking_rooms (
                    booking_id, hotel_id, room_number, guests_in_room
                ) VALUES (?, ?, ?, ?);
                """;
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, bookingId);
            stmt.setInt(2, hotelId);
            stmt.setString(3, roomNumber);
            stmt.setInt(4, guestsInRoom);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error adding room to booking", e);
        }

    }
}
