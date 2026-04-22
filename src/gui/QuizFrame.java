package gui;

import javax.swing.*;
import java.awt.*;

/**
 * QuizFrame - GUI for taking the quiz.
 */
public class QuizFrame extends JFrame {
    private JLabel questionLabel;
    private JRadioButton optionA, optionB, optionC, optionD;
    private ButtonGroup optionsGroup;
    private JButton nextButton;
    private JButton submitButton;
    private JLabel timerLabel;

    public QuizFrame() {
        setTitle("Active Quiz Session");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);
        
        initComponents();
        setVisible(true);
    }
    
    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Header Panel (Timer and Question number)
        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel qNoLabel = new JLabel("Question 1 of 10");
        qNoLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        timerLabel = new JLabel("Time Remaining: 15:00");
        timerLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        timerLabel.setForeground(Color.RED);
        
        headerPanel.add(qNoLabel, BorderLayout.WEST);
        headerPanel.add(timerLabel, BorderLayout.EAST);
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Center Panel (Question and Options)
        JPanel centerPanel = new JPanel(new GridLayout(6, 1, 5, 5));
        questionLabel = new JLabel("What is the capital of France?");
        questionLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        
        optionA = new JRadioButton("A) Berlin");
        optionB = new JRadioButton("B) Madrid");
        optionC = new JRadioButton("C) Paris");
        optionD = new JRadioButton("D) Rome");
        
        optionsGroup = new ButtonGroup();
        optionsGroup.add(optionA); optionsGroup.add(optionB);
        optionsGroup.add(optionC); optionsGroup.add(optionD);
        
        centerPanel.add(questionLabel);
        centerPanel.add(new JLabel("")); // spacer
        centerPanel.add(optionA);
        centerPanel.add(optionB);
        centerPanel.add(optionC);
        centerPanel.add(optionD);
        
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        
        // Footer Panel (Navigation)
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        nextButton = new JButton("Next Question >>");
        submitButton = new JButton("Submit Quiz");
        submitButton.setEnabled(false); // Enable only on last question
        
        footerPanel.add(nextButton);
        footerPanel.add(submitButton);
        mainPanel.add(footerPanel, BorderLayout.SOUTH);
        
        // Action Listeners
        nextButton.addActionListener(e -> {
            optionsGroup.clearSelection();
            questionLabel.setText("Sample Next Question...");
            // Toggle buttons just as a demo
            nextButton.setEnabled(false);
            submitButton.setEnabled(true);
            qNoLabel.setText("Question 10 of 10");
        });
        
        submitButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to submit?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                new ResultFrame();
                dispose();
            }
        });
        
        add(mainPanel);
    }
}
