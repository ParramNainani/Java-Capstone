package gui;

import dao.QuestionDAO;
import model.Question;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

/**
 * Admin panel to add questions
 */
public class QuestionPanel extends JPanel {

    private JTextField questionField;
    private JTextField optionsField;
    private JTextField answerField;
    private JTextField categoryField;

    public QuestionPanel() {
        setLayout(new GridLayout(6, 2));

        questionField = new JTextField();
        optionsField = new JTextField();
        answerField = new JTextField();
        categoryField = new JTextField();

        JButton addButton = new JButton("Add Question");

        add(new JLabel("Question:"));
        add(questionField);

        add(new JLabel("Options (comma separated):"));
        add(optionsField);

        add(new JLabel("Correct Answer:"));
        add(answerField);

        add(new JLabel("Category:"));
        add(categoryField);

        add(new JLabel(""));
        add(addButton);

        addButton.addActionListener(e -> addQuestion());
    }

    private void addQuestion() {
        String questionText = questionField.getText().trim();
        String answer = answerField.getText().trim();
        String category = categoryField.getText().trim();

        if (questionText.isEmpty() || answer.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Question and Answer cannot be empty.");
            return;
        }

        Question<String> q = new Question<String>(0, questionText, answer) {
            @Override
            public boolean checkAnswer(String ans) {
                return this.getCorrectAnswer().equalsIgnoreCase(ans);
            }
        };
        q.setQuestionType("MCQ");
        q.setMarks(1);
        q.setDifficultyLevel(category);

        QuestionDAO dao = new QuestionDAO();
        dao.insertQuestion(q);

        questionField.setText("");
        optionsField.setText("");
        answerField.setText("");
        categoryField.setText("");

        JOptionPane.showMessageDialog(this, "Question Added!");
    }
}