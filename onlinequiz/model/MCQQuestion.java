package model;

/**
 * MCQQuestion - Multiple choice question.
 */
public class MCQQuestion extends Question {
    private String[] options;
    private int correctOption;

    public MCQQuestion(String questionText, int marks, String[] options, int correctOption) {
        super(questionText, marks);
        this.options = options;
        this.correctOption = correctOption;
    }

    @Override
    public boolean checkAnswer(String answer) {
        // Implement MCQ answer checking
        return false;
    }
}
