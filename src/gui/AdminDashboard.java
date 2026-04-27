package gui;

import javax.swing.*;
import java.awt.*;

/**
 * AdminDashboard - GUI for admin operations.
 */
public class AdminDashboard extends JFrame {

    private CardLayout cardLayout;
    private JPanel mainPanel;

    public AdminDashboard() {
        setTitle("Admin Dashboard");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // 🔹 Sidebar
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new GridLayout(4, 1, 10, 10));

        JButton btnQuestions = new JButton("Manage Questions");
        JButton btnQuiz = new JButton("Create Quiz");
        JButton btnAnalytics = new JButton("Analytics");

        sidebar.add(btnQuestions);
        sidebar.add(btnQuiz);
        sidebar.add(btnAnalytics);

        // 🔹 Main Panel with CardLayout
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        mainPanel.add(new QuestionPanel(), "questions");
        mainPanel.add(new JLabel("Quiz Creation Panel (Coming Soon)"), "quiz");
        mainPanel.add(new JLabel("Analytics Panel (Coming Soon)"), "analytics");

        // 🔹 Button Actions
        btnQuestions.addActionListener(e -> cardLayout.show(mainPanel, "questions"));
        btnQuiz.addActionListener(e -> cardLayout.show(mainPanel, "quiz"));
        btnAnalytics.addActionListener(e -> cardLayout.show(mainPanel, "analytics"));

        add(sidebar, BorderLayout.WEST);
        add(mainPanel, BorderLayout.CENTER);

        setVisible(true);
    }
}