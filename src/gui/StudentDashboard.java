package gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class StudentDashboard extends JFrame {

    private final LoginFrame.User user;

    public StudentDashboard(LoginFrame.User user) {
        this.user = user;

        setTitle("EXAMIFY - Student Dashboard");
        setSize(1200, 720);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        DashboardBackgroundPanel root = new DashboardBackgroundPanel();
        root.setLayout(new GridBagLayout());

        JPanel card = new RoundedPanel(30, new Color(255, 255, 255, 32));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(35, 40, 35, 40));
        card.setPreferredSize(new Dimension(700, 420));

        JLabel title = new JLabel("Welcome to EXAMIFY");
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Serif", Font.BOLD, 38));

        JLabel subtitle = new JLabel("Logged in as " + user.fullName);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        subtitle.setForeground(new Color(215, 226, 244));
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 18));

        JPanel statsPanel = new JPanel(new GridLayout(1, 3, 18, 0));
        statsPanel.setOpaque(false);
        statsPanel.setMaximumSize(new Dimension(620, 110));

        statsPanel.add(buildMiniCard("Quizzes", "12"));
        statsPanel.add(buildMiniCard("Completed", "8"));
        statsPanel.add(buildMiniCard("Average", "84%"));

        JButton startQuizBtn = createPrimaryButton("Start Quiz");
        startQuizBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        startQuizBtn.addActionListener(e -> {
            dispose();
            new QuizFrame(user).setVisible(true);
        });

        JButton logoutBtn = createSecondaryButton("Logout");
        logoutBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        logoutBtn.addActionListener(e -> {
            dispose();
            new LoginFrame().setVisible(true);
        });

        card.add(title);
        card.add(Box.createVerticalStrut(12));
        card.add(subtitle);
        card.add(Box.createVerticalStrut(30));
        card.add(statsPanel);
        card.add(Box.createVerticalStrut(30));
        card.add(startQuizBtn);
        card.add(Box.createVerticalStrut(14));
        card.add(logoutBtn);

        root.add(card);
        setContentPane(root);
    }

    private JPanel buildMiniCard(String heading, String value) {
        JPanel panel = new RoundedPanel(22, new Color(255, 255, 255, 28));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(18, 18, 18, 18));

        JLabel h = new JLabel(heading);
        h.setForeground(new Color(210, 200, 255));
        h.setFont(new Font("SansSerif", Font.BOLD, 16));
        h.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel v = new JLabel(value);
        v.setForeground(Color.WHITE);
        v.setFont(new Font("SansSerif", Font.BOLD, 28));
        v.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(Box.createVerticalGlue());
        panel.add(h);
        panel.add(Box.createVerticalStrut(8));
        panel.add(v);
        panel.add(Box.createVerticalGlue());

        return panel;
    }

    private JButton createPrimaryButton(String text) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setBackground(new Color(183, 171, 255));
        button.setForeground(new Color(35, 24, 71));
        button.setFont(new Font("SansSerif", Font.BOLD, 17));
        button.setBorder(new EmptyBorder(14, 30, 14, 30));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private JButton createSecondaryButton(String text) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setBackground(new Color(126, 220, 223));
        button.setForeground(new Color(20, 40, 70));
        button.setFont(new Font("SansSerif", Font.BOLD, 16));
        button.setBorder(new EmptyBorder(12, 30, 12, 30));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
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
            g2.setColor(new Color(255, 255, 255, 50));
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    static class DashboardBackgroundPanel extends JPanel {
        private float shift = 0f;

        DashboardBackgroundPanel() {
            Timer timer = new Timer(40, e -> {
                shift += 0.01f;
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

            int x1 = (int) (80 + 40 * Math.sin(shift));
            int y1 = (int) (80 + 25 * Math.cos(shift));
            g2.setColor(new Color(180, 167, 255, 35));
            g2.fillOval(x1, y1, 250, 250);

            int x2 = (int) (getWidth() - 320 + 45 * Math.cos(shift));
            int y2 = (int) (getHeight() - 280 + 20 * Math.sin(shift));
            g2.setColor(new Color(126, 220, 223, 30));
            g2.fillOval(x2, y2, 240, 240);

            g2.dispose();
        }
    }
}