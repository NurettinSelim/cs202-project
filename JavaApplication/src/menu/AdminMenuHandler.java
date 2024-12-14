package menu;

import model.*;
import service.*;
import service.impl.*;
import util.DatabaseConnection;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

public class AdminMenuHandler {
    private static final Scanner scanner = new Scanner(System.in);
    private static final UserService userService = new UserServiceImpl();
    private static final HotelService hotelService = new HotelServiceImpl();
    private static final RoomService roomService = new RoomServiceImpl();
    private static final BookingService bookingService = new BookingServiceImpl();
    private static final StaffService staffService = new StaffServiceImpl();
    private static final RoomTypeService roomTypeService = new RoomTypeServiceImpl();

    public static void handleMenu() {
        while (true) {
            displayMenu();
            int choice = getUserChoice();

            switch (choice) {
                case 1:
                    addRoom();
                    break;
                case 2:
                    deleteRoom();
                    break;
                case 3:
                    manageRoomStatus();
                    break;
                case 4:
                    viewUserAccounts();
                    break;
                case 5:
                    generateRevenueReport();
                    break;
                case 6:
                    viewBookingRecords();
                    break;
                case 7:
                    viewHousekeepingRecords();
                    break;
                case 8:
                    addUserAccount();
                    break;
                case 9:
                    viewMostBookedRoomTypes();
                    break;
                case 10:
                    viewAllEmployees();
                    break;
                case 11:
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void displayMenu() {
        System.out.println("\n=== Administrator Menu ===");
        System.out.println("1. Add Room");
        System.out.println("2. Delete Room");
        System.out.println("3. Manage Room Status");
        System.out.println("4. View User Accounts");
        System.out.println("5. Generate Revenue Report");
        System.out.println("6. View All Booking Records");
        System.out.println("7. View All Housekeeping Records");
        System.out.println("8. Add User Account");
        System.out.println("9. View Most Booked Room Types");
        System.out.println("10. View All Employees");
        System.out.println("11. Return to Main Menu");
        System.out.print("Enter your choice: ");
    }

    private static int getUserChoice() {
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private static void addRoom() {
        try {
            // Get the current admin's hotel
            AdministratorStaff admin = (AdministratorStaff) userService.getCurrentUser();
            Hotel hotel = admin.getHotel();

            System.out.print("Enter room number: ");
            String roomNumber = scanner.nextLine();

            // Display available room types
            System.out.println("\nAvailable room types:");
            List<RoomType> roomTypes = roomTypeService.findByHotel(hotel);
            for (RoomType type : roomTypes) {
                System.out.printf("%d. %s (Capacity: %d, Beds: %d, Price: $%.2f)%n",
                    type.getTypeId(), type.getTypeName(), type.getCapacity(),
                    type.getBedCount(), type.getBasePrice());
            }

            System.out.print("Enter room type ID: ");
            int typeId = Integer.parseInt(scanner.nextLine());

            // Create and save the room
            Room room = new Room();
            room.setHotel(hotel);
            room.setRoomNumber(roomNumber);
            room.setRoomType(roomTypes.stream()
                .filter(t -> t.getTypeId() == typeId)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid room type ID")));
            
            // Set initial status as available
            RoomStatus status = new RoomStatus();
            status.setStatusId(1); // Assuming 1 is "Available"
            status.setStatusName("AVAILABLE");
            room.setStatus(status);

            roomService.create(room);
            System.out.println("Room added successfully!");

        } catch (Exception e) {
            System.err.println("Error adding room: " + e.getMessage());
        }
    }

    private static void deleteRoom() {
        try {
            AdministratorStaff admin = (AdministratorStaff) userService.getCurrentUser();
            Hotel hotel = admin.getHotel();

            System.out.print("Enter room number: ");
            String roomNumber = scanner.nextLine();

            // Check if room exists and can be deleted
            if (roomService.isRoomAvailable(hotel, roomNumber, Date.valueOf(LocalDate.now()),
                    Date.valueOf(LocalDate.now().plusDays(1)))) {
                roomService.delete(roomNumber);
                System.out.println("Room deleted successfully!");
            } else {
                System.out.println("Room cannot be deleted: It has active bookings or is not available.");
            }
        } catch (Exception e) {
            System.err.println("Error deleting room: " + e.getMessage());
        }
    }

    private static void manageRoomStatus() {
        try {
            AdministratorStaff admin = (AdministratorStaff) userService.getCurrentUser();
            Hotel hotel = admin.getHotel();

            System.out.print("Enter room number: ");
            String roomNumber = scanner.nextLine();

            System.out.println("Select new status:");
            System.out.println("1. Available");
            System.out.println("2. Maintenance");
            System.out.println("3. Out of Service");

            int statusChoice = Integer.parseInt(scanner.nextLine());
            RoomStatus newStatus = new RoomStatus();
            newStatus.setStatusId(statusChoice);
            switch (statusChoice) {
                case 1:
                    newStatus.setStatusName("AVAILABLE");
                    break;
                case 2:
                    newStatus.setStatusName("MAINTENANCE");
                    break;
                case 3:
                    newStatus.setStatusName("OUT_OF_SERVICE");
                    break;
                default:
                    System.out.println("Invalid status choice.");
                    return;
            }

            roomService.updateRoomStatus(hotel, roomNumber, newStatus);
            System.out.println("Room status updated successfully!");
        } catch (Exception e) {
            System.err.println("Error updating room status: " + e.getMessage());
        }
    }

    private static void viewUserAccounts() {
        try {
            List<User> users = userService.findAll();
            System.out.println("\nUser Accounts:");
            System.out.println("ID | Username | Role | Name | Phone");
            System.out.println("--------------------------------");

            for (User user : users) {
                System.out.printf("%d | %s | %s | %s %s | %s%n",
                    user.getUserId(),
                    user.getUsername(),
                    userService.getUserRole(user),
                    user.getFirstName(),
                    user.getLastName(),
                    user.getPhone());
            }
        } catch (Exception e) {
            System.err.println("Error viewing user accounts: " + e.getMessage());
        }
    }

    private static void generateRevenueReport() {
        try {
            AdministratorStaff admin = (AdministratorStaff) userService.getCurrentUser();
            Hotel hotel = admin.getHotel();

            System.out.println("\nRevenue Report");
            System.out.println("------------------------");

            // Get all bookings for the hotel
            List<Booking> bookings = bookingService.findByHotel(hotel);
            
            double totalRevenue = 0;
            int completedBookings = 0;

            for (Booking booking : bookings) {
                if (booking.getStatus().getStatusName().equals("COMPLETED")) {
                    totalRevenue += bookingService.calculateTotalPrice(booking.getBookingId());
                    completedBookings++;
                }
            }

            double avgBookingValue = completedBookings > 0 ? totalRevenue / completedBookings : 0;

            System.out.printf("Total Revenue: $%.2f%n", totalRevenue);
            System.out.printf("Total Completed Bookings: %d%n", completedBookings);
            System.out.printf("Average Booking Value: $%.2f%n", avgBookingValue);

        } catch (Exception e) {
            System.err.println("Error generating revenue report: " + e.getMessage());
        }
    }

    private static void viewBookingRecords() {
        try {
            AdministratorStaff admin = (AdministratorStaff) userService.getCurrentUser();
            Hotel hotel = admin.getHotel();

            List<Booking> bookings = bookingService.findByHotel(hotel);
            System.out.println("\nBooking Records:");
            System.out.println("Booking ID | Guest | Room | Check-in | Check-out | Status");
            System.out.println("--------------------------------------------------------");

            for (Booking booking : bookings) {
                System.out.printf("%10d | %s | %s | %s | %s | %s%n",
                    booking.getBookingId(),
                    booking.getGuest().getUsername(),
                    booking.getBookingRooms().get(0).getRoom().getRoomNumber(),
                    booking.getCheckInDate(),
                    booking.getCheckOutDate(),
                    booking.getStatus().getStatusName());
            }
        } catch (Exception e) {
            System.err.println("Error viewing booking records: " + e.getMessage());
        }
    }

    private static void viewHousekeepingRecords() {
        try {
            AdministratorStaff admin = (AdministratorStaff) userService.getCurrentUser();
            Hotel hotel = admin.getHotel();

            HousekeepingScheduleService scheduleService = new HousekeepingScheduleServiceImpl();
            List<HousekeepingSchedule> schedules = scheduleService.findByHotel(hotel);

            System.out.println("\nHousekeeping Records:");
            System.out.println("Schedule ID | Room | Date | Status | Housekeeper");
            System.out.println("------------------------------------------------");

            for (HousekeepingSchedule schedule : schedules) {
                System.out.printf("%11d | %s | %s | %s | %s%n",
                    schedule.getScheduleId(),
                    schedule.getRoom().getRoomNumber(),
                    schedule.getScheduledDate(),
                    schedule.getStatus().getStatusName(),
                    schedule.getStaff().getUsername());
            }
        } catch (Exception e) {
            System.err.println("Error viewing housekeeping records: " + e.getMessage());
        }
    }

    private static void addUserAccount() {
        try {
            System.out.print("Enter username: ");
            String username = scanner.nextLine();

            if (!userService.isUsernameUnique(username)) {
                System.out.println("Username already exists!");
                return;
            }

            System.out.print("Enter password: ");
            String password = scanner.nextLine();

            System.out.print("Enter first name: ");
            String firstName = scanner.nextLine();

            System.out.print("Enter last name: ");
            String lastName = scanner.nextLine();

            System.out.print("Enter phone: ");
            String phone = scanner.nextLine();

            if (!userService.isPhoneNumberUnique(phone)) {
                System.out.println("Phone number already exists!");
                return;
            }

            System.out.print("Enter role (ADMINISTRATOR/RECEPTIONIST/HOUSEKEEPING): ");
            String role = scanner.nextLine().toUpperCase();

            // Create base user
            User user = new User();
            user.setUsername(username);
            user.setPassword(password); // In real app, this should be hashed
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setPhone(phone);
            user.setCreatedAt(new Timestamp(System.currentTimeMillis()));

            // Create specific staff type based on role
            Staff staff;
            AdministratorStaff admin = (AdministratorStaff) userService.getCurrentUser();
            Hotel hotel = admin.getHotel();

            switch (role) {
                case "ADMINISTRATOR":
                    staff = new AdministratorStaff();
                    break;
                case "RECEPTIONIST":
                    staff = new ReceptionistStaff();
                    break;
                case "HOUSEKEEPING":
                    staff = new HousekeepingStaff();
                    break;
                default:
                    System.out.println("Invalid role!");
                    return;
            }

            // Set staff properties
            staff.setUsername(username);
            staff.setPassword(password);
            staff.setFirstName(firstName);
            staff.setLastName(lastName);
            staff.setPhone(phone);
            staff.setCreatedAt(user.getCreatedAt());
            staff.setHotel(hotel);
            staff.setSalary(new BigDecimal("0.00")); // Initial salary
            staff.setHireDate(Date.valueOf(LocalDate.now()));

            // Save the staff member
            staffService.create(staff);
            System.out.println("User account added successfully!");

        } catch (Exception e) {
            System.err.println("Error adding user account: " + e.getMessage());
        }
    }

    private static void viewMostBookedRoomTypes() {
        try {
            AdministratorStaff admin = (AdministratorStaff) userService.getCurrentUser();
            Hotel hotel = admin.getHotel();

            RoomTypeService roomTypeService = new RoomTypeServiceImpl();
            List<RoomType> popularTypes = roomTypeService.findMostPopularTypes(hotel, 5);

            System.out.println("\nMost Booked Room Types:");
            System.out.println("Room Type | Occupancy Rate | Total Bookings");
            System.out.println("----------------------------------------");

            for (RoomType type : popularTypes) {
                double occupancyRate = roomTypeService.getOccupancyRateByType(type.getTypeId());
                System.out.printf("%s | %.2f%% | %d bookings%n",
                    type.getTypeName(),
                    occupancyRate * 100,
                    type.getCapacity());
            }
        } catch (Exception e) {
            System.err.println("Error viewing most booked room types: " + e.getMessage());
        }
    }

    private static void viewAllEmployees() {
        try {
            AdministratorStaff admin = (AdministratorStaff) userService.getCurrentUser();
            Hotel hotel = admin.getHotel();

            List<Staff> employees = staffService.findByHotelAndRole(hotel, "STAFF");
            System.out.println("\nAll Employees:");
            System.out.println("ID | Username | Role | Name | Hire Date | Salary");
            System.out.println("------------------------------------------------");

            for (Staff employee : employees) {
                System.out.printf("%d | %s | %s | %s %s | %s | $%.2f%n",
                    employee.getUserId(),
                    employee.getUsername(),
                    userService.getUserRole(employee),
                    employee.getFirstName(),
                    employee.getLastName(),
                    employee.getHireDate(),
                    employee.getSalary());
            }
        } catch (Exception e) {
            System.err.println("Error viewing all employees: " + e.getMessage());
        }
    }
}