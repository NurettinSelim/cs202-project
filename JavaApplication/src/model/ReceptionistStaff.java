package model;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

public class ReceptionistStaff extends Staff {
    public ReceptionistStaff() {}

    public ReceptionistStaff(int userId, String username, String password, String firstName, String lastName,
                            String phone, Timestamp createdAt, Hotel hotel, BigDecimal salary, Date hireDate) {
        super(userId, username, password, firstName, lastName, phone, createdAt, hotel, salary, hireDate);
    }

    @Override
    public String toString() {
        return "ReceptionistStaff{" + super.toString() + "}";
    }
} 