package service;

import util.DatabaseConnection;
import java.sql.*;

public class LoginService {
    public static class UserSession {
        private int userId;
        private String username;
        private String role;
        private int hotelId;

        public UserSession(int userId, String username, String role, int hotelId) {
            this.userId = userId;
            this.username = username;
            this.role = role;
            this.hotelId = hotelId;
        }

        public int getUserId() { return userId; }
        public String getUsername() { return username; }
        public String getRole() { return role; }
        public int getHotelId() { return hotelId; }
    }

    private static UserSession currentUser = null;

    public UserSession getCurrentUser() { return currentUser; }

    public UserSession authenticate(String username, String password) throws SQLException {
        String sql = """
            SELECT u.user_id, u.username,
            CASE
                WHEN a.user_id IS NOT NULL THEN 'ADMINISTRATOR'
                WHEN r.user_id IS NOT NULL THEN 'RECEPTIONIST'
                WHEN h.user_id IS NOT NULL THEN 'HOUSEKEEPING'
                WHEN g.user_id IS NOT NULL THEN 'GUEST'
                ELSE NULL
            END as role,
            CASE
                WHEN a.hotel_id IS NOT NULL THEN a.hotel_id
                WHEN r.hotel_id IS NOT NULL THEN r.hotel_id
                WHEN h.hotel_id IS NOT NULL THEN h.hotel_id
                WHEN g.hotel_id IS NOT NULL THEN g.hotel_id
                ELSE NULL
            END as hotel_id
            FROM users u
            LEFT JOIN administrator_staff a ON u.user_id = a.user_id
            LEFT JOIN receptionist_staff r ON u.user_id = r.user_id
            LEFT JOIN housekeeping_staff h ON u.user_id = h.user_id
            LEFT JOIN guests g ON u.user_id = g.user_id
            WHERE u.username = ? AND u.password = ?""";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, username);
            stmt.setString(2, password);
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                UserSession user = new UserSession(
                    rs.getInt("user_id"),
                    rs.getString("username"),
                    rs.getString("role"),
                    rs.getInt("hotel_id")
                );
                setCurrentUser(user);
                return user;
            }
            return null;    
        }
    }

    public void setCurrentUser(UserSession user) { currentUser = user; }
} 