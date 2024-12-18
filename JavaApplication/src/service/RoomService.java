package service;

import model.Room;
import model.RoomStatus;
import model.Hotel;
import java.util.ArrayList;
import java.util.List;

public interface RoomService {
    void create(Room room);
    
    List<Room> findByHotel(Hotel hotel);
    
    void updateRoomStatus(Hotel hotel, String roomNumber, RoomStatus status);
    
    ArrayList<Room> getAvailableRooms(String checkInDate, String checkOutDate);
}
