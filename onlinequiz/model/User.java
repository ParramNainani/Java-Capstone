package model;

/**
 * User - Base class for all users in the system.
 */
public class User {
    protected String username;
    protected String password;
    protected String email;

    public User() {}

    public User(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }

    // Getters and setters
}
