package gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.*;
import java.util.ArrayList;
import java.util.List;

public class QuizFrame extends JFrame {

    private final LoginFrame.User user;
    private final List<Question> questions = new ArrayList<>();
    private final ButtonGroup optionsGroup = new ButtonGroup();

    private int currentIndex = 0;
    private int score        = 0;

    // UI refs
    private final JLabel questionCountLabel = new JLabel();
    private final JTextArea questionArea    = new JTextArea();
    private final JRadioButton[] options    = new JRadioButton[4];
    private final JLabel timerLabel         = new JLabel();
    private       int timeLeft              = 60;
    private       Timer quizTimer;

    // progress dots
    private JPanel dotsPanel;

    // colours (same brand palette)
    private static final Color CYAN     = new Color(38,  208, 206);
    private static final Color PURPLE   = new Color(138,  43, 226);
    private static final Color BLUE     = new Color(30,  144, 255);
    private static final Color BG_DARK  = new Color(10,   15,  30);
    private static final Color TEXT_DIM = new Color(200, 210, 230);
    private static final Color ACCENT   = new Color(255, 200,  80);

    public QuizFrame(LoginFrame.User user) {
        this.user = user;
        setTitle("EXAMIFY – Quiz");
        setSize(1280, 760);
        setMinimumSize(new Dimension(1000, 640));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        addSampleQuestions();

        StudentDashboard.BlobBackground root = new StudentDashboard.BlobBackground();
        root.setLayout(new BorderLayout());
        root.add(buildTopBar(), BorderLayout.NORTH);
        root.add(buildCenterPanel(), BorderLayout.CENTER);
        root.add(buildBottomBar(), BorderLayout.SOUTH);

        setContentPane(root);
        loadQuestion();
        startTimer();
    }

    // ── Top bar ───────────────────────────────────────────────────────────────
    private JPanel buildTopBar() {
        JPanel bar = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(new Color(10, 15, 30, 200));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(new Color(255,255,255,40));
                g2.drawLine(0, getHeight()-1, getWidth(), getHeight()-1);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        bar.setOpaque(false);
        bar.setBorder(new EmptyBorder(14, 32, 14, 32));

        // Left: brand + back
        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 0));
        left.setOpaque(false);

        LoginFrame.AnimatedButton backBtn = new LoginFrame.AnimatedButton(
                "← Dashboard", new Color(0,0,0,0), new Color(255,255,255,30));
        backBtn.setForeground(TEXT_DIM);
        backBtn.setFont(new Font("SansSerif", Font.BOLD, 14));
        backBtn.setBorder(new EmptyBorder(8, 16, 8, 16));
        backBtn.addActionListener(e -> { if (quizTimer != null) quizTimer.stop();
                                         dispose(); new StudentDashboard(user).setVisible(true); });

        JLabel brand = new JLabel("EXAMIFY");
        brand.setForeground(Color.WHITE);
        brand.setFont(new Font("SansSerif", Font.BOLD, 22));

        left.add(brand);
        left.add(backBtn);
        bar.add(left, BorderLayout.WEST);

        // Centre: progress dots
        dotsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        dotsPanel.setOpaque(false);
        bar.add(dotsPanel, BorderLayout.CENTER);

        // Right: timer pill
        JPanel timerPill = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255,255,255,20));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight());
                g2.setColor(new Color(255,255,255,50));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, getHeight(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        timerPill.setOpaque(false);
        timerPill.setLayout(new FlowLayout(FlowLayout.CENTER, 6, 8));

        JLabel clockIco = new JLabel("⏱");
        clockIco.setFont(new Font("SansSerif", Font.PLAIN, 18));
        timerLabel.setForeground(CYAN);
        timerLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        timerPill.add(clockIco);
        timerPill.add(timerLabel);
        bar.add(timerPill, BorderLayout.EAST);

        return bar;
    }

    // ── Centre ────────────────────────────────────────────────────────────────
    private JPanel buildCenterPanel() {
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setOpaque(false);
        wrapper.setBorder(new EmptyBorder(24, 48, 0, 48));

        StudentDashboard.GlassCard card = new StudentDashboard.GlassCard(26);
        card.setPreferredSize(new Dimension(820, 480));
        card.setLayout(new BorderLayout(0, 0));
        card.setBorder(new EmptyBorder(32, 36, 28, 36));

        // ── Question header ──
        JPanel qHead = new JPanel(new BorderLayout());
        qHead.setOpaque(false);

        questionCountLabel.setForeground(CYAN);
        questionCountLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        qHead.add(questionCountLabel, BorderLayout.WEST);

        // Category badge
        JLabel catBadge = new JLabel("  Java Fundamentals  ");
        catBadge.setForeground(PURPLE.brighter());
        catBadge.setFont(new Font("SansSerif", Font.BOLD, 12));
        catBadge.setOpaque(true);
        catBadge.setBackground(new Color(138,43,226, 60));
        catBadge.setBorder(BorderFactory.createEmptyBorder(4,10,4,10));
        qHead.add(catBadge, BorderLayout.EAST);

        // ── Question text ──
        questionArea.setEditable(false);
        questionArea.setLineWrap(true);
        questionArea.setWrapStyleWord(true);
        questionArea.setOpaque(false);
        questionArea.setForeground(Color.WHITE);
        questionArea.setFont(new Font("SansSerif", Font.BOLD, 22));
        questionArea.setBorder(null);
        questionArea.setFocusable(false);

        JPanel qTextWrapper = new JPanel();
        qTextWrapper.setOpaque(false);
        qTextWrapper.setLayout(new BoxLayout(qTextWrapper, BoxLayout.Y_AXIS));
        qTextWrapper.add(Box.createVerticalStrut(18));
        qTextWrapper.add(questionArea);

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(qHead, BorderLayout.NORTH);
        top.add(qTextWrapper, BorderLayout.CENTER);

        // ── Options ──
        JPanel optPanel = new JPanel();
        optPanel.setOpaque(false);
        optPanel.setLayout(new GridLayout(4, 1, 0, 10));

        for (int i = 0; i < 4; i++) {
            options[i] = createOptionButton(i);
            optionsGroup.add(options[i]);
            optPanel.add(options[i]);
        }

        JPanel centerInner = new JPanel(new BorderLayout(0, 22));
        centerInner.setOpaque(false);
        centerInner.add(top, BorderLayout.NORTH);
        centerInner.add(optPanel, BorderLayout.CENTER);

        card.add(centerInner, BorderLayout.CENTER);
        wrapper.add(card);
        return wrapper;
    }

    // ── Bottom nav bar ────────────────────────────────────────────────────────
    private JPanel buildBottomBar() {
        JPanel bar = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(new Color(10, 15, 30, 200));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(new Color(255,255,255,40));
                g2.drawLine(0, 0, getWidth(), 0);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        bar.setOpaque(false);
        bar.setBorder(new EmptyBorder(14, 48, 14, 48));

        // Score preview on the left
        JLabel scoreLabel = new JLabel("Score: -- / " + 4);
        scoreLabel.setForeground(TEXT_DIM);
        scoreLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        bar.add(scoreLabel, BorderLayout.WEST);

        // Buttons on the right
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        btnRow.setOpaque(false);

        LoginFrame.AnimatedButton prevBtn = new LoginFrame.AnimatedButton(
                "← Previous", new Color(255,255,255,25), new Color(255,255,255,50));
        prevBtn.setForeground(Color.WHITE);
        prevBtn.setFont(new Font("SansSerif", Font.BOLD, 14));
        prevBtn.setBorder(new EmptyBorder(12, 22, 12, 22));
        prevBtn.addActionListener(e -> previousQuestion());

        LoginFrame.AnimatedButton nextBtn = new LoginFrame.AnimatedButton(
                "Next →", CYAN, new Color(60, 230, 225));
        nextBtn.setForeground(new Color(10, 15, 30));
        nextBtn.setFont(new Font("SansSerif", Font.BOLD, 14));
        nextBtn.setBorder(new EmptyBorder(12, 22, 12, 22));
        nextBtn.addActionListener(e -> nextQuestion());

        LoginFrame.AnimatedButton submitBtn = new LoginFrame.AnimatedButton(
                "Submit Quiz ✓", new Color(116, 224, 187), new Color(80, 200, 160));
        submitBtn.setForeground(new Color(10, 40, 30));
        submitBtn.setFont(new Font("SansSerif", Font.BOLD, 14));
        submitBtn.setBorder(new EmptyBorder(12, 22, 12, 22));
        submitBtn.addActionListener(e -> submitQuiz());

        btnRow.add(prevBtn);
        btnRow.add(nextBtn);
        btnRow.add(submitBtn);
        bar.add(btnRow, BorderLayout.EAST);

        return bar;
    }

    // ── Option button (custom pill) ────────────────────────────────────────────
    private JRadioButton createOptionButton(int idx) {
        String[] labels = {"A", "B", "C", "D"};
        Color[] accents = {CYAN, PURPLE.brighter(), BLUE.brighter(), ACCENT};

        JRadioButton rb = new JRadioButton() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color base = isSelected()
                    ? new Color(accents[idx].getRed(), accents[idx].getGreen(), accents[idx].getBlue(), 60)
                    : new Color(255,255,255, 15);
                g2.setColor(base);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                Color border = isSelected() ? accents[idx] : new Color(255,255,255,50);
                g2.setColor(border);
                g2.setStroke(new BasicStroke(isSelected() ? 2f : 1f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 14, 14);
                // letter badge
                g2.setColor(isSelected() ? accents[idx] : new Color(255,255,255,120));
                g2.fillRoundRect(12, (getHeight()-28)/2, 28, 28, 8, 8);
                g2.setColor(new Color(10,15,30));
                g2.setFont(new Font("SansSerif", Font.BOLD, 14));
                FontMetrics fm = g2.getFontMetrics();
                String letter = labels[idx];
                g2.drawString(letter, 12+(28-fm.stringWidth(letter))/2,
                              (getHeight()-28)/2 + (28+fm.getAscent()-fm.getDescent())/2);
                g2.dispose();
                // don't call super – custom drawn
                // paint text manually
                Graphics2D g3 = (Graphics2D) g.create();
                g3.setFont(new Font("SansSerif", Font.PLAIN, 16));
                g3.setColor(isSelected() ? Color.WHITE : TEXT_DIM);
                g3.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                FontMetrics fm2 = g3.getFontMetrics();
                g3.drawString(getText(), 50, (getHeight() + fm2.getAscent() - fm2.getDescent()) / 2);
                g3.dispose();
            }
        };
        rb.setOpaque(false);
        rb.setFocusPainted(false);
        rb.setBorderPainted(false);
        rb.setContentAreaFilled(false);
        rb.setCursor(new Cursor(Cursor.HAND_CURSOR));
        rb.setPreferredSize(new Dimension(0, 52));
        rb.setMaximumSize(new Dimension(Integer.MAX_VALUE, 52));
        rb.addActionListener(e -> rb.repaint());
        return rb;
    }

    // ── Logic ─────────────────────────────────────────────────────────────────
    private void addSampleQuestions() {
        questions.add(new Question("Which keyword is used to inherit a class in Java?",
                new String[]{"implements", "extends", "inherits", "super"}, 1));
        questions.add(new Question("Which package contains Swing classes?",
                new String[]{"java.awt", "javax.swing", "java.io", "java.sql"}, 1));
        questions.add(new Question("Which method is the entry point of a Java program?",
                new String[]{"start()", "run()", "main()", "init()"}, 2));
        questions.add(new Question("Which layout arranges components like cards?",
                new String[]{"GridLayout", "FlowLayout", "BorderLayout", "CardLayout"}, 3));
    }

    private void loadQuestion() {
        Question q = questions.get(currentIndex);
        questionCountLabel.setText("Question " + (currentIndex+1) + " of " + questions.size());
        questionArea.setText(q.question);
        for (int i = 0; i < 4; i++) options[i].setText(q.options[i]);
        optionsGroup.clearSelection();
        if      (q.userAnswer == 0) options[0].setSelected(true);
        else if (q.userAnswer == 1) options[1].setSelected(true);
        else if (q.userAnswer == 2) options[2].setSelected(true);
        else if (q.userAnswer == 3) options[3].setSelected(true);
        refreshDots();
        for (JRadioButton rb : options) rb.repaint();
    }

    private void refreshDots() {
        dotsPanel.removeAll();
        for (int i = 0; i < questions.size(); i++) {
            final int fi = i;
            JPanel dot = new JPanel() {
                @Override protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    Color c = (fi == currentIndex)    ? CYAN
                            : (questions.get(fi).userAnswer >= 0) ? new Color(116,224,187)
                            : new Color(255,255,255,50);
                    g2.setColor(c);
                    g2.fillOval(0, 0, getWidth(), getHeight());
                    g2.dispose();
                }
            };
            int sz = (fi == currentIndex) ? 12 : 8;
            dot.setPreferredSize(new Dimension(sz, sz));
            dot.setOpaque(false);
            dotsPanel.add(dot);
        }
        dotsPanel.revalidate();
        dotsPanel.repaint();
    }

    private void saveCurrentSelection() {
        Question q = questions.get(currentIndex);
        if      (options[0].isSelected()) q.userAnswer = 0;
        else if (options[1].isSelected()) q.userAnswer = 1;
        else if (options[2].isSelected()) q.userAnswer = 2;
        else if (options[3].isSelected()) q.userAnswer = 3;
        else                              q.userAnswer = -1;
    }

    private void nextQuestion() {
        saveCurrentSelection();
        if (currentIndex < questions.size()-1) { currentIndex++; loadQuestion(); }
    }

    private void previousQuestion() {
        saveCurrentSelection();
        if (currentIndex > 0) { currentIndex--; loadQuestion(); }
    }

    private void submitQuiz() {
        saveCurrentSelection();
        if (quizTimer != null) quizTimer.stop();
        score = 0;
        for (Question q : questions) if (q.userAnswer == q.correctIndex) score++;
        dispose();
        new ResultFrame(user, score, questions.size()).setVisible(true);
    }

    private void startTimer() {
        quizTimer = new Timer(1000, e -> {
            timeLeft--;
            int mins = timeLeft / 60, secs = timeLeft % 60;
            timerLabel.setText(String.format("%d:%02d", mins, secs));
            if (timeLeft <= 10) timerLabel.setForeground(new Color(255, 120, 120));
            if (timeLeft <= 0) { quizTimer.stop(); submitQuiz(); }
        });
        timerLabel.setText("1:00");
        quizTimer.start();
    }

    // ── Data class ────────────────────────────────────────────────────────────
    static class Question {
        String question;
        String[] options;
        int correctIndex;
        int userAnswer = -1;

        Question(String question, String[] options, int correctIndex) {
            this.question     = question;
            this.options      = options;
            this.correctIndex = correctIndex;
        }
    }
}