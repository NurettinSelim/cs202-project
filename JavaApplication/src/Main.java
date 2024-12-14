import menu.AdminMenuHandler;
import menu.GuestMenuHandler;
import menu.HousekeepingMenuHandler;
import menu.ReceptionistMenuHandler;
import model.User;
import service.*;
import service.impl.*;
import util.DatabaseConnection;

import java.util.Optional;
import java.util.Scanner;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static final UserService userService = new UserServiceImpl();
    private static final HotelService hotelService = new HotelServiceImpl();

    public static void main(String[] args) {
        while (true) {
            if (!userService.isLoggedIn()) {
                if (!login()) {
                    continue;
                }
            }

            displayMainMenu();
            int choice = getUserChoice();

            if (choice == 5) {
                System.out.println("Thank you for using the Hotel Management System!");
                DatabaseConnection.closeConnection();
                System.exit(0);
            }

            boolean validChoice = false;
            String userRole = userService.getUserRole(userService.getCurrentUser());
            switch (userRole) {
                case "GUEST":
                    validChoice = choice == 1;
                    break;
                case "ADMINISTRATOR":
                    validChoice = choice == 2;
                    break;
                case "RECEPTIONIST":
                    validChoice = choice == 3;
                    break;
                case "HOUSEKEEPING":
                    validChoice = choice == 4;
                    break;
            }

            if (!validChoice) {
                System.out.println("You don't have permission to access this menu. Please select your designated menu.");
                continue;
            }

            switch (choice) {
                case 1:
                    GuestMenuHandler.handleMenu();
                    break;
                case 2:
                    AdminMenuHandler.handleMenu();
                    break;
                case 3:
                    ReceptionistMenuHandler.handleMenu();
                    break;
                case 4:
                    HousekeepingMenuHandler.handleMenu();
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static boolean login() {
        System.out.println("\n=== Login ===");
        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();

        try {
            if (userService.authenticate(username, password)) {
                Optional<User> user = userService.findByUsername(username);
                if (user.isPresent()) {
                    userService.setCurrentUser(user.get());
                    System.out.println("Welcome, " + user.get().getUsername() + "!");
                    return true;
                }
            }
            System.out.println("Invalid username or password. Please try again.");
            return false;
        } catch (Exception e) {
            System.err.println("Error during login: " + e.getMessage());
            return false;
        }
    }

    private static void displayMainMenu() {
        System.out.println("\n=== Hotel Management System ===");
        System.out.println("1. Guest Access");
        System.out.println("2. Administrator Access");
        System.out.println("3. Receptionist Access");
        System.out.println("4. Housekeeping Access");
        System.out.println("5. Exit");
        System.out.print("Enter your choice: ");
    }

    private static int getUserChoice() {
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    // Helper method to get the current logged-in user
    public static User getCurrentUser() {
        return userService.getCurrentUser();
    }
}