package gui;

import javax.swing.*;

/**
 * QuizFrame - GUI for taking quizzes.
 */
public class QuizFrame extends JFrame {
    public QuizFrame() {
        setTitle("Quiz");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        // Add quiz components here
        setVisible(true);
    }
}
