package service.impl;

import java.sql.*;
import java.util.ArrayList;
import model.Hotel;
import model.HousekeepingSchedule;
import model.HousekeepingStaff;
import model.Room;
import model.RoomStatus;
import model.Staff;
import service.HousekeepingScheduleService;
import util.DatabaseConnection;

public class HousekeepingScheduleServiceImpl implements HousekeepingScheduleService {

    @Override
    public ArrayList<HousekeepingSchedule> findAll() {
        String sql = "SELECT * FROM housekeeping_schedule";

        ArrayList<HousekeepingSchedule> schedules = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                HousekeepingSchedule schedule = new HousekeepingSchedule();
                schedule.setScheduleId(rs.getInt("schedule_id"));
                schedule.setRoom(new Room(null, rs.getString("room_number"), null, null));
                schedule.setScheduledDate(rs.getDate("scheduled_date"));
                schedule.setStatus(new RoomStatus(rs.getInt("status_id")));
                schedule.setStaff(
                        new HousekeepingStaff(rs.getInt("staff_id"), null, null, null, null, null, null, null));
                schedules.add(schedule);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return schedules;
    }

    @Override
    public ArrayList<HousekeepingSchedule> findPendingSchedules(Hotel hotel) {
        ArrayList<HousekeepingSchedule> schedules = new ArrayList<>();
        String query = "SELECT * FROM housekeeping_schedule WHERE hotel_id = ? AND status_id = 1";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, hotel.getHotelId());
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                HousekeepingSchedule schedule = new HousekeepingSchedule();
                schedule.setScheduleId(rs.getInt("schedule_id"));
                schedule.setRoom(new Room(null, rs.getString("room_number"), null, null));
                schedule.setScheduledDate(rs.getDate("scheduled_date"));
                schedule.setStatus(new RoomStatus(rs.getInt("status_id")));
                schedule.setStaff(
                        new HousekeepingStaff(rs.getInt("staff_id"), null, null, null, null, null, null, null));
                schedules.add(schedule);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return schedules;
    }

    @Override
    public ArrayList<HousekeepingSchedule> findCompletedSchedules(Hotel hotel) {
        ArrayList<HousekeepingSchedule> schedules = new ArrayList<>();
        String query = "SELECT * FROM housekeeping_schedule WHERE hotel_id = ? AND status_id = 2";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, hotel.getHotelId());
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                HousekeepingSchedule schedule = new HousekeepingSchedule();
                schedule.setScheduleId(rs.getInt("schedule_id"));
                schedule.setRoom(new Room(null, rs.getString("room_number"), null, null));
                schedule.setScheduledDate(rs.getDate("scheduled_date"));
                schedule.setStatus(new RoomStatus(rs.getInt("status_id")));
                schedule.setStaff(
                        new HousekeepingStaff(rs.getInt("staff_id"), null, null, null, null, null, null, null));
                schedules.add(schedule);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return schedules;
    }

    @Override
    public void updateStatus(int scheduleId, RoomStatus status) {
        String query = "UPDATE housekeeping_schedule SET status_id = ? WHERE schedule_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, status.getStatusId());
            stmt.setInt(2, scheduleId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update status", e);
        }
    }

    @Override
    public ArrayList<HousekeepingSchedule> findByStaff(Staff staff) {
        ArrayList<HousekeepingSchedule> schedules = new ArrayList<>();
        String query = "SELECT * FROM housekeeping_schedule WHERE staff_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, staff.getUserId());
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                HousekeepingSchedule schedule = new HousekeepingSchedule();
                schedule.setScheduleId(rs.getInt("schedule_id"));
                schedule.setRoom(new Room(null, rs.getString("room_number"), null, null));
                schedule.setScheduledDate(rs.getDate("scheduled_date"));
                schedule.setStatus(new RoomStatus(rs.getInt("status_id")));
                schedule.setStaff(
                        new HousekeepingStaff(rs.getInt("staff_id"), null, null, null, null, null, null, null));
                schedules.add(schedule);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find schedules by staff", e);
        }
        return schedules;
    }

    @Override
    public void create(HousekeepingSchedule schedule) {
        String query = "INSERT INTO housekeeping_schedule (hotel_id, room_number, scheduled_date, staff_id, status_id, created_by) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, schedule.getRoom().getHotel().getHotelId());
            stmt.setString(2, schedule.getRoom().getRoomNumber());
            stmt.setDate(3, schedule.getScheduledDate());
            stmt.setInt(4, schedule.getStaff().getUserId());
            stmt.setInt(5, schedule.getStatus().getStatusId());
            stmt.setInt(6, schedule.getCreatedBy().getUserId());

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create housekeeping schedule", e);
        }
    }

    @Override
    public ArrayList<HousekeepingSchedule> findByHotel(Hotel hotel) {
        ArrayList<HousekeepingSchedule> schedules = new ArrayList<>();
        String query = """
                    SELECT hs.*,
                    u.first_name, u.last_name
                    FROM housekeeping_schedule hs
                    JOIN users u ON hs.staff_id = u.user_id
                    WHERE hs.hotel_id = ?
                """;

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, hotel.getHotelId());
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                HousekeepingSchedule schedule = new HousekeepingSchedule();
                schedule.setScheduleId(rs.getInt("schedule_id"));
                schedule.setRoom(new Room(null, rs.getString("room_number"), null, null));
                schedule.setScheduledDate(rs.getDate("scheduled_date"));
                schedule.setStatus(new RoomStatus(rs.getInt("status_id")));
                schedule.setStaff(
                        new HousekeepingStaff(rs.getInt("staff_id"), rs.getString("first_name"), rs.getString("last_name"), null, null, null, null, null));
                schedules.add(schedule);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find schedules by hotel", e);
        }
        return schedules;
    }

}