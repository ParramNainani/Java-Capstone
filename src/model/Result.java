package model;

/**
 * Result - Represents the result of a quiz attempt.
 */
public class Result {
    private String studentId;
    private String quizId;
    private int score;
    private String grade;

    public Result() {}

    public Result(String studentId, String quizId, int score, String grade) {
        this.studentId = studentId;
        this.quizId = quizId;
        this.score = score;
        this.grade = grade;
    }

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public String getQuizId() { return quizId; }
    public void setQuizId(String quizId) { this.quizId = quizId; }

    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }

    public String getGrade() { return grade; }
    public void setGrade(String grade) { this.grade = grade; }
}
