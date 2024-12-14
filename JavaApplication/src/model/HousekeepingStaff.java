package model;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

public class HousekeepingStaff extends Staff {
    public HousekeepingStaff() {}

    public HousekeepingStaff(int userId, String username, String password, String firstName, String lastName,
                            String phone, Timestamp createdAt, Hotel hotel, BigDecimal salary, Date hireDate) {
        super(userId, username, password, firstName, lastName, phone, createdAt, hotel, salary, hireDate);
    }

    @Override
    public String toString() {
        return "HousekeepingStaff{" + super.toString() + "}";
    }
} 