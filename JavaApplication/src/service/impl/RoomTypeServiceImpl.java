package service.impl;

import model.Hotel;
import model.RoomType;
import service.RoomTypeService;
import util.DatabaseConnection;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RoomTypeServiceImpl extends BaseServiceImpl<RoomType, Integer> implements RoomTypeService {

    @Override
    protected String getTableName() {
        return "room_types";
    }

    @Override
    protected String getIdColumnName() {
        return "type_id";
    }

    @Override
    protected RoomType mapRow(ResultSet rs) throws SQLException {
        RoomType roomType = new RoomType();
        roomType.setTypeId(rs.getInt("type_id"));
        
        Hotel hotel = new Hotel();
        hotel.setHotelId(rs.getInt("hotel_id"));
        roomType.setHotel(hotel);
        
        roomType.setTypeName(rs.getString("type_name"));
        roomType.setBasePrice(rs.getBigDecimal("base_price"));
        roomType.setCapacity(rs.getInt("capacity"));
        roomType.setBedCount(rs.getInt("bed_count"));
        return roomType;
    }

    @Override
    protected String getCreateSQL() {
        return String.format("INSERT INTO %s (hotel_id, type_name, base_price, capacity, bed_count) VALUES (?, ?, ?, ?, ?)", getTableName());
    }

    @Override
    protected void setCreateStatement(PreparedStatement stmt, RoomType roomType) throws SQLException {
        stmt.setInt(1, roomType.getHotel().getHotelId());
        stmt.setString(2, roomType.getTypeName());
        stmt.setBigDecimal(3, roomType.getBasePrice());
        stmt.setInt(4, roomType.getCapacity());
        stmt.setInt(5, roomType.getBedCount());
    }

    @Override
    protected void setUpdateStatement(PreparedStatement stmt, RoomType roomType) throws SQLException {
        stmt.setString(1, roomType.getTypeName());
        stmt.setBigDecimal(2, roomType.getBasePrice());
        stmt.setInt(3, roomType.getCapacity());
        stmt.setInt(4, roomType.getBedCount());
        stmt.setInt(5, roomType.getTypeId());
    }

    @Override
    public List<RoomType> findByHotel(Hotel hotel) {
        String sql = "SELECT * FROM room_types WHERE hotel_id = ?";
        List<RoomType> types = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, hotel.getHotelId());
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                types.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding room types by hotel", e);
        }
        return types;
    }

    @Override
    public List<RoomType> findByPriceRange(Hotel hotel, BigDecimal minPrice, BigDecimal maxPrice) {
        String sql = "SELECT * FROM room_types WHERE hotel_id = ? AND base_price BETWEEN ? AND ?";
        List<RoomType> types = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, hotel.getHotelId());
            stmt.setBigDecimal(2, minPrice);
            stmt.setBigDecimal(3, maxPrice);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                types.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding room types by price range", e);
        }
        return types;
    }

    @Override
    public List<RoomType> findByCapacity(Hotel hotel, int minCapacity) {
        String sql = "SELECT * FROM room_types WHERE hotel_id = ? AND capacity >= ?";
        List<RoomType> types = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, hotel.getHotelId());
            stmt.setInt(2, minCapacity);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                types.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding room types by capacity", e);
        }
        return types;
    }

    @Override
    public List<RoomType> findByBedCount(Hotel hotel, int bedCount) {
        String sql = "SELECT * FROM room_types WHERE hotel_id = ? AND bed_count = ?";
        List<RoomType> types = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, hotel.getHotelId());
            stmt.setInt(2, bedCount);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                types.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding room types by bed count", e);
        }
        return types;
    }

    @Override
    public void updateBasePrice(Integer typeId, BigDecimal newPrice) {
        String sql = "UPDATE room_types SET base_price = ? WHERE type_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setBigDecimal(1, newPrice);
            stmt.setInt(2, typeId);
            
            int affected = stmt.executeUpdate();
            if (affected == 0) {
                throw new RuntimeException("Room type not found");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error updating base price", e);
        }
    }

    @Override
    public boolean isTypeNameUnique(Hotel hotel, String typeName) {
        String sql = "SELECT COUNT(*) FROM room_types WHERE hotel_id = ? AND type_name = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, hotel.getHotelId());
            stmt.setString(2, typeName);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) == 0;
            }
            return true;
        } catch (SQLException e) {
            throw new RuntimeException("Error checking type name uniqueness", e);
        }
    }

    @Override
    public double getOccupancyRateByType(Integer typeId) {
        String sql = "SELECT " +
                    "COUNT(DISTINCT br.room_number) as booked_rooms, " +
                    "(SELECT COUNT(*) FROM rooms WHERE type_id = ?) as total_rooms " +
                    "FROM rooms r " +
                    "LEFT JOIN booking_rooms br ON r.hotel_id = br.hotel_id AND r.room_number = br.room_number " +
                    "LEFT JOIN bookings b ON br.booking_id = b.booking_id " +
                    "WHERE r.type_id = ? " +
                    "AND b.status_id IN (2, 3) " + // CONFIRMED or CHECKED_IN
                    "AND b.check_in_date <= CURRENT_DATE " +
                    "AND b.check_out_date >= CURRENT_DATE";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, typeId);
            stmt.setInt(2, typeId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                int bookedRooms = rs.getInt("booked_rooms");
                int totalRooms = rs.getInt("total_rooms");
                return totalRooms > 0 ? (double) bookedRooms / totalRooms : 0;
            }
            return 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error calculating occupancy rate", e);
        }
    }

    @Override
    public List<RoomType> findMostPopularTypes(Hotel hotel, int limit) {
        String sql = "SELECT rt.*, " +
                    "COUNT(DISTINCT br.booking_id) as booking_count " +
                    "FROM room_types rt " +
                    "LEFT JOIN rooms r ON rt.type_id = r.type_id " +
                    "LEFT JOIN booking_rooms br ON r.hotel_id = br.hotel_id AND r.room_number = br.room_number " +
                    "LEFT JOIN bookings b ON br.booking_id = b.booking_id " +
                    "WHERE rt.hotel_id = ? " +
                    "GROUP BY rt.type_id " +
                    "ORDER BY booking_count DESC " +
                    "LIMIT ?";
        
        List<RoomType> types = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, hotel.getHotelId());
            stmt.setInt(2, limit);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                types.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding most popular room types", e);
        }
        return types;
    }
} 