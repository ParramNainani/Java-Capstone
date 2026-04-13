package model;

/**
 * Admin - Represents an admin user.
 */
public class Admin extends User {
    private String adminId;

    public Admin(String username, String password, String email, String adminId) {
        super(username, password, email);
        this.adminId = adminId;
    }

    // Getters and setters
}
