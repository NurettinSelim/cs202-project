package service;

import model.Payment;
import model.Booking;
import model.ReceptionistStaff;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;

public interface PaymentService extends BaseService<Payment, Integer> {
    List<Payment> findByBooking(Booking booking);
    List<Payment> findByReceptionist(ReceptionistStaff receptionist);
    List<Payment> findByDateRange(Date startDate, Date endDate);
    BigDecimal getTotalPaymentsForBooking(Integer bookingId);
    BigDecimal getRemainingBalance(Integer bookingId);
    Payment processPayment(Integer bookingId, BigDecimal amount, ReceptionistStaff processedBy);
    List<Payment> findPendingPayments(Integer bookingId);
    boolean isFullyPaid(Integer bookingId);
    List<Booking> findUnpaidBookings(Date checkOutBefore);
} 