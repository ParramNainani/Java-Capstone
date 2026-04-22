package model;

import java.util.Map;

public class MCQQuestion extends Question<String> {
    private Map<String, String> options;

    public MCQQuestion(int questionId, String questionText, Map<String, String> options, String correctAnswer) {
        super(questionId, questionText, correctAnswer);
        this.options = options;
    }

    public Map<String, String> getOptions() {
        return options;
    }

    @Override
    public boolean checkAnswer(String answer) {
        return getCorrectAnswer().equalsIgnoreCase(answer);
    }
}
