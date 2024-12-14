package service;

import model.Hotel;
import model.Room;
import model.Staff;

import java.util.List;

public interface HotelService extends BaseService<Hotel, Integer> {
    List<Room> findAllRooms(Integer hotelId);
    List<Staff> findAllStaff(Integer hotelId);
    List<Hotel> findByName(String name);
    List<Hotel> findByAddress(String address);
    boolean isPhoneNumberUnique(String phone);
} 