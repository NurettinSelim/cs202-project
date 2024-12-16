package menu;

import util.DatabaseConnection;

import java.sql.*;
import java.util.Scanner;

public class ReceptionistMenuHandler {
    private static Scanner scanner = new Scanner(System.in);

    public static void handleMenu() {
        while (true) {
            displayMenu();
            int choice = getUserChoice();

            switch (choice) {
                case 1:
                    addNewBooking();
                    break;
                case 2:
                    modifyBooking();
                    break;
                case 3:
                    deleteBooking();
                    break;
                case 4:
                    viewBookings();
                    break;
                case 5:
                    processPayment();
                    break;
                case 6:
                    assignHousekeepingTask();
                    break;
                case 7:
                    viewHousekeepers();
                    break;
                case 8:
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void displayMenu() {
        System.out.println("\n=== Receptionist Menu ===");
        System.out.println("1. Add New Booking");
        System.out.println("2. Modify Booking");
        System.out.println("3. Delete Booking");
        System.out.println("4. View Bookings");
        System.out.println("5. Process Payment");
        System.out.println("6. Assign Housekeeping Task");
        System.out.println("7. View Housekeepers");
        System.out.println("8. Return to Main Menu");
        System.out.print("Enter your choice: ");
    }

    private static int getUserChoice() {
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private static void addNewBooking() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            System.out.print("Enter guest ID: ");
            int guestId = Integer.parseInt(scanner.nextLine());

            System.out.print("Enter room ID: ");
            int roomId = Integer.parseInt(scanner.nextLine());

            System.out.print("Enter check-in date (YYYY-MM-DD): ");
            String checkInDate = scanner.nextLine();

            System.out.print("Enter check-out date (YYYY-MM-DD): ");
            String checkOutDate = scanner.nextLine();

            // Check room availability
            PreparedStatement checkStmt = conn.prepareStatement(
                    "SELECT COUNT(*) FROM bookings " +
                            "WHERE room_id = ? AND status != 'CANCELLED' " +
                            "AND ((check_in_date BETWEEN ? AND ?) OR (check_out_date BETWEEN ? AND ?))");

            checkStmt.setInt(1, roomId);
            checkStmt.setString(2, checkInDate);
            checkStmt.setString(3, checkOutDate);
            checkStmt.setString(4, checkInDate);
            checkStmt.setString(5, checkOutDate);

            ResultSet rs = checkStmt.executeQuery();
            rs.next();

            if (rs.getInt(1) > 0) {
                System.out.println("Room is not available for the selected dates.");
                return;
            }

            PreparedStatement insertStmt = conn.prepareStatement(
                    "INSERT INTO bookings (guest_id, room_id, check_in_date, check_out_date, status) " +
                            "VALUES (?, ?, ?, ?, 'PENDING')");

            insertStmt.setInt(1, guestId);
            insertStmt.setInt(2, roomId);
            insertStmt.setString(3, checkInDate);
            insertStmt.setString(4, checkOutDate);

            int affected = insertStmt.executeUpdate();
            if (affected > 0) {
                System.out.println("Booking added successfully!");
            }
        } catch (SQLException e) {
            System.err.println("Error adding booking: " + e.getMessage());
        }
    }

    private static void modifyBooking() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            System.out.print("Enter booking ID to modify: ");
            int bookingId = Integer.parseInt(scanner.nextLine());

            // First check if booking exists and is modifiable
            PreparedStatement checkStmt = conn.prepareStatement(
                    "SELECT status FROM bookings WHERE booking_id = ?");
            checkStmt.setInt(1, bookingId);
            ResultSet rs = checkStmt.executeQuery();

            if (!rs.next()) {
                System.out.println("Booking not found.");
                return;
            }

            if (!"PENDING".equals(rs.getString("status"))) {
                System.out.println("Only pending bookings can be modified.");
                return;
            }

            System.out.println("What would you like to modify?");
            System.out.println("1. Check-in date");
            System.out.println("2. Check-out date");
            System.out.println("3. Room");
            System.out.print("Enter choice: ");

            int choice = Integer.parseInt(scanner.nextLine());
            String updateQuery = "";

            switch (choice) {
                case 1:
                    System.out.print("Enter new check-in date (YYYY-MM-DD): ");
                    String newCheckIn = scanner.nextLine();
                    updateQuery = "UPDATE bookings SET check_in_date = ? WHERE booking_id = ?";
                    try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
                        updateStmt.setString(1, newCheckIn);
                        updateStmt.setInt(2, bookingId);
                        updateStmt.executeUpdate();
                    }
                    break;

                case 2:
                    System.out.print("Enter new check-out date (YYYY-MM-DD): ");
                    String newCheckOut = scanner.nextLine();
                    updateQuery = "UPDATE bookings SET check_out_date = ? WHERE booking_id = ?";
                    try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
                        updateStmt.setString(1, newCheckOut);
                        updateStmt.setInt(2, bookingId);
                        updateStmt.executeUpdate();
                    }
                    break;

                case 3:
                    System.out.print("Enter new room ID: ");
                    int newRoomId = Integer.parseInt(scanner.nextLine());
                    updateQuery = "UPDATE bookings SET room_id = ? WHERE booking_id = ?";
                    try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
                        updateStmt.setInt(1, newRoomId);
                        updateStmt.setInt(2, bookingId);
                        updateStmt.executeUpdate();
                    }
                    break;

                default:
                    System.out.println("Invalid choice.");
                    return;
            }

            System.out.println("Booking modified successfully!");

        } catch (SQLException e) {
            System.err.println("Error modifying booking: " + e.getMessage());
        }
    }

    private static void deleteBooking() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            System.out.print("Enter booking ID to delete: ");
            int bookingId = Integer.parseInt(scanner.nextLine());

            PreparedStatement stmt = conn.prepareStatement(
                    "UPDATE bookings SET status = 'CANCELLED' WHERE booking_id = ? AND status = 'PENDING'");
            stmt.setInt(1, bookingId);

            int affected = stmt.executeUpdate();
            if (affected > 0) {
                System.out.println("Booking cancelled successfully!");
            } else {
                System.out.println("Could not cancel booking. It may not exist or already be processed.");
            }
        } catch (SQLException e) {
            System.err.println("Error cancelling booking: " + e.getMessage());
        }
    }

    private static void viewBookings() {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT b.booking_id, u.first_name, u.last_name, r.room_number, " +
                             "b.check_in_date, b.check_out_date, b.status " +
                             "FROM bookings b " +
                             "JOIN users u ON b.guest_id = u.user_id " +
                             "JOIN rooms r ON b.room_id = r.room_id " +
                             "ORDER BY b.check_in_date")) {

            ResultSet rs = stmt.executeQuery();
            System.out.println("\nCurrent Bookings:");
            System.out.println("Booking ID | Guest | Room | Check-in | Check-out | Status");
            System.out.println("--------------------------------------------------------");

            while (rs.next()) {
                System.out.printf("%10d | %s | %s | %s | %s | %s%n",
                        rs.getInt("booking_id"),
                        rs.getString("first_name") + " " + rs.getString("last_name"),
                        rs.getString("room_number"),
                        rs.getDate("check_in_date"),
                        rs.getDate("check_out_date"),
                        rs.getString("status"));
            }
        } catch (SQLException e) {
            System.err.println("Error viewing bookings: " + e.getMessage());
        }
    }

    private static void processPayment() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            System.out.print("Enter booking ID: ");
            int bookingId = Integer.parseInt(scanner.nextLine());

            System.out.print("Enter payment amount: ");
            double amount = Double.parseDouble(scanner.nextLine());

            System.out.print("Enter payment method (CASH/CARD): ");
            String paymentMethod = scanner.nextLine().toUpperCase();

            // First check if booking exists and payment is needed
            PreparedStatement checkStmt = conn.prepareStatement(
                    "SELECT status FROM bookings WHERE booking_id = ?");
            checkStmt.setInt(1, bookingId);
            ResultSet rs = checkStmt.executeQuery();

            if (!rs.next()) {
                System.out.println("Booking not found.");
                return;
            }

            // Process payment
            PreparedStatement paymentStmt = conn.prepareStatement(
                    "INSERT INTO payments (booking_id, amount, payment_method, status) " +
                            "VALUES (?, ?, ?, 'COMPLETED')");
            paymentStmt.setInt(1, bookingId);
            paymentStmt.setDouble(2, amount);
            paymentStmt.setString(3, paymentMethod);
            paymentStmt.executeUpdate();

            // Update booking status
            PreparedStatement updateStmt = conn.prepareStatement(
                    "UPDATE bookings SET status = 'PAID' WHERE booking_id = ?");
            updateStmt.setInt(1, bookingId);
            updateStmt.executeUpdate();

            System.out.println("Payment processed successfully!");

        } catch (SQLException e) {
            System.err.println("Error processing payment: " + e.getMessage());
        }
    }

    private static void assignHousekeepingTask() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            System.out.print("Enter room ID: ");
            int roomId = Integer.parseInt(scanner.nextLine());

            System.out.print("Enter housekeeper ID: ");
            int housekeeperId = Integer.parseInt(scanner.nextLine());

            System.out.print("Enter cleaning date (YYYY-MM-DD): ");
            String cleaningDate = scanner.nextLine();

            PreparedStatement stmt = conn.prepareStatement(
                    "INSERT INTO housekeeping_schedule (room_id, housekeeper_id, cleaning_date, status) " +
                            "VALUES (?, ?, ?, 'PENDING')");
            stmt.setInt(1, roomId);
            stmt.setInt(2, housekeeperId);
            stmt.setString(3, cleaningDate);

            int affected = stmt.executeUpdate();
            if (affected > 0) {
                System.out.println("Housekeeping task assigned successfully!");
            }
        } catch (SQLException e) {
            System.err.println("Error assigning housekeeping task: " + e.getMessage());
        }
    }

    private static void viewHousekeepers() {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT u.user_id, u.first_name, u.last_name, " +
                             "COUNT(h.schedule_id) as pending_tasks " +
                             "FROM users u " +
                             "LEFT JOIN housekeeping_schedule h ON u.user_id = h.housekeeper_id " +
                             "AND h.status = 'PENDING' " +
                             "WHERE u.role = 'HOUSEKEEPING' " +
                             "GROUP BY u.user_id, u.first_name, u.last_name")) {

            ResultSet rs = stmt.executeQuery();
            System.out.println("\nHousekeepers and Their Workload:");
            System.out.println("ID | Name | Pending Tasks");
            System.out.println("-------------------------");

            while (rs.next()) {
                System.out.printf("%d | %s | %d%n",
                        rs.getInt("user_id"),
                        rs.getString("first_name") + " " + rs.getString("last_name"),
                        rs.getInt("pending_tasks"));
            }
        } catch (SQLException e) {
            System.err.println("Error viewing housekeepers: " + e.getMessage());
        }
    }
} 