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
        return housekeepingService.findCompletedSchedules(hotel);
    }

    public void updateTaskStatus(int scheduleId) throws SQLException {
        RoomStatus completedStatus = new RoomStatus();
        completedStatus.setStatusId(2);
        housekeepingService.updateStatus(scheduleId, completedStatus);
    }

    public List<HousekeepingSchedule> viewMySchedule() throws SQLException {
        User currentUser = userService.getCurrentUser();
        HousekeepingStaff staff = new HousekeepingStaff();
        staff.setUserId(currentUser.getUserId());
        return housekeepingService.findByStaff(staff);
    }
} 