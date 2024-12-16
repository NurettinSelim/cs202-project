package service;

import model.User;

public interface UserService extends BaseService<User, Integer> {
    String getCurrentUserRole();
    User getCurrentUser();
    User login(Integer userId);
    void logout();
    boolean isLoggedIn();
} 