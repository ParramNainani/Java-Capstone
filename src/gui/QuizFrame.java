package gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import model.Quiz;
import model.User;
import model.Result;
import model.Question;
import dao.QuestionDAO;
import dao.ResponseDAO;
import dao.ResultDAO;

public class QuizFrame extends JFrame {

    private final User user;
    private final Quiz quiz;
    private final List<QuestionWrapper> questions = new ArrayList<>();
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
    private final JTextField shortAnswerField = createShortAnswerField();
    private final JLabel timerLabel = new JLabel("Time Left: 60");
    private int timeLeft = 60;
    private Timer quizTimer;

    // Container panel that holds the options area (center section below question)
    private JPanel optionsContainer;

    public QuizFrame(User user, Quiz quiz) {
        this.user = user;
        this.quiz = quiz;
        this.timeLeft = quiz.getTimeLimit();

        setTitle("EXAMIFY - Quiz");
        setSize(1200, 720);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        quizTitle.setText(quiz.getTitle());
        loadQuizQuestions();

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

        // Build the options container — this will be rebuilt dynamically per question
        optionsContainer = new JPanel();
        optionsContainer.setOpaque(false);
        optionsContainer.setLayout(new BoxLayout(optionsContainer, BoxLayout.Y_AXIS));

        center.add(questionCountLabel);
        center.add(Box.createVerticalStrut(20));
        center.add(questionArea);
        center.add(Box.createVerticalStrut(20));
        center.add(optionsContainer);

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

    private void loadQuizQuestions() {
        QuestionDAO qDao = new QuestionDAO();
        List<Question<?>> dbQuestions = qDao.getQuestionsByQuizId(quiz.getNumericQuizId());
        
        for (Question<?> q : dbQuestions) {
            String qType = q.getQuestionType() != null ? q.getQuestionType() : "MCQ";
            String[] optionsArray;
            int correctIndex = 0;
            String correctText = q.getCorrectAnswer() != null ? q.getCorrectAnswer().toString() : "";

            if ("TRUE_FALSE".equals(qType)) {
                optionsArray = new String[]{"True", "False"};
                correctIndex = "False".equalsIgnoreCase(correctText) ? 1 : 0;
            } else if ("SHORT_ANSWER".equals(qType)) {
                optionsArray = new String[0]; // no options for short answer
                correctIndex = -99; // special marker
            } else { // MCQ
                optionsArray = new String[]{"Option A", "Option B", "Option C", "Option D"};
                if (q instanceof model.MCQQuestion) {
                    model.MCQQuestion mcq = (model.MCQQuestion) q;
                    java.util.Map<String, String> opts = mcq.getOptions();
                    if (opts != null && !opts.isEmpty()) {
                        optionsArray[0] = opts.getOrDefault("A", "Option A");
                        optionsArray[1] = opts.getOrDefault("B", "Option B");
                        optionsArray[2] = opts.getOrDefault("C", "Option C");
                        optionsArray[3] = opts.getOrDefault("D", "Option D");
                        for (int i = 0; i < 4; i++) {
                            if (optionsArray[i].equalsIgnoreCase(correctText)) { correctIndex = i; break; }
                        }
                    }
                }
            }
            QuestionWrapper w = new QuestionWrapper(q.getQuestionText(), optionsArray, correctIndex);
            w.questionId = q.getQuestionId();
            w.questionType = qType;
            w.correctAnswerText = correctText;
            questions.add(w);
        }
    }

    private void loadQuestion() {
        QuestionWrapper q = questions.get(currentIndex);
        questionCountLabel.setText("Question " + (currentIndex + 1) + " of " + questions.size());
        questionArea.setText(q.question);

        // Clear and rebuild the options container based on question type
        optionsContainer.removeAll();

        if ("SHORT_ANSWER".equals(q.questionType)) {
            // Show a text field for short answer
            shortAnswerField.setText(q.userTextAnswer != null ? q.userTextAnswer : "");
            optionsContainer.add(shortAnswerField);

            // Hide radio buttons from any stale selection
            optionsGroup.clearSelection();
        } else if ("TRUE_FALSE".equals(q.questionType)) {
            // Show only 2 radio buttons: True and False
            option1.setText(q.options[0]); // "True"
            option2.setText(q.options[1]); // "False"

            optionsContainer.add(option1);
            optionsContainer.add(Box.createVerticalStrut(10));
            optionsContainer.add(option2);

            optionsGroup.clearSelection();
            if (q.userAnswer == 0) option1.setSelected(true);
            else if (q.userAnswer == 1) option2.setSelected(true);
        } else {
            // MCQ — show all 4 radio buttons
            option1.setText(q.options[0]);
            option2.setText(q.options[1]);
            option3.setText(q.options[2]);
            option4.setText(q.options[3]);

            optionsContainer.add(option1);
            optionsContainer.add(Box.createVerticalStrut(10));
            optionsContainer.add(option2);
            optionsContainer.add(Box.createVerticalStrut(10));
            optionsContainer.add(option3);
            optionsContainer.add(Box.createVerticalStrut(10));
            optionsContainer.add(option4);

            optionsGroup.clearSelection();
            if (q.userAnswer == 0) option1.setSelected(true);
            else if (q.userAnswer == 1) option2.setSelected(true);
            else if (q.userAnswer == 2) option3.setSelected(true);
            else if (q.userAnswer == 3) option4.setSelected(true);
        }

        optionsContainer.revalidate();
        optionsContainer.repaint();
    }

    private void saveCurrentSelection() {
        QuestionWrapper q = questions.get(currentIndex);

        if ("SHORT_ANSWER".equals(q.questionType)) {
            q.userTextAnswer = shortAnswerField.getText();
        } else {
            if (option1.isSelected()) q.userAnswer = 0;
            else if (option2.isSelected()) q.userAnswer = 1;
            else if (option3.isSelected()) q.userAnswer = 2;
            else if (option4.isSelected()) q.userAnswer = 3;
            else q.userAnswer = -1;
        }
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
        for (QuestionWrapper q : questions) {
            if ("SHORT_ANSWER".equals(q.questionType)) {
                if (q.userTextAnswer != null && q.correctAnswerText != null &&
                    q.userTextAnswer.trim().toLowerCase().contains(q.correctAnswerText.trim().toLowerCase())) {
                    score++;
                }
            } else {
                if (q.userAnswer == q.correctIndex) {
                    score++;
                }
            }
        }

        // Save result to database
        Result result = new Result();
        result.setUserId(user.getUserId());
        result.setNumericQuizId(quiz.getNumericQuizId());
        result.setScore(score);
        result.setTotalMarks(questions.size());
        
        ResultDAO resultDAO = new ResultDAO();
        resultDAO.insertResult(result);

        // Save individual responses for each question
        ResponseDAO responseDAO = new ResponseDAO();
        for (QuestionWrapper q : questions) {
            model.Response resp = new model.Response();
            resp.setResultId(result.getResultId());
            resp.setQuestionId(q.questionId);

            if ("SHORT_ANSWER".equals(q.questionType)) {
                resp.setSelectedOptionId(null);
                resp.setShortAnswerText(q.userTextAnswer != null ? q.userTextAnswer : "");
                boolean correct = q.userTextAnswer != null && q.correctAnswerText != null &&
                    q.userTextAnswer.trim().toLowerCase().contains(q.correctAnswerText.trim().toLowerCase());
                resp.setCorrect(correct);
            } else {
                // For MCQ and TRUE_FALSE, store the selected option index + chosen text
                resp.setSelectedOptionId(q.userAnswer >= 0 ? q.userAnswer : null);
                String chosenText = (q.userAnswer >= 0 && q.userAnswer < q.options.length)
                    ? q.options[q.userAnswer] : "";
                resp.setShortAnswerText(null);
                resp.setChosenAnswer(chosenText);
                resp.setCorrect(q.userAnswer == q.correctIndex);
            }

            responseDAO.insertResponse(resp);
        }

        dispose();
        JOptionPane.showMessageDialog(null, "Quiz submitted! Score: " + score + " / " + questions.size());
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

    private JTextField createShortAnswerField() {
        JTextField field = new JTextField();
        field.setFont(new Font("SansSerif", Font.PLAIN, 18));
        field.setForeground(Color.WHITE);
        field.setBackground(new Color(255, 255, 255, 30));
        field.setCaretColor(Color.WHITE);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(126, 220, 223), 1),
            new EmptyBorder(12, 14, 12, 14)
        ));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        return field;
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

    static class QuestionWrapper {
        String question;
        String[] options;
        int correctIndex;
        int userAnswer = -1;
        int questionId;
        String questionType = "MCQ";
        String correctAnswerText = "";
        String userTextAnswer = "";

        QuestionWrapper(String question, String[] options, int correctIndex) {
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