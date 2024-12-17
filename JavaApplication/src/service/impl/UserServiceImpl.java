package service.impl;

import model.User;
import service.UserService;
import util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class UserServiceImpl implements UserService {
    private static User currentUser;
    private static String currentRole;

    protected User mapRow(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUserId(rs.getInt("user_id"));
        user.setFirstName(rs.getString("first_name"));
        user.setLastName(rs.getString("last_name"));
        user.setPhone(rs.getString("phone"));
        user.setCreatedAt(rs.getTimestamp("created_at"));
        return user;
    }

    public User create(User user) {
        String sql = "INSERT INTO users (first_name, last_name, phone, created_at) VALUES (?, ?, ?, CURRENT_TIMESTAMP)";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, user.getFirstName());
            stmt.setString(2, user.getLastName());
            stmt.setString(3, user.getPhone());
            stmt.executeUpdate();
            return user;
        } catch (SQLException e) {
            throw new RuntimeException("Error getting user by id", e);
        }
    }

    public ArrayList<User> findAll() {
        String sql = "SELECT * FROM users";

        ArrayList<User> users = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                users.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error getting all users", e);
        }
        return users;
    }

    public HashMap<User, String> findAllWithRole() {
        String sql = """
                SELECT u.*, 'ADMINISTRATOR' as role
                FROM users u
                JOIN administrator_staff a ON u.user_id = a.user_id

                UNION

                SELECT u.*, 'RECEPTIONIST' as role
                FROM users u
                JOIN receptionist_staff r ON u.user_id = r.user_id

                UNION

                SELECT u.*, 'HOUSEKEEPING' as role
                FROM users u
                JOIN housekeeping_staff h ON u.user_id = h.user_id;
                """;

        HashMap<User, String> users = new HashMap<>();
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                User user = mapRow(rs);
                users.put(user, rs.getString("role"));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error getting all users", e);
        }
        return users;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public String getCurrentRole() {
        return currentRole;
    }

    public void login(int userId) {
        String sql = "SELECT * FROM users WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                currentUser = mapRow(rs);
                getRole(userId);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error logging in", e);
        }
    }

    private void getRole(int userId) {
        String sql = """
                SELECT u.*, 'ADMINISTRATOR' as role
                FROM users u
                JOIN administrator_staff a ON u.user_id = a.user_id
                WHERE u.user_id = ?

                UNION

                SELECT u.*, 'RECEPTIONIST' as role
                FROM users u
                JOIN receptionist_staff r ON u.user_id = r.user_id
                WHERE u.user_id = ?

                UNION

                SELECT u.*, 'HOUSEKEEPING' as role
                FROM users u
                JOIN housekeeping_staff h ON u.user_id = h.user_id
                WHERE u.user_id = ?;
                """;
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, userId);
            stmt.setInt(3, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                currentRole = rs.getString("role");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error getting role", e);
        }
    }

    public int getCurrentHotelId() {
        String sql = "SELECT hotel_id FROM staff WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, currentUser.getUserId());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("hotel_id");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error getting hotel id", e);
        }
        return -1;
    }

    public void logout() {
        currentUser = null;
    }
}