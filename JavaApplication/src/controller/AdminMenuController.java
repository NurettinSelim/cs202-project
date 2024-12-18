package controller;

import service.*;
import service.impl.*;
import util.DatabaseConnection;
import model.*;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

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
            "10. View All Employees");

    private final RoomService roomService;
    private final UserService userService;
    private final BookingService bookingService;
    private final HousekeepingScheduleService housekeepingService;
    private final StaffService staffService;
    private final RoomTypeService roomTypeService;
    private final HotelService hotelService;

    public AdminMenuController() {
        this.roomService = new RoomServiceImpl();
        this.userService = new UserServiceImpl();
        this.bookingService = new BookingServiceImpl();
        this.housekeepingService = new HousekeepingScheduleServiceImpl();
        this.staffService = new StaffServiceImpl();
        this.roomTypeService = new RoomTypeServiceImpl();
        this.hotelService = new HotelServiceImpl();
    }

    private int getCurrentUserHotelId() throws SQLException {
        User currentUser = getCurrentUser();
        String sql = "SELECT hotel_id FROM staff WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, currentUser.getUserId());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("hotel_id");
            }
            throw new SQLException("Current user is not associated with any hotel");
        }
    }

    public ArrayList<RoomType> getRoomTypes() throws SQLException {
        int hotelId = getCurrentUserHotelId();
        return roomTypeService.findByHotel(hotelId);
    }

    public List<RoomStatus> getRoomStatuses() throws SQLException {
        String sql = "SELECT * FROM room_statuses";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            List<RoomStatus> statuses = new ArrayList<>();
            while (rs.next()) {
                RoomStatus status = new RoomStatus();
                status.setStatusId(rs.getInt("status_id"));
                status.setStatusName(rs.getString("status_name"));
                statuses.add(status);
            }
            return statuses;
        }
    }

    public void addRoom(int roomNumber, int typeId, int statusId) throws SQLException {
        int hotelId = getCurrentUserHotelId();
        Room room = new Room();
        Hotel hotel = new Hotel();
        hotel.setHotelId(hotelId);
        room.setHotel(hotel);
        room.setRoomNumber(String.valueOf(roomNumber));

        RoomType roomType = new RoomType();
        roomType.setTypeId(typeId);
        room.setRoomType(roomType);

        RoomStatus status = new RoomStatus();
        status.setStatusId(statusId);
        room.setStatus(status);

        roomService.create(room);
    }

    public List<Room> getRooms() throws SQLException {
        int hotelId = getCurrentUserHotelId();
        Hotel hotel = new Hotel();
        hotel.setHotelId(hotelId);
        return roomService.findByHotel(hotel);
    }

    public void deleteRoom(int roomNumber) throws SQLException {
        int hotelId = getCurrentUserHotelId();
        String sql = "DELETE FROM rooms WHERE hotel_id = ? AND room_number = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, hotelId);
            stmt.setInt(2, roomNumber);
            stmt.executeUpdate();
        }
    }

    public void manageRoomStatus(int roomNumber, int statusId) throws SQLException {
        int hotelId = getCurrentUserHotelId();
        Hotel hotel = new Hotel();
        hotel.setHotelId(hotelId);
        RoomStatus status = new RoomStatus();
        status.setStatusId(statusId);
        roomService.updateRoomStatus(hotel, String.valueOf(roomNumber), status);
    }

    public void addUser(String firstName, String lastName, String phone) throws SQLException {
        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setPhone(phone);
        userService.create(user);
    }

    public List<User> viewUserAccounts() throws SQLException {
        return userService.findAll();
    }

    public HotelService.Revenue generateRevenueReport() throws SQLException {
        int hotelId = getCurrentUserHotelId();
        return hotelService.getRevenue(hotelId);
    }

    public List<Booking> viewBookingRecords() throws SQLException {
        return bookingService.findAllWithGuest();
    }

    public List<HousekeepingSchedule> viewHousekeepingRecords() throws SQLException {
        return housekeepingService.findAll();
    }

    public List<BookingService.RoomTypeStats> viewMostBookedRoomTypes() throws SQLException {
        int hotelId = getCurrentUserHotelId();
        // TODO: get check in date and check out date from user
        return bookingService.getMostBookedRoomTypes(hotelId, null, null);
    }

    public HashMap<Staff, String> viewAllEmployees() throws SQLException {
        int hotelId = getCurrentUserHotelId();
        return staffService.findAllWithRole(hotelId);
    }

    public void displayMenu() {
        super.displayMenu(menuItems);
    }
}
