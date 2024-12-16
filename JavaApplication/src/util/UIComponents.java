package util;

import javax.swing.*;
import java.awt.*;

public final class UIComponents {
    public static final Font TITLE_FONT = new Font("Arial", Font.BOLD, 18);
    public static final Font BUTTON_FONT = new Font("Arial", Font.PLAIN, 14);
    public static final Font LABEL_FONT = new Font("Arial", Font.PLAIN, 12);

    public static final Dimension BUTTON_SIZE = new Dimension(200, 40);
    public static final Dimension DIALOG_SIZE = new Dimension(500, 400);
    public static final Dimension SMALL_DIALOG_SIZE = new Dimension(400, 300);

    public static final int PADDING = 20;
    public static final int SMALL_PADDING = 10;
    public static final int TINY_PADDING = 5;

    private static final String DEFAULT_ERROR_TITLE = "Error";
    private static final String DEFAULT_WARNING_TITLE = "Warning";
    private static final String DEFAULT_INFO_TITLE = "Information";

    public static void showError(Component parentComponent, String message) {
        showError(parentComponent, message, DEFAULT_ERROR_TITLE);
    }

    public static void showError(Component parentComponent, String message, String title) {
        SwingUtilities.invokeLater(() -> 
            JOptionPane.showMessageDialog(parentComponent,
                formatMessage(message),
                title,
                JOptionPane.ERROR_MESSAGE)
        );
    }

    public static void showWarning(Component parentComponent, String message) {
        showWarning(parentComponent, message, DEFAULT_WARNING_TITLE);
    }

    public static void showWarning(Component parentComponent, String message, String title) {
        SwingUtilities.invokeLater(() -> 
            JOptionPane.showMessageDialog(parentComponent,
                formatMessage(message),
                title,
                JOptionPane.WARNING_MESSAGE)
        );
    }

    public static void showInfo(Component parentComponent, String message) {
        showInfo(parentComponent, message, DEFAULT_INFO_TITLE);
    }

    public static void showInfo(Component parentComponent, String message, String title) {
        SwingUtilities.invokeLater(() -> 
            JOptionPane.showMessageDialog(parentComponent,
                formatMessage(message),
                title,
                JOptionPane.INFORMATION_MESSAGE)
        );
    }

    public static boolean showConfirmDialog(Component parentComponent, String message, String title) {
        return JOptionPane.showConfirmDialog(parentComponent,
            formatMessage(message),
            title,
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION;
    }

    public static void handleException(Component parentComponent, Exception e, String context) {
        String message = String.format("%s: %s", context, e.getMessage());
        showError(parentComponent, message);
        e.printStackTrace();
    }

    private static String formatMessage(String message) {
        if (message == null) {
            return "";
        }

        final int MAX_LINE_LENGTH = 80;
        if (message.length() <= MAX_LINE_LENGTH) {
            return message;
        }

        StringBuilder formatted = new StringBuilder("<html>");
        int start = 0;
        while (start < message.length()) {
            int end = Math.min(start + MAX_LINE_LENGTH, message.length());
            if (end < message.length()) {
                int lastSpace = message.lastIndexOf(' ', end);
                if (lastSpace > start) {
                    end = lastSpace;
                }
            }
            formatted.append(message.substring(start, end)).append("<br>");
            start = end + 1;
        }
        formatted.append("</html>");
        return formatted.toString();
    }

    public static JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setPreferredSize(BUTTON_SIZE);
        button.setFont(BUTTON_FONT);
        button.setFocusPainted(false);
        return button;
    }

    public static JDialog createStyledDialog(JFrame parent, String title, Dimension size) {
        JDialog dialog = new JDialog(parent, title, true);
        dialog.setSize(size);
        dialog.setLocationRelativeTo(parent);
        return dialog;
    }

    public static JPanel createMainPanel() {
        JPanel panel = new JPanel(new BorderLayout(SMALL_PADDING, SMALL_PADDING));
        panel.setBorder(BorderFactory.createEmptyBorder(PADDING, PADDING, PADDING, PADDING));
        return panel;
    }

    public static JPanel createButtonPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(PADDING, PADDING, PADDING, PADDING));
        return panel;
    }

    public static GridBagConstraints createGBC() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(TINY_PADDING, TINY_PADDING, TINY_PADDING, TINY_PADDING);
        return gbc;
    }

    public static JLabel createTitleLabel(String text) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(TITLE_FONT);
        return label;
    }

    public static JPanel createFormPanel(int rows, int cols) {
        JPanel panel = new JPanel(new GridLayout(rows, cols, SMALL_PADDING, SMALL_PADDING));
        panel.setBorder(BorderFactory.createEmptyBorder(PADDING, PADDING, PADDING, PADDING));
        return panel;
    }
} 