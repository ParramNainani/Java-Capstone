package model;

import java.util.List;

/**
 * Quiz - Represents a quiz assessment.
 */
public class Quiz {
    private String quizId;
    private String title;
    private List<Question<?>> questions;
    private int timeLimit;

    public Quiz() {}

    public Quiz(String quizId, String title, List<Question<?>> questions, int timeLimit) {
        this.quizId = quizId;
        this.title = title;
        this.questions = questions;
        this.timeLimit = timeLimit;
    }

    public String getQuizId() { return quizId; }
    public void setQuizId(String quizId) { this.quizId = quizId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public List<Question<?>> getQuestions() { return questions; }
    public void setQuestions(List<Question<?>> questions) { this.questions = questions; }

    public int getTimeLimit() { return timeLimit; }
    public void setTimeLimit(int timeLimit) { this.timeLimit = timeLimit; }
}
