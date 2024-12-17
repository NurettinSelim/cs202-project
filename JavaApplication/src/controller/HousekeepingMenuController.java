package controller;

import service.*;
import service.impl.*;
import model.*;
import java.sql.SQLException;
import java.util.List;
import java.sql.Date;

public class HousekeepingMenuController extends BaseControlller {
    private final HousekeepingScheduleService housekeepingService;
    private final UserService userService;

    public HousekeepingMenuController() {
        this.housekeepingService = new HousekeepingScheduleServiceImpl();
        this.userService = new UserServiceImpl();
    }

    public List<HousekeepingSchedule> viewPendingTasks() throws SQLException {
        int hotelId = userService.getCurrentHotelId();
        Hotel hotel = new Hotel();
        hotel.setHotelId(hotelId);
        return housekeepingService.findPendingSchedules(hotel);
    }

    public List<HousekeepingSchedule> viewCompletedTasks() throws SQLException {
        int hotelId = userService.getCurrentHotelId();
        Hotel hotel = new Hotel();
        hotel.setHotelId(hotelId);
        // Get completed tasks for the last 30 days
        java.util.Date today = new java.util.Date();
        Date endDate = new Date(today.getTime());
        Date startDate = new Date(today.getTime() - 30L * 24 * 60 * 60 * 1000);
        return housekeepingService.findCompletedSchedules(hotel, startDate, endDate);
    }

    public void updateTaskStatus(int scheduleId) throws SQLException {
        RoomStatus completedStatus = new RoomStatus();
        completedStatus.setStatusId(3); // Assuming 3 is COMPLETED status
        housekeepingService.updateStatus(scheduleId, completedStatus);
    }

    public List<HousekeepingSchedule> viewMySchedule() throws SQLException {
        User currentUser = userService.getCurrentUser();
        HousekeepingStaff staff = new HousekeepingStaff();
        staff.setUserId(currentUser.getUserId());
        return housekeepingService.findByStaff(staff);
    }
} 