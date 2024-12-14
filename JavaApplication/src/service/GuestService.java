package service;

import model.Guest;
import model.Booking;
import java.sql.Date;
import java.util.List;

public interface GuestService extends BaseService<Guest, Integer> {
    List<Booking> findBookingHistory(Integer guestId);
    List<Booking> findActiveBookings(Integer guestId);
    List<Booking> findBookingsByDateRange(Integer guestId, Date startDate, Date endDate);
    double getTotalSpent(Integer guestId);
    int getBookingsCount(Integer guestId);
    List<Guest> findFrequentGuests(int minimumStays);
    List<Guest> findGuestsByLastStay(Date startDate, Date endDate);
} 