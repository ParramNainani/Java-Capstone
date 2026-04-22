package model;

public abstract class Question<T> {
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

    public abstract boolean checkAnswer(T answer);
}
