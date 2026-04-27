package model;

import java.io.Serializable;

/**
 * Quiz - Represents a quiz assessment.
 */
public class Quiz implements Serializable {
    private static final long serialVersionUID = 1L;
    private String quizId;
    private String title;
    private int timeLimit;
    private int numericQuizId;
    private int creatorId;
    private boolean active;

    public Quiz() {}

    public Quiz(String quizId, String title, int timeLimit) {
        this.quizId = quizId;
        this.title = title;
        this.timeLimit = timeLimit;
    }

    public String getQuizId() { return quizId; }
    public void setQuizId(String quizId) { this.quizId = quizId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public int getTimeLimit() { return timeLimit; }
    public void setTimeLimit(int timeLimit) { this.timeLimit = timeLimit; }

    public int getNumericQuizId() { return numericQuizId; }
    public void setNumericQuizId(int numericQuizId) { this.numericQuizId = numericQuizId; }

    public int getCreatorId() { return creatorId; }
    public void setCreatorId(int creatorId) { this.creatorId = creatorId; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}

