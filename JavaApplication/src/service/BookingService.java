package service;

import model.*;
import service.impl.BookingServiceImpl;

import java.util.ArrayList;
import java.util.HashMap;

public interface BookingService {
    Booking findById(int bookingId);

    void updateBooking(Booking booking);

    ArrayList<Booking> findByGuestId(int guestId);

    ArrayList<Booking> findAllWithGuest();

    void cancelBooking(int bookingId);

    void processPayment(int bookingId);

    ArrayList<BookingServiceImpl.RoomTypeStats> getMostBookedRoomTypes(int hotelId, String checkInDate,
            String checkOutDate);

    void addNewBooking(int guestId, String checkInDate, String checkOutDate, int statusId, int totalGuests,
            HashMap<Room, Integer> rooms);
}
