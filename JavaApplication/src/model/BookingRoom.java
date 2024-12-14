package model;

public class BookingRoom {
    private Booking booking;
    private Room room;
    private int guestsInRoom;

    // Constructors
    public BookingRoom() {}

    public BookingRoom(Booking booking, Room room, int guestsInRoom) {
        this.booking = booking;
        this.room = room;
        this.guestsInRoom = guestsInRoom;
    }

    // Getters and Setters
    public Booking getBooking() {
        return booking;
    }

    public void setBooking(Booking booking) {
        this.booking = booking;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public int getGuestsInRoom() {
        return guestsInRoom;
    }

    public void setGuestsInRoom(int guestsInRoom) {
        this.guestsInRoom = guestsInRoom;
    }

    @Override
    public String toString() {
        return "BookingRoom{" +
                "booking=" + booking.getBookingId() +
                ", room=" + room +
                ", guestsInRoom=" + guestsInRoom +
                '}';
    }
} 