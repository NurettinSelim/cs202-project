package service.impl;

import model.Payment;
import service.PaymentService;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;

public class PaymentServiceImpl extends BaseServiceImpl<Payment, Integer> implements PaymentService {
    
    @Override
    protected String getTableName() {
        return "payments";
    }

    @Override
    protected String getIdColumnName() {
        return "payment_number";
    }

    @Override
    protected Payment mapRow(ResultSet rs) throws SQLException {
        Payment payment = new Payment();
        payment.setPaymentId(rs.getInt("payment_number"));
        payment.setBookingId(rs.getInt("booking_id"));
        payment.setAmount(rs.getBigDecimal("amount"));
        payment.setPaymentDate(rs.getTimestamp("payment_date"));
        payment.setPaymentMethod(rs.getString("payment_method"));
        return payment;
    }

    @Override
    protected String getCreateSQL() {
        return String.format("INSERT INTO %s (booking_id, amount, payment_date, payment_method) VALUES (?, ?, ?, ?)", getTableName());
    }

    @Override
    protected void setCreateStatement(PreparedStatement stmt, Payment payment) throws SQLException {
        stmt.setInt(1, payment.getBookingId());
        stmt.setBigDecimal(2, payment.getAmount());
        stmt.setTimestamp(3, payment.getPaymentDate());
        stmt.setString(4, payment.getPaymentMethod());
    }

    @Override
    protected void setUpdateStatement(PreparedStatement stmt, Payment payment) throws SQLException {
        stmt.setInt(1, payment.getBookingId());
        stmt.setBigDecimal(2, payment.getAmount());
        stmt.setTimestamp(3, payment.getPaymentDate());
        stmt.setString(4, payment.getPaymentMethod());
        stmt.setInt(5, payment.getPaymentId());
    }
} 