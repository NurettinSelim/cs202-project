package service;

import model.User;
import java.util.ArrayList;
import java.util.HashMap;

public interface UserService {
    User create(User user);
    ArrayList<User> findAll();
    HashMap<User, String> findAllWithRole();
    User getCurrentUser();
    String getCurrentRole();
    void login(int userId);
    int getCurrentHotelId();
    void logout();
}