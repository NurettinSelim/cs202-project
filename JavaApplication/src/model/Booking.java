package model;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

public class Booking {
    private int bookingId;
    private Guest guest;
    private Date checkInDate;
    private Date checkOutDate;
    private BookingStatus status;
    private int totalGuests;
    private Timestamp createdAt;
    private Staff confirmedBy;
    private List<BookingRoom> bookingRooms;

    // Constructors
    public Booking() {}

    public Booking(int bookingId, Guest guest, Date checkInDate, Date checkOutDate,
                  BookingStatus status, int totalGuests, Timestamp createdAt, Staff confirmedBy) {
        this.bookingId = bookingId;
        this.guest = guest;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.status = status;
        this.totalGuests = totalGuests;
        this.createdAt = createdAt;
        this.confirmedBy = confirmedBy;
    }

    // Getters and Setters
    public int getBookingId() {
        return bookingId;
    }

    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
    }

    public Guest getGuest() {
        return guest;
    }

    public void setGuest(Guest guest) {
        this.guest = guest;
    }

    public Date getCheckInDate() {
        return checkInDate;
    }

    public void setCheckInDate(Date checkInDate) {
        this.checkInDate = checkInDate;
    }

    public Date getCheckOutDate() {
        return checkOutDate;
    }

    public void setCheckOutDate(Date checkOutDate) {
        this.checkOutDate = checkOutDate;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }

    public int getTotalGuests() {
        return totalGuests;
    }

    public void setTotalGuests(int totalGuests) {
        this.totalGuests = totalGuests;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Staff getConfirmedBy() {
        return confirmedBy;
    }

    public void setConfirmedBy(Staff confirmedBy) {
        this.confirmedBy = confirmedBy;
    }

    public List<BookingRoom> getBookingRooms() {
        return bookingRooms;
    }

    public void setBookingRooms(List<BookingRoom> bookingRooms) {
        this.bookingRooms = bookingRooms;
    }

    @Override
    public String toString() {
        return "Booking{" +
                "bookingId=" + bookingId +
                ", guest=" + guest +
                ", checkInDate=" + checkInDate +
                ", checkOutDate=" + checkOutDate +
                ", status=" + status +
                ", totalGuests=" + totalGuests +
                ", createdAt=" + createdAt +
                ", confirmedBy=" + confirmedBy +
                ", bookingRooms=" + bookingRooms +
                '}';
    }

    public String toDisplayString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Booking ID: ").append(bookingId).append("\n");
        sb.append("Guest: ").append(guest.getFirstName()).append(" ").append(guest.getLastName()).append("\n");
        sb.append("Check-In Date: ").append(checkInDate).append("\n");
        sb.append("Check-Out Date: ").append(checkOutDate).append("\n");
        sb.append("Status: ").append(status.getStatusName()).append("\n");
        if (bookingRooms != null) {
            for (BookingRoom bookingRoom : bookingRooms) {
                sb.append("Room: ").append(bookingRoom.getRoom().getRoomNumber()).append("\n");
            }
        }
        return sb.toString();
    }
} 
