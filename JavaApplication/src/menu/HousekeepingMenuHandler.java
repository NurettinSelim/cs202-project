package menu;

import util.DatabaseConnection;

import java.sql.*;
import java.util.Scanner;

public class HousekeepingMenuHandler {
    private static Scanner scanner = new Scanner(System.in);

    public static void handleMenu() {
        while (true) {
            displayMenu();
            int choice = getUserChoice();

            switch (choice) {
                case 1:
                    viewPendingTasks();
                    break;
                case 2:
                    viewCompletedTasks();
                    break;
                case 3:
                    updateTaskStatus();
                    break;
                case 4:
                    viewSchedule();
                    break;
                case 5:
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void displayMenu() {
        System.out.println("\n=== Housekeeping Menu ===");
        System.out.println("1. View Pending Tasks");
        System.out.println("2. View Completed Tasks");
        System.out.println("3. Update Task Status");
        System.out.println("4. View My Cleaning Schedule");
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

    private static void viewPendingTasks() {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT h.schedule_id, r.room_number, h.cleaning_date " +
                             "FROM housekeeping_schedule h " +
                             "JOIN rooms r ON h.room_id = r.room_id " +
                             "WHERE h.status = 'PENDING' " +
                             "ORDER BY h.cleaning_date")) {

            ResultSet rs = stmt.executeQuery();
            System.out.println("\nPending Tasks:");
            System.out.println("Schedule ID | Room | Date");
            System.out.println("-------------------------");

            while (rs.next()) {
                System.out.printf("%11d | %s | %s%n",
                        rs.getInt("schedule_id"),
                        rs.getString("room_number"),
                        rs.getDate("cleaning_date"));
            }
        } catch (SQLException e) {
            System.err.println("Error viewing pending tasks: " + e.getMessage());
        }
    }

    private static void viewCompletedTasks() {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT h.schedule_id, r.room_number, h.cleaning_date, h.completion_date " +
                             "FROM housekeeping_schedule h " +
                             "JOIN rooms r ON h.room_id = r.room_id " +
                             "WHERE h.status = 'COMPLETED' " +
                             "ORDER BY h.completion_date DESC")) {

            ResultSet rs = stmt.executeQuery();
            System.out.println("\nCompleted Tasks:");
            System.out.println("Schedule ID | Room | Cleaning Date | Completion Date");
            System.out.println("------------------------------------------------");

            while (rs.next()) {
                System.out.printf("%11d | %s | %s | %s%n",
                        rs.getInt("schedule_id"),
                        rs.getString("room_number"),
                        rs.getDate("cleaning_date"),
                        rs.getDate("completion_date"));
            }
        } catch (SQLException e) {
            System.err.println("Error viewing completed tasks: " + e.getMessage());
        }
    }

    private static void updateTaskStatus() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            System.out.print("Enter schedule ID to mark as completed: ");
            int scheduleId = Integer.parseInt(scanner.nextLine());

            PreparedStatement stmt = conn.prepareStatement(
                    "UPDATE housekeeping_schedule SET status = 'COMPLETED', " +
                            "completion_date = CURRENT_DATE " +
                            "WHERE schedule_id = ? AND status = 'PENDING'");
            stmt.setInt(1, scheduleId);

            int affected = stmt.executeUpdate();
            if (affected > 0) {
                System.out.println("Task marked as completed!");
            } else {
                System.out.println("Could not update task. It may not exist or already be completed.");
            }
        } catch (SQLException e) {
            System.err.println("Error updating task status: " + e.getMessage());
        }
    }

    private static void viewSchedule() {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT h.schedule_id, r.room_number, h.cleaning_date, h.status " +
                             "FROM housekeeping_schedule h " +
                             "JOIN rooms r ON h.room_id = r.room_id " +
                             "WHERE h.housekeeper_id = ? " +
                             "AND h.cleaning_date >= CURRENT_DATE " +
                             "ORDER BY h.cleaning_date")) {

            System.out.print("Enter your housekeeper ID: ");
            int housekeeperId = Integer.parseInt(scanner.nextLine());
            stmt.setInt(1, housekeeperId);

            ResultSet rs = stmt.executeQuery();
            System.out.println("\nMy Cleaning Schedule:");
            System.out.println("Schedule ID | Room | Date | Status");
            System.out.println("--------------------------------");

            while (rs.next()) {
                System.out.printf("%11d | %s | %s | %s%n",
                        rs.getInt("schedule_id"),
                        rs.getString("room_number"),
                        rs.getDate("cleaning_date"),
                        rs.getString("status"));
            }
        } catch (SQLException e) {
            System.err.println("Error viewing schedule: " + e.getMessage());
        }
    }
} 