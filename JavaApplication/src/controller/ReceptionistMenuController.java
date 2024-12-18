package controller;

import service.*;
import service.impl.*;
import util.DatabaseConnection;
import model.*;
import java.sql.SQLException;
import java.util.List;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.HashMap;
import java.util.Map;

public class ReceptionistMenuController extends BaseControlller {
    private final BookingService bookingService;
    private final UserService userService;
    private final RoomService roomService;
    private final PaymentService paymentService;
    private final HousekeepingScheduleService housekeepingService;

    public ReceptionistMenuController() {
        this.bookingService = new BookingServiceImpl();
        this.userService = new UserServiceImpl();
        this.roomService = new RoomServiceImpl();
        this.paymentService = new PaymentServiceImpl();
        this.housekeepingService = new HousekeepingScheduleServiceImpl();
    }

    public List<Room> getAvailableRooms(String checkInDate, String checkOutDate) throws SQLException {
        return roomService.getAvailableRooms(checkInDate, checkOutDate);
    }

    public List<User> searchGuests(String searchTerm) throws SQLException {
        String sql = "SELECT * FROM users u JOIN guests g ON u.user_id = g.user_id " +
                "WHERE u.first_name LIKE ? OR u.last_name LIKE ? OR u.phone LIKE ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            String term = "%" + searchTerm + "%";
            stmt.setString(1, term);
            stmt.setString(2, term);
            stmt.setString(3, term);
            ResultSet rs = stmt.executeQuery();
            List<User> guests = new ArrayList<>();
            while (rs.next()) {
                User guest = new User();
                guest.setUserId(rs.getInt("user_id"));
                guest.setFirstName(rs.getString("first_name"));
                guest.setLastName(rs.getString("last_name"));
                guest.setPhone(rs.getString("phone"));
                guests.add(guest);
            }
            return guests;
        }
    }

    public void addBooking(int guestId, String checkInDate, String checkOutDate,
            int totalGuests, Map<String, Integer> roomGuestCounts) throws SQLException {
        // Create map of rooms and guests per room
        HashMap<Room, Integer> rooms = new HashMap<>();
        int hotelId = userService.getCurrentHotelId();
        
        // Create Room objects with their assigned guest counts
        for (Map.Entry<String, Integer> entry : roomGuestCounts.entrySet()) {
            Room room = new Room();
            room.setRoomNumber(entry.getKey());
            Hotel hotel = new Hotel();
            hotel.setHotelId(hotelId);
            room.setHotel(hotel);
            rooms.put(room, entry.getValue());
        }

        bookingService.addNewBooking(guestId, checkInDate, checkOutDate, 1, totalGuests, rooms);
    }

    public void modifyBooking(int bookingId, String checkInDate, String checkOutDate,
            int totalGuests, int statusId) throws SQLException {
        Booking booking = bookingService.findById(bookingId);
        if (booking == null) {
            throw new SQLException("Booking not found");
        }

        booking.setCheckInDate(Date.valueOf(checkInDate));
        booking.setCheckOutDate(Date.valueOf(checkOutDate));
        booking.setTotalGuests(totalGuests);
        
        BookingStatus newStatus = new BookingStatus();
        newStatus.setStatusId(statusId);
        booking.setStatus(newStatus);

        bookingService.updateBooking(booking);
    }

    public void deleteBooking(int bookingId) throws SQLException {
        bookingService.cancelBooking(bookingId);
    }

    public List<Booking> viewAllBookings() throws SQLException {
        return bookingService.findAllWithGuest();
    }

    public void processPayment(int bookingId, BigDecimal amount, String paymentMethod) throws SQLException {
        Payment payment = new Payment();
        payment.setBookingId(bookingId);
        payment.setPaymentNumber(1);
        payment.setAmount(amount);
        payment.setProcessedBy(userService.getCurrentUser().getUserId());
        paymentService.processPayment(payment);
        bookingService.processPayment(bookingId);
    }

    public void assignHousekeepingTask(String roomNumber, int staffId, String scheduledDate) throws SQLException {
        HousekeepingSchedule schedule = new HousekeepingSchedule();

        Room room = new Room();
        Hotel hotel = new Hotel();
        hotel.setHotelId(userService.getCurrentHotelId());
        room.setHotel(hotel);
        room.setRoomNumber(roomNumber);
        schedule.setRoom(room);

        HousekeepingStaff staff = new HousekeepingStaff();
        staff.setUserId(staffId);
        schedule.setStaff(staff);

        schedule.setScheduledDate(Date.valueOf(scheduledDate));

        RoomStatus status = new RoomStatus();
        status.setStatusId(1); 
        schedule.setStatus(status);

        Staff createdBy = new Staff();
        createdBy.setUserId(userService.getCurrentUser().getUserId());
        schedule.setCreatedBy(createdBy);

        housekeepingService.create(schedule);
    }

    public List<HousekeepingStaff> getAvailableHousekeepers(String date) throws SQLException {
        int hotelId = userService.getCurrentHotelId();
        Hotel hotel = new Hotel();
        hotel.setHotelId(hotelId);
        return new ArrayList<>();
        // return staffService.findAvailableHousekeepers(hotelId, Date.valueOf(date));
    }

    public List<HousekeepingSchedule> viewHousekeepingRecords() throws SQLException {
        int hotelId = userService.getCurrentHotelId();
        Hotel hotel = new Hotel();
        hotel.setHotelId(hotelId);
        return housekeepingService.findByHotel(hotel);
    }

    public List<Room> getRooms() throws SQLException {
        int hotelId = userService.getCurrentHotelId();
        Hotel hotel = new Hotel();
        hotel.setHotelId(hotelId);
        return roomService.findByHotel(hotel);
    }

    public List<BookingStatus> getBookingStatuses() throws SQLException {
        String sql = "SELECT * FROM booking_statuses ORDER BY status_id";
        List<BookingStatus> statuses = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                BookingStatus status = new BookingStatus();
                status.setStatusId(rs.getInt("status_id"));
                status.setStatusName(rs.getString("status_name"));
                statuses.add(status);
            }
            return statuses;
        }
    }

}