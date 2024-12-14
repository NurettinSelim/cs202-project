package service;

import model.HousekeepingSchedule;
import model.HousekeepingStaff;
import model.Room;
import model.Hotel;
import model.RoomStatus;
import java.sql.Date;
import java.util.List;

public interface HousekeepingScheduleService extends BaseService<HousekeepingSchedule, Integer> {
    List<HousekeepingSchedule> findByHotel(Hotel hotel);
    List<HousekeepingSchedule> findByStaff(HousekeepingStaff staff);
    List<HousekeepingSchedule> findByDate(Hotel hotel, Date date);
    List<HousekeepingSchedule> findByRoom(Room room);
    List<HousekeepingSchedule> findPendingTasks(Hotel hotel);
    void assignStaff(Integer scheduleId, HousekeepingStaff staff);
    void updateStatus(Integer scheduleId, RoomStatus newStatus);
    void scheduleRoom(Room room, Date date, HousekeepingStaff staff);
    List<HousekeepingStaff> findAvailableStaff(Hotel hotel, Date date);
    int getCompletedTasksCount(HousekeepingStaff staff, Date startDate, Date endDate);
    List<Room> findRoomsToClean(Hotel hotel, Date date);
    List<HousekeepingSchedule> findByDateRange(Hotel hotel, Date startDate, Date endDate);
    List<HousekeepingSchedule> findPendingSchedules(Hotel hotel);
    List<HousekeepingSchedule> findCompletedSchedules(Hotel hotel, Date startDate, Date endDate);
} 