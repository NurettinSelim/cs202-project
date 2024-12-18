package service.impl;

import model.Payment;
import service.PaymentService;
import util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PaymentServiceImpl implements PaymentService {

    public void processPayment(Payment payment)  {
        String sql = "INSERT INTO payments (booking_id, payment_number, amount, processed_by) VALUES (?, 1, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, payment.getBookingId());
            stmt.setBigDecimal(2, payment.getAmount());
            stmt.setInt(3, payment.getProcessedBy());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to process payment", e);
        }
    }   
} 