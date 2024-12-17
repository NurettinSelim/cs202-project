package service;

import java.math.BigDecimal;
import java.util.List;

public interface RevenueService {
    record RevenueReport(int totalBookings, BigDecimal totalRevenue, BigDecimal averageRevenuePerBooking) {}
    record RoomTypeStats(String typeName, int totalBookings, BigDecimal totalRevenue, double occupancyRate) {}

    RevenueReport generateRevenueReport(int hotelId);
    List<RoomTypeStats> getMostBookedRoomTypes(int hotelId);
} 