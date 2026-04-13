package model;

/**
 * TrueFalseQuestion - True/False question.
 */
public class TrueFalseQuestion extends Question {
    private boolean correctAnswer;

    public TrueFalseQuestion(String questionText, int marks, boolean correctAnswer) {
        super(questionText, marks);
        this.correctAnswer = correctAnswer;
    }

    @Override
    public boolean checkAnswer(String answer) {
        // Implement True/False answer checking
        return false;
    }
}
