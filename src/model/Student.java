package model;

/**
 * Student - Represents a student participant.
 */
public class Student extends User {
    private String studentId;
    private int gradeLevel;

    public Student() { super(); }

    public Student(String username, String password, String email, String studentId, int gradeLevel) {
        super(username, password, email);
        this.studentId = studentId;
        this.gradeLevel = gradeLevel;
    }

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public int getGradeLevel() { return gradeLevel; }
    public void setGradeLevel(int gradeLevel) { this.gradeLevel = gradeLevel; }
}
