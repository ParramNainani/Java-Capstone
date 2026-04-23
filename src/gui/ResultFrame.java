package gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

import model.User;

public class ResultFrame extends JFrame {

    private final User user;
    private final int score;
    private final int total;

    public ResultFrame(User user, int score, int total) {
        this.user = user;
        this.score = score;
        this.total = total;

        setTitle("EXAMIFY - Result");
        setSize(1200, 720);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel root = new ResultBackgroundPanel();
        root.setLayout(new GridBagLayout());

        JPanel card = new RoundedPanel(30, new Color(255, 255, 255, 30));
        card.setPreferredSize(new Dimension(650, 420));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(35, 35, 35, 35));

        JLabel title = new JLabel("Quiz Completed");
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Serif", Font.BOLD, 38));

        JLabel userLabel = new JLabel("Student: " + user.getUsername());
        userLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        userLabel.setForeground(new Color(215, 226, 244));
        userLabel.setFont(new Font("SansSerif", Font.PLAIN, 18));

        JLabel scoreLabel = new JLabel("Your Score: " + score + " / " + total);
        scoreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        scoreLabel.setForeground(new Color(126, 220, 223));
        scoreLabel.setFont(new Font("SansSerif", Font.BOLD, 34));

        int percent = (int) Math.round((score * 100.0) / total);
        JLabel percentLabel = new JLabel("Percentage: " + percent + "%");
        percentLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        percentLabel.setForeground(new Color(210, 200, 255));
        percentLabel.setFont(new Font("SansSerif", Font.BOLD, 24));

        JLabel message = new JLabel(getPerformanceMessage(percent));
        message.setAlignmentX(Component.CENTER_ALIGNMENT);
        message.setForeground(Color.WHITE);
        message.setFont(new Font("SansSerif", Font.PLAIN, 18));

        JButton dashboardBtn = createPrimaryButton("Back to Dashboard");
        dashboardBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        dashboardBtn.addActionListener(e -> {
            dispose();
            new StudentDashboard(user).setVisible(true);
        });

        JButton logoutBtn = createSecondaryButton("Logout");
        logoutBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        logoutBtn.addActionListener(e -> {
            dispose();
            new LoginFrame().setVisible(true);
        });

        card.add(title);
        card.add(Box.createVerticalStrut(12));
        card.add(userLabel);
        card.add(Box.createVerticalStrut(28));
        card.add(scoreLabel);
        card.add(Box.createVerticalStrut(12));
        card.add(percentLabel);
        card.add(Box.createVerticalStrut(18));
        card.add(message);
        card.add(Box.createVerticalStrut(30));
        card.add(dashboardBtn);
        card.add(Box.createVerticalStrut(12));
        card.add(logoutBtn);

        root.add(card);
        setContentPane(root);
    }

    private String getPerformanceMessage(int percent) {
        if (percent >= 85) return "Excellent performance!";
        if (percent >= 60) return "Good job! Keep improving.";
        return "Keep practicing. You can do better.";
    }

    private JButton createPrimaryButton(String text) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setBackground(new Color(183, 171, 255));
        button.setForeground(new Color(35, 24, 71));
        button.setFont(new Font("SansSerif", Font.BOLD, 16));
        button.setBorder(new EmptyBorder(13, 24, 13, 24));
        return button;
    }

    private JButton createSecondaryButton(String text) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setBackground(new Color(126, 220, 223));
        button.setForeground(new Color(20, 40, 70));
        button.setFont(new Font("SansSerif", Font.BOLD, 16));
        button.setBorder(new EmptyBorder(13, 24, 13, 24));
        return button;
    }

    static class RoundedPanel extends JPanel {
        private final int radius;
        private final Color bgColor;

        RoundedPanel(int radius, Color bgColor) {
            this.radius = radius;
            this.bgColor = bgColor;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(bgColor);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
            g2.setColor(new Color(255, 255, 255, 55));
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    static class ResultBackgroundPanel extends JPanel {
        private float wave = 0f;

        ResultBackgroundPanel() {
            Timer timer = new Timer(45, e -> {
                wave += 0.05f;
                repaint();
            });
            timer.start();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();

            GradientPaint gp = new GradientPaint(
                    0, 0, new Color(19, 43, 73),
                    getWidth(), getHeight(), new Color(48, 95, 145)
            );
            g2.setPaint(gp);
            g2.fillRect(0, 0, getWidth(), getHeight());

            for (int i = 0; i < 14; i++) {
                int x = (int) (70 + i * 80 + 25 * Math.sin(wave + i));
                int y = (int) (100 + 40 * Math.cos(wave + i * 0.7));
                g2.setColor(new Color(180, 167, 255, 40));
                g2.fillOval(x, y, 14, 14);
            }

            g2.dispose();
        }
    }
}