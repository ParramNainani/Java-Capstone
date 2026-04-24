package gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

public class ResultFrame extends JFrame {

    private final model.User user;
    private final int latestScore;
    private final int latestTotal;

    private int totalQuizzesAvailable;
    private int completedQuizzesCount;
    private int scoresOver90Count;
    private java.util.List<model.ResultDetail> quizHistory;

    // ── Brand colours (mirrors LoginFrame exactly) ──────────────────────────────
    private static final Color CYAN      = new Color(38,  208, 206);
    private static final Color PURPLE    = new Color(138,  43, 226);
    private static final Color BLUE      = new Color(30,  144, 255);
    private static final Color BG_DARK   = new Color(10,   15,  30);
    private static final Color TEXT_DIM  = new Color(200, 210, 230);
    private static final Color GREEN     = new Color(116, 224, 187);
    private static final Color YELLOW    = new Color(255, 200,  80);
    private static final Color RED       = new Color(255,  90, 120);

    public ResultFrame(model.User user, int score, int total) {
        this.user = user;
        this.latestScore = score;
        this.latestTotal = total;

        setTitle("EXAMIFY – Performance Results");
        setSize(1350, 800);
        setMinimumSize(new Dimension(1200, 750));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        dao.ResultDAO rDao = new dao.ResultDAO();
        dao.QuizDAO qDao = new dao.QuizDAO();
        this.totalQuizzesAvailable = qDao.getTotalQuizzesCount();
        this.completedQuizzesCount = rDao.getStudentCompletedQuizzesCount(user.getUserId());
        this.scoresOver90Count = rDao.getStudentScoresOver90Count(user.getUserId());
        this.quizHistory = rDao.getStudentResults(user.getUserId());

        StudentDashboard.BlobBackground root = new StudentDashboard.BlobBackground();
        root.setLayout(new BorderLayout());

        root.add(buildSidebar(), BorderLayout.WEST);
        root.add(buildMainArea(), BorderLayout.CENTER);

        setContentPane(root);
    }

    // ── Sidebar (Reused from Dashboard for consistency) ────────────────────────
    private JPanel buildSidebar() {
        JPanel side = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(new Color(10, 15, 30, 200));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(new Color(255, 255, 255, 40));
                g2.drawLine(getWidth()-1, 0, getWidth()-1, getHeight());
                g2.dispose();
            }
        };
        side.setOpaque(false);
        side.setPreferredSize(new Dimension(240, 0));
        side.setLayout(new BoxLayout(side, BoxLayout.Y_AXIS));
        side.setBorder(new EmptyBorder(40, 24, 40, 24));

        JLabel brand = new JLabel("EXAMIFY");
        brand.setFont(new Font("SansSerif", Font.BOLD, 28));
        brand.setForeground(Color.WHITE);
        brand.setAlignmentX(Component.LEFT_ALIGNMENT);
        side.add(brand);

        JLabel role = new JLabel("Performance Portal");
        role.setFont(new Font("SansSerif", Font.PLAIN, 13));
        role.setForeground(CYAN);
        role.setAlignmentX(Component.LEFT_ALIGNMENT);
        side.add(role);

        side.add(Box.createVerticalStrut(50));

        side.add(buildNavItem("🏠", "Dashboard",  false));
        side.add(Box.createVerticalStrut(10));
        side.add(buildNavItem("📚", "Quizzes", false));
        side.add(Box.createVerticalStrut(10));
        side.add(buildNavItem("📈", "Performance",  true));

        side.add(Box.createVerticalGlue());

        JButton logout = new JButton("⬅  Logout");
        logout.setFocusPainted(false);
        logout.setBorderPainted(false);
        logout.setContentAreaFilled(false);
        logout.setForeground(RED);
        logout.setFont(new Font("SansSerif", Font.BOLD, 15));
        logout.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logout.setAlignmentX(Component.LEFT_ALIGNMENT);
        logout.addActionListener(e -> { dispose(); new LoginFrame().setVisible(true); });
        side.add(logout);

        return side;
    }

    private JPanel buildNavItem(String icon, String label, boolean active) {
        JPanel item = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 10)) {
            @Override protected void paintComponent(Graphics g) {
                if (active) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    GradientPaint gp = new GradientPaint(0,0, new Color(38,208,206,60),
                                                          getWidth(),0, new Color(138,43,226,40));
                    g2.setPaint(gp);
                    g2.fillRoundRect(0,0,getWidth(),getHeight(),14,14);
                    g2.setColor(CYAN);
                    g2.fillRoundRect(0, getHeight()/2-12, 4, 24, 4, 4);
                    g2.dispose();
                }
                super.paintComponent(g);
            }
        };
        item.setOpaque(false);
        item.setAlignmentX(Component.LEFT_ALIGNMENT);
        item.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        item.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel lbl = new JLabel(icon + "  " + label);
        lbl.setForeground(active ? Color.WHITE : TEXT_DIM);
        lbl.setFont(new Font("SansSerif", active ? Font.BOLD : Font.PLAIN, 16));
        item.add(lbl);

        item.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { if(!active) lbl.setForeground(Color.WHITE); }
            @Override public void mouseExited(MouseEvent e)  { if(!active) lbl.setForeground(TEXT_DIM); }
            @Override public void mouseClicked(MouseEvent e) {
                if (active) return;
                if (label.equals("Dashboard")) { dispose(); StudentDashboard sd = new StudentDashboard(user); sd.setVisible(true); }
                else if (label.equals("Quizzes")) { dispose(); StudentDashboard sd = new StudentDashboard(user); sd.setVisible(true); sd.openTab("Quizzes"); }
            }
        });

        return item;
    }

    // ── Main Area ──────────────────────────────────────────────────────────────
    private JPanel buildMainArea() {
        JPanel main = new JPanel(new BorderLayout());
        main.setOpaque(false);
        
        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(20, 30, 10, 30));
        
        JLabel title = new JLabel("Performance Overview");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("SansSerif", Font.BOLD, 32));
        header.add(title, BorderLayout.WEST);

        // Top right profile mock
        JPanel topNav = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        topNav.setOpaque(false);
        header.add(topNav, BorderLayout.EAST);
        
        main.add(header, BorderLayout.NORTH);

        // Center Content Scroll
        JPanel contentArea = new JPanel(new GridBagLayout());
        contentArea.setOpaque(false);
        contentArea.setBorder(new EmptyBorder(0, 30, 20, 30));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;

        // Top Stats Row
        JPanel statsRow = new JPanel(new GridLayout(1, 4, 15, 0));
        statsRow.setOpaque(false);
        statsRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        statsRow.setPreferredSize(new Dimension(1000, 80));
        
        int inProgress = Math.max(0, totalQuizzesAvailable - completedQuizzesCount);
        int completionRate = totalQuizzesAvailable == 0 ? 0 : (int)((completedQuizzesCount * 100.0) / totalQuizzesAvailable);
        int over90Rate = completedQuizzesCount == 0 ? 0 : (int)((scoresOver90Count * 100.0) / completedQuizzesCount);
        
        statsRow.add(buildTopStatCard("Pending quizzes", String.valueOf(inProgress), "", YELLOW));
        statsRow.add(buildTopStatCard("Completed quizzes", String.valueOf(completedQuizzesCount), "", CYAN));
        statsRow.add(buildTopStatCard("Completed quiz rate", completionRate + "%", "", GREEN));
        statsRow.add(buildTopStatCard("Scores over 90%", over90Rate + "%", "", GREEN));

        // Create a wrapper for statsRow to keep it at top
        JPanel topWrapper = new JPanel(new BorderLayout());
        topWrapper.setOpaque(false);
        topWrapper.add(statsRow, BorderLayout.NORTH);
        topWrapper.setBorder(new EmptyBorder(0, 0, 12, 0));

        // Main Column
        JPanel mainCol = new JPanel();
        mainCol.setOpaque(false);
        mainCol.setLayout(new BoxLayout(mainCol, BoxLayout.Y_AXIS));

        // 1. Trophy Row (Full Width)
        JLabel tProgress = new JLabel("Track your progress");
        tProgress.setForeground(Color.WHITE);
        tProgress.setFont(new Font("SansSerif", Font.BOLD, 16));
        tProgress.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainCol.add(tProgress);
        mainCol.add(Box.createVerticalStrut(8));
        mainCol.add(buildTrackProgressCard());
        mainCol.add(Box.createVerticalStrut(10));

        // 2. Split Row
        JPanel splitRow = new JPanel(new GridBagLayout());
        splitRow.setOpaque(false);
        GridBagConstraints sbc = new GridBagConstraints();
        sbc.fill = GridBagConstraints.BOTH;
        sbc.weighty = 1.0;

        // Left Part: Donut Chart
        JPanel leftPart = new JPanel();
        leftPart.setOpaque(false);
        leftPart.setLayout(new BoxLayout(leftPart, BoxLayout.Y_AXIS));
        
        JLabel tOverall = new JLabel("Overall quiz performance");
        tOverall.setForeground(Color.WHITE);
        tOverall.setFont(new Font("SansSerif", Font.BOLD, 16));
        tOverall.setAlignmentX(Component.LEFT_ALIGNMENT);
        leftPart.add(tOverall);
        leftPart.add(Box.createVerticalStrut(8));
        leftPart.add(buildOverallPerformanceCard());

        // Right Part: History & Keep Practising
        JPanel rightPart = new JPanel();
        rightPart.setOpaque(false);
        rightPart.setLayout(new BoxLayout(rightPart, BoxLayout.Y_AXIS));
        
        JLabel tHistory = new JLabel("Quiz history");
        tHistory.setForeground(Color.WHITE);
        tHistory.setFont(new Font("SansSerif", Font.BOLD, 16));
        tHistory.setAlignmentX(Component.LEFT_ALIGNMENT);
        rightPart.add(tHistory);
        rightPart.add(Box.createVerticalStrut(8));
        rightPart.add(buildQuizHistoryCard());
        rightPart.add(Box.createVerticalStrut(12));
        rightPart.add(buildKeepPractisingCard());

        sbc.weightx = 0.35;
        sbc.insets = new Insets(0, 0, 0, 12);
        splitRow.add(leftPart, sbc);

        sbc.gridx = 1;
        sbc.weightx = 0.65;
        sbc.insets = new Insets(0, 0, 0, 0);
        splitRow.add(rightPart, sbc);

        mainCol.add(splitRow);

        JPanel contentScrollWrapper = new JPanel(new BorderLayout());
        contentScrollWrapper.setOpaque(false);
        contentScrollWrapper.add(topWrapper, BorderLayout.NORTH);
        
        JPanel centerWrap = new JPanel(new BorderLayout());
        centerWrap.setOpaque(false);
        centerWrap.add(mainCol, BorderLayout.NORTH); 
        
        contentScrollWrapper.add(centerWrap, BorderLayout.CENTER);

        JScrollPane scroll = new JScrollPane(contentScrollWrapper);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        main.add(scroll, BorderLayout.CENTER);
        return main;
    }

    // ── Individual Components ──────────────────────────────────────────────────

    private JPanel buildTopStatCard(String title, String value, String change, Color tagColor) {
        StudentDashboard.GlassCard card = new StudentDashboard.GlassCard(16);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(8, 12, 8, 12));
        
        JLabel tLbl = new JLabel(title);
        tLbl.setForeground(TEXT_DIM);
        tLbl.setFont(new Font("SansSerif", Font.PLAIN, 12));
        tLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JPanel valRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        valRow.setOpaque(false);
        valRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel vLbl = new JLabel(value);
        vLbl.setForeground(Color.WHITE);
        vLbl.setFont(new Font("SansSerif", Font.BOLD, 20));
        
        JPanel tag = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(tagColor.getRed(), tagColor.getGreen(), tagColor.getBlue(), 40));
                g2.fillRoundRect(0,0,getWidth(),getHeight(), 10, 10);
                g2.dispose();
            }
        };
        tag.setOpaque(false);
        tag.setLayout(new FlowLayout(FlowLayout.CENTER, 6, 2));
        JLabel cLbl = new JLabel(change);
        cLbl.setForeground(tagColor);
        cLbl.setFont(new Font("SansSerif", Font.BOLD, 10));
        tag.add(cLbl);
        
        valRow.add(vLbl);
        valRow.add(tag);
        
        JLabel l7 = new JLabel("Last 7 days");
        l7.setForeground(new Color(150, 160, 180));
        l7.setFont(new Font("SansSerif", Font.PLAIN, 11));
        l7.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(tLbl);
        card.add(Box.createVerticalStrut(8));
        card.add(valRow);
        card.add(Box.createVerticalStrut(5));
        card.add(l7);
        
        return card;
    }

    private JPanel buildTrackProgressCard() {
        JPanel card = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(30, 40, 70, 200));
                g2.fillRoundRect(0,0,getWidth(),getHeight(), 20, 20);
                
                // Draw decorative waves
                g2.setColor(new Color(255,255,255, 30));
                g2.setStroke(new BasicStroke(2f));
                g2.drawArc(-50, getHeight()-60, 200, 100, 0, 180);
                g2.drawArc(150, getHeight()-80, 250, 120, 0, 180);
                g2.drawArc(350, getHeight()-50, 200, 100, 0, 180);
                
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(10, 20, 10, 20));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        
        JPanel leftBlock = new JPanel();
        leftBlock.setOpaque(false);
        leftBlock.setLayout(new BoxLayout(leftBlock, BoxLayout.Y_AXIS));
        
        JPanel valRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        valRow.setOpaque(false);
        valRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel valLbl = new JLabel(String.valueOf(completedQuizzesCount));
        valLbl.setForeground(Color.WHITE);
        valLbl.setFont(new Font("SansSerif", Font.BOLD, 28));
        
        JPanel tag = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(138,43,226, 120));
                g2.fillRoundRect(0,0,getWidth(),getHeight(), 12, 12);
                g2.dispose();
            }
        };
        tag.setOpaque(false);
        tag.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 4));
        JLabel tLbl = new JLabel("Quizzes completed");
        tLbl.setForeground(Color.WHITE);
        tLbl.setFont(new Font("SansSerif", Font.BOLD, 12));
        tag.add(tLbl);
        
        valRow.add(valLbl);
        valRow.add(tag);
        
        JLabel sub1 = new JLabel("Exceptional milestone!");
        sub1.setForeground(Color.WHITE);
        sub1.setFont(new Font("SansSerif", Font.PLAIN, 15));
        sub1.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel sub2 = new JLabel("Congratulations on achieving this milestone");
        sub2.setForeground(TEXT_DIM);
        sub2.setFont(new Font("SansSerif", Font.PLAIN, 12));
        sub2.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        leftBlock.add(valRow);
        leftBlock.add(Box.createVerticalStrut(4));
        leftBlock.add(sub1);
        leftBlock.add(Box.createVerticalStrut(2));
        leftBlock.add(sub2);
        
        JLabel trophy = new JLabel("🏆");
        trophy.setFont(new Font("SansSerif", Font.PLAIN, 36));
        
        card.add(leftBlock, BorderLayout.WEST);
        card.add(trophy, BorderLayout.EAST);
        
        return card;
    }

    private JPanel buildQuizHistoryCard() {
        StudentDashboard.GlassCard card = new StudentDashboard.GlassCard(20);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(10, 15, 10, 15));
        
        // Header
        JPanel headerRow = new JPanel(new GridLayout(1, 4, 10, 0));
        headerRow.setOpaque(false);
        headerRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        headerRow.add(createColHeader("Subject"));
        headerRow.add(createColHeader("Score"));
        headerRow.add(createColHeader("Status"));
        headerRow.add(createColHeader("Actions"));
        
        card.add(headerRow);
        card.add(Box.createVerticalStrut(5));
        
        // Rows
        for (int i = 0; i < quizHistory.size() && i < 10; i++) {
            model.ResultDetail rd = quizHistory.get(i);
            card.add(buildHistoryRow(rd.getQuizTitle(), rd.getScore() + "/" + rd.getTotalMarks(), "Completed", GREEN));
        }
        
        if (quizHistory.isEmpty()) {
            card.add(buildHistoryRow("No quizzes taken yet.", "-", "-", TEXT_DIM));
        }
        
        // Latest Quiz Row if passed and not already at top
        if (latestTotal > 0 && quizHistory.isEmpty()) {
            String status = "Completed";
            card.add(buildHistoryRow("Latest Quiz", latestScore + "/" + latestTotal, status, CYAN));
        }

        return card;
    }

    private JLabel createColHeader(String text) {
        JLabel l = new JLabel(text);
        l.setForeground(TEXT_DIM);
        l.setFont(new Font("SansSerif", Font.BOLD, 12));
        return l;
    }

    private JPanel buildHistoryRow(String subject, String score, String status, Color statusColor) {
        JPanel row = new JPanel(new GridLayout(1, 4, 10, 0));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        row.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(255,255,255,20)));
        
        JLabel sub = new JLabel(subject);
        sub.setForeground(Color.WHITE);
        sub.setFont(new Font("SansSerif", Font.PLAIN, 14));
        
        JLabel sc = new JLabel(score);
        sc.setForeground(Color.WHITE);
        sc.setFont(new Font("SansSerif", Font.BOLD, 14));
        
        JLabel st = new JLabel(status);
        st.setForeground(statusColor);
        st.setFont(new Font("SansSerif", Font.PLAIN, 13));
        
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        actions.setOpaque(false);
        JLabel rIco = new JLabel("↻"); rIco.setForeground(TEXT_DIM); rIco.setCursor(new Cursor(Cursor.HAND_CURSOR));
        JLabel pIco = new JLabel("📄"); pIco.setForeground(YELLOW); pIco.setCursor(new Cursor(Cursor.HAND_CURSOR));
        actions.add(rIco);
        actions.add(pIco);
        
        row.add(sub);
        row.add(sc);
        row.add(st);
        row.add(actions);
        
        return row;
    }

    private JPanel buildOverallPerformanceCard() {
        StudentDashboard.GlassCard card = new StudentDashboard.GlassCard(20);
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        // Donut Chart Area
        JPanel chartPanel = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                int strokeWidth = 12;
                int size = Math.min(getWidth(), getHeight()) - strokeWidth * 2 - 10;
                int x = (getWidth() - size) / 2;
                int y = (getHeight() - size) / 2;
                
                // Base ring
                g2.setColor(new Color(255,255,255, 15));
                g2.setStroke(new BasicStroke(strokeWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawArc(x, y, size, size, 0, 360);
                
                int completed = completedQuizzesCount;
                int total = totalQuizzesAvailable;
                int over90 = scoresOver90Count;
                int inProgress = Math.max(0, total - completed);

                int angleCompleted = total == 0 ? 0 : (int)((completed * 360.0) / total);
                int angleOver90 = total == 0 ? 0 : (int)((over90 * 360.0) / total);
                int angleInProgress = total == 0 ? 0 : (int)((inProgress * 360.0) / total);
                
                // Completed (Purple)
                g2.setColor(PURPLE);
                g2.drawArc(x, y, size, size, 90, angleCompleted);
                
                // Scores over 90% (Yellow)
                g2.setColor(YELLOW);
                g2.drawArc(x, y, size, size, 90 + angleCompleted, angleOver90);
                
                // In progress (Grey/Cyan)
                g2.setColor(CYAN);
                g2.drawArc(x, y, size, size, 90 + angleCompleted + angleOver90, angleInProgress);
                
                // Center Text
                g2.setColor(TEXT_DIM);
                g2.setFont(new Font("SansSerif", Font.PLAIN, 11));
                FontMetrics fm = g2.getFontMetrics();
                String t1 = "Total quizzes";
                g2.drawString(t1, getWidth()/2 - fm.stringWidth(t1)/2, getHeight()/2 - 10);
                
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("SansSerif", Font.BOLD, 22));
                fm = g2.getFontMetrics();
                String t2 = String.valueOf(total);
                g2.drawString(t2, getWidth()/2 - fm.stringWidth(t2)/2, getHeight()/2 + 25);
                
                g2.dispose();
            }
        };
        chartPanel.setOpaque(false);
        chartPanel.setPreferredSize(new Dimension(250, 180));
        
        // Legends
        JPanel legends = new JPanel(new GridLayout(2, 2, 8, 8));
        legends.setOpaque(false);
        legends.setBorder(new EmptyBorder(8, 10, 0, 10));
        
        int inProgress = Math.max(0, totalQuizzesAvailable - completedQuizzesCount);
        legends.add(buildLegend("Completed", completedQuizzesCount + "/" + totalQuizzesAvailable, PURPLE));
        legends.add(buildLegend("In progress", inProgress + "/" + totalQuizzesAvailable, CYAN));
        legends.add(buildLegend("Scores over 90%", scoresOver90Count + "/" + totalQuizzesAvailable, YELLOW));
        
        card.add(chartPanel, BorderLayout.CENTER);
        card.add(legends, BorderLayout.SOUTH);
        
        return card;
    }

    private JPanel buildLegend(String title, String val, Color color) {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        
        JPanel dot = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                g2.fillOval(5, 8, 12, 12);
                g2.dispose();
            }
        };
        dot.setOpaque(false);
        dot.setPreferredSize(new Dimension(25, 25));
        
        JPanel texts = new JPanel();
        texts.setOpaque(false);
        texts.setLayout(new BoxLayout(texts, BoxLayout.Y_AXIS));
        
        JLabel t = new JLabel(title);
        t.setForeground(Color.WHITE);
        t.setFont(new Font("SansSerif", Font.PLAIN, 13));
        
        JLabel v = new JLabel(val);
        v.setForeground(TEXT_DIM);
        v.setFont(new Font("SansSerif", Font.PLAIN, 11));
        
        texts.add(t);
        texts.add(v);
        
        p.add(dot, BorderLayout.WEST);
        p.add(texts, BorderLayout.CENTER);
        return p;
    }

    private JPanel buildKeepPractisingCard() {
        StudentDashboard.GlassCard card = new StudentDashboard.GlassCard(20);
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(12, 15, 12, 15));
        
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        
        JLabel scoreL = new JLabel(completedQuizzesCount + "/" + totalQuizzesAvailable);
        scoreL.setForeground(Color.WHITE);
        scoreL.setFont(new Font("SansSerif", Font.BOLD, 24));
        
        JPanel tag = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255,255,255, 30));
                g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1, 15, 15);
                g2.dispose();
            }
        };
        tag.setOpaque(false);
        tag.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 3));
        JLabel tagT = new JLabel("Computer Science");
        tagT.setForeground(TEXT_DIM);
        tagT.setFont(new Font("SansSerif", Font.PLAIN, 12));
        tag.add(tagT);
        
        top.add(scoreL, BorderLayout.WEST);
        top.add(tag, BorderLayout.EAST);
        
        JPanel center = new JPanel();
        center.setOpaque(false);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setBorder(new EmptyBorder(8, 0, 8, 0));
        
        JLabel msgTitle = new JLabel("Keep practising!");
        msgTitle.setForeground(Color.WHITE);
        msgTitle.setFont(new Font("SansSerif", Font.BOLD, 15));
        
        JLabel msgSub = new JLabel("<html>Improve your confidence in Computer Science by<br>practicing another quiz</html>");
        msgSub.setForeground(TEXT_DIM);
        msgSub.setFont(new Font("SansSerif", Font.PLAIN, 11));
        
        center.add(msgTitle);
        center.add(Box.createVerticalStrut(5));
        center.add(msgSub);
        
        JPanel bot = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        bot.setOpaque(false);
        
        JButton newQuiz = new JButton("New quiz  ➔");
        newQuiz.setFocusPainted(false);
        newQuiz.setBorderPainted(false);
        newQuiz.setOpaque(true);
        newQuiz.setBackground(new Color(60, 80, 150));
        newQuiz.setForeground(Color.WHITE);
        newQuiz.setFont(new Font("SansSerif", Font.BOLD, 12));
        newQuiz.setCursor(new Cursor(Cursor.HAND_CURSOR));
        newQuiz.addActionListener(e -> { dispose(); new StudentDashboard(user).setVisible(true); });
        
        bot.add(newQuiz);
        
        card.add(top, BorderLayout.NORTH);
        card.add(center, BorderLayout.CENTER);
        card.add(bot, BorderLayout.SOUTH);
        
        return card;
    }
}