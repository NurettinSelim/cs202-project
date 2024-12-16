package controller;

import java.util.List;
import java.util.Scanner;

import model.User;
import service.UserService;
import service.impl.UserServiceImpl;

public class BaseControlller {
    private Scanner scanner = new Scanner(System.in);
    private UserService userService = new UserServiceImpl();
    public int getUserChoice() {
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public User getCurrentUser() {
        return userService.getCurrentUser();
    }

    public String readInput(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }

    public void displayMenu(List<String> menuItems) {
        System.out.println("\n=== Menu ===");
        for (String item : menuItems) {
            System.out.println(item);
        }
        System.out.println(String.format("%d. Return to Main Menu", menuItems.size() + 1));
    }
}
