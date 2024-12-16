package model;

import java.sql.Timestamp;

public class Guest extends User {
    public Guest() {}

    public Guest(int userId, String firstName, String lastName, String phone, Timestamp createdAt) {
        super(userId, firstName, lastName, phone, createdAt);
    }

    @Override
    public String toString() {
        return "Guest{" + super.toString() + "}";
    }
} 