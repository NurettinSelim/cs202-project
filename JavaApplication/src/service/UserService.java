package service;

import model.User;
import java.util.ArrayList;

public interface UserService {
    User create(User user);
    ArrayList<User> findAll();
    User getCurrentUser();
    String getCurrentRole();
    void login(int userId);
    int getCurrentHotelId();
    void logout();
}