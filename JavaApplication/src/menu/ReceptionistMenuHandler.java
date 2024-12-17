package menu;

import controller.ReceptionistMenuController;
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
import java.util.Map;
import java.math.BigDecimal;
import java.util.Vector;

public class ReceptionistMenuHandler {
    private final ReceptionistMenuController receptionistMenuController;
    private JPanel mainPanel;
    private final JFrame parentFrame;

    public ReceptionistMenuHandler(JFrame parentFrame) {
        this.parentFrame = parentFrame;
        this.receptionistMenuController = new ReceptionistMenuController();
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

        // Create buttons for all receptionist functionalities
        JButton addBookingBtn = UIComponents.createStyledButton("Add New Booking");
        JButton modifyBookingBtn = UIComponents.createStyledButton("Modify Booking");
        JButton deleteBookingBtn = UIComponents.createStyledButton("Delete Booking");
        JButton viewBookingsBtn = UIComponents.createStyledButton("View Bookings");
        JButton processPaymentBtn = UIComponents.createStyledButton("Process Payment");
        JButton assignHousekeepingBtn = UIComponents.createStyledButton("Assign Housekeeping Task");
        JButton viewHousekeepingBtn = UIComponents.createStyledButton("View Housekeeping Records");

        // Add action listeners
        addBookingBtn.addActionListener(e -> showAddBookingDialog());
        modifyBookingBtn.addActionListener(e -> showModifyBookingDialog());
        deleteBookingBtn.addActionListener(e -> showDeleteBookingDialog());
        viewBookingsBtn.addActionListener(e -> showViewBookingsDialog());
        processPaymentBtn.addActionListener(e -> showProcessPaymentDialog());
        assignHousekeepingBtn.addActionListener(e -> showAssignHousekeepingDialog());
        viewHousekeepingBtn.addActionListener(e -> showHousekeepingRecordsDialog());

        // Add components to panel
        mainPanel.add(addBookingBtn, gbc);
        mainPanel.add(modifyBookingBtn, gbc);
        mainPanel.add(deleteBookingBtn, gbc);
        mainPanel.add(viewBookingsBtn, gbc);
        mainPanel.add(processPaymentBtn, gbc);
        mainPanel.add(assignHousekeepingBtn, gbc);
        mainPanel.add(viewHousekeepingBtn, gbc);
    }

    private void showAddBookingDialog() {
        JDialog dialog = UIComponents.createStyledDialog(parentFrame, "Add New Booking", UIComponents.LARGE_DIALOG_SIZE);
        JPanel panel = UIComponents.createMainPanel();
        panel.setLayout(new BorderLayout());

        // Create form panel
        JPanel formPanel = new JPanel(new GridLayout(0, 2, 5, 5));
        
        // Guest search
        JTextField guestSearchField = new JTextField(20);
        JComboBox<User> guestCombo = new JComboBox<>();
        guestCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                if (value instanceof User) {
                    User user = (User) value;
                    value = String.format("%s %s (%s)", user.getFirstName(), user.getLastName(), user.getPhone());
                }
                return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            }
        });

        JButton searchButton = UIComponents.createStyledButton("Search Guest");
        searchButton.addActionListener(e -> {
            try {
                String searchTerm = guestSearchField.getText().trim();
                if (!searchTerm.isEmpty()) {
                    List<User> guests = receptionistMenuController.searchGuests(searchTerm);
                    guestCombo.removeAllItems();
                    for (User guest : guests) {
                        guestCombo.addItem(guest);
                    }
                }
            } catch (SQLException ex) {
                UIComponents.handleException(dialog, ex, "Failed to search guests");
            }
        });

        // Date fields
        JTextField checkInField = new JTextField(10);
        JTextField checkOutField = new JTextField(10);
        JTextField totalGuestsField = new JTextField(5);
        
        // Available rooms table
        String[] columns = {"Select", "Room Number", "Room Type", "Capacity", "Price"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public Class<?> getColumnClass(int column) {
                return column == 0 ? Boolean.class : Object.class;
            }
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 0;
            }
        };
        JTable roomsTable = UIComponents.createTable(model, false);
        JScrollPane scrollPane = new JScrollPane(roomsTable);
        scrollPane.setVisible(false);

        JButton checkAvailabilityBtn = UIComponents.createStyledButton("Check Availability");
        checkAvailabilityBtn.addActionListener(e -> {
            try {
                if (checkInField.getText().isEmpty() || checkOutField.getText().isEmpty()) {
                    UIComponents.showWarning(dialog, "Please enter both check-in and check-out dates");
                    return;
                }

                model.setRowCount(0);
                List<Room> rooms = receptionistMenuController.getAvailableRooms(
                    checkInField.getText().trim(),
                    checkOutField.getText().trim()
                );
                
                if (rooms.isEmpty()) {
                    UIComponents.showInfo(dialog, "No rooms available for the selected dates");
                    scrollPane.setVisible(false);
                } else {
                    for (Room room : rooms) {
                        model.addRow(new Object[]{
                            false,
                            room.getRoomNumber(),
                            room.getRoomType().getTypeName(),
                            room.getRoomType().getCapacity(),
                            room.getRoomType().getBasePrice()
                        });
                    }
                    scrollPane.setVisible(true);
                    panel.revalidate();
                    panel.repaint();
                }
                dialog.pack();
            } catch (SQLException ex) {
                UIComponents.handleException(dialog, ex, "Failed to check room availability");
            }
        });

        // Add components to form
        formPanel.add(new JLabel("Search Guest:"));
        formPanel.add(guestSearchField);
        formPanel.add(new JLabel("Select Guest:"));
        formPanel.add(guestCombo);
        formPanel.add(new JLabel(""));
        formPanel.add(searchButton);
        formPanel.add(new JLabel("Check-in Date (YYYY-MM-DD):"));
        formPanel.add(checkInField);
        formPanel.add(new JLabel("Check-out Date (YYYY-MM-DD):"));
        formPanel.add(checkOutField);
        formPanel.add(new JLabel("Total Guests:"));
        formPanel.add(totalGuestsField);
        formPanel.add(new JLabel(""));
        formPanel.add(checkAvailabilityBtn);

        // Create button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton addButton = UIComponents.createStyledButton("Add Booking");
        addButton.addActionListener(e -> {
            try {
                User selectedGuest = (User) guestCombo.getSelectedItem();
                if (selectedGuest == null) {
                    UIComponents.showError(dialog, "Please select a guest");
                    return;
                }

                if (checkInField.getText().isEmpty() || 
                    checkOutField.getText().isEmpty() || 
                    totalGuestsField.getText().isEmpty()) {
                    UIComponents.showWarning(dialog, "Please fill in all fields");
                    return;
                }

                int totalGuestsRequested;
                int totalCapacityOfSelectedRooms = 0;
                Map<String, Integer> selectedRoomNumbers = new HashMap<>();

                try {
                    totalGuestsRequested = Integer.parseInt(totalGuestsField.getText());
                    if (totalGuestsRequested <= 0) {
                        UIComponents.showWarning(dialog, "Total guests must be greater than 0");
                        return;
                    }

                    for (int i = 0; i < model.getRowCount(); i++) {
                        boolean isSelected = (boolean) model.getValueAt(i, 0);
                        if (isSelected) {
                            selectedRoomNumbers.put((String) model.getValueAt(i, 1), (int) model.getValueAt(i, 3));
                            totalCapacityOfSelectedRooms += (int) model.getValueAt(i, 3);
                        }
                    }

                    if (selectedRoomNumbers.isEmpty()) {
                        UIComponents.showWarning(dialog, "Please select at least one room");
                        return;
                    }
                    if (totalGuestsRequested > totalCapacityOfSelectedRooms) {
                        UIComponents.showWarning(dialog, "Total guests requested exceeds the capacity of selected rooms");
                        return;
                    }
                } catch (NumberFormatException ex) {
                    UIComponents.showWarning(dialog, "Please enter a valid number for total guests");
                    return;
                }

                receptionistMenuController.addBooking(
                    selectedGuest.getUserId(),
                    checkInField.getText().trim(),
                    checkOutField.getText().trim(),
                    Integer.parseInt(totalGuestsField.getText().trim()),
                    selectedRoomNumbers
                );
                UIComponents.showInfo(dialog, "Booking added successfully!");
                dialog.dispose();
            } catch (SQLException ex) {
                UIComponents.handleException(dialog, ex, "Failed to add booking");
            }
        });
        buttonPanel.add(addButton);

        // Layout
        panel.setLayout(new BorderLayout());
        panel.add(formPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void showModifyBookingDialog() {
        JDialog dialog = UIComponents.createStyledDialog(parentFrame, "Modify Booking", UIComponents.LARGE_DIALOG_SIZE);
        JPanel panel = UIComponents.createMainPanel();

        // Create table for bookings
        String[] columns = {"Booking ID", "Guest", "Check In", "Check Out", "Status", "Total Guests"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable table = UIComponents.createTable(model, false);
        JScrollPane scrollPane = new JScrollPane(table);

        // Load bookings and statuses
        List<BookingStatus> bookingStatuses = new ArrayList<>();
        DefaultComboBoxModel<BookingStatus> statusModel = new DefaultComboBoxModel<>();
        JComboBox<BookingStatus> statusCombo = new JComboBox<>(statusModel);
        statusCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                if (value instanceof BookingStatus) {
                    value = ((BookingStatus) value).getStatusName();
                }
                return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            }
        });

        try {
            // Load booking statuses
            bookingStatuses = receptionistMenuController.getBookingStatuses();
            for (BookingStatus status : bookingStatuses) {
                statusModel.addElement(status);
            }

            // Load bookings
            List<Booking> bookings = receptionistMenuController.viewAllBookings();
            for (Booking booking : bookings) {
                model.addRow(new Object[]{
                    booking.getBookingId(),
                    booking.getGuest().getFirstName() + " " + booking.getGuest().getLastName(),
                    booking.getCheckInDate(),
                    booking.getCheckOutDate(),
                    booking.getStatus().getStatusName(),
                    booking.getTotalGuests()
                });
            }
        } catch (SQLException ex) {
            UIComponents.handleException(dialog, ex, "Failed to load data");
        }

        // Create form for modification
        JPanel formPanel = new JPanel(new GridLayout(0, 2, 5, 5));
        JTextField checkInField = new JTextField(10);
        JTextField checkOutField = new JTextField(10);
        JTextField totalGuestsField = new JTextField(5);

        formPanel.add(new JLabel("New Check-in Date (YYYY-MM-DD):"));
        formPanel.add(checkInField);
        formPanel.add(new JLabel("New Check-out Date (YYYY-MM-DD):"));
        formPanel.add(checkOutField);
        formPanel.add(new JLabel("New Total Guests:"));
        formPanel.add(totalGuestsField);
        formPanel.add(new JLabel("New Status:"));
        formPanel.add(statusCombo);

        // Add selection listener to populate fields when row is selected
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow != -1) {
                    checkInField.setText(table.getValueAt(selectedRow, 2).toString());
                    checkOutField.setText(table.getValueAt(selectedRow, 3).toString());
                    totalGuestsField.setText(table.getValueAt(selectedRow, 5).toString());
                    String currentStatus = table.getValueAt(selectedRow, 4).toString();
                    for (int i = 0; i < statusModel.getSize(); i++) {
                        BookingStatus status = statusModel.getElementAt(i);
                        if (status.getStatusName().equals(currentStatus)) {
                            statusCombo.setSelectedIndex(i);
                            break;
                        }
                    }
                }
            }
        });

        JButton modifyButton = UIComponents.createStyledButton("Modify Booking");
        modifyButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                UIComponents.showError(dialog, "Please select a booking to modify");
                return;
            }

            try {
                String checkIn = checkInField.getText().trim();
                String checkOut = checkOutField.getText().trim();
                String totalGuestsStr = totalGuestsField.getText().trim();
                
                if (checkIn.isEmpty() || checkOut.isEmpty() || totalGuestsStr.isEmpty()) {
                    UIComponents.showWarning(dialog, "Please fill in all fields");
                    return;
                }

                int totalGuests = Integer.parseInt(totalGuestsStr);
                int bookingId = (int) table.getValueAt(selectedRow, 0);
                
                // Get selected status ID
                BookingStatus selectedStatus = (BookingStatus) statusCombo.getSelectedItem();
                if (selectedStatus == null) {
                    UIComponents.showWarning(dialog, "Please select a status");
                    return;
                }
                
                receptionistMenuController.modifyBooking(
                    bookingId,
                    checkIn,
                    checkOut,
                    totalGuests,
                    selectedStatus.getStatusId()
                );
                UIComponents.showInfo(dialog, "Booking modified successfully!");
                dialog.dispose();
            } catch (SQLException ex) {
                UIComponents.handleException(dialog, ex, "Failed to modify booking");
            } catch (NumberFormatException ex) {
                UIComponents.showWarning(dialog, "Please enter a valid number for total guests");
            }
        });

        // Layout
        panel.setLayout(new BorderLayout());
        panel.add(scrollPane, BorderLayout.NORTH);
        panel.add(formPanel, BorderLayout.CENTER);
        panel.add(modifyButton, BorderLayout.SOUTH);
        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void showDeleteBookingDialog() {
        JDialog dialog = UIComponents.createStyledDialog(parentFrame, "Cancel Booking", UIComponents.MEDIUM_DIALOG_SIZE);
        JPanel panel = UIComponents.createMainPanel();

        String[] columns = {"Booking ID", "Guest", "Check In", "Check Out", "Status", "Total Guests"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable table = UIComponents.createTable(model, false);
        JScrollPane scrollPane = new JScrollPane(table);

        try {
            List<Booking> bookings = receptionistMenuController.viewAllBookings();
            for (Booking booking : bookings) {
                model.addRow(new Object[]{
                    booking.getBookingId(),
                    booking.getGuest().getFirstName() + " " + booking.getGuest().getLastName(),
                    booking.getCheckInDate(),
                    booking.getCheckOutDate(),
                    booking.getStatus().getStatusName(),
                    booking.getTotalGuests()
                });
            }
        } catch (SQLException ex) {
            UIComponents.handleException(dialog, ex, "Failed to load bookings");
        }

        JButton cancelButton = UIComponents.createStyledButton("Cancel Selected Booking");
        cancelButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                UIComponents.showError(dialog, "Please select a booking to cancel");
                return;
            }

            try {
                int bookingId = (int) table.getValueAt(selectedRow, 0);
                if (UIComponents.showConfirmDialog(dialog, 
                    "Are you sure you want to cancel this booking?",
                    "Confirm Cancellation")) {
                    receptionistMenuController.deleteBooking(bookingId);
                    UIComponents.showInfo(dialog, "Booking cancelled successfully!");
                    dialog.dispose();
                }
            } catch (SQLException ex) {
                UIComponents.handleException(dialog, ex, "Failed to cancel booking");
            }
        });

        panel.setLayout(new BorderLayout());
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(cancelButton, BorderLayout.SOUTH);
        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void showViewBookingsDialog() {
        JDialog dialog = UIComponents.createStyledDialog(parentFrame, "View Bookings", UIComponents.LARGE_DIALOG_SIZE);
        JPanel panel = UIComponents.createMainPanel();

        String[] columns = {"Booking ID", "Guest", "Check In", "Check Out", "Status", "Total Guests", "Created At"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable table = UIComponents.createTable(model, false);
        JScrollPane scrollPane = new JScrollPane(table);

        try {
            List<Booking> bookings = receptionistMenuController.viewAllBookings();
            for (Booking booking : bookings) {
                model.addRow(new Object[]{
                    booking.getBookingId(),
                    booking.getGuest().getFirstName() + " " + booking.getGuest().getLastName(),
                    booking.getCheckInDate(),
                    booking.getCheckOutDate(),
                    booking.getStatus().getStatusName(),
                    booking.getTotalGuests(),
                    booking.getCreatedAt()
                });
            }
        } catch (SQLException ex) {
            UIComponents.handleException(dialog, ex, "Failed to load bookings");
        }

        panel.setLayout(new BorderLayout());
        panel.add(scrollPane, BorderLayout.CENTER);
        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void showProcessPaymentDialog() {
        JDialog dialog = UIComponents.createStyledDialog(parentFrame, "Process Payment", UIComponents.LARGE_DIALOG_SIZE);
        JPanel panel = UIComponents.createMainPanel();

        // Create table for bookings
        String[] columns = {"Booking ID", "Guest", "Check In", "Check Out", "Status", "Total Guests"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable table = UIComponents.createTable(model, false);
        JScrollPane scrollPane = new JScrollPane(table);

        try {
            List<Booking> bookings = receptionistMenuController.viewAllBookings();
            for (Booking booking : bookings) {
                model.addRow(new Object[]{
                    booking.getBookingId(),
                    booking.getGuest().getFirstName() + " " + booking.getGuest().getLastName(),
                    booking.getCheckInDate(),
                    booking.getCheckOutDate(),
                    booking.getStatus().getStatusName(),
                    booking.getTotalGuests()
                });
            }
        } catch (SQLException ex) {
            UIComponents.handleException(dialog, ex, "Failed to load bookings");
        }

        // Payment details panel
        JPanel paymentPanel = new JPanel(new GridLayout(0, 2, 5, 5));
        JTextField amountField = new JTextField(10);
        String[] paymentMethods = {"Credit Card", "Debit Card", "Cash"};
        JComboBox<String> paymentMethodCombo = new JComboBox<>(paymentMethods);

        paymentPanel.add(new JLabel("Amount:"));
        paymentPanel.add(amountField);
        paymentPanel.add(new JLabel("Payment Method:"));
        paymentPanel.add(paymentMethodCombo);

        JButton processButton = UIComponents.createStyledButton("Process Payment");
        processButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                UIComponents.showError(dialog, "Please select a booking");
                return;
            }

            try {
                int bookingId = (int) table.getValueAt(selectedRow, 0);
                String amountStr = amountField.getText().trim();
                
                if (amountStr.isEmpty()) {
                    UIComponents.showWarning(dialog, "Please enter payment amount");
                    return;
                }

                receptionistMenuController.processPayment(
                    bookingId,
                    new BigDecimal(amountStr),
                    (String) paymentMethodCombo.getSelectedItem()
                );
                UIComponents.showInfo(dialog, "Payment processed successfully!");
                dialog.dispose();
            } catch (SQLException ex) {
                UIComponents.handleException(dialog, ex, "Failed to process payment");
            } catch (NumberFormatException ex) {
                UIComponents.showWarning(dialog, "Please enter a valid amount");
            }
        });

        // Layout
        panel.setLayout(new BorderLayout());
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(paymentPanel, BorderLayout.SOUTH);
        panel.add(processButton, BorderLayout.SOUTH);
        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void showAssignHousekeepingDialog() {
        JDialog dialog = UIComponents.createStyledDialog(parentFrame, "Assign Housekeeping Task", UIComponents.MEDIUM_DIALOG_SIZE);
        JPanel panel = UIComponents.createMainPanel();

        // Room selection
        DefaultComboBoxModel<Room> roomModel = new DefaultComboBoxModel<>();
        JComboBox<Room> roomCombo = new JComboBox<>(roomModel);
        roomCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                if (value instanceof Room) {
                    Room room = (Room) value;
                    value = String.format("Room %s - %s", 
                        room.getRoomNumber(),
                        room.getStatus().getStatusName());
                }
                return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            }
        });

        // Staff selection
        DefaultComboBoxModel<Staff> staffModel = new DefaultComboBoxModel<>();
        JComboBox<Staff> staffCombo = new JComboBox<>(staffModel);
        staffCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                if (value instanceof Staff) {
                    Staff staff = (Staff) value;
                    value = staff.getFirstName() + " " + staff.getLastName();
                }
                return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            }
        });

        // Date field
        JTextField dateField = new JTextField(10);

        // Load rooms
        try {
            List<Room> rooms = receptionistMenuController.getRooms();
            for (Room room : rooms) {
                roomModel.addElement(room);
            }
        } catch (SQLException ex) {
            UIComponents.handleException(dialog, ex, "Failed to load rooms");
        }

        // Check staff availability button
        JButton checkStaffBtn = UIComponents.createStyledButton("Check Available Staff");
        checkStaffBtn.addActionListener(e -> {
            try {
                String date = dateField.getText().trim();
                List<Staff> availableStaff = receptionistMenuController.getAvailableHousekeepers(date);
                staffModel.removeAllElements();
                for (Staff staff : availableStaff) {
                    staffModel.addElement(staff);
                }
            } catch (SQLException ex) {
                UIComponents.handleException(dialog, ex, "Failed to check staff availability");
            }
        });

        // Layout
        panel.setLayout(new GridLayout(0, 2, 5, 5));
        panel.add(new JLabel("Select Room:"));
        panel.add(roomCombo);
        panel.add(new JLabel("Date (YYYY-MM-DD):"));
        panel.add(dateField);
        panel.add(new JLabel(""));
        panel.add(checkStaffBtn);
        panel.add(new JLabel("Select Staff:"));
        panel.add(staffCombo);

        JButton assignButton = UIComponents.createStyledButton("Assign Task");
        assignButton.addActionListener(e -> {
            try {
                Room selectedRoom = (Room) roomCombo.getSelectedItem();
                Staff selectedStaff = (Staff) staffCombo.getSelectedItem();
                
                if (selectedRoom == null || selectedStaff == null) {
                    UIComponents.showError(dialog, "Please select both room and staff");
                    return;
                }

                receptionistMenuController.assignHousekeepingTask(
                    selectedRoom.getRoomNumber(),
                    selectedStaff.getUserId(),
                    dateField.getText().trim()
                );
                UIComponents.showInfo(dialog, "Housekeeping task assigned successfully!");
                dialog.dispose();
            } catch (SQLException ex) {
                UIComponents.handleException(dialog, ex, "Failed to assign housekeeping task");
            }
        });

        panel.add(new JLabel(""));
        panel.add(assignButton);
        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void showHousekeepingRecordsDialog() {
        JDialog dialog = UIComponents.createStyledDialog(parentFrame, "Housekeeping Records", UIComponents.LARGE_DIALOG_SIZE);
        JPanel panel = UIComponents.createMainPanel();

        String[] columns = {"Schedule ID", "Room", "Staff", "Date"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable table = UIComponents.createTable(model, false);
        JScrollPane scrollPane = new JScrollPane(table);

        try {
            List<HousekeepingSchedule> schedules = receptionistMenuController.viewHousekeepingRecords();
            for (HousekeepingSchedule schedule : schedules) {
                model.addRow(new Object[]{
                    schedule.getScheduleId(),
                    schedule.getRoom().getRoomNumber(),
                    schedule.getStaff().getFirstName() + " " + schedule.getStaff().getLastName(),
                    schedule.getScheduledDate(),
                });
            }
        } catch (SQLException ex) {
            UIComponents.handleException(dialog, ex, "Failed to load housekeeping records");
        }

        panel.setLayout(new BorderLayout());
        panel.add(scrollPane, BorderLayout.CENTER);
        dialog.add(panel);
        dialog.setVisible(true);
    }
} 