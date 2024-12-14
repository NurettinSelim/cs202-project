package model;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class Payment {
    private Booking booking;
    private int paymentNumber;
    private BigDecimal amount;
    private Timestamp paymentDate;
    private ReceptionistStaff processedBy;

    // Constructors
    public Payment() {}

    public Payment(Booking booking, int paymentNumber, BigDecimal amount, Timestamp paymentDate, ReceptionistStaff processedBy) {
        this.booking = booking;
        this.paymentNumber = paymentNumber;
        this.amount = amount;
        this.paymentDate = paymentDate;
        this.processedBy = processedBy;
    }

    // Getters and Setters
    public Booking getBooking() {
        return booking;
    }

    public void setBooking(Booking booking) {
        this.booking = booking;
    }

    public int getPaymentNumber() {
        return paymentNumber;
    }

    public void setPaymentNumber(int paymentNumber) {
        this.paymentNumber = paymentNumber;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Timestamp getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(Timestamp paymentDate) {
        this.paymentDate = paymentDate;
    }

    public ReceptionistStaff getProcessedBy() {
        return processedBy;
    }

    public void setProcessedBy(ReceptionistStaff processedBy) {
        this.processedBy = processedBy;
    }

    @Override
    public String toString() {
        return "Payment{" +
                "booking=" + booking.getBookingId() +
                ", paymentNumber=" + paymentNumber +
                ", amount=" + amount +
                ", paymentDate=" + paymentDate +
                ", processedBy=" + processedBy +
                '}';
    }
} 