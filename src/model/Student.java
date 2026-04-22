package model;

/**
 * Student - Represents a student user.
 */
public class Student extends User {
    private String studentId;

    public Student(String username, String password, String email, String studentId) {
        super(username, password, email);
        this.studentId = studentId;
    }

    // Getters and setters
}
