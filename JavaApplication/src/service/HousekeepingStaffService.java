package service;

import model.HousekeepingStaff;
import model.Hotel;
import model.HousekeepingSchedule;
import java.sql.Date;
import java.util.List;

public interface HousekeepingStaffService extends BaseService<HousekeepingStaff, Integer> {
    List<HousekeepingSchedule> findSchedulesByStaff(Integer staffId);
    List<HousekeepingSchedule> findSchedulesByStaffAndDate(Integer staffId, Date date);
    List<HousekeepingStaff> findAvailableStaff(Hotel hotel, Date date);
    int getCompletedTasksCount(Integer staffId, Date startDate, Date endDate);
    double getAverageTasksPerDay(Integer staffId, Date startDate, Date endDate);
} 