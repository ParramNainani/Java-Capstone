package gui;

import dao.QuizDAO;
import model.Quiz;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class QuizManagementPanel extends JPanel {

    private final Color BG = new Color(245, 247, 250);
    private final Color DARK = new Color(20, 40, 70);
    private final Color MUTED = new Color(113, 128, 150);
    private final Color PURPLE = new Color(138, 43, 226);
    private final Color RED = new Color(255, 80, 80);

    private CardLayout cardLayout;
    private JPanel container;
    
    private JPanel listPanel;
    private QuizCreationPanel editPanel;
    private Runnable onRefreshDashboard;

    public QuizManagementPanel(Runnable onRefreshDashboard) {
        this.onRefreshDashboard = onRefreshDashboard;
        setLayout(new BorderLayout());
        setOpaque(false);

        cardLayout = new CardLayout();
        container = new JPanel(cardLayout);
        container.setOpaque(false);

        listPanel = new JPanel(new BorderLayout());
        listPanel.setOpaque(false);
        
        editPanel = new QuizCreationPanel(() -> showList());

        container.add(listPanel, "list");
        container.add(editPanel, "edit");

        add(container, BorderLayout.CENTER);
        
        buildListPanel();
    }

    private void buildListPanel() {
        listPanel.removeAll();

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(0, 0, 20, 0));

        JLabel title = new JLabel("All Quizzes");
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        title.setForeground(DARK);
        header.add(title, BorderLayout.WEST);

        JButton addBtn = new JButton("+ Add a Quiz");
        addBtn.setFont(new Font("SansSerif", Font.BOLD, 14));
        addBtn.setBackground(PURPLE);
        addBtn.setForeground(Color.BLACK);
        addBtn.setFocusPainted(false);
        addBtn.setBorder(new EmptyBorder(10, 20, 10, 20));
        addBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        addBtn.addActionListener(e -> {
            editPanel.reset();
            cardLayout.show(container, "edit");
        });
        header.add(addBtn, BorderLayout.EAST);

        listPanel.add(header, BorderLayout.NORTH);

        JPanel listWrap = new JPanel();
        listWrap.setLayout(new BoxLayout(listWrap, BoxLayout.Y_AXIS));
        listWrap.setOpaque(false);

        QuizDAO qDao = new QuizDAO();
        List<Quiz> quizzes = qDao.getAllActiveQuizzes();

        if (quizzes.isEmpty()) {
            JLabel empty = new JLabel("No quizzes found. Click '+ Add a Quiz' to create one.");
            empty.setFont(new Font("SansSerif", Font.ITALIC, 16));
            empty.setForeground(MUTED);
            empty.setAlignmentX(Component.CENTER_ALIGNMENT);
            listWrap.add(Box.createVerticalStrut(50));
            listWrap.add(empty);
        } else {
            for (Quiz q : quizzes) {
                listWrap.add(buildQuizRow(q, qDao));
                listWrap.add(Box.createVerticalStrut(15));
            }
        }

        JScrollPane scroll = new JScrollPane(listWrap);
        scroll.setBorder(null);
        scroll.getViewport().setOpaque(false);
        scroll.setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        listPanel.add(scroll, BorderLayout.CENTER);
        listPanel.revalidate();
        listPanel.repaint();
    }

    private JPanel buildQuizRow(Quiz q, QuizDAO dao) {
        JPanel row = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.dispose();
            }
        };
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        row.setPreferredSize(new Dimension(800, 80));
        row.setBorder(new EmptyBorder(15, 25, 15, 25));

        JPanel info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setOpaque(false);

        JLabel title = new JLabel(q.getTitle());
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        title.setForeground(DARK);

        JLabel sub = new JLabel(q.getTimeLimit() + " seconds limit");
        sub.setFont(new Font("SansSerif", Font.PLAIN, 13));
        sub.setForeground(MUTED);

        info.add(title);
        info.add(sub);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        actions.setOpaque(false);

        JButton edit = createActionButton("Edit", PURPLE);
        edit.addActionListener(e -> {
            editPanel.loadQuiz(q);
            cardLayout.show(container, "edit");
        });

        JButton delete = createActionButton("Delete", RED);
        delete.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, 
                "Are you sure you want to delete this quiz?", "Delete Quiz", 
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (confirm == JOptionPane.YES_OPTION) {
                dao.deleteQuiz(q.getNumericQuizId());
                onRefreshDashboard.run();
                refreshList();
            }
        });

        actions.add(edit);
        actions.add(delete);

        row.add(info, BorderLayout.CENTER);
        row.add(actions, BorderLayout.EAST);

        return row;
    }

    private JButton createActionButton(String text, Color fg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("SansSerif", Font.BOLD, 14));
        btn.setForeground(fg);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    public void showList() {
        refreshList();
        cardLayout.show(container, "list");
    }

    public void refreshList() {
        buildListPanel();
    }
    
    public void editQuizDirectly(Quiz q) {
        editPanel.loadQuiz(q);
        cardLayout.show(container, "edit");
    }
}
