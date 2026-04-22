package model;

import java.util.List;

/**
 * Represents Question data for DB and UI.
 * Separate from core logic Question<T>
 */
public class QuestionModel {

    private int id;
    private String questionText;
    private List<String> options;
    private String correctAnswer;
    private String category;

    public QuestionModel() {}

    public QuestionModel(int id, String questionText, List<String> options, String correctAnswer, String category) {
        this.id = id;
        this.questionText = questionText;
        this.options = options;
        this.correctAnswer = correctAnswer;
        this.category = category;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getQuestionText() { return questionText; }
    public void setQuestionText(String questionText) { this.questionText = questionText; }

    public List<String> getOptions() { return options; }
    public void setOptions(List<String> options) { this.options = options; }

    public String getCorrectAnswer() { return correctAnswer; }
    public void setCorrectAnswer(String correctAnswer) { this.correctAnswer = correctAnswer; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    @Override
    public String toString() {
        return "QuestionModel{" +
                "id=" + id +
                ", questionText='" + questionText + '\'' +
                ", options=" + options +
                ", correctAnswer='" + correctAnswer + '\'' +
                ", category='" + category + '\'' +
                '}';
    }
}