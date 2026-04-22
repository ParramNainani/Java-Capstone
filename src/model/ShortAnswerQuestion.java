package model;

public class ShortAnswerQuestion extends Question<String> {

    public ShortAnswerQuestion(int questionId, String questionText, String correctAnswer) {
        super(questionId, questionText, correctAnswer);
    }

    @Override
    public boolean checkAnswer(String answer) {
        return getCorrectAnswer().equalsIgnoreCase(answer.trim());
    }
}
