package menu;

import controller.GuestMenuController;
import model.Booking;
import model.BookingRoom;
import model.Room;
import util.UIComponents;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

public class GuestMenuHandler {
    private final GuestMenuController guestMenuController;
    private JPanel mainPanel;
    private final JFrame parentFrame;

    public GuestMenuHandler(JFrame parentFrame) {
        this.parentFrame = parentFrame;
        this.guestMenuController = new GuestMenuController();
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

        // Create buttons
        JButton addBookingBtn = UIComponents.createStyledButton("Make New Booking");
        JButton viewRoomsBtn = UIComponents.createStyledButton("View Available Rooms");
        JButton viewBookingsBtn = UIComponents.createStyledButton("View My Bookings");
        JButton cancelBookingBtn = UIComponents.createStyledButton("Cancel Booking");

        // Add action listeners
        addBookingBtn.addActionListener(e -> showAddBookingDialog());
        viewRoomsBtn.addActionListener(e -> showAvailableRooms());
        viewBookingsBtn.addActionListener(e -> showMyBookings());
        cancelBookingBtn.addActionListener(e -> showCancelBookingDialog());

        // Add components to panel
        mainPanel.add(addBookingBtn, gbc);
        mainPanel.add(viewRoomsBtn, gbc);
        mainPanel.add(viewBookingsBtn, gbc);
        mainPanel.add(cancelBookingBtn, gbc);
    }

    private void showAddBookingDialog() {
        try {
            JDialog dialog = UIComponents.createStyledDialog(parentFrame, "Make New Booking",
                    UIComponents.SMALL_DIALOG_SIZE);
            JPanel panel = UIComponents.createMainPanel();

            // Create form panel for input fields
            JPanel formPanel = UIComponents.createFormPanel(3, 2);
            formPanel.add(new JLabel("Check-in Date:"));
            JTextField checkInDateField = new JTextField(10);
            formPanel.add(checkInDateField);

            formPanel.add(new JLabel("Check-out Date:"));
            JTextField checkOutDateField = new JTextField(10);
            formPanel.add(checkOutDateField);

            formPanel.add(new JLabel("Total Guests:"));
            JTextField totalGuestsField = new JTextField(10);
            formPanel.add(totalGuestsField);

            // Create room selection table
            String[] columnNames = { "Select", "Room Number", "Room Type", "Capacity", "Price" };
            DefaultTableModel model = new DefaultTableModel(columnNames, 0);
            JTable roomsTable = UIComponents.createTable(model, true, 0);
            JScrollPane scrollPane = new JScrollPane(roomsTable);
            scrollPane.setPreferredSize(new Dimension(500, 200));
            scrollPane.setVisible(false);

            // Add panels to main panel
            panel.add(formPanel, BorderLayout.NORTH);
            panel.add(scrollPane, BorderLayout.CENTER);

            // Button panel
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            JButton listAvailableRoomsButton = UIComponents.createStyledButton("List Available Rooms");
            listAvailableRoomsButton.addActionListener(e -> {
                try {
                    if (checkInDateField.getText().isEmpty() || checkOutDateField.getText().isEmpty()) {
                        UIComponents.showWarning(dialog, "Please enter both check-in and check-out dates");
                        return;
                    }

                    model.setRowCount(0);
                    ArrayList<Room> availableRoomsList = guestMenuController.viewAvailableRooms(
                            checkInDateField.getText(),
                            checkOutDateField.getText());

                    if (availableRoomsList.isEmpty()) {
                        UIComponents.showInfo(dialog, "No rooms available for the selected dates");
                        scrollPane.setVisible(false);
                    } else {
                        for (Room room : availableRoomsList) {
                            model.addRow(new Object[] {
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
                } catch (Exception ex) {
                    UIComponents.handleException(dialog, ex, "Failed to list available rooms");
                }
            });
            buttonPanel.add(listAvailableRoomsButton);

            JButton addBookingButton = UIComponents.createStyledButton("Add Booking");
            addBookingButton.addActionListener(e -> {
                try {
                    if (checkInDateField.getText().isEmpty() ||
                            checkOutDateField.getText().isEmpty() ||
                            totalGuestsField.getText().isEmpty()) {
                        UIComponents.showWarning(dialog, "Please fill in all fields");
                        return;
                    }

                    int totalGuestsRequested;
                    int totalCapacityOfSelectedRooms = 0;
                    HashMap<Room, Integer> selectedRooms = new HashMap<>();
                    ArrayList<Room> availableRooms = guestMenuController.viewAvailableRooms(
                            checkInDateField.getText(),
                            checkOutDateField.getText());

                    try {
                        totalGuestsRequested = Integer.parseInt(totalGuestsField.getText());
                        if (totalGuestsRequested <= 0) {
                            UIComponents.showWarning(dialog, "Total guests must be greater than 0");
                            return;
                        }

                        for (int i = 0; i < model.getRowCount(); i++) {
                            boolean isSelected = (boolean) model.getValueAt(i, 0);
                            if (isSelected) {
                                Room room = availableRooms.get(i);
                                selectedRooms.put(room, room.getRoomType().getCapacity());
                                totalCapacityOfSelectedRooms += room.getRoomType().getCapacity();
                            }
                        }

                        if (selectedRooms.isEmpty()) {
                            UIComponents.showWarning(dialog, "Please select at least one room");
                            return;
                        }
                        if (totalGuestsRequested > totalCapacityOfSelectedRooms) {
                            UIComponents.showWarning(dialog,
                                    "Total guests requested exceeds the capacity of the selected rooms");
                            return;
                        }
                    } catch (NumberFormatException ex) {
                        UIComponents.showWarning(dialog, "Please enter a valid number for total guests");
                        return;
                    }

                    guestMenuController.addNewBooking(
                            checkInDateField.getText(),
                            checkOutDateField.getText(),
                            totalGuestsRequested,
                            selectedRooms);
                    UIComponents.showInfo(dialog, "Booking added successfully");
                    dialog.dispose();
                } catch (Exception ex) {
                    UIComponents.handleException(dialog, ex, "Failed to add booking");
                }
            });
            buttonPanel.add(addBookingButton);

            JButton closeButton = UIComponents.createStyledButton("Close");
            closeButton.addActionListener(e -> dialog.dispose());
            buttonPanel.add(closeButton);

            panel.add(buttonPanel, BorderLayout.SOUTH);
            dialog.add(panel);
            dialog.setVisible(true);
        } catch (Exception e) {
            UIComponents.handleException(parentFrame, e, "Failed to show booking dialog");
        }
    }

    private void showAvailableRooms() {
        try {
            JDialog dialog = UIComponents.createStyledDialog(parentFrame, "Available Rooms", UIComponents.DIALOG_SIZE);
            JPanel panel = UIComponents.createMainPanel();

            // Create date input panel
            JPanel datePanel = UIComponents.createFormPanel(2, 2);
            datePanel.add(new JLabel("Check-in Date:"));
            JTextField checkInDateField = new JTextField(10);
            datePanel.add(checkInDateField);

            datePanel.add(new JLabel("Check-out Date:"));
            JTextField checkOutDateField = new JTextField(10);
            datePanel.add(checkOutDateField);

            // Create table to display available rooms
            String[] columnNames = { "Room Number", "Room Type", "Capacity", "Base Price", "Status" };
            DefaultTableModel model = new DefaultTableModel(columnNames, 0);
            JTable roomsTable = UIComponents.createTable(model, false);
            JScrollPane scrollPane = new JScrollPane(roomsTable);
            scrollPane.setPreferredSize(new Dimension(500, 300));
            scrollPane.setVisible(false);

            JButton searchButton = UIComponents.createStyledButton("Search");
            searchButton.addActionListener(e -> {
                try {
                    if (checkInDateField.getText().isEmpty() || checkOutDateField.getText().isEmpty()) {
                        UIComponents.showWarning(dialog, "Please enter both check-in and check-out dates");
                        return;
                    }

                    model.setRowCount(0);
                    ArrayList<Room> availableRooms = guestMenuController.viewAvailableRooms(
                            checkInDateField.getText(),
                            checkOutDateField.getText());

                    if (availableRooms.isEmpty()) {
                        UIComponents.showInfo(dialog, "No rooms available for the selected dates");
                        scrollPane.setVisible(false);
                    } else {
                        for (Room room : availableRooms) {
                            model.addRow(new Object[] {
                                room.getRoomNumber(),
                                room.getRoomType().getTypeName(),
                                room.getRoomType().getCapacity(),
                                room.getRoomType().getBasePrice(),
                                room.getStatus().getStatusName()
                            });
                        }
                        scrollPane.setVisible(true);
                        panel.revalidate();
                        panel.repaint();
                    }
                    dialog.pack();
                } catch (Exception ex) {
                    UIComponents.handleException(dialog, ex, "Failed to search for available rooms");
                }
            });

            // Layout panels
            JPanel searchPanel = new JPanel(new BorderLayout());
            searchPanel.add(datePanel, BorderLayout.CENTER);
            searchPanel.add(searchButton, BorderLayout.SOUTH);
            panel.add(searchPanel, BorderLayout.NORTH);
            panel.add(scrollPane, BorderLayout.CENTER);

            JButton closeButton = UIComponents.createStyledButton("Close");
            closeButton.addActionListener(e -> dialog.dispose());
            panel.add(closeButton, BorderLayout.SOUTH);

            dialog.add(panel);
            dialog.setVisible(true);
        } catch (Exception e) {
            UIComponents.handleException(parentFrame, e, "Failed to show available rooms dialog");
        }
    }

    private void showMyBookings() {
        try {
            JDialog dialog = UIComponents.createStyledDialog(parentFrame, "My Bookings", UIComponents.DIALOG_SIZE);
            JPanel panel = UIComponents.createMainPanel();
            String[] columnNames = { "Booking ID", "Check-in Date", "Check-out Date", "Status",
                    "Total Guests", "Created At", "Confirmed By" };
            DefaultTableModel model = new DefaultTableModel(columnNames, 0);
            JTable bookingsTable = UIComponents.createTable(model, false);
            JScrollPane scrollPane = new JScrollPane(bookingsTable);
            panel.add(scrollPane, BorderLayout.CENTER);

            try {
                ArrayList<Booking> bookings = guestMenuController.viewMyBookings();

                if (bookings.isEmpty()) {
                    UIComponents.showInfo(dialog, "You have no bookings");
                } else {
                    for (Booking booking : bookings) {
                        model.addRow(new Object[] {
                                booking.getBookingId(),
                                booking.getCheckInDate(),
                                booking.getCheckOutDate(),
                                booking.getStatus().getStatusName(),
                                booking.getTotalGuests(),
                                booking.getCreatedAt(),
                                booking.getConfirmedBy(),
                        });
                    }
                }
            } catch (Exception e) {
                UIComponents.handleException(dialog, e, "Failed to load bookings");
            }

            JButton closeButton = UIComponents.createStyledButton("Close");
            closeButton.addActionListener(e -> dialog.dispose());
            panel.add(closeButton, BorderLayout.SOUTH);

            dialog.add(panel);
            dialog.setVisible(true);
        } catch (Exception e) {
            UIComponents.handleException(parentFrame, e, "Failed to show bookings dialog");
        }
    }

    private void showCancelBookingDialog() {
        try {
            JDialog dialog = UIComponents.createStyledDialog(parentFrame, "Cancel Booking",
                    UIComponents.SMALL_DIALOG_SIZE);
            JPanel panel = UIComponents.createMainPanel();

            // Create table to display active bookings
            String[] columnNames = { "Booking ID", "Check-in Date", "Check-out Date", "Status", "Total Guests" };
            DefaultTableModel model = new DefaultTableModel(columnNames, 0);
            JTable bookingsTable = UIComponents.createTable(model, false);
            JScrollPane scrollPane = new JScrollPane(bookingsTable);
            panel.add(scrollPane, BorderLayout.CENTER);

            // Load active bookings
            try {
                ArrayList<Booking> bookings = guestMenuController.viewMyBookings();

                if (bookings.isEmpty()) {
                    UIComponents.showInfo(dialog, "You have no bookings to cancel");
                } else {
                    for (Booking booking : bookings) {
                        model.addRow(new Object[] {
                                booking.getBookingId(),
                                booking.getCheckInDate(),
                                booking.getCheckOutDate(),
                                booking.getStatus().getStatusName(),
                                booking.getTotalGuests()
                        });
                    }
                }
            } catch (Exception e) {
                UIComponents.handleException(dialog, e, "Failed to load bookings");
            }

            // Button panel
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            
            JButton cancelBookingButton = UIComponents.createStyledButton("Cancel Selected Booking");
            cancelBookingButton.addActionListener(e -> {
                int selectedRow = bookingsTable.getSelectedRow();
                if (selectedRow == -1) {
                    UIComponents.showWarning(dialog, "Please select a booking to cancel");
                    return;
                }

                int bookingId = (int) bookingsTable.getValueAt(selectedRow, 0);
                int confirm = JOptionPane.showConfirmDialog(
                        dialog,
                        "Are you sure you want to cancel this booking?",
                        "Confirm Cancellation",
                        JOptionPane.YES_NO_OPTION);

                if (confirm == JOptionPane.YES_OPTION) {
                    try {
                        guestMenuController.cancelBooking(bookingId);
                        UIComponents.showInfo(dialog, "Booking cancelled successfully");
                        dialog.dispose();
                    } catch (Exception ex) {
                        UIComponents.handleException(dialog, ex, "Failed to cancel booking");
                    }
                }
            });
            buttonPanel.add(cancelBookingButton);

            JButton closeButton = UIComponents.createStyledButton("Close");
            closeButton.addActionListener(e -> dialog.dispose());
            buttonPanel.add(closeButton);

            panel.add(buttonPanel, BorderLayout.SOUTH);
            dialog.add(panel);
            dialog.setVisible(true);
        } catch (Exception e) {
            UIComponents.handleException(parentFrame, e, "Failed to show cancellation dialog");
        }
    }
}