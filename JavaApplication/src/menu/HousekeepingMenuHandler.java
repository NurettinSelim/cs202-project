package menu;

import controller.HousekeepingMenuController;
import model.*;
import util.UIComponents;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class HousekeepingMenuHandler {
    private final HousekeepingMenuController housekeepingMenuController;
    private JPanel mainPanel;
    private final JFrame parentFrame;

    public HousekeepingMenuHandler(JFrame parentFrame) {
        this.parentFrame = parentFrame;
        this.housekeepingMenuController = new HousekeepingMenuController();
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

        // Create buttons for all housekeeping functionalities
        JButton viewPendingBtn = UIComponents.createStyledButton("View Pending Tasks");
        JButton viewCompletedBtn = UIComponents.createStyledButton("View Completed Tasks");
        JButton updateStatusBtn = UIComponents.createStyledButton("Update Task Status");
        JButton viewScheduleBtn = UIComponents.createStyledButton("View My Schedule");

        // Add action listeners
        viewPendingBtn.addActionListener(e -> showPendingTasksDialog());
        viewCompletedBtn.addActionListener(e -> showCompletedTasksDialog());
        updateStatusBtn.addActionListener(e -> showUpdateTaskStatusDialog());
        viewScheduleBtn.addActionListener(e -> showMyScheduleDialog());

        // Add components to panel
        mainPanel.add(viewPendingBtn, gbc);
        mainPanel.add(viewCompletedBtn, gbc);
        mainPanel.add(updateStatusBtn, gbc);
        mainPanel.add(viewScheduleBtn, gbc);
    }

    private JPanel createTaskTablePanel(List<HousekeepingSchedule> tasks, boolean showCompleteButton, JDialog dialog) {
        JPanel panel = UIComponents.createMainPanel();

        String[] columns = {"Schedule ID", "Room", "Date", "Status"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable table = UIComponents.createTable(model, false);
        JScrollPane scrollPane = new JScrollPane(table);

        for (HousekeepingSchedule task : tasks) {
            model.addRow(new Object[]{
                task.getScheduleId(),
                task.getRoom().getRoomNumber(),
                task.getScheduledDate(),
                task.getStatus().getStatusName()
            });
        }

        panel.setLayout(new BorderLayout());
        panel.add(scrollPane, BorderLayout.CENTER);

        if (showCompleteButton) {
            JButton completeButton = UIComponents.createStyledButton("Mark as Completed");
            completeButton.addActionListener(e -> {
                int selectedRow = table.getSelectedRow();
                if (selectedRow == -1) {
                    UIComponents.showError(dialog, "Please select a task to complete");
                    return;
                }

                try {
                    int scheduleId = (int) table.getValueAt(selectedRow, 0);
                    housekeepingMenuController.updateTaskStatus(scheduleId);
                    model.removeRow(selectedRow);
                    UIComponents.showInfo(dialog, "Task marked as completed!");
                } catch (SQLException ex) {
                    UIComponents.handleException(dialog, ex, "Failed to update task status");
                }
            });
            panel.add(completeButton, BorderLayout.SOUTH);
        }

        return panel;
    }

    private void showPendingTasksDialog() {
        JDialog dialog = UIComponents.createStyledDialog(parentFrame, "Pending Housekeeping Tasks", UIComponents.LARGE_DIALOG_SIZE);
        try {
            List<HousekeepingSchedule> tasks = housekeepingMenuController.viewPendingTasks();
            JPanel panel = createTaskTablePanel(tasks, false, dialog);
            dialog.add(panel);
            dialog.setVisible(true);
        } catch (SQLException ex) {
            UIComponents.handleException(dialog, ex, "Failed to load pending tasks");
        }
    }

    private void showCompletedTasksDialog() {
        JDialog dialog = UIComponents.createStyledDialog(parentFrame, "Completed Housekeeping Tasks", UIComponents.LARGE_DIALOG_SIZE);
        try {
            List<HousekeepingSchedule> tasks = housekeepingMenuController.viewCompletedTasks();
            JPanel panel = createTaskTablePanel(tasks, false, dialog);
            dialog.add(panel);
            dialog.setVisible(true);
        } catch (SQLException ex) {
            UIComponents.handleException(dialog, ex, "Failed to load completed tasks");
        }
    }

    private void showUpdateTaskStatusDialog() {
        JDialog dialog = UIComponents.createStyledDialog(parentFrame, "Update Task Status", UIComponents.LARGE_DIALOG_SIZE);
        try {
            List<HousekeepingSchedule> tasks = housekeepingMenuController.viewPendingTasks();
            JPanel panel = createTaskTablePanel(tasks, true, dialog);
            dialog.add(panel);
            dialog.setVisible(true);
        } catch (SQLException ex) {
            UIComponents.handleException(dialog, ex, "Failed to load tasks");
        }
    }

    private void showMyScheduleDialog() {
        JDialog dialog = UIComponents.createStyledDialog(parentFrame, "My Cleaning Schedule", UIComponents.LARGE_DIALOG_SIZE);
        try {
            List<HousekeepingSchedule> tasks = housekeepingMenuController.viewMySchedule();
            JPanel panel = createTaskTablePanel(tasks, false, dialog);
            dialog.add(panel);
            dialog.setVisible(true);
        } catch (SQLException ex) {
            UIComponents.handleException(dialog, ex, "Failed to load schedule");
        }
    }
} 