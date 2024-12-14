package model;

import java.sql.Timestamp;

public class Guest extends User {
    public Guest() {}

    public Guest(int userId, String username, String password, String firstName, String lastName,
                String phone, Timestamp createdAt) {
        super(userId, username, password, firstName, lastName, phone, createdAt);
    }

    @Override
    public String toString() {
        return "Guest{" + super.toString() + "}";
    }
} 