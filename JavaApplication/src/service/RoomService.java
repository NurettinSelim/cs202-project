package service;

import model.Room;
import model.RoomType;
import model.RoomStatus;
import model.Hotel;
import java.sql.Date;
import java.util.List;

public interface RoomService extends BaseService<Room, String> {
    List<Room> findByHotel(Hotel hotel);
    List<Room> findByType(Hotel hotel, RoomType type);
    List<Room> findByStatus(Hotel hotel, RoomStatus status);
    List<Room> findAvailableRooms(Hotel hotel, Date checkIn, Date checkOut);
    List<Room> findAvailableRoomsByType(Hotel hotel, RoomType type, Date checkIn, Date checkOut);
    boolean isRoomAvailable(Hotel hotel, String roomNumber, Date checkIn, Date checkOut);
    void updateRoomStatus(Hotel hotel, String roomNumber, RoomStatus newStatus);
    double getOccupancyRate(Hotel hotel, Date startDate, Date endDate);
    List<Room> findRoomsNeedingCleaning(Hotel hotel);
}