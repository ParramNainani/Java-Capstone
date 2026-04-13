package model;

/**
 * Question - Abstract base class for quiz questions.
 */
public abstract class Question {
    protected String questionText;
    protected int marks;

    public Question(String questionText, int marks) {
        this.questionText = questionText;
        this.marks = marks;
    }

    public abstract boolean checkAnswer(String answer);
}
