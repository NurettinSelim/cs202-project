package service;

import model.User;
import java.util.List;
import java.util.Optional;

public interface UserService extends BaseService<User, Integer> {
    Optional<User> findByUsername(String username);
    boolean authenticate(String username, String password);
    void changePassword(Integer userId, String oldPassword, String newPassword);
    List<User> findByName(String firstName, String lastName);
    boolean isUsernameUnique(String username);
    boolean isPhoneNumberUnique(String phone);
    
    // New methods for user role and session management
    String getUserRole(User user);
    User getCurrentUser();
    void setCurrentUser(User user);
    void logout();
    boolean isLoggedIn();
} 