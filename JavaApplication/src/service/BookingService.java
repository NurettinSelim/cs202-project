package service;

import model.Booking;
import model.BookingStatus;
import model.Guest;
import model.Hotel;
import model.Room;
import java.sql.Date;
import java.util.List;

public interface BookingService extends BaseService<Booking, Integer> {
    List<Booking> findByGuest(Guest guest);
    List<Booking> findByHotel(Hotel hotel);
    List<Booking> findByStatus(Hotel hotel, BookingStatus status);
    List<Booking> findByDateRange(Hotel hotel, Date startDate, Date endDate);
    List<Booking> findCurrentBookings(Hotel hotel);
    void assignRoom(Integer bookingId, Room room, int guestsInRoom);
    void updateStatus(Integer bookingId, BookingStatus newStatus);
    void checkIn(Integer bookingId);
    void checkOut(Integer bookingId);
    void cancel(Integer bookingId);
    boolean canBeCancelled(Integer bookingId);
    double calculateTotalPrice(Integer bookingId);
    List<Room> getAssignedRooms(Integer bookingId);
} 