package service;

import model.AdministratorStaff;
import model.Staff;
import model.Hotel;
import java.util.List;

public interface AdministratorStaffService extends BaseService<AdministratorStaff, Integer> {
    List<Staff> findManagedStaff(Integer adminId);
    List<Staff> findManagedStaffByHotel(Integer adminId, Integer hotelId);
    void assignStaffToHotel(Integer staffId, Integer hotelId);
    void updateStaffRole(Integer staffId, String newRole);
    List<Hotel> findManagedHotels(Integer adminId);
    boolean canManageHotel(Integer adminId, Integer hotelId);
} 