package service;

import model.ReceptionistStaff;
import model.Booking;
import model.Payment;
import java.sql.Date;
import java.util.List;

public interface ReceptionistStaffService extends BaseService<ReceptionistStaff, Integer> {
    List<Booking> findBookingsProcessed(Integer staffId);
    List<Booking> findBookingsProcessedByDate(Integer staffId, Date date);
    List<Payment> findPaymentsProcessed(Integer staffId);
    List<Payment> findPaymentsProcessedByDate(Integer staffId, Date date);
    double getTotalPaymentsProcessed(Integer staffId, Date startDate, Date endDate);
    int getBookingsProcessedCount(Integer staffId, Date startDate, Date endDate);
} 