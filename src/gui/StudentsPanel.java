package gui;

import dao.ResultDAO;
import model.ResultDetail;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.List;

public class StudentsPanel extends JPanel {

    private final Color BG_COLOR = new Color(244, 247, 251);
    private final Color TEXT_DARK = new Color(45, 55, 72);
    private final Color TEXT_MUTED = new Color(113, 128, 150);
    private final Color PRIMARY_PURPLE = new Color(136, 117, 255);

    public StudentsPanel() {
        setLayout(new BorderLayout());
        setBackground(BG_COLOR);
        setBorder(new EmptyBorder(40, 40, 40, 40));

        JLabel title = new JLabel("Student Submissions");
        title.setFont(new Font("SansSerif", Font.BOLD, 28));
        title.setForeground(TEXT_DARK);
        title.setBorder(new EmptyBorder(0, 0, 20, 0));
        add(title, BorderLayout.NORTH);

        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setOpaque(false);

        ResultDAO rDao = new ResultDAO();
        // Get more results for the full students view (e.g., 50)
        List<ResultDetail> results = rDao.getRecentResults(50);

        if (results.isEmpty()) {
            listPanel.add(new JLabel("No student has taken a quiz yet."));
        } else {
            for (ResultDetail res : results) {
                listPanel.add(createStudentRow(res));
                listPanel.add(Box.createVerticalStrut(10));
            }
        }

        JScrollPane scrollPane = new JScrollPane(listPanel);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(BG_COLOR);
        add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel createStudentRow(ResultDetail res) {
        JPanel row = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                g2.dispose();
            }
        };
        row.setOpaque(false);
        row.setBorder(new EmptyBorder(15, 20, 15, 20));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        left.setOpaque(false);
        JLabel name = new JLabel(res.getStudentName());
        name.setFont(new Font("SansSerif", Font.BOLD, 16));
        name.setForeground(TEXT_DARK);
        name.setPreferredSize(new Dimension(200, 20));

        JLabel quizName = new JLabel("Quiz: " + res.getQuizTitle());
        quizName.setFont(new Font("SansSerif", Font.PLAIN, 14));
        quizName.setForeground(TEXT_MUTED);
        quizName.setPreferredSize(new Dimension(300, 20));

        left.add(name);
        left.add(quizName);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 0));
        right.setOpaque(false);

        JLabel score = new JLabel(res.getPercentage() + "%");
        score.setFont(new Font("SansSerif", Font.BOLD, 16));
        score.setForeground(TEXT_DARK);

        JLabel status = new JLabel(" Completed ");
        status.setFont(new Font("SansSerif", Font.BOLD, 12));
        status.setForeground(new Color(40, 167, 69)); // Green
        status.setBorder(new LineBorder(new Color(40, 167, 69), 1, true));

        right.add(score);
        right.add(status);

        row.add(left, BorderLayout.WEST);
        row.add(right, BorderLayout.EAST);
        return row;
    }
}
