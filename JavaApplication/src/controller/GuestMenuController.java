package controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import model.Booking;
import model.Room;
import service.BookingService;
import service.impl.BookingServiceImpl;
import service.RoomService;
import service.impl.RoomServiceImpl;

public class GuestMenuController extends BaseControlller {
    private BookingService bookingService = new BookingServiceImpl();
    private RoomService roomService = new RoomServiceImpl();

    private static List<String> menuItems = Arrays.asList(
            "1. Add New Booking",
            "2. View Available Rooms",
            "3. View My Bookings",
            "4. Cancel Booking");

    public void displayMenu() {
        super.displayMenu(menuItems);
    }
    public void addNewBooking(String checkInDate, String checkOutDate, int totalGuests, HashMap<Room, Integer> rooms) {
        int guestId = super.getCurrentUser().getUserId();
        int statusId = 1;
        bookingService.addNewBooking(guestId, checkInDate, checkOutDate, statusId, totalGuests, rooms);
    }

    public ArrayList<Room> viewAvailableRooms(String checkInDate, String checkOutDate) {
        ArrayList<Room> availableRooms = roomService.getAvailableRooms(checkInDate, checkOutDate);
        return availableRooms;
    }

    public ArrayList<Booking> viewMyBookings() {
        int guestId = super.getCurrentUser().getUserId();
        ArrayList<Booking> myBookings = bookingService.findByGuestId(guestId);
        return myBookings;
    }

    public void cancelBooking(int bookingId) {
        bookingService.cancelBooking(bookingId);
    }
}
