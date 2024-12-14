package menu;

import util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class GuestMenuHandler {
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
                    viewAvailableRooms();
                    break;
                case 3:
                    viewMyBookings();
                    break;
                case 4:
                    cancelBooking();
                    break;
                case 5:
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void displayMenu() {
        System.out.println("\n=== Guest Menu ===");
        System.out.println("1. Add New Booking");
        System.out.println("2. View Available Rooms");
        System.out.println("3. View My Bookings");
        System.out.println("4. Cancel Booking");
        System.out.println("5. Return to Main Menu");
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
            // Implementation for adding new booking
            System.out.println("Enter guest ID: ");
            int guestId = Integer.parseInt(scanner.nextLine());

            System.out.println("Enter check-in date (YYYY-MM-DD): ");
            String checkInDate = scanner.nextLine();

            System.out.println("Enter check-out date (YYYY-MM-DD): ");
            String checkOutDate = scanner.nextLine();

            // Add validation and booking logic here

        } catch (SQLException e) {
            System.err.println("Error while adding booking: " + e.getMessage());
        }
    }

    private static void viewAvailableRooms() {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT * FROM rooms WHERE status = 'AVAILABLE'")) {

            ResultSet rs = stmt.executeQuery();
            System.out.println("\nAvailable Rooms:");
            System.out.println("Room ID | Type | Price | Status");
            System.out.println("--------------------------------");

            while (rs.next()) {
                System.out.printf("%7d | %4s | %5.2f | %s%n",
                        rs.getInt("room_id"),
                        rs.getString("room_type"),
                        rs.getDouble("price"),
                        rs.getString("status"));
            }

        } catch (SQLException e) {
            System.err.println("Error viewing available rooms: " + e.getMessage());
        }
    }

    private static void viewMyBookings() {
        System.out.print("Enter your guest ID: ");
        int guestId = Integer.parseInt(scanner.nextLine());

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT * FROM bookings WHERE guest_id = ?")) {

            stmt.setInt(1, guestId);
            ResultSet rs = stmt.executeQuery();

            System.out.println("\nYour Bookings:");
            System.out.println("Booking ID | Room ID | Check-in | Check-out | Status");
            System.out.println("------------------------------------------------");

            while (rs.next()) {
                System.out.printf("%10d | %7d | %9s | %9s | %s%n",
                        rs.getInt("booking_id"),
                        rs.getInt("room_id"),
                        rs.getDate("check_in_date"),
                        rs.getDate("check_out_date"),
                        rs.getString("status"));
            }

        } catch (SQLException e) {
            System.err.println("Error viewing bookings: " + e.getMessage());
        }
    }

    private static void cancelBooking() {
        System.out.print("Enter booking ID to cancel: ");
        int bookingId = Integer.parseInt(scanner.nextLine());

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "UPDATE bookings SET status = 'CANCELLED' WHERE booking_id = ? AND status = 'PENDING'")) {

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
}