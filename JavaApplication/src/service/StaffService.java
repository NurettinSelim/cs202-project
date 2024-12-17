package service;

import model.Staff;
import model.Hotel;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;

public interface StaffService extends BaseService<Staff, Integer> {
    record StaffWithRole(String firstName, String lastName, String phone, String role, Date hireDate, BigDecimal salary) {}

    List<Staff> findByHotel(Hotel hotel);
    List<Staff> findByHotelAndRole(Hotel hotel, String role);
    List<Staff> findBySalaryRange(BigDecimal minSalary, BigDecimal maxSalary);
    List<Staff> findByHireDate(Date startDate, Date endDate);
    void updateSalary(Integer staffId, BigDecimal newSalary);
    void transferToHotel(Integer staffId, Integer newHotelId);
    List<StaffWithRole> findAllEmployeesWithRoles(int hotelId);
} 