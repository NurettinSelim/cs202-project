package service.impl;

import model.*;
import service.HousekeepingScheduleService;
import util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class HousekeepingScheduleServiceImpl extends BaseServiceImpl<HousekeepingSchedule, Integer>
        implements HousekeepingScheduleService {

    @Override
    protected String getTableName() {
        return "housekeeping_schedule";
    }

    @Override
    protected String getIdColumnName() {
        return "schedule_id";
    }

    @Override
    protected HousekeepingSchedule mapRow(ResultSet rs) throws SQLException {
        HousekeepingSchedule schedule = new HousekeepingSchedule();
        schedule.setScheduleId(rs.getInt("schedule_id"));

        // Map hotel
        Hotel hotel = new Hotel();
        hotel.setHotelId(rs.getInt("hotel_id"));

        // Map room
        Room room = new Room();
        room.setHotel(hotel);
        room.setRoomNumber(rs.getString("room_number"));
        schedule.setRoom(room);

        // Map staff
        HousekeepingStaff staff = new HousekeepingStaff();
        staff.setUserId(rs.getInt("staff_id"));
        schedule.setStaff(staff);

        schedule.setScheduledDate(rs.getDate("scheduled_date"));

        // Map status
        RoomStatus status = new RoomStatus();
        status.setStatusId(rs.getInt("status_id"));
        schedule.setStatus(status);

        return schedule;
    }

    @Override
    protected void setCreateStatement(PreparedStatement stmt, HousekeepingSchedule schedule) throws SQLException {
        stmt.setInt(1, schedule.getRoom().getHotel().getHotelId());
        stmt.setString(2, schedule.getRoom().getRoomNumber());
        stmt.setInt(3, schedule.getStaff().getUserId());
        stmt.setDate(4, schedule.getScheduledDate());
        stmt.setInt(5, schedule.getStatus().getStatusId());
    }

    @Override
    protected void setUpdateStatement(PreparedStatement stmt, HousekeepingSchedule schedule) throws SQLException {
        stmt.setInt(1, schedule.getStaff().getUserId());
        stmt.setDate(2, schedule.getScheduledDate());
        stmt.setInt(3, schedule.getStatus().getStatusId());
        stmt.setInt(4, schedule.getScheduleId());
    }

    @Override
    public List<HousekeepingSchedule> findByHotel(Hotel hotel) {
        String sql = "SELECT hs.*, u.first_name, u.last_name, " +
                "hks.status_name FROM housekeeping_schedule hs " +
                "JOIN users u ON hs.staff_id = u.user_id " +
                "JOIN housekeeping_statuses hks ON hs.status_id = hks.status_id " +
                "WHERE hs.hotel_id = ? " +
                "ORDER BY hs.scheduled_date DESC";
        List<HousekeepingSchedule> schedules = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, hotel.getHotelId());
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                HousekeepingSchedule schedule = mapRow(rs);
                // Set additional properties from joins
                schedule.getStaff().setFirstName(rs.getString("first_name"));
                schedule.getStaff().setLastName(rs.getString("last_name"));
                schedule.getStatus().setStatusName(rs.getString("status_name"));
                schedules.add(schedule);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding schedules by hotel", e);
        }
        return schedules;
    }

    @Override
    public List<HousekeepingSchedule> findByStaff(HousekeepingStaff staff) {
        String sql = "SELECT hs.*, hks.status_name FROM housekeeping_schedule hs " +
                "JOIN housekeeping_statuses hks ON hs.status_id = hks.status_id " +
                "WHERE hs.staff_id = ? " +
                "ORDER BY hs.scheduled_date DESC";
        List<HousekeepingSchedule> schedules = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, staff.getUserId());
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                HousekeepingSchedule schedule = mapRow(rs);
                schedule.getStatus().setStatusName(rs.getString("status_name"));
                schedules.add(schedule);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding schedules by staff", e);
        }
        return schedules;
    }

    @Override
    public List<HousekeepingSchedule> findByDate(Hotel hotel, Date date) {
        String sql = "SELECT hs.*, u.first_name, u.last_name, " +
                "hks.status_name FROM housekeeping_schedule hs " +
                "JOIN users u ON hs.staff_id = u.user_id " +
                "JOIN housekeeping_statuses hks ON hs.status_id = hks.status_id " +
                "WHERE hs.hotel_id = ? AND hs.scheduled_date = ?";
        List<HousekeepingSchedule> schedules = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, hotel.getHotelId());
            stmt.setDate(2, date);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                HousekeepingSchedule schedule = mapRow(rs);
                schedule.getStaff().setFirstName(rs.getString("first_name"));
                schedule.getStaff().setLastName(rs.getString("last_name"));
                schedule.getStatus().setStatusName(rs.getString("status_name"));
                schedules.add(schedule);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding schedules by date", e);
        }
        return schedules;
    }

    @Override
    public List<HousekeepingSchedule> findByDateRange(Hotel hotel, Date startDate, Date endDate) {
        String sql = "SELECT hs.*, u.first_name, u.last_name, " +
                "hks.status_name FROM housekeeping_schedule hs " +
                "JOIN users u ON hs.staff_id = u.user_id " +
                "JOIN housekeeping_statuses hks ON hs.status_id = hks.status_id " +
                "WHERE hs.hotel_id = ? AND hs.scheduled_date BETWEEN ? AND ? " +
                "ORDER BY hs.scheduled_date";
        List<HousekeepingSchedule> schedules = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, hotel.getHotelId());
            stmt.setDate(2, startDate);
            stmt.setDate(3, endDate);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                HousekeepingSchedule schedule = mapRow(rs);
                schedule.getStaff().setFirstName(rs.getString("first_name"));
                schedule.getStaff().setLastName(rs.getString("last_name"));
                schedule.getStatus().setStatusName(rs.getString("status_name"));
                schedules.add(schedule);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding schedules by date range", e);
        }
        return schedules;
    }

    @Override
    public List<HousekeepingSchedule> findByRoom(Room room) {
        String sql = "SELECT hs.*, u.first_name, u.last_name, " +
                "hks.status_name FROM housekeeping_schedule hs " +
                "JOIN users u ON hs.staff_id = u.user_id " +
                "JOIN housekeeping_statuses hks ON hs.status_id = hks.status_id " +
                "WHERE hs.hotel_id = ? AND hs.room_number = ? " +
                "ORDER BY hs.scheduled_date DESC";
        List<HousekeepingSchedule> schedules = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, room.getHotel().getHotelId());
            stmt.setString(2, room.getRoomNumber());
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                HousekeepingSchedule schedule = mapRow(rs);
                schedule.getStaff().setFirstName(rs.getString("first_name"));
                schedule.getStaff().setLastName(rs.getString("last_name"));
                schedule.getStatus().setStatusName(rs.getString("status_name"));
                schedules.add(schedule);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding schedules by room", e);
        }
        return schedules;
    }

    @Override
    public void updateStatus(Integer scheduleId, RoomStatus newStatus) {
        String sql = "UPDATE housekeeping_schedule SET status_id = ? WHERE schedule_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, newStatus.getStatusId());
            stmt.setInt(2, scheduleId);

            int affected = stmt.executeUpdate();
            if (affected == 0) {
                throw new RuntimeException("Schedule not found");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error updating schedule status", e);
        }
    }

    @Override
    public void assignStaff(Integer scheduleId, HousekeepingStaff staff) {
        String sql = "UPDATE housekeeping_schedule SET staff_id = ? WHERE schedule_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, staff.getUserId());
            stmt.setInt(2, scheduleId);

            int affected = stmt.executeUpdate();
            if (affected == 0) {
                throw new RuntimeException("Schedule not found");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error assigning staff to schedule", e);
        }
    }

    @Override
    public List<HousekeepingSchedule> findPendingSchedules(Hotel hotel) {
        String sql = "SELECT hs.*, u.first_name, u.last_name, " +
                "hks.status_name FROM housekeeping_schedule hs " +
                "JOIN users u ON hs.staff_id = u.user_id " +
                "JOIN housekeeping_statuses hks ON hs.status_id = hks.status_id " +
                "WHERE hs.hotel_id = ? AND hs.status_id = 1 " + // 1 = PENDING
                "ORDER BY hs.scheduled_date";
        List<HousekeepingSchedule> schedules = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, hotel.getHotelId());
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                HousekeepingSchedule schedule = mapRow(rs);
                schedule.getStaff().setFirstName(rs.getString("first_name"));
                schedule.getStaff().setLastName(rs.getString("last_name"));
                schedule.getStatus().setStatusName(rs.getString("status_name"));
                schedules.add(schedule);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding pending schedules", e);
        }
        return schedules;
    }

    @Override
    public List<HousekeepingSchedule> findCompletedSchedules(Hotel hotel, Date startDate, Date endDate) {
        String sql = "SELECT hs.*, u.first_name, u.last_name, " +
                "hks.status_name FROM housekeeping_schedule hs " +
                "JOIN users u ON hs.staff_id = u.user_id " +
                "JOIN housekeeping_statuses hks ON hs.status_id = hks.status_id " +
                "WHERE hs.hotel_id = ? AND hs.status_id = 3 " + // 3 = COMPLETED
                "AND hs.scheduled_date BETWEEN ? AND ? " +
                "ORDER BY hs.scheduled_date DESC";
        List<HousekeepingSchedule> schedules = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, hotel.getHotelId());
            stmt.setDate(2, startDate);
            stmt.setDate(3, endDate);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                HousekeepingSchedule schedule = mapRow(rs);
                schedule.getStaff().setFirstName(rs.getString("first_name"));
                schedule.getStaff().setLastName(rs.getString("last_name"));
                schedule.getStatus().setStatusName(rs.getString("status_name"));
                schedules.add(schedule);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding completed schedules", e);
        }
        return schedules;
    }

    @Override
    public List<Room> findRoomsToClean(Hotel hotel, Date date) {
        String sql = "SELECT r.* FROM rooms r " +
                "LEFT JOIN housekeeping_schedule hs ON r.hotel_id = hs.hotel_id " +
                "AND r.room_number = hs.room_number " +
                "AND DATE(hs.scheduled_date) = ? " +
                "WHERE r.hotel_id = ? " +
                "AND (hs.schedule_id IS NULL OR hs.status_id != 3)"; // Not completed

        List<Room> rooms = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, date);
            stmt.setInt(2, hotel.getHotelId());
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Room room = new Room();
                room.setHotel(hotel);
                room.setRoomNumber(rs.getString("room_number"));
                room.setRoomType(new RoomType(rs.getInt("type_id")));
                room.setStatus(new RoomStatus(rs.getInt("status_id")));
                rooms.add(room);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding rooms to clean", e);
        }
        return rooms;
    }

    @Override
    public List<HousekeepingSchedule> findPendingTasks(Hotel hotel) {
        return findPendingSchedules(hotel); // Reuse existing implementation
    }

    @Override
    public void scheduleRoom(Room room, Date date, HousekeepingStaff staff) {
        HousekeepingSchedule schedule = new HousekeepingSchedule();
        schedule.setRoom(room);
        schedule.setScheduledDate(date);
        schedule.setStaff(staff);

        // Set initial status as PENDING (1)
        RoomStatus status = new RoomStatus();
        status.setStatusId(1);
        schedule.setStatus(status);

        create(schedule);
    }

    @Override
    public List<HousekeepingStaff> findAvailableStaff(Hotel hotel, Date date) {
        String sql = "SELECT DISTINCT u.*, s.hotel_id, s.salary, s.hire_date " +
                "FROM users u " +
                "JOIN staff s ON u.user_id = s.user_id " +
                "WHERE s.hotel_id = ? AND u.role = 'HOUSEKEEPING' " +
                "AND NOT EXISTS (" +
                "   SELECT 1 FROM housekeeping_schedule hs " +
                "   WHERE hs.staff_id = u.user_id " +
                "   AND hs.scheduled_date = ? " +
                "   AND hs.status_id IN (1, 2)" + // PENDING or IN_PROGRESS
                ")";

        List<HousekeepingStaff> staff = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, hotel.getHotelId());
            stmt.setDate(2, date);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                HousekeepingStaff housekeeper = new HousekeepingStaff();
                housekeeper.setUserId(rs.getInt("user_id"));
                housekeeper.setFirstName(rs.getString("first_name"));
                housekeeper.setLastName(rs.getString("last_name"));
                housekeeper.setHotel(hotel);
                housekeeper.setSalary(rs.getBigDecimal("salary"));
                housekeeper.setHireDate(rs.getDate("hire_date"));
                staff.add(housekeeper);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding available staff", e);
        }
        return staff;
    }

    @Override
    public int getCompletedTasksCount(HousekeepingStaff staff, Date startDate, Date endDate) {
        String sql = "SELECT COUNT(*) FROM housekeeping_schedule " +
                "WHERE staff_id = ? AND status_id = 3 " + // 3 = COMPLETED
                "AND scheduled_date BETWEEN ? AND ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, staff.getUserId());
            stmt.setDate(2, startDate);
            stmt.setDate(3, endDate);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error counting completed tasks", e);
        }
    }
}