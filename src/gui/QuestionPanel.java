package gui;

import dao.QuestionDAO;
import model.QuestionModel;

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
        QuestionModel q = new QuestionModel();

        q.setQuestionText(questionField.getText());
        q.setOptions(Arrays.asList(optionsField.getText().split(",")));
        q.setCorrectAnswer(answerField.getText());
        q.setCategory(categoryField.getText());

        QuestionDAO dao = new QuestionDAO();
        dao.addQuestion(q);

        JOptionPane.showMessageDialog(this, "Question Added!");
    }
}