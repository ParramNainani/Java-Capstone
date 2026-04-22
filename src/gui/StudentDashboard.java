package gui;

import javax.swing.*;
import java.awt.*;

/**
 * StudentDashboard - GUI for student options after login.
 */
public class StudentDashboard extends JFrame {
    public StudentDashboard(String username) {
        setTitle("Student Dashboard - " + username);
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        initComponents(username);
        setVisible(true);
    }
    
    private void initComponents(String username) {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel welcomeLabel = new JLabel("Welcome, " + username + "!", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        mainPanel.add(welcomeLabel, BorderLayout.NORTH);
        
        JPanel buttonPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        
        JButton takeQuizBtn = new JButton("Take Quiz");
        JButton viewResultsBtn = new JButton("View Results / Certificates");
        JButton logoutBtn = new JButton("Logout");
        
        takeQuizBtn.setFont(new Font("SansSerif", Font.PLAIN, 16));
        viewResultsBtn.setFont(new Font("SansSerif", Font.PLAIN, 16));
        logoutBtn.setFont(new Font("SansSerif", Font.PLAIN, 16));
        
        buttonPanel.add(takeQuizBtn);
        buttonPanel.add(viewResultsBtn);
        buttonPanel.add(logoutBtn);
        
        mainPanel.add(buttonPanel, BorderLayout.CENTER);
        
        // Action Listeners
        takeQuizBtn.addActionListener(e -> {
            new QuizFrame();
            dispose();
        });
        
        viewResultsBtn.addActionListener(e -> {
            new ResultFrame();
        });
        
        logoutBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to logout?", "Logout", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                new LoginFrame();
                dispose();
            }
        });
        
        add(mainPanel);
    }
}
