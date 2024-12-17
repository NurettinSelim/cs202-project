package service;

public interface HotelService {
    record Revenue(int totalBookings, double totalRevenue, double averageRevenuePerBooking) {
    }

    Revenue getRevenue(Integer hotelId);
}
