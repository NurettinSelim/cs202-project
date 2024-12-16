package model;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

public class AdministratorStaff extends Staff {
    public AdministratorStaff() {
    }

    public AdministratorStaff(int userId, String firstName, String lastName, String phone, Timestamp createdAt,
            Hotel hotel, BigDecimal salary, Date hireDate) {
        super(userId, firstName, lastName, phone, createdAt, hotel, salary, hireDate);
    }

    @Override
    public String toString() {
        return "AdministratorStaff{" + super.toString() + "}";
    }
}