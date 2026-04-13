package model;

/**
 * ShortAnswerQuestion - Short answer question.
 */
public class ShortAnswerQuestion extends Question {
    private String correctAnswer;

    public ShortAnswerQuestion(String questionText, int marks, String correctAnswer) {
        super(questionText, marks);
        this.correctAnswer = correctAnswer;
    }

    @Override
    public boolean checkAnswer(String answer) {
        // Implement short answer checking
        return false;
    }
}
