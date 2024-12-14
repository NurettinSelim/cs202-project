package service.impl;

import model.*;
import service.StaffService;
import util.DatabaseConnection;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class StaffServiceImpl extends BaseServiceImpl<Staff, Integer> implements StaffService {

    @Override
    protected String getTableName() {
        return "users";
    }

    @Override
    protected String getIdColumnName() {
        return "user_id";
    }

    private String getStaffRole(Staff staff) {
        if (staff instanceof AdministratorStaff) {
            return "ADMINISTRATOR";
        } else if (staff instanceof ReceptionistStaff) {
            return "RECEPTIONIST";
        } else if (staff instanceof HousekeepingStaff) {
            return "HOUSEKEEPING";
        }
        throw new IllegalArgumentException("Unknown staff type");
    }

    @Override
    protected Staff mapRow(ResultSet rs) throws SQLException {
        Staff staff;
        String role = rs.getString("role");
        
        switch (role) {
            case "ADMINISTRATOR":
                staff = new AdministratorStaff();
                break;
            case "RECEPTIONIST":
                staff = new ReceptionistStaff();
                break;
            case "HOUSEKEEPING":
                staff = new HousekeepingStaff();
                break;
            default:
                throw new SQLException("Unknown staff role: " + role);
        }
        
        staff.setUserId(rs.getInt("user_id"));
        staff.setUsername(rs.getString("username"));
        staff.setPassword(rs.getString("password"));
        staff.setFirstName(rs.getString("first_name"));
        staff.setLastName(rs.getString("last_name"));
        staff.setPhone(rs.getString("phone"));
        staff.setCreatedAt(rs.getTimestamp("created_at"));
        
        // Map hotel
        Hotel hotel = new Hotel();
        hotel.setHotelId(rs.getInt("hotel_id"));
        staff.setHotel(hotel);
        
        staff.setSalary(rs.getBigDecimal("salary"));
        staff.setHireDate(rs.getDate("hire_date"));
        
        return staff;
    }

    @Override
    protected void setCreateStatement(PreparedStatement stmt, Staff staff) throws SQLException {
        stmt.setString(1, staff.getUsername());
        stmt.setString(2, staff.getPassword());
        stmt.setString(3, staff.getFirstName());
        stmt.setString(4, staff.getLastName());
        stmt.setString(5, staff.getPhone());
        stmt.setTimestamp(6, staff.getCreatedAt());
        stmt.setString(7, getStaffRole(staff));
        stmt.setInt(8, staff.getHotel().getHotelId());
        stmt.setBigDecimal(9, staff.getSalary());
        stmt.setDate(10, new Date(staff.getHireDate().getTime()));
    }

    @Override
    protected void setUpdateStatement(PreparedStatement stmt, Staff staff) throws SQLException {
        stmt.setString(1, staff.getPassword());
        stmt.setString(2, staff.getFirstName());
        stmt.setString(3, staff.getLastName());
        stmt.setString(4, staff.getPhone());
        stmt.setBigDecimal(5, staff.getSalary());
        stmt.setInt(6, staff.getUserId());
    }

    @Override
    public List<Staff> findByHotel(Hotel hotel) {
        String sql = "SELECT u.*, s.hotel_id, s.salary, s.hire_date FROM users u " +
                    "JOIN staff s ON u.user_id = s.user_id " +
                    "WHERE s.hotel_id = ? AND u.role != 'GUEST'";
        List<Staff> staffList = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, hotel.getHotelId());
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                staffList.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding staff by hotel", e);
        }
        return staffList;
    }

    @Override
    public List<Staff> findByHotelAndRole(Hotel hotel, String role) {
        String sql = "SELECT u.*, s.hotel_id, s.salary, s.hire_date FROM users u " +
                    "JOIN staff s ON u.user_id = s.user_id " +
                    "WHERE s.hotel_id = ? AND u.role = ?";
        List<Staff> staffList = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, hotel.getHotelId());
            stmt.setString(2, role);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                staffList.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding staff by role", e);
        }
        return staffList;
    }

    @Override
    public List<Staff> findBySalaryRange(BigDecimal minSalary, BigDecimal maxSalary) {
        String sql = "SELECT u.*, s.hotel_id, s.salary, s.hire_date FROM users u " +
                    "JOIN staff s ON u.user_id = s.user_id " +
                    "WHERE s.salary BETWEEN ? AND ?";
        List<Staff> staffList = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setBigDecimal(1, minSalary);
            stmt.setBigDecimal(2, maxSalary);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                staffList.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding staff by salary range", e);
        }
        return staffList;
    }

    @Override
    public List<Staff> findByHireDate(Date startDate, Date endDate) {
        String sql = "SELECT u.*, s.hotel_id, s.salary, s.hire_date FROM users u " +
                    "JOIN staff s ON u.user_id = s.user_id " +
                    "WHERE s.hire_date BETWEEN ? AND ?";
        List<Staff> staffList = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, startDate);
            stmt.setDate(2, endDate);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                staffList.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding staff by hire date", e);
        }
        return staffList;
    }

    @Override
    public void updateSalary(Integer staffId, BigDecimal newSalary) {
        String sql = "UPDATE staff SET salary = ? WHERE user_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setBigDecimal(1, newSalary);
            stmt.setInt(2, staffId);
            
            int affected = stmt.executeUpdate();
            if (affected == 0) {
                throw new RuntimeException("Staff not found");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error updating staff salary", e);
        }
    }

    @Override
    public void transferToHotel(Integer staffId, Integer newHotelId) {
        String sql = "UPDATE staff SET hotel_id = ? WHERE user_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, newHotelId);
            stmt.setInt(2, staffId);
            
            int affected = stmt.executeUpdate();
            if (affected == 0) {
                throw new RuntimeException("Staff not found");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error transferring staff", e);
        }
    }
} 