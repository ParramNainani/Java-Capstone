package service;

import model.User;

/**
 * AuthService - Handles authentication logic.
 */
public class AuthService {
    
    // In a real application, this would interface with a UserDAO and database.
    public boolean login(String username, String password) {
        if (username == null || password == null) return false;
        // Dummy check for now
        return username.length() > 0 && password.equals("password123");
    }

    public boolean register(User user) {
        if (user == null || user.getUsername() == null || user.getPassword() == null) {
            return false;
        }
        // Save to DB
        return true;
    }
}
