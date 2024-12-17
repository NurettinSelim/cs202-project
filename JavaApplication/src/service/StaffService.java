package service;

import model.Staff;
import java.util.HashMap;

public interface StaffService {
    HashMap<Staff, String> findAllWithRole(int hotelId);
}
