package model;

public class TrueFalseQuestion extends Question<Boolean> {

    public TrueFalseQuestion(int questionId, String questionText, Boolean correctAnswer) {
        super(questionId, questionText, correctAnswer);
    }

    @Override
    public boolean checkAnswer(Boolean answer) {
        return getCorrectAnswer().equals(answer);
    }
}
