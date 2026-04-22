package model;

/**
 * Analytics - Holds statistics related to a quiz.
 */
public class Analytics {
    private String quizId;
    private int totalAttempted;
    private double averageScore;
    private int highestScore;

    public Analytics() {}

    public Analytics(String quizId, int totalAttempted, double averageScore, int highestScore) {
        this.quizId = quizId;
        this.totalAttempted = totalAttempted;
        this.averageScore = averageScore;
        this.highestScore = highestScore;
    }

    public String getQuizId() { return quizId; }
    public void setQuizId(String quizId) { this.quizId = quizId; }

    public int getTotalAttempted() { return totalAttempted; }
    public void setTotalAttempted(int totalAttempted) { this.totalAttempted = totalAttempted; }

    public double getAverageScore() { return averageScore; }
    public void setAverageScore(double averageScore) { this.averageScore = averageScore; }

    public int getHighestScore() { return highestScore; }
    public void setHighestScore(int highestScore) { this.highestScore = highestScore; }
}
