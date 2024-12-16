package menu;

import controller.GuestMenuController;
import model.Booking;
import model.Room;
import util.UIComponents;

import javax.swing.*;
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

            // Create room selection panel (initially empty)
            JPanel roomSelectionPanel = new JPanel();
            roomSelectionPanel.setLayout(new BoxLayout(roomSelectionPanel, BoxLayout.Y_AXIS));
            JScrollPane scrollPane = new JScrollPane(roomSelectionPanel);
            scrollPane.setPreferredSize(new Dimension(300, 150));
            scrollPane.setVisible(false);

            // Add panels to main panel
            panel.add(formPanel, BorderLayout.NORTH);
            panel.add(scrollPane, BorderLayout.CENTER);

            // Button panel
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            HashMap<Room, JPanel> roomPanels = new HashMap<>();

            JButton listAvailableRoomsButton = UIComponents.createStyledButton("List Available Rooms");
            listAvailableRoomsButton.addActionListener(e -> {
                try {
                    if (checkInDateField.getText().isEmpty() || checkOutDateField.getText().isEmpty()) {
                        UIComponents.showWarning(dialog, "Please enter both check-in and check-out dates");
                        return;
                    }

                    roomPanels.clear();
                    roomSelectionPanel.removeAll();

                    ArrayList<Room> availableRoomsList = guestMenuController.viewAvailableRooms(
                            checkInDateField.getText(),
                            checkOutDateField.getText());

                    if (availableRoomsList.isEmpty()) {
                        UIComponents.showInfo(dialog, "No rooms available for the selected dates");
                        scrollPane.setVisible(false);
                    } else {
                        for (Room room : availableRoomsList) {
                            JPanel roomPanel = new JPanel();
                            roomPanel.setLayout(new BoxLayout(roomPanel, BoxLayout.X_AXIS));

                            JCheckBox roomCheckBox = new JCheckBox(room.toDisplayString());
                            JLabel maxGuestsLabel = new JLabel("Max Guests: " + room.getRoomType().getCapacity());

                            roomPanel.add(roomCheckBox);
                            roomPanel.add(Box.createHorizontalStrut(10));
                            roomPanel.add(maxGuestsLabel);

                            roomPanels.put(room, roomPanel);
                            roomSelectionPanel.add(roomPanel);
                        }
                        scrollPane.setVisible(true);
                        roomSelectionPanel.revalidate();
                        roomSelectionPanel.repaint();
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
                    try {
                        totalGuestsRequested = Integer.parseInt(totalGuestsField.getText());
                        if (totalGuestsRequested <= 0) {
                            UIComponents.showWarning(dialog, "Total guests must be greater than 0");
                            return;
                        }
                    } catch (NumberFormatException ex) {
                        UIComponents.showWarning(dialog, "Please enter a valid number for total guests");
                        return;
                    }

                    // Collect selected rooms and their guest counts
                    HashMap<Room, Integer> selectedRooms = new HashMap<>();
                    int totalGuestsAssigned = 0;

                    for (Room room : roomPanels.keySet()) {
                        JPanel roomPanel = roomPanels.get(room);
                        JCheckBox checkBox = (JCheckBox) roomPanel.getComponent(0);
                        if (checkBox.isSelected()) {
                            JSpinner spinner = (JSpinner) roomPanel.getComponent(3);
                            int guestsInRoom = (Integer) spinner.getValue();
                            selectedRooms.put(room, guestsInRoom);
                            totalGuestsAssigned += guestsInRoom;
                        }
                    }

                    if (selectedRooms.isEmpty()) {
                        UIComponents.showWarning(dialog, "Please select at least one room");
                        return;
                    }

                    if (totalGuestsAssigned != totalGuestsRequested) {
                        UIComponents.showWarning(dialog,
                                "Total guests assigned to rooms (" + totalGuestsAssigned +
                                        ") must match total guests requested (" + totalGuestsRequested + ")");
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

            JPanel datePanel = UIComponents.createFormPanel(2, 2);
            datePanel.add(new JLabel("Check-in Date:"));
            JTextField checkInDateField = new JTextField(10);
            datePanel.add(checkInDateField);

            datePanel.add(new JLabel("Check-out Date:"));
            JTextField checkOutDateField = new JTextField(10);
            datePanel.add(checkOutDateField);

            JButton searchButton = UIComponents.createStyledButton("Search");
            searchButton.addActionListener(e -> {
                try {
                    if (checkInDateField.getText().isEmpty() || checkOutDateField.getText().isEmpty()) {
                        UIComponents.showWarning(dialog, "Please enter both check-in and check-out dates");
                        return;
                    }

                    ArrayList<Room> availableRooms = guestMenuController.viewAvailableRooms(
                            checkInDateField.getText(),
                            checkOutDateField.getText());

                    if (availableRooms.isEmpty()) {
                        UIComponents.showInfo(dialog, "No rooms available for the selected dates");
                    } else {
                        StringBuilder roomListString = new StringBuilder();
                        for (Room room : availableRooms) {
                            roomListString.append(room.toDisplayString()).append("\n");
                        }
                        JTextArea roomList = new JTextArea(roomListString.toString());
                        roomList.setEditable(false);
                        panel.add(new JScrollPane(roomList), BorderLayout.CENTER);
                        dialog.revalidate();
                    }
                } catch (Exception ex) {
                    UIComponents.handleException(dialog, ex, "Failed to search for available rooms");
                }
            });

            JPanel searchPanel = new JPanel(new BorderLayout());
            searchPanel.add(datePanel, BorderLayout.CENTER);
            searchPanel.add(searchButton, BorderLayout.SOUTH);
            panel.add(searchPanel, BorderLayout.NORTH);

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

            JTextArea bookingsList = new JTextArea();
            bookingsList.setEditable(false);
            panel.add(new JScrollPane(bookingsList), BorderLayout.CENTER);

            try {
                ArrayList<Booking> bookings = guestMenuController.viewMyBookings();
                String bookingsString = "";
                for (Booking booking : bookings) {
                    bookingsString += booking.toDisplayString() + "\n";
                }
                if (bookingsString.isEmpty()) {
                    UIComponents.showInfo(dialog, "You have no bookings");
                } else {
                    bookingsList.setText(bookingsString);
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

            // TODO: Add booking cancellation form
            panel.add(new JLabel("Booking cancellation form will be implemented here"), BorderLayout.CENTER);

            JButton closeButton = UIComponents.createStyledButton("Close");
            closeButton.addActionListener(e -> dialog.dispose());
            panel.add(closeButton, BorderLayout.SOUTH);

            dialog.add(panel);
            dialog.setVisible(true);
        } catch (Exception e) {
            UIComponents.handleException(parentFrame, e, "Failed to show cancellation dialog");
        }
    }
}