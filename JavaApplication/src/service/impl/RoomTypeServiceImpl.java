package service.impl;

import model.Hotel;
import model.RoomType;
import service.RoomTypeService;
import util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class RoomTypeServiceImpl implements RoomTypeService {

    @Override
    
    public ArrayList<RoomType> findByHotel(int hotelId) {
        String sql = "SELECT * FROM room_types WHERE hotel_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, hotelId);
            ResultSet rs = stmt.executeQuery();
            ArrayList<RoomType> roomTypes = new ArrayList<>();
            while (rs.next()) {
                RoomType roomType = new RoomType();
                roomType.setTypeId(rs.getInt("type_id"));
                roomType.setTypeName(rs.getString("type_name"));
                roomType.setBasePrice(rs.getBigDecimal("base_price"));
                roomType.setHotel(new Hotel(rs.getInt("hotel_id")));
                roomType.setCapacity(rs.getInt("capacity"));
                roomType.setBedCount(rs.getInt("bed_count"));
                roomTypes.add(roomType);
            }
            return roomTypes;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get room types", e);
        }
    }
} 