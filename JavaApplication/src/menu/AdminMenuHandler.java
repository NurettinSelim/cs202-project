package menu;

import controller.AdminMenuController;
import model.*;
import util.UIComponents;
import service.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AdminMenuHandler {
    private final AdminMenuController adminMenuController;
    private JPanel mainPanel;
    private final JFrame parentFrame;

    public AdminMenuHandler(JFrame parentFrame) {
        this.parentFrame = parentFrame;
        this.adminMenuController = new AdminMenuController();
    }

    public JPanel getMainPanel() {
        if (mainPanel == null) {
            createMainPanel();
        }
        return mainPanel;
    }

    private void createMainPanel() {
        mainPanel = UIComponents.createButtonPanel();
        GridBagConstraints gbc = UIComponents.createGBC();

        // Create buttons for all admin functionalities
        JButton addRoomBtn = UIComponents.createStyledButton("Add Room");
        JButton deleteRoomBtn = UIComponents.createStyledButton("Delete Room");
        JButton manageRoomStatusBtn = UIComponents.createStyledButton("Manage Room Status");
        JButton addUserBtn = UIComponents.createStyledButton("Add User Account");
        JButton viewUsersBtn = UIComponents.createStyledButton("View User Accounts");
        JButton revenueReportBtn = UIComponents.createStyledButton("Generate Revenue Report");
        JButton viewBookingsBtn = UIComponents.createStyledButton("View All Booking Records");
        JButton viewHousekeepingBtn = UIComponents.createStyledButton("View All Housekeeping Records");
        JButton viewMostBookedBtn = UIComponents.createStyledButton("View Most Booked Room Types");
        JButton viewEmployeesBtn = UIComponents.createStyledButton("View All Employees");

        // Add action listeners
        addRoomBtn.addActionListener(e -> showAddRoomDialog());
        deleteRoomBtn.addActionListener(e -> showDeleteRoomDialog());
        manageRoomStatusBtn.addActionListener(e -> showManageRoomStatusDialog());
        addUserBtn.addActionListener(e -> showAddUserDialog());
        viewUsersBtn.addActionListener(e -> showUserAccounts());
        revenueReportBtn.addActionListener(e -> showRevenueReport());
        viewBookingsBtn.addActionListener(e -> showAllBookings());
        viewHousekeepingBtn.addActionListener(e -> showHousekeepingRecords());
        viewMostBookedBtn.addActionListener(e -> showMostBookedRoomTypes());
        viewEmployeesBtn.addActionListener(e -> showAllEmployees());

        // Add components to panel
        mainPanel.add(addRoomBtn, gbc);
        mainPanel.add(deleteRoomBtn, gbc);
        mainPanel.add(manageRoomStatusBtn, gbc);
        mainPanel.add(addUserBtn, gbc);
        mainPanel.add(viewUsersBtn, gbc);
        mainPanel.add(revenueReportBtn, gbc);
        mainPanel.add(viewBookingsBtn, gbc);
        mainPanel.add(viewHousekeepingBtn, gbc);
        mainPanel.add(viewMostBookedBtn, gbc);
        mainPanel.add(viewEmployeesBtn, gbc);
    }

    private void showAddRoomDialog() {
        JDialog dialog = UIComponents.createStyledDialog(parentFrame, "Add New Room", UIComponents.SMALL_DIALOG_SIZE);
        JPanel panel = UIComponents.createMainPanel();

        // Form fields
        JTextField roomNumberField = new JTextField(10);

        // Create combo boxes for room types and statuses
        DefaultComboBoxModel<RoomType> roomTypeModel = new DefaultComboBoxModel<>();
        JComboBox<RoomType> roomTypeCombo = new JComboBox<>(roomTypeModel);
        roomTypeCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
                    boolean cellHasFocus) {
                if (value instanceof RoomType) {
                    RoomType type = (RoomType) value;
                    value = type.getTypeName() + " (Capacity: " + type.getCapacity() + ", Price: $"
                            + type.getBasePrice() + ")";
                }
                return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            }
        });

        DefaultComboBoxModel<RoomStatus> roomStatusModel = new DefaultComboBoxModel<>();
        JComboBox<RoomStatus> roomStatusCombo = new JComboBox<>(roomStatusModel);
        roomStatusCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
                    boolean cellHasFocus) {
                if (value instanceof RoomStatus) {
                    value = ((RoomStatus) value).getStatusName();
                }
                return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            }
        });

        // Populate combo boxes
        try {
            List<RoomType> roomTypes = adminMenuController.getRoomTypes();
            for (RoomType type : roomTypes) {
                roomTypeModel.addElement(type);
            }

            List<RoomStatus> roomStatuses = adminMenuController.getRoomStatuses();
            for (RoomStatus status : roomStatuses) {
                roomStatusModel.addElement(status);
            }
        } catch (SQLException ex) {
            UIComponents.showError(dialog, "Error loading room types and statuses: " + ex.getMessage());
            dialog.dispose();
            return;
        }

        // Layout
        panel.setLayout(new GridLayout(4, 2, 5, 5));
        panel.add(new JLabel("Room Number:"));
        panel.add(roomNumberField);
        panel.add(new JLabel("Room Type:"));
        panel.add(roomTypeCombo);
        panel.add(new JLabel("Status:"));
        panel.add(roomStatusCombo);

        JButton submitButton = UIComponents.createStyledButton("Add Room");
        submitButton.addActionListener(e -> {
            try {
                RoomType selectedType = (RoomType) roomTypeCombo.getSelectedItem();
                RoomStatus selectedStatus = (RoomStatus) roomStatusCombo.getSelectedItem();

                if (selectedType == null || selectedStatus == null) {
                    UIComponents.showError(dialog, "Please select both room type and status");
                    return;
                }

                adminMenuController.addRoom(
                        Integer.parseInt(roomNumberField.getText()),
                        selectedType.getTypeId(),
                        selectedStatus.getStatusId());
                UIComponents.showInfo(dialog, "Room added successfully!");
                dialog.dispose();
            } catch (NumberFormatException ex) {
                UIComponents.showError(dialog, "Please enter a valid room number");
            } catch (SQLException ex) {
                UIComponents.showError(dialog, "Error adding room: " + ex.getMessage());
            }
        });

        panel.add(submitButton);
        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void showDeleteRoomDialog() {
        JDialog dialog = UIComponents.createStyledDialog(parentFrame, "Delete Room", UIComponents.SMALL_DIALOG_SIZE);
        JPanel panel = UIComponents.createMainPanel();

        // Create combo box for rooms
        DefaultComboBoxModel<Room> roomModel = new DefaultComboBoxModel<>();
        JComboBox<Room> roomCombo = new JComboBox<>(roomModel);
        roomCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
                    boolean cellHasFocus) {
                if (value instanceof Room) {
                    Room room = (Room) value;
                    value = String.format("Room %s - %s (%s)",
                            room.getRoomNumber(),
                            room.getRoomType().getTypeName(),
                            room.getStatus().getStatusName());
                }
                return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            }
        });

        // Populate combo box
        try {
            List<Room> rooms = adminMenuController.getRooms();
            for (Room room : rooms) {
                roomModel.addElement(room);
            }
        } catch (SQLException ex) {
            UIComponents.showError(dialog, "Error loading rooms: " + ex.getMessage());
            dialog.dispose();
            return;
        }

        panel.setLayout(new GridLayout(2, 2, 5, 5));
        panel.add(new JLabel("Select Room:"));
        panel.add(roomCombo);

        JButton deleteButton = UIComponents.createStyledButton("Delete Room");
        deleteButton.addActionListener(e -> {
            try {
                Room selectedRoom = (Room) roomCombo.getSelectedItem();

                if (selectedRoom == null) {
                    UIComponents.showError(dialog, "Please select a room");
                    return;
                }

                if (UIComponents.showConfirmDialog(dialog,
                        "Are you sure you want to delete room " + selectedRoom.getRoomNumber() + "?\n" +
                                "This action cannot be undone if the room has no active bookings or pending tasks.",
                        "Confirm Deletion")) {
                    adminMenuController.deleteRoom(Integer.parseInt(selectedRoom.getRoomNumber()));
                    UIComponents.showInfo(dialog, "Room deleted successfully!");
                    dialog.dispose();
                }
            } catch (SQLException ex) {
                UIComponents.showError(dialog, "Error deleting room: " + ex.getMessage());
            }
        });

        panel.add(deleteButton);
        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void showManageRoomStatusDialog() {
        JDialog dialog = UIComponents.createStyledDialog(parentFrame, "Manage Room Status",
                UIComponents.SMALL_DIALOG_SIZE);
        JPanel panel = UIComponents.createMainPanel();

        // Create combo box for rooms
        DefaultComboBoxModel<Room> roomModel = new DefaultComboBoxModel<>();
        JComboBox<Room> roomCombo = new JComboBox<>(roomModel);
        roomCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
                    boolean cellHasFocus) {
                if (value instanceof Room) {
                    Room room = (Room) value;
                    value = String.format("Room %s - %s (Current Status: %s)",
                            room.getRoomNumber(),
                            room.getRoomType().getTypeName(),
                            room.getStatus().getStatusName());
                }
                return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            }
        });

        // Create combo box for statuses
        DefaultComboBoxModel<RoomStatus> statusModel = new DefaultComboBoxModel<>();
        JComboBox<RoomStatus> statusCombo = new JComboBox<>(statusModel);
        statusCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
                    boolean cellHasFocus) {
                if (value instanceof RoomStatus) {
                    value = ((RoomStatus) value).getStatusName();
                }
                return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            }
        });

        // Populate combo boxes
        try {
            List<Room> rooms = adminMenuController.getRooms();
            for (Room room : rooms) {
                roomModel.addElement(room);
            }

            List<RoomStatus> statuses = adminMenuController.getRoomStatuses();
            for (RoomStatus status : statuses) {
                statusModel.addElement(status);
            }
        } catch (SQLException ex) {
            UIComponents.showError(dialog, "Error loading rooms and statuses: " + ex.getMessage());
            dialog.dispose();
            return;
        }

        panel.setLayout(new GridLayout(3, 2, 5, 5));
        panel.add(new JLabel("Select Room:"));
        panel.add(roomCombo);
        panel.add(new JLabel("New Status:"));
        panel.add(statusCombo);

        JButton updateButton = UIComponents.createStyledButton("Update Status");
        updateButton.addActionListener(e -> {
            try {
                Room selectedRoom = (Room) roomCombo.getSelectedItem();
                RoomStatus selectedStatus = (RoomStatus) statusCombo.getSelectedItem();

                if (selectedRoom == null || selectedStatus == null) {
                    UIComponents.showError(dialog, "Please select both room and status");
                    return;
                }

                adminMenuController.manageRoomStatus(
                        Integer.parseInt(selectedRoom.getRoomNumber()),
                        selectedStatus.getStatusId());
                UIComponents.showInfo(dialog, "Room status updated successfully!");
                dialog.dispose();
            } catch (SQLException ex) {
                UIComponents.showError(dialog, "Error updating room status: " + ex.getMessage());
            }
        });

        panel.add(updateButton);
        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void showAddUserDialog() {
        JDialog dialog = UIComponents.createStyledDialog(parentFrame, "Add User Account",
                UIComponents.SMALL_DIALOG_SIZE);
        JPanel panel = UIComponents.createMainPanel();

        JTextField firstNameField = new JTextField(20);
        JTextField lastNameField = new JTextField(20);
        JTextField phoneField = new JTextField(20);

        panel.setLayout(new GridLayout(4, 2, 5, 5));
        panel.add(new JLabel("First Name:"));
        panel.add(firstNameField);
        panel.add(new JLabel("Last Name:"));
        panel.add(lastNameField);
        panel.add(new JLabel("Phone:"));
        panel.add(phoneField);

        JButton addButton = UIComponents.createStyledButton("Add User");
        addButton.addActionListener(e -> {
            try {
                adminMenuController.addUser(
                        firstNameField.getText().trim(),
                        lastNameField.getText().trim(),
                        phoneField.getText().trim());
                UIComponents.showInfo(dialog, "User added successfully!");
                dialog.dispose();
            } catch (Exception ex) {
                String context = "Failed to add user";
                if (ex.getCause().getMessage().contains("users_chk_1")) {
                    context = "Phone number must be in format: XXX-XXX-XXXX";
                } else if (ex.getCause().getMessage().contains("Duplicate entry")) {
                    context = "A user with this phone number already exists";
                }
                UIComponents.handleException(dialog, ex, context);
            }
        });

        panel.add(new JLabel("Phone Format: XXX-XXX-XXXX"));
        panel.add(addButton);
        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void showUserAccounts() {
        JDialog dialog = UIComponents.createStyledDialog(parentFrame, "User Accounts", UIComponents.LARGE_DIALOG_SIZE);
        JPanel panel = UIComponents.createMainPanel();

        String[] columns = { "User ID", "First Name", "Last Name", "Phone", "Created At" };
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable table = UIComponents.createTable(model, false);
        JScrollPane scrollPane = new JScrollPane(table);

        try {
            List<User> users = adminMenuController.viewUserAccounts();
            for (User user : users) {
                model.addRow(new Object[] {
                        user.getUserId(),
                        user.getFirstName(),
                        user.getLastName(),
                        user.getPhone(),
                        user.getCreatedAt()
                });
            }
        } catch (SQLException ex) {
            UIComponents.showError(dialog, "Error loading users: " + ex.getMessage());
        }

        panel.add(scrollPane);
        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void showRevenueReport() {
        JDialog dialog = UIComponents.createStyledDialog(parentFrame, "Revenue Report", UIComponents.DIALOG_SIZE);
        JPanel panel = UIComponents.createMainPanel();

        String[] columns = { "Total Bookings", "Total Revenue", "Average Revenue per Booking" };
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable table = UIComponents.createTable(model, false);
        JScrollPane scrollPane = new JScrollPane(table);

        try {
            HotelService.Revenue report = adminMenuController.generateRevenueReport();
            model.addRow(new Object[] {
                    report.totalBookings(),
                    report.totalRevenue(),
                    report.averageRevenuePerBooking()
            });
        } catch (SQLException ex) {
            UIComponents.showError(dialog, "Error generating revenue report: " + ex.getMessage());
        }

        panel.add(scrollPane);
        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void showAllBookings() {
        JDialog dialog = UIComponents.createStyledDialog(parentFrame, "All Booking Records",
                UIComponents.LARGE_DIALOG_SIZE);
        JPanel panel = UIComponents.createMainPanel();

        String[] columns = { "Booking ID", "Guest", "Check In", "Check Out", "Status", "Total Guests" };
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable table = UIComponents.createTable(model, false);
        JScrollPane scrollPane = new JScrollPane(table);

        try {
            ArrayList<Booking> bookings = adminMenuController.viewBookingRecords();
            for (Booking booking : bookings) {
                model.addRow(new Object[] {
                        booking.getBookingId(),
                        booking.getGuest().getFirstName() + " " + booking.getGuest().getLastName(),
                        booking.getCheckInDate(),
                        booking.getCheckOutDate(),
                        booking.getStatus().getStatusName(),
                        booking.getTotalGuests()
                });
            }
        } catch (SQLException ex) {
            UIComponents.showError(dialog, "Error loading bookings: " + ex.getMessage());
        }

        panel.add(scrollPane);
        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void showHousekeepingRecords() {
        JDialog dialog = UIComponents.createStyledDialog(parentFrame, "Housekeeping Records",
                UIComponents.LARGE_DIALOG_SIZE);
        JPanel panel = UIComponents.createMainPanel();

        String[] columns = { "Schedule ID", "Room", "Staff", "Date", "Status" };
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable table = UIComponents.createTable(model, false);
        JScrollPane scrollPane = new JScrollPane(table);

        try {
            List<HousekeepingSchedule> schedules = adminMenuController.viewHousekeepingRecords();
            for (HousekeepingSchedule schedule : schedules) {
                model.addRow(new Object[] {
                        schedule.getScheduleId(),
                        schedule.getRoom().getRoomNumber(),
                        schedule.getStaff().getFirstName() + " " + schedule.getStaff().getLastName(),
                        schedule.getScheduledDate(),
                        schedule.getStatus().getStatusName()
                });
            }
        } catch (SQLException ex) {
            UIComponents.showError(dialog, "Error loading housekeeping records: " + ex.getMessage());
        }

        panel.add(scrollPane);
        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void showMostBookedRoomTypes() {
        JDialog dialog = UIComponents.createStyledDialog(parentFrame, "Most Booked Room Types", UIComponents.DIALOG_SIZE);
        JPanel panel = UIComponents.createMainPanel();
        panel.setLayout(new BorderLayout());

        // Create date selection panel
        JPanel datePanel = new JPanel(new GridLayout(2, 2, 5, 5));
        JTextField startDateField = new JTextField(10);
        JTextField endDateField = new JTextField(10);
        
        datePanel.add(new JLabel("Start Date (YYYY-MM-DD):"));
        datePanel.add(startDateField);
        datePanel.add(new JLabel("End Date (YYYY-MM-DD):"));
        datePanel.add(endDateField);

        // Create table panel
        JPanel tablePanel = new JPanel(new BorderLayout());
        String[] columns = {"Room Type", "Total Bookings"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable table = UIComponents.createTable(model, false);
        JScrollPane scrollPane = new JScrollPane(table);

        // Create search button
        JButton searchButton = UIComponents.createStyledButton("Search");
        searchButton.addActionListener(e -> {
            try {
                String startDate = startDateField.getText().trim();
                String endDate = endDateField.getText().trim();

                // Clear existing rows
                model.setRowCount(0);

                // Fetch and display new data
                List<BookingService.RoomTypeStats> stats = adminMenuController.viewMostBookedRoomTypes(startDate, endDate);
                for (BookingService.RoomTypeStats stat : stats) {
                    model.addRow(new Object[]{
                        stat.typeName(),
                        stat.bookingCount(),
                    });
                }
            } catch (SQLException ex) {
                UIComponents.showError(dialog, "Error loading most booked room types: " + ex.getMessage());
            }
        });

        // Add components to panels
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(datePanel, BorderLayout.CENTER);
        topPanel.add(searchButton, BorderLayout.SOUTH);

        tablePanel.add(scrollPane, BorderLayout.CENTER);

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(tablePanel, BorderLayout.CENTER);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void showAllEmployees() {
        JDialog dialog = UIComponents.createStyledDialog(parentFrame, "All Employees", UIComponents.DIALOG_SIZE);
        JPanel panel = UIComponents.createMainPanel();

        String[] columns = { "Name", "Role", "Phone", "Hire Date", "Salary" };
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable table = UIComponents.createTable(model, false);
        JScrollPane scrollPane = new JScrollPane(table);

        try {
            HashMap<Staff, String> employees = adminMenuController.viewAllEmployees();
            for (Staff employee : employees.keySet()) {
                model.addRow(new Object[] {
                        employee.getFirstName() + " " + employee.getLastName(),
                        employees.get(employee),
                        employee.getPhone(),
                        employee.getCreatedAt(),
                        employee.getSalary()
                });
            }
        } catch (SQLException ex) {
            UIComponents.showError(dialog, "Error loading employees: " + ex.getMessage());
        }

        panel.add(scrollPane);
        dialog.add(panel);
        dialog.setVisible(true);
    }
}