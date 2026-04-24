package gui;

import dao.QuizDAO;
import dao.ResultDAO;
import model.Quiz;
import model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class StudentQuizzesPanel extends JPanel {

    // Dark theme colors matching the dashboard
    private static final Color CYAN      = new Color(38,  208, 206);
    private static final Color PURPLE    = new Color(138,  43, 226);
    private static final Color TEXT_DIM  = new Color(200, 210, 230);
    private static final Color GLASS     = new Color(255, 255, 255, 18);
    private static final Color GLASS_BD  = new Color(255, 255, 255, 40);
    private static final Color GREEN     = new Color(116, 224, 187);

    private User user;
    private JPanel listWrap;

    public StudentQuizzesPanel(User user, Runnable onRefreshDashboard) {
        this.user = user;

        setLayout(new BorderLayout());
        setOpaque(false);
        setBorder(new EmptyBorder(25, 30, 20, 30));

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(0, 0, 15, 0));

        JLabel title = new JLabel("My Quizzes");
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        title.setForeground(Color.WHITE);
        header.add(title, BorderLayout.WEST);

        add(header, BorderLayout.NORTH);

        listWrap = new JPanel();
        listWrap.setLayout(new BoxLayout(listWrap, BoxLayout.Y_AXIS));
        listWrap.setOpaque(false);

        JScrollPane scroll = new JScrollPane(listWrap);
        scroll.setBorder(null);
        scroll.getViewport().setOpaque(false);
        scroll.setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        add(scroll, BorderLayout.CENTER);
        
        refreshData();
    }

    public void refreshData() {
        listWrap.removeAll();

        QuizDAO qDao = new QuizDAO();
        ResultDAO rDao = new ResultDAO();

        List<Quiz> activeQuizzes = qDao.getAllActiveQuizzes();
        List<model.ResultDetail> userResults = rDao.getStudentResults(user.getUserId());

        Set<String> completedQuizTitles = userResults.stream()
                .map(r -> r.getQuizTitle())
                .collect(Collectors.toSet());

        List<Quiz> unattempted = new ArrayList<>();
        List<Quiz> attempted = new ArrayList<>();

        for (Quiz q : activeQuizzes) {
            if (completedQuizTitles.contains(q.getTitle())) {
                attempted.add(q);
            } else {
                unattempted.add(q);
            }
        }

        // ── Unattempted Section ──
        JLabel unattemptedLbl = new JLabel("  Unattempted Quizzes (" + unattempted.size() + ")");
        unattemptedLbl.setFont(new Font("SansSerif", Font.BOLD, 16));
        unattemptedLbl.setForeground(CYAN);
        unattemptedLbl.setBorder(new EmptyBorder(5, 0, 8, 0));
        unattemptedLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        listWrap.add(unattemptedLbl);

        if (unattempted.isEmpty()) {
            JLabel empty = new JLabel("  All quizzes completed! Great work.");
            empty.setFont(new Font("SansSerif", Font.ITALIC, 13));
            empty.setForeground(TEXT_DIM);
            empty.setAlignmentX(Component.LEFT_ALIGNMENT);
            listWrap.add(empty);
        } else {
            for (Quiz q : unattempted) {
                listWrap.add(buildQuizRow(q, false));
                listWrap.add(Box.createVerticalStrut(6));
            }
        }

        listWrap.add(Box.createVerticalStrut(20));

        // ── Attempted Section ──
        JLabel attemptedLbl = new JLabel("  Attempted Quizzes (" + attempted.size() + ")");
        attemptedLbl.setFont(new Font("SansSerif", Font.BOLD, 16));
        attemptedLbl.setForeground(GREEN);
        attemptedLbl.setBorder(new EmptyBorder(5, 0, 8, 0));
        attemptedLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        listWrap.add(attemptedLbl);

        if (attempted.isEmpty()) {
            JLabel empty = new JLabel("  You haven't attempted any quizzes yet.");
            empty.setFont(new Font("SansSerif", Font.ITALIC, 13));
            empty.setForeground(TEXT_DIM);
            empty.setAlignmentX(Component.LEFT_ALIGNMENT);
            listWrap.add(empty);
        } else {
            for (Quiz q : attempted) {
                listWrap.add(buildQuizRow(q, true));
                listWrap.add(Box.createVerticalStrut(6));
            }
        }

        listWrap.revalidate();
        listWrap.repaint();
    }

    private JPanel buildQuizRow(Quiz q, boolean isAttempted) {
        // Glass-style row matching the dashboard aesthetic
        JPanel row = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(GLASS);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                g2.setColor(GLASS_BD);
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 14, 14);
                g2.dispose();
            }
        };
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        row.setPreferredSize(new Dimension(800, 60));
        row.setBorder(new EmptyBorder(10, 20, 10, 20));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setOpaque(false);

        JLabel title = new JLabel(q.getTitle());
        title.setFont(new Font("SansSerif", Font.BOLD, 15));
        title.setForeground(Color.WHITE);

        JLabel sub = new JLabel(q.getTimeLimit() + "s time limit");
        sub.setFont(new Font("SansSerif", Font.PLAIN, 12));
        sub.setForeground(TEXT_DIM);

        info.add(title);
        info.add(sub);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        actionPanel.setOpaque(false);

        if (isAttempted) {
            JLabel lbl = new JLabel("Completed ✓");
            lbl.setFont(new Font("SansSerif", Font.BOLD, 13));
            lbl.setForeground(GREEN);
            actionPanel.add(lbl);
        } else {
            JButton startBtn = new JButton("Start Quiz") {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    GradientPaint gp = new GradientPaint(0, 0, CYAN, getWidth(), 0, PURPLE);
                    g2.setPaint(gp);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                    g2.dispose();
                    super.paintComponent(g);
                }
            };
            startBtn.setFont(new Font("SansSerif", Font.BOLD, 12));
            startBtn.setForeground(Color.WHITE);
            startBtn.setOpaque(false);
            startBtn.setContentAreaFilled(false);
            startBtn.setFocusPainted(false);
            startBtn.setBorderPainted(false);
            startBtn.setBorder(new EmptyBorder(6, 14, 6, 14));
            startBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            startBtn.addActionListener(e -> {
                Window win = SwingUtilities.getWindowAncestor(this);
                if (win instanceof JFrame) ((JFrame)win).dispose();
                new QuizFrame(user, q).setVisible(true);
            });
            actionPanel.add(startBtn);
        }

        row.add(info, BorderLayout.CENTER);
        row.add(actionPanel, BorderLayout.EAST);

        return row;
    }
}
