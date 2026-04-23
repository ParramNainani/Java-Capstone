package model;

import java.io.Serializable;

public abstract class Question<T> implements Serializable {
    private static final long serialVersionUID = 1L;
    private int questionId;
    private String questionText;
    private T correctAnswer;

    public Question(int questionId, String questionText, T correctAnswer) {
        this.questionId = questionId;
        this.questionText = questionText;
        this.correctAnswer = correctAnswer;
    }

    public int getQuestionId() {
        return questionId;
    }

    public String getQuestionText() {
        return questionText;
    }

    public T getCorrectAnswer() {
        return correctAnswer;
    }

    // Database Fields & Setter Extensions
    private int quizId;
    private String questionType;
    private int marks;
    private String difficultyLevel;

    public void setQuestionId(int questionId) { this.questionId = questionId; }

    public int getQuizId() { return quizId; }
    public void setQuizId(int quizId) { this.quizId = quizId; }

    public String getQuestionType() { return questionType; }
    public void setQuestionType(String questionType) { this.questionType = questionType; }

    public int getMarks() { return marks; }
    public void setMarks(int marks) { this.marks = marks; }

    public String getDifficultyLevel() { return difficultyLevel; }
    public void setDifficultyLevel(String difficultyLevel) { this.difficultyLevel = difficultyLevel; }

    public abstract boolean checkAnswer(T answer);
}
