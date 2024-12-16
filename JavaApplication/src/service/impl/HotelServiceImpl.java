package service.impl;

import model.Hotel;
import model.Room;
import model.Staff;
import service.HotelService;
import util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class HotelServiceImpl extends BaseServiceImpl<Hotel, Integer> implements HotelService {

    @Override
    protected String getTableName() {
        return "hotels";
    }

    @Override
    protected String getIdColumnName() {
        return "hotel_id";
    }

    @Override
    protected Hotel mapRow(ResultSet rs) throws SQLException {
        Hotel hotel = new Hotel();
        hotel.setHotelId(rs.getInt("hotel_id"));
        hotel.setHotelName(rs.getString("hotel_name"));
        hotel.setAddress(rs.getString("address"));
        hotel.setPhone(rs.getString("phone"));
        return hotel;
    }

    @Override
    protected void setCreateStatement(PreparedStatement stmt, Hotel hotel) throws SQLException {
        stmt.setString(1, hotel.getHotelName());
        stmt.setString(2, hotel.getAddress());
        stmt.setString(3, hotel.getPhone());
    }

    @Override
    protected void setUpdateStatement(PreparedStatement stmt, Hotel hotel) throws SQLException {
        stmt.setString(1, hotel.getHotelName());
        stmt.setString(2, hotel.getAddress());
        stmt.setString(3, hotel.getPhone());
        stmt.setInt(4, hotel.getHotelId());
    }

    @Override
    public List<Room> findAllRooms(Integer hotelId) {
        String sql = "SELECT r.*, rt.type_name, rt.base_price, rt.capacity, rt.bed_count, " +
                    "rs.status_name FROM rooms r " +
                    "JOIN room_types rt ON r.type_id = rt.type_id " +
                    "JOIN room_statuses rs ON r.status_id = rs.status_id " +
                    "WHERE r.hotel_id = ?";
        List<Room> rooms = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, hotelId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Room room = new Room();
                room.setHotel(findById(hotelId));
                room.setRoomNumber(rs.getString("room_number"));
                // Set other room properties
                rooms.add(room);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding rooms for hotel", e);
        }
        return rooms;
    }

    @Override
    public List<Staff> findAllStaff(Integer hotelId) {
        String sql = "SELECT s.*, u.* FROM staff s " +
                    "JOIN users u ON s.user_id = u.user_id " +
                    "WHERE s.hotel_id = ?";
        List<Staff> staffList = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, hotelId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Staff staff = new Staff();
                staff.setUserId(rs.getInt("user_id"));
                staff.setFirstName(rs.getString("first_name"));
                staff.setLastName(rs.getString("last_name"));
                staff.setPhone(rs.getString("phone"));
                staff.setCreatedAt(rs.getTimestamp("created_at"));
                staff.setHotel(findById(hotelId));
                staff.setSalary(rs.getBigDecimal("salary"));
                staff.setHireDate(rs.getDate("hire_date"));
                staffList.add(staff);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding staff for hotel", e);
        }
        return staffList;
    }

    @Override
    public List<Hotel> findByName(String name) {
        String sql = "SELECT * FROM hotels WHERE hotel_name LIKE ?";
        List<Hotel> hotels = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, "%" + name + "%");
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                hotels.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding hotels by name", e);
        }
        return hotels;
    }

    @Override
    public List<Hotel> findByAddress(String address) {
        String sql = "SELECT * FROM hotels WHERE address LIKE ?";
        List<Hotel> hotels = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, "%" + address + "%");
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                hotels.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding hotels by address", e);
        }
        return hotels;
    }

    @Override
    public boolean isPhoneNumberUnique(String phone) {
        String sql = "SELECT COUNT(*) FROM hotels WHERE phone = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, phone);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) == 0;
            }
            return true;
        } catch (SQLException e) {
            throw new RuntimeException("Error checking phone number uniqueness", e);
        }
    }
} 