package service;

import model.*;
import java.sql.Date;
import java.util.ArrayList;

public interface HousekeepingScheduleService {
    ArrayList<HousekeepingSchedule> findAll();

    ArrayList<HousekeepingSchedule> findPendingSchedules(Hotel hotel);

    ArrayList<HousekeepingSchedule> findCompletedSchedules(Hotel hotel, Date startDate, Date endDate);

    void updateStatus(int scheduleId, RoomStatus status);

    ArrayList<HousekeepingSchedule> findByStaff(Staff staff);

    void create(HousekeepingSchedule schedule);

    ArrayList<HousekeepingSchedule> findByHotel(Hotel hotel);
}