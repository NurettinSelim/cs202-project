package service;

import model.RoomType;
import model.Hotel;
import java.math.BigDecimal;
import java.util.List;

public interface RoomTypeService extends BaseService<RoomType, Integer> {
    List<RoomType> findByHotel(Hotel hotel);
    List<RoomType> findByPriceRange(Hotel hotel, BigDecimal minPrice, BigDecimal maxPrice);
    List<RoomType> findByCapacity(Hotel hotel, int minCapacity);
    List<RoomType> findByBedCount(Hotel hotel, int bedCount);
    void updateBasePrice(Integer typeId, BigDecimal newPrice);
    boolean isTypeNameUnique(Hotel hotel, String typeName);
    double getOccupancyRateByType(Integer typeId);
    List<RoomType> findMostPopularTypes(Hotel hotel, int limit);
} 