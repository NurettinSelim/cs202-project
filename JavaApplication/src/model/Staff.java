package model;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

public class Staff extends User {
    private Hotel hotel;
    private BigDecimal salary;
    private Date hireDate;

    // Constructors
    public Staff() {}

    public Staff(int userId, String firstName, String lastName, String phone, Timestamp createdAt, Hotel hotel, BigDecimal salary, Date hireDate) {
        super(userId, firstName, lastName, phone, createdAt);
        this.hotel = hotel;
        this.salary = salary;
        this.hireDate = hireDate;
    }

    // Getters and Setters
    public Hotel getHotel() {
        return hotel;
    }

    public void setHotel(Hotel hotel) {
        this.hotel = hotel;
    }

    public BigDecimal getSalary() {
        return salary;
    }

    public void setSalary(BigDecimal salary) {
        this.salary = salary;
    }

    public Date getHireDate() {
        return hireDate;
    }

    public void setHireDate(Date hireDate) {
        this.hireDate = hireDate;
    }

    @Override
    public String toString() {
        return "Staff{" +
                "userId=" + getUserId() +
                ", firstName='" + getFirstName() + '\'' +
                ", lastName='" + getLastName() + '\'' +
                ", hotel=" + hotel +
                ", salary=" + salary +
                ", hireDate=" + hireDate +
                '}';
    }
} 