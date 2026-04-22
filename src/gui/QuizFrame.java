package gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class QuizFrame extends JFrame {

    private final LoginFrame.User user;
    private final List<Question> questions = new ArrayList<>();
    private final ButtonGroup optionsGroup = new ButtonGroup();

    private int currentIndex = 0;
    private int score = 0;

    private final JLabel quizTitle = new JLabel("Java Fundamentals Quiz");
    private final JLabel questionCountLabel = new JLabel();
    private final JTextArea questionArea = new JTextArea();
    private final JRadioButton option1 = createOption();
    private final JRadioButton option2 = createOption();
    private final JRadioButton option3 = createOption();
    private final JRadioButton option4 = createOption();
    private final JLabel timerLabel = new JLabel("Time Left: 60");
    private int timeLeft = 60;
    private Timer quizTimer;

    public QuizFrame(LoginFrame.User user) {
        this.user = user;

        setTitle("EXAMIFY - Quiz");
        setSize(1200, 720);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        addSampleQuestions();

        JPanel root = new QuizBackgroundPanel();
        root.setLayout(new GridBagLayout());

        JPanel card = new RoundedPanel(28, new Color(255, 255, 255, 30));
        card.setPreferredSize(new Dimension(780, 500));
        card.setLayout(new BorderLayout(18, 18));
        card.setBorder(new EmptyBorder(25, 25, 25, 25));

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);

        quizTitle.setForeground(Color.WHITE);
        quizTitle.setFont(new Font("Serif", Font.BOLD, 30));

        timerLabel.setForeground(new Color(126, 220, 223));
        timerLabel.setFont(new Font("SansSerif", Font.BOLD, 18));

        top.add(quizTitle, BorderLayout.WEST);
        top.add(timerLabel, BorderLayout.EAST);

        JPanel center = new JPanel();
        center.setOpaque(false);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));

        questionCountLabel.setForeground(new Color(210, 200, 255));
        questionCountLabel.setFont(new Font("SansSerif", Font.BOLD, 16));

        questionArea.setEditable(false);
        questionArea.setLineWrap(true);
        questionArea.setWrapStyleWord(true);
        questionArea.setOpaque(false);
        questionArea.setForeground(Color.WHITE);
        questionArea.setFont(new Font("SansSerif", Font.BOLD, 24));
        questionArea.setBorder(null);

        optionsGroup.add(option1);
        optionsGroup.add(option2);
        optionsGroup.add(option3);
        optionsGroup.add(option4);

        center.add(questionCountLabel);
        center.add(Box.createVerticalStrut(20));
        center.add(questionArea);
        center.add(Box.createVerticalStrut(20));
        center.add(option1);
        center.add(Box.createVerticalStrut(10));
        center.add(option2);
        center.add(Box.createVerticalStrut(10));
        center.add(option3);
        center.add(Box.createVerticalStrut(10));
        center.add(option4);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        bottom.setOpaque(false);

        JButton previousBtn = createSecondaryButton("Previous");
        JButton nextBtn = createPrimaryButton("Next");
        JButton submitBtn = createPrimaryButton("Submit Quiz");

        previousBtn.addActionListener(e -> previousQuestion());
        nextBtn.addActionListener(e -> nextQuestion());
        submitBtn.addActionListener(e -> submitQuiz());

        bottom.add(previousBtn);
        bottom.add(nextBtn);
        bottom.add(submitBtn);

        card.add(top, BorderLayout.NORTH);
        card.add(center, BorderLayout.CENTER);
        card.add(bottom, BorderLayout.SOUTH);

        root.add(card);
        setContentPane(root);

        loadQuestion();
        startTimer();
    }

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
        questionCountLabel.setText("Question " + (currentIndex + 1) + " of " + questions.size());
        questionArea.setText(q.question);
        option1.setText(q.options[0]);
        option2.setText(q.options[1]);
        option3.setText(q.options[2]);
        option4.setText(q.options[3]);

        optionsGroup.clearSelection();

        if (q.userAnswer == 0) option1.setSelected(true);
        else if (q.userAnswer == 1) option2.setSelected(true);
        else if (q.userAnswer == 2) option3.setSelected(true);
        else if (q.userAnswer == 3) option4.setSelected(true);
    }

    private void saveCurrentSelection() {
        Question q = questions.get(currentIndex);
        if (option1.isSelected()) q.userAnswer = 0;
        else if (option2.isSelected()) q.userAnswer = 1;
        else if (option3.isSelected()) q.userAnswer = 2;
        else if (option4.isSelected()) q.userAnswer = 3;
        else q.userAnswer = -1;
    }

    private void nextQuestion() {
        saveCurrentSelection();
        if (currentIndex < questions.size() - 1) {
            currentIndex++;
            loadQuestion();
        }
    }

    private void previousQuestion() {
        saveCurrentSelection();
        if (currentIndex > 0) {
            currentIndex--;
            loadQuestion();
        }
    }

    private void submitQuiz() {
        saveCurrentSelection();

        if (quizTimer != null) {
            quizTimer.stop();
        }

        score = 0;
        for (Question q : questions) {
            if (q.userAnswer == q.correctIndex) {
                score++;
            }
        }

        dispose();
        new ResultFrame(user, score, questions.size()).setVisible(true);
    }

    private void startTimer() {
        quizTimer = new Timer(1000, e -> {
            timeLeft--;
            timerLabel.setText("Time Left: " + timeLeft);

            if (timeLeft <= 0) {
                quizTimer.stop();
                submitQuiz();
            }
        });
        quizTimer.start();
    }

    private JRadioButton createOption() {
        JRadioButton rb = new JRadioButton();
        rb.setOpaque(false);
        rb.setForeground(Color.WHITE);
        rb.setFont(new Font("SansSerif", Font.PLAIN, 18));
        rb.setFocusPainted(false);
        return rb;
    }

    private JButton createPrimaryButton(String text) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setBackground(new Color(183, 171, 255));
        button.setForeground(new Color(35, 24, 71));
        button.setFont(new Font("SansSerif", Font.BOLD, 15));
        button.setBorder(new EmptyBorder(12, 18, 12, 18));
        return button;
    }

    private JButton createSecondaryButton(String text) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setBackground(new Color(126, 220, 223));
        button.setForeground(new Color(20, 40, 70));
        button.setFont(new Font("SansSerif", Font.BOLD, 15));
        button.setBorder(new EmptyBorder(12, 18, 12, 18));
        return button;
    }

    static class Question {
        String question;
        String[] options;
        int correctIndex;
        int userAnswer = -1;

        Question(String question, String[] options, int correctIndex) {
            this.question = question;
            this.options = options;
            this.correctIndex = correctIndex;
        }
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

    static class QuizBackgroundPanel extends JPanel {
        private float phase = 0;

        QuizBackgroundPanel() {
            Timer timer = new Timer(35, e -> {
                phase += 0.03f;
                repaint();
            });
            timer.start();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();

            GradientPaint gp = new GradientPaint(
                    0, 0, new Color(21, 48, 83),
                    getWidth(), getHeight(), new Color(34, 70, 111)
            );
            g2.setPaint(gp);
            g2.fillRect(0, 0, getWidth(), getHeight());

            for (int i = 0; i < 20; i++) {
                int x = (int) ((i * 60 + 30 * Math.sin(phase + i)) % (getWidth() + 100));
                int y = (int) ((i * 35 + 25 * Math.cos(phase + i)) % (getHeight() + 100));
                g2.setColor(new Color(220, 230, 255, 40));
                g2.fillOval(x, y, 8, 8);
            }

            g2.dispose();
        }
    }
}