package model;

/**
 * Admin - Represents a system administrator.
 */
public class Admin extends User {
    private String adminRole;

    public Admin() { super(); }

    public Admin(String username, String password, String email, String adminRole) {
        super(username, password, email);
        this.adminRole = adminRole;
    }

    public String getAdminRole() { return adminRole; }
    public void setAdminRole(String adminRole) { this.adminRole = adminRole; }
}
