package gui;

import dao.QuestionDAO;
import dao.QuizDAO;
import model.MCQQuestion;
import model.Quiz;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class QuizCreationPanel extends JPanel {

    private JTextField titleField, timeLimitField;
    private JPanel questionsListPanel;
    private Runnable backAction;
    private List<QuestionBlock> questionBlocks;
    private Integer editingQuizId = null;
    private String editingQuizUuid = null;

    private final Color BG = new Color(244, 247, 251);
    private final Color CARD = Color.WHITE;
    private final Color DARK = new Color(45, 55, 72);
    private final Color PURPLE = new Color(136, 117, 255);
    private final Color MUTED = new Color(113, 128, 150);

    public QuizCreationPanel(Runnable backAction) {
        this.backAction = backAction;
        this.questionBlocks = new ArrayList<>();
        setLayout(new BorderLayout());
        setBackground(BG);

        // Header
        JPanel hdr = new JPanel(new BorderLayout()); hdr.setOpaque(false); hdr.setBorder(new EmptyBorder(0, 0, 15, 0));
        JButton backBtn = makeBtn("← Back to Dashboard", Color.WHITE, DARK);
        backBtn.addActionListener(e -> backAction.run());
        hdr.add(backBtn, BorderLayout.WEST);
        JButton saveBtn = makeBtn("Save Quiz", PURPLE, Color.BLACK);
        saveBtn.addActionListener(e -> saveQuiz());
        hdr.add(saveBtn, BorderLayout.EAST);
        add(hdr, BorderLayout.NORTH);

        // Content
        JPanel main = new JPanel(); main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS)); main.setOpaque(false);
        main.add(buildSettingsCard()); main.add(Box.createVerticalStrut(15));

        questionsListPanel = new JPanel(); questionsListPanel.setLayout(new BoxLayout(questionsListPanel, BoxLayout.Y_AXIS)); questionsListPanel.setOpaque(false);
        main.add(questionsListPanel);
        addQuestionBlock();

        JPanel addWrap = new JPanel(new FlowLayout(FlowLayout.CENTER)); addWrap.setOpaque(false); addWrap.setBorder(new EmptyBorder(15, 0, 30, 0));
        JButton addBtn = makeBtn("+ Add Question", Color.WHITE, PURPLE);
        addBtn.addActionListener(e -> addQuestionBlock());
        addWrap.add(addBtn);
        main.add(addWrap);

        JScrollPane sp = new JScrollPane(main); sp.setBorder(null); sp.getViewport().setBackground(BG); sp.getVerticalScrollBar().setUnitIncrement(16);
        add(sp, BorderLayout.CENTER);
    }

    private JPanel buildSettingsCard() {
        JPanel card = rCard(); card.setLayout(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints(); g.fill = GridBagConstraints.HORIZONTAL; g.insets = new Insets(8, 8, 8, 8); g.weightx = 1.0;
        JLabel tl = new JLabel("Quiz Title"); tl.setFont(new Font("SansSerif", Font.BOLD, 18)); tl.setForeground(DARK);
        titleField = new JTextField(); titleField.setFont(new Font("SansSerif", Font.PLAIN, 16)); titleField.setPreferredSize(new Dimension(0, 38));
        JLabel tml = new JLabel("Time Limit (seconds)"); tml.setFont(new Font("SansSerif", Font.BOLD, 14)); tml.setForeground(DARK);
        timeLimitField = new JTextField("60"); timeLimitField.setFont(new Font("SansSerif", Font.PLAIN, 16)); timeLimitField.setPreferredSize(new Dimension(0, 38));
        g.gridx=0; g.gridy=0; card.add(tl, g); g.gridy=1; card.add(titleField, g); g.gridy=2; card.add(tml, g); g.gridy=3; card.add(timeLimitField, g);
        return card;
    }

    private void addQuestionBlock() {
        QuestionBlock b = new QuestionBlock(questionBlocks.size() + 1);
        questionBlocks.add(b); questionsListPanel.add(b); questionsListPanel.add(Box.createVerticalStrut(12));
        questionsListPanel.revalidate(); questionsListPanel.repaint();
    }

    private JButton makeBtn(String text, Color bg, Color fg) {
        JButton b = new JButton(text); b.setBackground(bg); b.setForeground(fg); b.setFocusPainted(false);
        b.setFont(new Font("SansSerif", Font.BOLD, 13)); b.setBorder(new EmptyBorder(10, 22, 10, 22));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR)); b.setOpaque(true); b.setContentAreaFilled(false); b.setBorderPainted(false);
        b.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        return b;
    }

    private JPanel rCard() {
        JPanel p = new JPanel() {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create(); g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(CARD); g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14); g2.dispose();
            }
        };
        p.setOpaque(false); p.setBorder(new EmptyBorder(20, 20, 20, 20)); return p;
    }

    private void saveQuiz() {
        String title = titleField.getText().trim();
        if (title.isEmpty()) { JOptionPane.showMessageDialog(this, "Quiz Title cannot be empty."); return; }
        int timeLimit;
        try { timeLimit = Integer.parseInt(timeLimitField.getText().trim()); } catch (NumberFormatException e) { JOptionPane.showMessageDialog(this, "Invalid time limit."); return; }

        List<MCQQuestion> parsed = new ArrayList<>();
        for (QuestionBlock b : questionBlocks) {
            MCQQuestion q = b.getQuestionData();
            if (q == null) return;
            parsed.add(q);
        }
        if (parsed.isEmpty()) { JOptionPane.showMessageDialog(this, "Add at least one question."); return; }

        Quiz quiz = new Quiz(editingQuizUuid != null ? editingQuizUuid : UUID.randomUUID().toString(), title, null, timeLimit);
        quiz.setActive(true); quiz.setCreatorId(1);
        QuizDAO quizDAO = new QuizDAO(); QuestionDAO qDao = new QuestionDAO();

        if (editingQuizId != null) {
            quiz.setNumericQuizId(editingQuizId);
            if (quizDAO.updateQuiz(quiz)) {
                qDao.deleteQuestionsByQuizId(editingQuizId);
                for (MCQQuestion q : parsed) { q.setQuizId(editingQuizId); qDao.insertQuestion(q); }
                JOptionPane.showMessageDialog(this, "Quiz Updated!"); reset(); backAction.run();
            }
        } else {
            int id = quizDAO.insertQuiz(quiz);
            if (id > 0) {
                for (MCQQuestion q : parsed) { q.setQuizId(id); qDao.insertQuestion(q); }
                JOptionPane.showMessageDialog(this, "Quiz Created!"); reset(); backAction.run();
            }
        }
    }

    public void reset() {
        editingQuizId = null; editingQuizUuid = null;
        titleField.setText(""); timeLimitField.setText("60");
        questionBlocks.clear(); questionsListPanel.removeAll(); addQuestionBlock();
    }

    public void loadQuiz(model.Quiz quiz) {
        reset(); questionBlocks.clear(); questionsListPanel.removeAll();
        editingQuizId = quiz.getNumericQuizId(); editingQuizUuid = quiz.getQuizId();
        titleField.setText(quiz.getTitle()); timeLimitField.setText(String.valueOf(quiz.getTimeLimit()));
        QuestionDAO qDao = new QuestionDAO();
        List<model.Question<?>> dbQ = qDao.getQuestionsByQuizId(editingQuizId);
        if (dbQ.isEmpty()) { addQuestionBlock(); } else {
            for (model.Question<?> q : dbQ) {
                QuestionBlock b = new QuestionBlock(questionBlocks.size() + 1);
                b.questionField.setText(q.getQuestionText());
                String type = q.getQuestionType();
                if (type != null) {
                    b.typeCombo.setSelectedItem(type);
                    b.switchType(type);
                }
                if (q instanceof model.MCQQuestion) {
                    model.MCQQuestion mcq = (model.MCQQuestion) q;
                    Map<String, String> opts = mcq.getOptions();
                    if (opts != null) {
                        b.optA.setText(opts.getOrDefault("A", "")); b.optB.setText(opts.getOrDefault("B", ""));
                        b.optC.setText(opts.getOrDefault("C", "")); b.optD.setText(opts.getOrDefault("D", ""));
                        String correct = q.getCorrectAnswer() != null ? q.getCorrectAnswer().toString() : "";
                        if (correct != null) {
                            if (correct.equalsIgnoreCase(b.optA.getText())) b.correctCombo.setSelectedItem("A");
                            else if (correct.equalsIgnoreCase(b.optB.getText())) b.correctCombo.setSelectedItem("B");
                            else if (correct.equalsIgnoreCase(b.optC.getText())) b.correctCombo.setSelectedItem("C");
                            else if (correct.equalsIgnoreCase(b.optD.getText())) b.correctCombo.setSelectedItem("D");
                        }
                    }
                } else if ("TRUE_FALSE".equals(type)) {
                    String ans = q.getCorrectAnswer() != null ? q.getCorrectAnswer().toString() : "True";
                    b.tfCombo.setSelectedItem(ans);
                } else if ("SHORT_ANSWER".equals(type)) {
                    b.shortAnsField.setText(q.getCorrectAnswer() != null ? q.getCorrectAnswer().toString() : "");
                }
                questionBlocks.add(b); questionsListPanel.add(b); questionsListPanel.add(Box.createVerticalStrut(12));
            }
        }
        questionsListPanel.revalidate(); questionsListPanel.repaint();
    }

    // === QUESTION BLOCK ===
    class QuestionBlock extends JPanel {
        JTextField questionField, optA, optB, optC, optD, shortAnsField;
        JComboBox<String> correctCombo, typeCombo, tfCombo;
        JPanel optionsPanel;
        CardLayout optionsLayout;
        int qNum;

        QuestionBlock(int qNum) {
            this.qNum = qNum; setOpaque(false); setLayout(new BorderLayout()); setBorder(new EmptyBorder(8, 8, 8, 8));
            JPanel inner = new JPanel(new GridBagLayout()) {
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create(); g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(CARD); g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                    g2.setColor(PURPLE); g2.fillRoundRect(0, 0, 6, getHeight(), 6, 6); g2.fillRect(3, 0, 3, getHeight()); g2.dispose();
                }
            };
            inner.setOpaque(false); inner.setBorder(new EmptyBorder(16, 24, 16, 16));
            GridBagConstraints gc = new GridBagConstraints(); gc.fill = GridBagConstraints.HORIZONTAL; gc.insets = new Insets(4, 4, 4, 4); gc.weightx = 1.0;

            // Header row: Question # + Type selector
            JPanel headerRow = new JPanel(new BorderLayout()); headerRow.setOpaque(false);
            JLabel qLbl = new JLabel("Question " + qNum); qLbl.setFont(new Font("SansSerif", Font.BOLD, 15));
            typeCombo = new JComboBox<>(new String[]{"MCQ", "TRUE_FALSE", "SHORT_ANSWER"});
            typeCombo.setFont(new Font("SansSerif", Font.PLAIN, 12));
            typeCombo.addActionListener(e -> switchType((String) typeCombo.getSelectedItem()));
            headerRow.add(qLbl, BorderLayout.WEST);
            headerRow.add(typeCombo, BorderLayout.EAST);
            gc.gridx = 0; gc.gridy = 0; gc.gridwidth = 2; inner.add(headerRow, gc);

            // Question text
            questionField = new JTextField(); questionField.setPreferredSize(new Dimension(0, 34));
            gc.gridy = 1; inner.add(questionField, gc);

            // Options area (CardLayout for different types)
            optionsLayout = new CardLayout();
            optionsPanel = new JPanel(optionsLayout); optionsPanel.setOpaque(false);

            // MCQ panel
            JPanel mcqPanel = new JPanel(new GridBagLayout()); mcqPanel.setOpaque(false);
            GridBagConstraints mg = new GridBagConstraints(); mg.fill = GridBagConstraints.HORIZONTAL; mg.insets = new Insets(3, 3, 3, 3); mg.weightx = 1.0;
            optA = tf(); optB = tf(); optC = tf(); optD = tf();
            correctCombo = new JComboBox<>(new String[]{"A", "B", "C", "D"});
            mg.gridx=0; mg.gridy=0; mg.weightx=0.05; mcqPanel.add(new JLabel("A:"), mg); mg.gridx=1; mg.weightx=0.95; mcqPanel.add(optA, mg);
            mg.gridx=0; mg.gridy=1; mg.weightx=0.05; mcqPanel.add(new JLabel("B:"), mg); mg.gridx=1; mg.weightx=0.95; mcqPanel.add(optB, mg);
            mg.gridx=0; mg.gridy=2; mg.weightx=0.05; mcqPanel.add(new JLabel("C:"), mg); mg.gridx=1; mg.weightx=0.95; mcqPanel.add(optC, mg);
            mg.gridx=0; mg.gridy=3; mg.weightx=0.05; mcqPanel.add(new JLabel("D:"), mg); mg.gridx=1; mg.weightx=0.95; mcqPanel.add(optD, mg);
            JPanel mcqBot = new JPanel(new FlowLayout(FlowLayout.RIGHT)); mcqBot.setOpaque(false);
            mcqBot.add(new JLabel("Correct:")); mcqBot.add(correctCombo);
            mg.gridx=0; mg.gridy=4; mg.gridwidth=2; mcqPanel.add(mcqBot, mg);
            optionsPanel.add(mcqPanel, "MCQ");

            // True/False panel
            JPanel tfPanel = new JPanel(new FlowLayout(FlowLayout.LEFT)); tfPanel.setOpaque(false);
            tfCombo = new JComboBox<>(new String[]{"True", "False"});
            tfPanel.add(new JLabel("Correct Answer: ")); tfPanel.add(tfCombo);
            optionsPanel.add(tfPanel, "TRUE_FALSE");

            // Short Answer panel
            JPanel saPanel = new JPanel(new BorderLayout()); saPanel.setOpaque(false);
            saPanel.setBorder(new EmptyBorder(5, 0, 5, 0));
            shortAnsField = new JTextField(); shortAnsField.setPreferredSize(new Dimension(0, 34));
            JLabel saLbl = new JLabel("Expected Answer (keyword):"); saLbl.setFont(new Font("SansSerif", Font.PLAIN, 12));
            saPanel.add(saLbl, BorderLayout.NORTH); saPanel.add(shortAnsField, BorderLayout.CENTER);
            optionsPanel.add(saPanel, "SHORT_ANSWER");

            gc.gridy = 2; inner.add(optionsPanel, gc);

            // Bottom: Remove button
            JPanel bot = new JPanel(new FlowLayout(FlowLayout.RIGHT)); bot.setOpaque(false);
            JButton rm = new JButton("Remove"); rm.setForeground(Color.RED); rm.setFocusPainted(false);
            rm.setContentAreaFilled(false); rm.setBorderPainted(false); rm.setCursor(new Cursor(Cursor.HAND_CURSOR));
            rm.addActionListener(e -> removeBlock(this));
            bot.add(rm);
            gc.gridy = 3; inner.add(bot, gc);
            add(inner, BorderLayout.CENTER);
        }

        void switchType(String type) { optionsLayout.show(optionsPanel, type); }
        JTextField tf() { JTextField f = new JTextField(); f.setPreferredSize(new Dimension(0, 28)); return f; }

        MCQQuestion getQuestionData() {
            String txt = questionField.getText().trim();
            if (txt.isEmpty()) { JOptionPane.showMessageDialog(this, "Question " + qNum + " text is empty."); return null; }
            String type = (String) typeCombo.getSelectedItem();

            if ("MCQ".equals(type)) {
                Map<String, String> opts = new HashMap<>();
                opts.put("A", optA.getText().trim()); opts.put("B", optB.getText().trim());
                opts.put("C", optC.getText().trim()); opts.put("D", optD.getText().trim());
                for (String v : opts.values()) if (v.isEmpty()) { JOptionPane.showMessageDialog(this, "Fill all options for Q" + qNum); return null; }
                String correct = opts.get((String) correctCombo.getSelectedItem());
                MCQQuestion q = new MCQQuestion(0, txt, opts, correct);
                q.setQuestionType("MCQ"); q.setMarks(1); q.setDifficultyLevel("Medium"); return q;
            } else if ("TRUE_FALSE".equals(type)) {
                String ans = (String) tfCombo.getSelectedItem();
                Map<String, String> opts = new HashMap<>(); opts.put("A", "True"); opts.put("B", "False"); opts.put("C", ""); opts.put("D", "");
                MCQQuestion q = new MCQQuestion(0, txt, opts, ans);
                q.setQuestionType("TRUE_FALSE"); q.setMarks(1); q.setDifficultyLevel("Easy"); return q;
            } else { // SHORT_ANSWER
                String ans = shortAnsField.getText().trim();
                if (ans.isEmpty()) { JOptionPane.showMessageDialog(this, "Enter expected answer for Q" + qNum); return null; }
                Map<String, String> opts = new HashMap<>(); opts.put("A", ""); opts.put("B", ""); opts.put("C", ""); opts.put("D", "");
                MCQQuestion q = new MCQQuestion(0, txt, opts, ans);
                q.setQuestionType("SHORT_ANSWER"); q.setMarks(1); q.setDifficultyLevel("Medium"); return q;
            }
        }
    }

    private void removeBlock(QuestionBlock block) {
        questionBlocks.remove(block); questionsListPanel.removeAll();
        for (QuestionBlock qb : questionBlocks) { questionsListPanel.add(qb); questionsListPanel.add(Box.createVerticalStrut(12)); }
        questionsListPanel.revalidate(); questionsListPanel.repaint();
    }
}
