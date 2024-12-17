package service.impl;

import model.User;
import service.UserService;
import util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserServiceImpl extends BaseServiceImpl<User, Integer> implements UserService {
    private static User currentUser = null;
    private static String currentUserRole = null;

    @Override
    protected String getTableName() {
        return "users";
    }

    @Override
    protected String getIdColumnName() {
        return "user_id";
    }

    @Override
    protected User mapRow(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUserId(rs.getInt("user_id"));
        user.setFirstName(rs.getString("first_name"));
        user.setLastName(rs.getString("last_name"));
        user.setPhone(rs.getString("phone"));
        user.setCreatedAt(rs.getTimestamp("created_at"));
        return user;
    }

    @Override
    protected String getCreateSQL() {
        return String.format("INSERT INTO %s (first_name, last_name, phone, created_at) VALUES (?, ?, ?, CURRENT_TIMESTAMP)", getTableName());
    }

    @Override
    protected void setCreateStatement(PreparedStatement stmt, User user) throws SQLException {
        stmt.setString(1, user.getFirstName());
        stmt.setString(2, user.getLastName());
        stmt.setString(3, user.getPhone());
    }

    @Override
    protected void setUpdateStatement(PreparedStatement stmt, User user) throws SQLException {
        stmt.setString(3, user.getFirstName());
        stmt.setString(4, user.getLastName());
        stmt.setString(5, user.getPhone());
        stmt.setInt(6, user.getUserId());
    }

    @Override
    public String getCurrentUserRole() {
        if (currentUserRole != null) {
            return currentUserRole;
        }
        String sql = """
                SELECT * , 'ADMINISTRATOR' AS role FROM administrator_staff WHERE user_id = ?
                UNION
                SELECT * , 'RECEPTIONIST' AS role FROM receptionist_staff WHERE user_id = ?
                UNION
                SELECT * , 'HOUSEKEEPING' AS role FROM housekeeping_staff WHERE user_id = ?
                UNION
                SELECT * , 'GUEST' AS role FROM guests WHERE user_id = ?
                """;
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, currentUser.getUserId());
            stmt.setInt(2, currentUser.getUserId());
            stmt.setInt(3, currentUser.getUserId());
            stmt.setInt(4, currentUser.getUserId());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                currentUserRole = rs.getString("role");
                return currentUserRole;
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding current user", e);
        }
    }

    @Override
    public User login(Integer userId) {
        User user = findById(userId);
        if (user != null) {
            currentUser = user;
            getCurrentUserRole();
        }
        return user;
    }

    @Override
    public User getCurrentUser() {
        return currentUser;
    }

    @Override
    public void logout() {
        this.currentUser = null;
        this.currentUserRole = null;
    }

    @Override
    public boolean isLoggedIn() {
        return currentUser != null;
    }

    @Override
    public int getCurrentHotelId() {
        String sql = "SELECT hotel_id FROM staff WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, currentUser.getUserId());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("hotel_id");
            }
            return -1;
        } catch (SQLException e) {
            return -1;
        }
    }
}