package gui;

import dao.QuestionDAO;
import dao.QuizDAO;
import dao.ResultDAO;
import model.ResultDetail;
import model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TeacherDashboard extends JFrame {

    private final Color BG = new Color(244, 247, 251);
    private final Color SIDEBAR_BG = new Color(248, 250, 252);
    private final Color PURPLE = new Color(136, 117, 255);
    private final Color DARK = new Color(45, 55, 72);
    private final Color MUTED = new Color(113, 128, 150);
    private final Color HOVER = new Color(237, 242, 247);

    private CardLayout cardLayout;
    private JPanel contentPanel, dashWrap;
    private QuizCreationPanel quizPanel;
    private User currentUser;
    private final List<SidebarItem> sidebarItems = new ArrayList<>();

    public TeacherDashboard(User user) {
        this.currentUser = user;
        setTitle("Examify - Dashboard");
        setSize(1400, 900);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(BG);

        add(buildSidebar(), BorderLayout.WEST);

        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setOpaque(false);

        dashWrap = new JPanel(new BorderLayout());
        dashWrap.setBackground(BG);
        refreshDashboard();
        contentPanel.add(dashWrap, "dashboard");

        JPanel quizWrap = new JPanel(new BorderLayout());
        quizWrap.setBackground(BG);
        quizWrap.setBorder(new EmptyBorder(20, 30, 20, 30));
        quizPanel = new QuizCreationPanel(() -> { refreshDashboard(); setActiveTab("Dashboard"); cardLayout.show(contentPanel, "dashboard"); });
        quizWrap.add(quizPanel, BorderLayout.CENTER);
        contentPanel.add(quizWrap, "quiz");

        contentPanel.add(new StudentsPanel(), "students");
        contentPanel.add(buildPerformancePanel(), "performance");

        add(contentPanel, BorderLayout.CENTER);
    }

    // === SIDEBAR ===
    private JPanel buildSidebar() {
        JPanel sb = new JPanel();
        sb.setPreferredSize(new Dimension(210, 0));
        sb.setBackground(SIDEBAR_BG);
        sb.setBorder(new LineBorder(new Color(230, 235, 240), 1));
        sb.setLayout(new BoxLayout(sb, BoxLayout.Y_AXIS));

        JPanel logo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        logo.setOpaque(false);
        logo.setBorder(new EmptyBorder(25, 15, 30, 15));
        JLabel t1 = new JLabel("Examify"); t1.setFont(new Font("SansSerif", Font.BOLD, 20)); t1.setForeground(PURPLE);
        logo.add(t1);
        sb.add(logo);

        addItem(sb, "Dashboard", true, () -> { refreshDashboard(); cardLayout.show(contentPanel, "dashboard"); });
        sb.add(Box.createVerticalStrut(4));
        addItem(sb, "Quizzes", false, () -> { quizPanel.reset(); cardLayout.show(contentPanel, "quiz"); });
        sb.add(Box.createVerticalStrut(4));
        addItem(sb, "Students", false, () -> cardLayout.show(contentPanel, "students"));
        sb.add(Box.createVerticalStrut(4));
        addItem(sb, "Performance", false, () -> cardLayout.show(contentPanel, "performance"));
        sb.add(Box.createVerticalGlue());
        addItem(sb, "Logout", false, () -> { dispose(); new LoginFrame().setVisible(true); });
        sb.add(Box.createVerticalStrut(20));
        return sb;
    }

    private void addItem(JPanel sb, String text, boolean active, Runnable action) {
        SidebarItem item = new SidebarItem(text, action);
        if (active) item.setActive(true);
        sidebarItems.add(item);
        sb.add(item);
    }

    private void setActiveTab(String n) {
        for (SidebarItem si : sidebarItems) si.setActive(si.itemName.equals(n));
    }

    private class SidebarItem extends JPanel {
        JLabel label; boolean active; String itemName; Runnable action;
        SidebarItem(String itemName, Runnable action) {
            this.itemName = itemName; this.action = action;
            setLayout(new BorderLayout());
            setMaximumSize(new Dimension(190, 40));
            setPreferredSize(new Dimension(190, 40));
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setBorder(new EmptyBorder(0, 18, 0, 10));
            setOpaque(false);
            label = new JLabel(itemName);
            label.setFont(new Font("SansSerif", Font.PLAIN, 14));
            label.setForeground(MUTED);
            add(label, BorderLayout.CENTER);
            addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) { if (!itemName.equals("Logout")) setActiveTab(itemName); action.run(); }
                public void mouseEntered(MouseEvent e) { if (!active) repaint(); }
                public void mouseExited(MouseEvent e) { if (!active) repaint(); }
            });
        }
        void setActive(boolean a) {
            active = a;
            label.setForeground(a ? Color.WHITE : MUTED);
            label.setFont(new Font("SansSerif", a ? Font.BOLD : Font.PLAIN, 14));
            repaint();
        }
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Point mp = getMousePosition();
            if (active) g2.setColor(PURPLE);
            else if (mp != null) g2.setColor(HOVER);
            else g2.setColor(SIDEBAR_BG);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
            g2.dispose();
        }
    }

    // === DASHBOARD ===
    private JPanel buildMainDashboard() {
        JPanel d = new JPanel(); d.setLayout(new BoxLayout(d, BoxLayout.Y_AXIS)); d.setBackground(BG); d.setBorder(new EmptyBorder(25, 35, 25, 35));
        String name = currentUser != null ? currentUser.getUsername() : "Teacher";
        LocalDate today = LocalDate.now();
        String dateStr = today.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH) + " " + today.getDayOfMonth() + ", " + today.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH);

        // Header
        JPanel hdr = new JPanel(new BorderLayout()); hdr.setOpaque(false); hdr.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        JPanel leftH = new JPanel(); leftH.setOpaque(false); leftH.setLayout(new BoxLayout(leftH, BoxLayout.Y_AXIS));
        JLabel welcome = new JLabel("Welcome back, " + name); welcome.setFont(new Font("SansSerif", Font.BOLD, 24)); welcome.setForeground(DARK);
        JLabel dateLbl = new JLabel(dateStr); dateLbl.setFont(new Font("SansSerif", Font.PLAIN, 13)); dateLbl.setForeground(MUTED);
        leftH.add(welcome); leftH.add(dateLbl);
        hdr.add(leftH, BorderLayout.WEST);

        // Avatar
        JPanel av = new JPanel() {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create(); g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(PURPLE); g2.fillOval(0, 0, 40, 40); g2.setColor(Color.WHITE); g2.setFont(new Font("SansSerif", Font.BOLD, 18));
                FontMetrics fm = g2.getFontMetrics(); String ini = name.substring(0, 1).toUpperCase();
                g2.drawString(ini, (40 - fm.stringWidth(ini)) / 2, (40 + fm.getAscent() - fm.getDescent()) / 2); g2.dispose();
            }
        };
        av.setOpaque(false); av.setPreferredSize(new Dimension(40, 40));
        hdr.add(av, BorderLayout.EAST);
        d.add(hdr); d.add(Box.createVerticalStrut(20));

        // Overview label
        JLabel ovLbl = new JLabel("Overview"); ovLbl.setFont(new Font("SansSerif", Font.BOLD, 18)); ovLbl.setForeground(DARK);
        ovLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        d.add(ovLbl); d.add(Box.createVerticalStrut(12));

        // Stats
        ResultDAO rDao = new ResultDAO(); QuizDAO qDao = new QuizDAO(); QuestionDAO quDao = new QuestionDAO();
        JPanel stats = new JPanel(new GridLayout(1, 4, 16, 0)); stats.setOpaque(false);
        stats.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100)); stats.setPreferredSize(new Dimension(800, 100));
        stats.add(statCard("No of Students", String.valueOf(rDao.getUniqueStudentCount()), new Color(93, 95, 239)));
        stats.add(statCard("No of Quizzes", String.valueOf(qDao.getTotalQuizzesCount()), new Color(255, 160, 0)));
        stats.add(statCard("Quizzes Active", String.valueOf(qDao.getAllActiveQuizzes().size()), new Color(76, 175, 80)));
        stats.add(statCard("Quizzes Done", String.valueOf(rDao.getCompletedQuizzesCount()), new Color(239, 83, 80)));
        d.add(stats); d.add(Box.createVerticalStrut(20));

        // Row 2: Performance + Calendar
        JPanel row2 = new JPanel(new GridLayout(1, 2, 20, 0)); row2.setOpaque(false);
        row2.setMaximumSize(new Dimension(Integer.MAX_VALUE, 280)); row2.setPreferredSize(new Dimension(800, 280));
        row2.add(buildDonutPanel(rDao));
        row2.add(buildCalendarPanel());
        d.add(row2); d.add(Box.createVerticalStrut(20));

        // Row 3: Students Table + Weekly Schedule
        JPanel row3 = new JPanel(new GridLayout(1, 2, 20, 0)); row3.setOpaque(false);
        row3.setMaximumSize(new Dimension(Integer.MAX_VALUE, 300)); row3.setPreferredSize(new Dimension(800, 300));
        row3.add(buildStudentsTable(rDao));
        row3.add(buildWeeklySchedule(qDao));
        d.add(row3);
        d.add(Box.createVerticalGlue());
        return d;
    }

    private void refreshDashboard() {
        dashWrap.removeAll();
        JScrollPane sp = new JScrollPane(buildMainDashboard());
        sp.setBorder(null); sp.getVerticalScrollBar().setUnitIncrement(16); sp.getViewport().setBackground(BG);
        dashWrap.add(sp, BorderLayout.CENTER); dashWrap.revalidate(); dashWrap.repaint();
    }

    // === STAT CARD ===
    private JPanel statCard(String label, String value, Color accent) {
        JPanel c = rCard(); c.setLayout(new BorderLayout()); c.setBorder(new EmptyBorder(18, 18, 18, 18));
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0)); top.setOpaque(false);
        JPanel dot = new JPanel() { protected void paintComponent(Graphics g) { Graphics2D g2=(Graphics2D)g.create(); g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON); g2.setColor(accent); g2.fillRoundRect(0,0,28,28,8,8); g2.dispose(); }};
        dot.setOpaque(false); dot.setPreferredSize(new Dimension(28, 28));
        JLabel lbl = new JLabel(label); lbl.setFont(new Font("SansSerif", Font.PLAIN, 12)); lbl.setForeground(MUTED);
        top.add(dot); top.add(lbl); c.add(top, BorderLayout.NORTH);
        JLabel val = new JLabel(value); val.setFont(new Font("SansSerif", Font.BOLD, 32)); val.setForeground(DARK); val.setBorder(new EmptyBorder(5,5,0,0));
        c.add(val, BorderLayout.CENTER);
        return c;
    }

    // === DONUT CHART ===
    private JPanel buildDonutPanel(ResultDAO rDao) {
        int[] bd = rDao.getPerformanceBreakdown();
        int total = bd[0] + bd[1] + bd[2];
        JPanel card = rCard(); card.setLayout(new BorderLayout()); card.setBorder(new EmptyBorder(18, 18, 18, 18));
        JLabel t = new JLabel("Student Performance"); t.setFont(new Font("SansSerif", Font.BOLD, 16)); t.setForeground(DARK);
        card.add(t, BorderLayout.NORTH);
        JPanel donut = new JPanel() {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create(); g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int sz = Math.min(getWidth(), getHeight()) - 40, cx = (getWidth()-sz)/2, cy = (getHeight()-sz)/2;
                if (total == 0) { g2.setColor(new Color(230,230,230)); g2.fillOval(cx,cy,sz,sz); }
                else {
                    int a1 = 0;
                    Color[] cols = {new Color(93,95,239), new Color(255,160,0), new Color(76,175,80)};
                    for (int i = 0; i < 3; i++) {
                        int sweep = (int) Math.round(360.0 * bd[i] / total);
                        if (i == 2) sweep = 360 - a1;
                        g2.setColor(cols[i]); g2.fillArc(cx, cy, sz, sz, a1, sweep); a1 += sweep;
                    }
                }
                g2.setColor(Color.WHITE); int hole = sz/2; g2.fillOval(cx+(sz-hole)/2, cy+(sz-hole)/2, hole, hole);
                String pct = total > 0 ? (bd[0]*100/total)+"%": "0%";
                g2.setColor(DARK); g2.setFont(new Font("SansSerif", Font.BOLD, 22));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(pct, (getWidth()-fm.stringWidth(pct))/2, getHeight()/2+fm.getAscent()/3);
                g2.dispose();
            }
        };
        donut.setOpaque(false);
        card.add(donut, BorderLayout.CENTER);
        JPanel legend = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5)); legend.setOpaque(false);
        legend.add(legendItem("Excellent", new Color(93,95,239)));
        legend.add(legendItem("Good", new Color(255,160,0)));
        legend.add(legendItem("Average", new Color(76,175,80)));
        card.add(legend, BorderLayout.SOUTH);
        return card;
    }

    private JPanel legendItem(String text, Color c) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0)); p.setOpaque(false);
        JPanel sq = new JPanel() { protected void paintComponent(Graphics g) { g.setColor(c); g.fillRect(0,0,10,10); }};
        sq.setPreferredSize(new Dimension(10, 10)); sq.setOpaque(false);
        JLabel l = new JLabel(text); l.setFont(new Font("SansSerif", Font.PLAIN, 11)); l.setForeground(MUTED);
        p.add(sq); p.add(l); return p;
    }

    // === CALENDAR ===
    private JPanel buildCalendarPanel() {
        JPanel card = rCard(); card.setLayout(new BorderLayout()); card.setBorder(new EmptyBorder(18, 18, 18, 18));
        YearMonth ym = YearMonth.now(); LocalDate today = LocalDate.now();
        JLabel monthLbl = new JLabel(ym.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH) + " " + ym.getYear());
        monthLbl.setFont(new Font("SansSerif", Font.BOLD, 16)); monthLbl.setForeground(DARK);
        card.add(monthLbl, BorderLayout.NORTH);

        JPanel grid = new JPanel(new GridLayout(0, 7, 2, 2)); grid.setOpaque(false);
        String[] days = {"S","M","T","W","T","F","S"};
        for (String day : days) {
            JLabel dl = new JLabel(day, SwingConstants.CENTER); dl.setFont(new Font("SansSerif", Font.BOLD, 12)); dl.setForeground(MUTED); grid.add(dl);
        }
        int firstDay = ym.atDay(1).getDayOfWeek().getValue() % 7;
        for (int i = 0; i < firstDay; i++) grid.add(new JLabel(""));
        for (int d = 1; d <= ym.lengthOfMonth(); d++) {
            final int day = d;
            JLabel dl = new JLabel(String.valueOf(d), SwingConstants.CENTER) {
                protected void paintComponent(Graphics g) {
                    if (day == today.getDayOfMonth() && ym.getMonth() == today.getMonth()) {
                        Graphics2D g2 = (Graphics2D) g.create(); g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g2.setColor(PURPLE); g2.fillOval(2, 2, getWidth()-4, getHeight()-4); g2.dispose();
                        setForeground(Color.WHITE);
                    }
                    super.paintComponent(g);
                }
            };
            dl.setFont(new Font("SansSerif", Font.PLAIN, 13));
            dl.setForeground(day == today.getDayOfMonth() ? Color.WHITE : DARK);
            dl.setOpaque(false);
            grid.add(dl);
        }
        card.add(grid, BorderLayout.CENTER);
        return card;
    }

    // === STUDENTS TABLE ===
    private JPanel buildStudentsTable(ResultDAO rDao) {
        JPanel card = rCard(); card.setLayout(new BorderLayout()); card.setBorder(new EmptyBorder(18, 18, 18, 18));
        JLabel t = new JLabel("Students Table"); t.setFont(new Font("SansSerif", Font.BOLD, 16)); t.setForeground(DARK);
        card.add(t, BorderLayout.NORTH);
        String[] cols = {"#", "Student", "Quiz", "Grade", "Status"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) { public boolean isCellEditable(int r, int c) { return false; }};
        List<ResultDetail> results = rDao.getRecentResults(10);
        int idx = 1;
        for (ResultDetail r : results) {
            int pct = r.getPercentage();
            String grade = pct >= 90 ? "A+" : pct >= 80 ? "A" : pct >= 70 ? "B+" : pct >= 60 ? "B" : pct >= 50 ? "C" : "F";
            model.addRow(new Object[]{idx++, r.getStudentName(), r.getQuizTitle(), grade + " (" + pct + "%)", "Completed"});
        }
        JTable table = new JTable(model);
        table.setFont(new Font("SansSerif", Font.PLAIN, 13)); table.setRowHeight(32);
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        table.setShowGrid(false); table.setIntercellSpacing(new Dimension(0, 0));
        DefaultTableCellRenderer ctr = new DefaultTableCellRenderer(); ctr.setHorizontalAlignment(SwingConstants.LEFT);
        for (int i = 0; i < table.getColumnCount(); i++) table.getColumnModel().getColumn(i).setCellRenderer(ctr);
        JScrollPane sp = new JScrollPane(table); sp.setBorder(null);
        card.add(sp, BorderLayout.CENTER);
        return card;
    }

    // === WEEKLY SCHEDULE ===
    private JPanel buildWeeklySchedule(QuizDAO qDao) {
        JPanel card = rCard(); card.setLayout(new BorderLayout()); card.setBorder(new EmptyBorder(18, 18, 18, 18));
        JLabel t = new JLabel("Weekly Quiz Schedule"); t.setFont(new Font("SansSerif", Font.BOLD, 16)); t.setForeground(DARK);
        card.add(t, BorderLayout.NORTH);
        JPanel list = new JPanel(); list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS)); list.setOpaque(false);
        List<model.Quiz> quizzes = qDao.getAllActiveQuizzes();
        Color[] accents = {new Color(93,95,239), new Color(76,175,80), new Color(255,160,0), new Color(33,150,243)};
        Color[] bgs = {new Color(240,237,255), new Color(232,245,233), new Color(255,243,224), new Color(227,242,253)};
        if (quizzes.isEmpty()) {
            JLabel e = new JLabel("No active quizzes."); e.setFont(new Font("SansSerif", Font.ITALIC, 13)); e.setForeground(MUTED); list.add(e);
        } else {
            for (int i = 0; i < Math.min(quizzes.size(), 5); i++) {
                model.Quiz q = quizzes.get(i); int ci = i % accents.length;
                JPanel row = new JPanel(new BorderLayout()) {
                    protected void paintComponent(Graphics g) {
                        Graphics2D g2=(Graphics2D)g.create(); g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                        g2.setColor(bgs[ci]); g2.fillRoundRect(0,0,getWidth(),getHeight(),12,12);
                        g2.setColor(accents[ci]); g2.fillRoundRect(0,4,5,getHeight()-8,5,5); g2.dispose();
                    }
                };
                row.setOpaque(false); row.setBorder(new EmptyBorder(10, 18, 10, 12));
                row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
                JPanel info = new JPanel(); info.setOpaque(false); info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
                JLabel n = new JLabel(q.getTitle()); n.setFont(new Font("SansSerif", Font.BOLD, 13)); n.setForeground(DARK);
                JLabel tm = new JLabel(q.getTimeLimit() + "s • Active"); tm.setFont(new Font("SansSerif", Font.PLAIN, 11)); tm.setForeground(MUTED);
                info.add(n); info.add(tm); row.add(info, BorderLayout.CENTER);
                JButton ed = new JButton("Edit"); ed.setFont(new Font("SansSerif", Font.BOLD, 11)); ed.setForeground(accents[ci]);
                ed.setContentAreaFilled(false); ed.setBorderPainted(false); ed.setFocusPainted(false); ed.setCursor(new Cursor(Cursor.HAND_CURSOR));
                ed.addActionListener(ev -> { quizPanel.loadQuiz(q); setActiveTab("Quizzes"); cardLayout.show(contentPanel, "quiz"); });
                row.add(ed, BorderLayout.EAST);
                list.add(row); list.add(Box.createVerticalStrut(8));
            }
        }
        JScrollPane sp = new JScrollPane(list); sp.setBorder(null); sp.getViewport().setOpaque(false); sp.setOpaque(false);
        card.add(sp, BorderLayout.CENTER);
        return card;
    }

    // === PERFORMANCE PANEL ===
    private JPanel buildPerformancePanel() {
        JPanel p = new JPanel(new BorderLayout()); p.setBackground(BG); p.setBorder(new EmptyBorder(30, 40, 30, 40));
        JLabel t = new JLabel("Performance Overview"); t.setFont(new Font("SansSerif", Font.BOLD, 26)); t.setForeground(DARK);
        p.add(t, BorderLayout.NORTH);
        ResultDAO rDao = new ResultDAO();
        JPanel content = new JPanel(new GridLayout(1, 2, 20, 0)); content.setOpaque(false);
        content.add(buildDonutPanel(rDao));
        content.add(buildStudentsTable(rDao));
        p.add(content, BorderLayout.CENTER);
        return p;
    }

    private JPanel rCard() {
        JPanel p = new JPanel() {
            protected void paintComponent(Graphics g) {
                Graphics2D g2=(Graphics2D)g.create(); g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE); g2.fillRoundRect(0,0,getWidth(),getHeight(),16,16); g2.dispose();
            }
        };
        p.setOpaque(false); return p;
    }
}
