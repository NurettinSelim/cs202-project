package service;

import model.Hotel;
import model.HousekeepingStaff;
import model.Staff;

import java.util.HashMap;
import java.util.List;

public interface StaffService {
    HashMap<Staff, String> findAllWithRole(int hotelId);

    List<HousekeepingStaff> findAvailableHousekeepers(Hotel hotel, String date);
}
