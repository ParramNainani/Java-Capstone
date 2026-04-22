package model;

import java.util.Date;

public class QuizResult {

    private int id;
    private int userId;
    private int score;
    private int totalQuestions;
    private Date date;

    public QuizResult() {}

    public QuizResult(int id, int userId, int score, int totalQuestions, Date date) {
        this.id = id;
        this.userId = userId;
        this.score = score;
        this.totalQuestions = totalQuestions;
        this.date = date;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }

    public int getTotalQuestions() { return totalQuestions; }
    public void setTotalQuestions(int totalQuestions) { this.totalQuestions = totalQuestions; }

    public Date getDate() { return date; }
    public void setDate(Date date) { this.date = date; }
}