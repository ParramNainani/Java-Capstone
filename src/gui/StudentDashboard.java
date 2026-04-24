package gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import model.User;

public class StudentDashboard extends JFrame {

    private final User user;

    // ── Brand colours (mirrors LoginFrame exactly) ──────────────────────────────
    private static final Color CYAN      = new Color(38,  208, 206);
    private static final Color PURPLE    = new Color(138,  43, 226);
    private static final Color BLUE      = new Color(30,  144, 255);
    private static final Color BG_DARK   = new Color(10,   15,  30);
    private static final Color GLASS     = new Color(255, 255, 255, 18);
    private static final Color GLASS_BD  = new Color(255, 255, 255, 40);
    private static final Color TEXT_DIM  = new Color(200, 210, 230);
    private static final Color ACCENT_ORANGE = new Color(255, 150, 100);
    private static final Color ACCENT_YELLOW = new Color(255, 215, 0);

    public StudentDashboard(User user) {
        this.user = user;
        setTitle("EXAMIFY – Student Dashboard");
        setSize(1350, 800);
        setMinimumSize(new Dimension(1200, 750));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        BlobBackground root = new BlobBackground();
        root.setLayout(new BorderLayout());

        root.add(buildSidebar(), BorderLayout.WEST);
        root.add(buildMainArea(), BorderLayout.CENTER);

        setContentPane(root);
    }

    // ── Sidebar ────────────────────────────────────────────────────────────────
    private JPanel buildSidebar() {
        JPanel side = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(new Color(10, 15, 30, 200));
                g2.fillRect(0, 0, getWidth(), getHeight());
                // right divider
                g2.setColor(GLASS_BD);
                g2.drawLine(getWidth()-1, 0, getWidth()-1, getHeight());
                g2.dispose();
            }
        };
        side.setOpaque(false);
        side.setPreferredSize(new Dimension(240, 0));
        side.setLayout(new BoxLayout(side, BoxLayout.Y_AXIS));
        side.setBorder(new EmptyBorder(40, 24, 40, 24));

        // Brand
        JLabel brand = new JLabel("EXAMIFY");
        brand.setFont(new Font("SansSerif", Font.BOLD, 28));
        brand.setForeground(Color.WHITE);
        brand.setAlignmentX(Component.LEFT_ALIGNMENT);
        side.add(brand);

        JLabel role = new JLabel("Student Portal");
        role.setFont(new Font("SansSerif", Font.PLAIN, 13));
        role.setForeground(CYAN);
        role.setAlignmentX(Component.LEFT_ALIGNMENT);
        side.add(role);

        side.add(Box.createVerticalStrut(50));

        // Nav items
        side.add(buildNavItem("🏠", "Dashboard",  true));
        side.add(Box.createVerticalStrut(10));
        side.add(buildNavItem("📚", "Courses", false));
        side.add(Box.createVerticalStrut(10));
        side.add(buildNavItem("📝", "Start Quiz", false));
        side.add(Box.createVerticalStrut(10));
        side.add(buildNavItem("📈", "Performance",  false));

        side.add(Box.createVerticalGlue());

        // Logout
        JButton logout = new JButton("⬅  Logout");
        logout.setFocusPainted(false);
        logout.setBorderPainted(false);
        logout.setContentAreaFilled(false);
        logout.setForeground(new Color(255, 120, 140));
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
                if (label.equals("Start Quiz")) { dispose(); new QuizFrame(user).setVisible(true); }
                else if (label.equals("Performance")) { dispose(); new ResultFrame(user, 0, 0).setVisible(true); }
                else if (label.equals("Courses")) { JOptionPane.showMessageDialog(StudentDashboard.this, "Courses portal coming soon!"); }
            }
        });

        return item;
    }

    // ── Main Content Area ─────────────────────────────────────────────────────
    private JPanel buildMainArea() {
        JPanel main = new JPanel(new BorderLayout());
        main.setOpaque(false);
        
        // Header (Welcome & Top Bar)
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(30, 40, 20, 40));
        
        JPanel greetBlock = new JPanel();
        greetBlock.setOpaque(false);
        greetBlock.setLayout(new BoxLayout(greetBlock, BoxLayout.Y_AXIS));

        String firstName = user.fullName.split(" ")[0];
        JLabel greet = new JLabel("Welcome back, " + firstName + " 👋");
        greet.setForeground(Color.WHITE);
        greet.setFont(new Font("SansSerif", Font.BOLD, 32));

        JLabel sub = new JLabel("Let's learn something new today");
        sub.setForeground(TEXT_DIM);
        sub.setFont(new Font("SansSerif", Font.PLAIN, 16));

        greetBlock.add(greet);
        greetBlock.add(Box.createVerticalStrut(5));
        greetBlock.add(sub);
        
        // Top Right Profile/Search area
        JPanel topNav = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        topNav.setOpaque(false);
        
        // Search Bar
        GlassCard searchBar = new GlassCard(20);
        searchBar.setPreferredSize(new Dimension(200, 40));
        searchBar.setLayout(new BorderLayout());
        JLabel searchIcon = new JLabel("  🔍 ");
        searchIcon.setForeground(TEXT_DIM);
        JTextField searchTxt = new JTextField("Search courses...");
        searchTxt.setOpaque(false);
        searchTxt.setBorder(null);
        searchTxt.setForeground(Color.WHITE);
        searchTxt.setCaretColor(Color.WHITE);
        searchTxt.setFont(new Font("SansSerif", Font.PLAIN, 13));
        searchTxt.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) { if(searchTxt.getText().equals("Search courses...")) searchTxt.setText(""); }
            public void focusLost(FocusEvent e) { if(searchTxt.getText().isEmpty()) searchTxt.setText("Search courses..."); }
        });
        searchTxt.addActionListener(e -> JOptionPane.showMessageDialog(this, "Searching for: " + searchTxt.getText()));
        searchBar.add(searchIcon, BorderLayout.WEST);
        searchBar.add(searchTxt, BorderLayout.CENTER);
        
        JLabel bell = new JLabel("🔔");
        bell.setFont(new Font("SansSerif", Font.PLAIN, 20));
        bell.setForeground(Color.WHITE);
        
        topNav.add(searchBar);
        topNav.add(bell);
        
        header.add(greetBlock, BorderLayout.WEST);
        header.add(topNav, BorderLayout.EAST);
        
        main.add(header, BorderLayout.NORTH);

        // Center Content Split
        JPanel contentArea = new JPanel(new GridBagLayout());
        contentArea.setOpaque(false);
        contentArea.setBorder(new EmptyBorder(0, 40, 30, 40));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;
        
        // Left Column
        JPanel leftCol = new JPanel();
        leftCol.setOpaque(false);
        leftCol.setLayout(new BoxLayout(leftCol, BoxLayout.Y_AXIS));
        
        // Top Stats Row
        JPanel statsRow = new JPanel(new GridLayout(1, 3, 20, 0));
        statsRow.setOpaque(false);
        statsRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));
        statsRow.add(buildStatCard("11", "Completed courses", new Color(138,43,226, 80)));
        statsRow.add(buildStatCard("03", "Studied courses", new Color(38,208,206, 80)));
        statsRow.add(buildStatCard("12", "Pending courses", new Color(255,200,80, 80)));
        leftCol.add(statsRow);
        leftCol.add(Box.createVerticalStrut(20));
        
        // Productivity Line Graph
        GlassCard graphCard = new GlassCard(20);
        graphCard.setLayout(new BorderLayout());
        graphCard.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JPanel graphHeader = new JPanel(new BorderLayout());
        graphHeader.setOpaque(false);
        JLabel pTitle = new JLabel("Productivity");
        pTitle.setForeground(Color.WHITE);
        pTitle.setFont(new Font("SansSerif", Font.BOLD, 18));
        JLabel pWeek = new JLabel("Week ▼");
        pWeek.setForeground(TEXT_DIM);
        pWeek.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        ProductivityGraph pGraph = new ProductivityGraph();
        pWeek.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (pWeek.getText().equals("Week ▼")) {
                    pWeek.setText("Month ▼");
                } else {
                    pWeek.setText("Week ▼");
                }
                pGraph.toggleData();
            }
        });

        graphHeader.add(pTitle, BorderLayout.WEST);
        graphHeader.add(pWeek, BorderLayout.EAST);
        
        graphCard.add(graphHeader, BorderLayout.NORTH);
        graphCard.add(pGraph, BorderLayout.CENTER);
        
        leftCol.add(graphCard);
        leftCol.add(Box.createVerticalStrut(20));
        
        // Courses Row
        JPanel coursesHeader = new JPanel(new BorderLayout());
        coursesHeader.setOpaque(false);
        coursesHeader.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        JLabel cTitle = new JLabel("Courses");
        cTitle.setForeground(Color.WHITE);
        cTitle.setFont(new Font("SansSerif", Font.BOLD, 18));
        JLabel cView = new JLabel("View all");
        cView.setForeground(TEXT_DIM);
        coursesHeader.add(cTitle, BorderLayout.WEST);
        coursesHeader.add(cView, BorderLayout.EAST);
        
        JPanel coursesRow = new JPanel(new GridLayout(1, 3, 20, 0));
        coursesRow.setOpaque(false);
        coursesRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 180));
        coursesRow.add(buildCourseCard("UI/UX Design", "Projecting interfaces", 45, new Color(138,43,226)));
        coursesRow.add(buildCourseCard("Figma Pro", "UX designer scratch", 80, new Color(255,200,80)));
        coursesRow.add(buildCourseCard("HTML/CSS", "Frontend basics", 65, new Color(38,208,206)));
        
        leftCol.add(coursesHeader);
        leftCol.add(Box.createVerticalStrut(10));
        leftCol.add(coursesRow);
        
        // Right Column
        JPanel rightCol = new JPanel();
        rightCol.setOpaque(false);
        rightCol.setLayout(new BoxLayout(rightCol, BoxLayout.Y_AXIS));
        
        // Calendar Card
        GlassCard calCard = new GlassCard(20);
        calCard.setLayout(new BorderLayout());
        calCard.setBorder(new EmptyBorder(20, 20, 20, 20));
        calCard.setMaximumSize(new Dimension(350, 320));
        calCard.add(new CalendarPanel(), BorderLayout.CENTER);
        
        // Achievement Board / Profile Card
        GlassCard profileCard = new GlassCard(20);
        profileCard.setLayout(new BorderLayout());
        profileCard.setMaximumSize(new Dimension(350, 300));
        profileCard.add(buildAchievementBoard(), BorderLayout.CENTER);
        
        rightCol.add(calCard);
        rightCol.add(Box.createVerticalStrut(20));
        rightCol.add(profileCard);
        
        gbc.weightx = 0.7; // Left takes more space
        gbc.insets = new Insets(0, 0, 0, 20);
        contentArea.add(leftCol, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 0.3; // Right takes less space
        gbc.insets = new Insets(0, 0, 0, 0);
        contentArea.add(rightCol, gbc);

        main.add(contentArea, BorderLayout.CENTER);
        return main;
    }

    private JPanel buildStatCard(String value, String label, Color bgColor) {
        JPanel card = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bgColor);
                g2.fillRoundRect(0,0,getWidth(),getHeight(), 18, 18);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        card.setOpaque(false);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(20, 25, 20, 25));
        
        JLabel valL = new JLabel(value);
        valL.setForeground(Color.WHITE);
        valL.setFont(new Font("SansSerif", Font.BOLD, 32));
        valL.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel lbl = new JLabel(label);
        lbl.setForeground(new Color(255,255,255,200));
        lbl.setFont(new Font("SansSerif", Font.PLAIN, 14));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        card.add(valL);
        card.add(Box.createVerticalStrut(5));
        card.add(lbl);
        return card;
    }

    private JPanel buildCourseCard(String tag, String title, int progress, Color accent) {
        GlassCard card = new GlassCard(16);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(15, 15, 15, 15));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JOptionPane.showMessageDialog(StudentDashboard.this, "Opening course: " + title);
            }
        });
        
        // Placeholder illustration area
        JPanel imgArea = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 30));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                
                // Draw some abstract shapes to mimic illustration
                g2.setColor(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 80));
                g2.fillRoundRect(20, 20, getWidth()-40, 40, 5, 5);
                g2.fillOval(getWidth()/2-15, getHeight()/2+5, 30, 30);
                
                g2.dispose();
            }
        };
        imgArea.setOpaque(false);
        imgArea.setPreferredSize(new Dimension(200, 100));
        imgArea.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        
        JLabel tLbl = new JLabel("<html><b>" + title + "</b></html>");
        tLbl.setForeground(Color.WHITE);
        tLbl.setFont(new Font("SansSerif", Font.PLAIN, 14));
        tLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JPanel progRow = new JPanel(new BorderLayout());
        progRow.setOpaque(false);
        progRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
        
        JLabel pLbl = new JLabel(progress + "%");
        pLbl.setForeground(accent);
        pLbl.setFont(new Font("SansSerif", Font.BOLD, 12));
        
        JPanel pBar = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255,255,255,30));
                g2.fillRoundRect(0, getHeight()/2-3, getWidth(), 6, 6, 6);
                int w = (int)(getWidth() * (progress/100.0));
                g2.setColor(accent);
                g2.fillRoundRect(0, getHeight()/2-3, w, 6, 6, 6);
                g2.dispose();
            }
        };
        pBar.setOpaque(false);
        pBar.setPreferredSize(new Dimension(100, 20));
        
        progRow.add(tLbl, BorderLayout.WEST);
        progRow.add(pLbl, BorderLayout.EAST);
        
        card.add(imgArea);
        card.add(Box.createVerticalStrut(10));
        card.add(progRow);
        card.add(pBar);
        
        return card;
    }

    private JPanel buildAchievementBoard() {
        JPanel board = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0,0, ACCENT_ORANGE, 0,getHeight(), new Color(255,90,120));
                g2.setPaint(gp);
                g2.fillRoundRect(0,0,getWidth(),getHeight(), 20, 20);
                g2.dispose();
            }
        };
        board.setOpaque(false);
        
        // Profile Info
        JPanel top = new JPanel();
        top.setOpaque(false);
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        top.setBorder(new EmptyBorder(30, 20, 20, 20));
        
        // Avatar
        JPanel avatar = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255,255,255, 40));
                g2.fillOval(0,0,getWidth(),getHeight());
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("SansSerif", Font.BOLD, 30));
                String in = getInitials(user.fullName);
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(in, (getWidth()-fm.stringWidth(in))/2, (getHeight()+fm.getAscent()-fm.getDescent())/2);
                g2.dispose();
            }
        };
        avatar.setOpaque(false);
        avatar.setPreferredSize(new Dimension(80, 80));
        avatar.setMaximumSize(new Dimension(80, 80));
        avatar.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel nameL = new JLabel(user.fullName);
        nameL.setForeground(Color.WHITE);
        nameL.setFont(new Font("SansSerif", Font.BOLD, 22));
        nameL.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel verified = new JLabel("Verified Student ✓");
        verified.setForeground(new Color(255,255,255,200));
        verified.setFont(new Font("SansSerif", Font.PLAIN, 14));
        verified.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        top.add(avatar);
        top.add(Box.createVerticalStrut(15));
        top.add(nameL);
        top.add(Box.createVerticalStrut(5));
        top.add(verified);
        
        // Stats Row
        JPanel stats = new JPanel(new GridLayout(1, 3, 10, 0));
        stats.setOpaque(false);
        stats.setBorder(new EmptyBorder(10, 20, 20, 20));
        
        stats.add(buildBoardStat("38", "Friends", new Color(255,255,255, 40)));
        stats.add(buildBoardStat("14", "Achievement", new Color(255,255,255, 40)));
        stats.add(buildBoardStat("5.0", "Rating", new Color(255,255,255, 40)));
        
        board.add(top, BorderLayout.CENTER);
        board.add(stats, BorderLayout.SOUTH);
        
        return board;
    }

    private JPanel buildBoardStat(String val, String lbl, Color bg) {
        JPanel p = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bg);
                g2.fillRoundRect(0,0,getWidth(),getHeight(), 15, 15);
                g2.dispose();
            }
        };
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(new EmptyBorder(15, 5, 15, 5));
        
        JLabel v = new JLabel(val);
        v.setForeground(Color.WHITE);
        v.setFont(new Font("SansSerif", Font.BOLD, 22));
        v.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel l = new JLabel(lbl);
        l.setForeground(Color.WHITE);
        l.setFont(new Font("SansSerif", Font.PLAIN, 11));
        l.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        p.add(v);
        p.add(Box.createVerticalStrut(5));
        p.add(l);
        return p;
    }

    // ── Components ────────────────────────────────────────────────────────────

    static class ProductivityGraph extends JPanel {
        private int[] targetData = { 2, 4, 3, 6, 4, 8, 5 };
        private float[] currentData = { 2, 4, 3, 6, 4, 8, 5 };
        private boolean isWeekly = true;
        private Timer animTimer;
        
        ProductivityGraph() {
            setOpaque(false);
            animTimer = new Timer(20, e -> {
                boolean done = true;
                for (int i=0; i<currentData.length; i++) {
                    if (Math.abs(currentData[i] - targetData[i]) > 0.1f) {
                        currentData[i] += (targetData[i] - currentData[i]) * 0.15f;
                        done = false;
                    } else {
                        currentData[i] = targetData[i];
                    }
                }
                repaint();
                if (done) animTimer.stop();
            });
        }
        
        public void toggleData() {
            isWeekly = !isWeekly;
            if (isWeekly) {
                targetData = new int[]{ 2, 4, 3, 6, 4, 8, 5 };
            } else {
                targetData = new int[]{ 5, 2, 7, 3, 6, 4, 8 };
            }
            animTimer.start();
        }

        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            int w = getWidth();
            int h = getHeight() - 30; // leave room for labels
            
            // Draw grid lines
            g2.setColor(new Color(255,255,255, 20));
            for(int i=0; i<4; i++) {
                int y = h - (i * h/3);
                g2.drawLine(40, y, w-20, y);
                g2.setColor(TEXT_DIM);
                g2.setFont(new Font("SansSerif", Font.PLAIN, 11));
                g2.drawString((i*2)+" h", 10, y+4);
                g2.setColor(new Color(255,255,255, 20));
            }
            
            // Draw labels
            String[] days = isWeekly ? new String[]{"Mo", "Tu", "We", "Th", "Fr", "Sa", "Su"} : new String[]{"W1", "W2", "W3", "W4", "W1", "W2", "W3"};
            g2.setColor(TEXT_DIM);
            int step = (w - 60) / 6;
            for(int i=0; i<7; i++) {
                int x = 40 + (i * step);
                g2.drawString(days[i], x-8, h+20);
            }
            
            // Create Path
            Path2D.Float path = new Path2D.Float();
            Path2D.Float fillPath = new Path2D.Float();
            
            int maxVal = 8;
            for(int i=0; i<currentData.length; i++) {
                int x = 40 + (i * step);
                int y = h - (int)(currentData[i] * h / maxVal);
                
                if (i == 0) {
                    path.moveTo(x, y);
                    fillPath.moveTo(40, h);
                    fillPath.lineTo(x, y);
                } else {
                    int prevX = 40 + ((i-1) * step);
                    int prevY = h - (int)(currentData[i-1] * h / maxVal);
                    int cpX1 = prevX + (x - prevX)/2;
                    int cpX2 = prevX + (x - prevX)/2;
                    path.curveTo(cpX1, prevY, cpX2, y, x, y);
                    fillPath.curveTo(cpX1, prevY, cpX2, y, x, y);
                }
            }
            
            fillPath.lineTo(40 + (6 * step), h);
            fillPath.closePath();
            
            // Fill gradient
            GradientPaint gp = new GradientPaint(0, 0, new Color(38, 208, 206, 150), 0, h, new Color(38, 208, 206, 0));
            g2.setPaint(gp);
            g2.fill(fillPath);
            
            // Draw line
            g2.setColor(CYAN);
            g2.setStroke(new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.draw(path);
            
            // Draw points
            for(int i=0; i<currentData.length; i++) {
                int x = 40 + (i * step);
                int y = h - (int)(currentData[i] * h / maxVal);
                g2.setColor(BG_DARK);
                g2.fillOval(x-5, y-5, 10, 10);
                g2.setColor(CYAN);
                g2.setStroke(new BasicStroke(2f));
                g2.drawOval(x-5, y-5, 10, 10);
            }
            
            g2.dispose();
        }
    }

    static class CalendarPanel extends JPanel {
        private String[] days = {"Mo", "Tu", "We", "Th", "Fr", "Sa", "Su"};
        private int currentMonth;
        private int currentYear;
        private int selectedDay = -1;
        private JLabel monthLabel;
        private JPanel grid;
        
        CalendarPanel() {
            setOpaque(false);
            setLayout(new BorderLayout());
            
            Calendar cal = Calendar.getInstance();
            currentMonth = cal.get(Calendar.MONTH);
            currentYear = cal.get(Calendar.YEAR);
            selectedDay = cal.get(Calendar.DAY_OF_MONTH);
            
            JPanel header = new JPanel(new BorderLayout());
            header.setOpaque(false);
            header.setBorder(new EmptyBorder(0, 10, 15, 10));
            
            monthLabel = new JLabel("", SwingConstants.CENTER);
            monthLabel.setForeground(Color.WHITE);
            monthLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
            
            JLabel lArr = new JLabel(" < ");
            lArr.setForeground(TEXT_DIM);
            lArr.setCursor(new Cursor(Cursor.HAND_CURSOR));
            lArr.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) { changeMonth(-1); }
            });
            
            JLabel rArr = new JLabel(" > ");
            rArr.setForeground(TEXT_DIM);
            rArr.setCursor(new Cursor(Cursor.HAND_CURSOR));
            rArr.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) { changeMonth(1); }
            });
            
            header.add(lArr, BorderLayout.WEST);
            header.add(monthLabel, BorderLayout.CENTER);
            header.add(rArr, BorderLayout.EAST);
            
            grid = new JPanel(new GridLayout(7, 7, 5, 5));
            grid.setOpaque(false);
            
            add(header, BorderLayout.NORTH);
            add(grid, BorderLayout.CENTER);
            
            updateCalendar();
        }
        
        private void changeMonth(int offset) {
            currentMonth += offset;
            if (currentMonth > 11) { currentMonth = 0; currentYear++; }
            else if (currentMonth < 0) { currentMonth = 11; currentYear--; }
            selectedDay = -1; // reset selection
            updateCalendar();
        }
        
        private void updateCalendar() {
            grid.removeAll();
            
            String[] monthNames = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
            monthLabel.setText(monthNames[currentMonth] + " " + currentYear);
            
            for(String d : days) {
                JLabel dl = new JLabel(d, SwingConstants.CENTER);
                dl.setForeground(TEXT_DIM);
                dl.setFont(new Font("SansSerif", Font.PLAIN, 12));
                grid.add(dl);
            }
            
            Calendar cal = Calendar.getInstance();
            cal.set(currentYear, currentMonth, 1);
            int startDay = cal.get(Calendar.DAY_OF_WEEK) - 2; // Monday first
            if (startDay < 0) startDay = 6;
            
            int daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
            
            for(int i=0; i<42; i++) {
                if (i < startDay || i >= startDay + daysInMonth) {
                    grid.add(new JLabel(""));
                } else {
                    int dayNum = i - startDay + 1;
                    JPanel cell = new JPanel(new BorderLayout()) {
                        @Override protected void paintComponent(Graphics g) {
                            Graphics2D g2 = (Graphics2D) g.create();
                            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                            if (dayNum == selectedDay) {
                                g2.setColor(ACCENT_ORANGE);
                                g2.fillOval(getWidth()/2-12, getHeight()/2-12, 24, 24);
                            } else if ((dayNum == 16 || dayNum == 27) && currentMonth == Calendar.getInstance().get(Calendar.MONTH)) {
                                g2.setColor(new Color(138,43,226, 80));
                                g2.fillOval(getWidth()/2-12, getHeight()/2-12, 24, 24);
                            }
                            g2.dispose();
                            super.paintComponent(g);
                        }
                    };
                    cell.setOpaque(false);
                    cell.setCursor(new Cursor(Cursor.HAND_CURSOR));
                    cell.addMouseListener(new MouseAdapter() {
                        public void mouseClicked(MouseEvent e) {
                            selectedDay = dayNum;
                            grid.repaint();
                            JOptionPane.showMessageDialog(CalendarPanel.this, "Selected date: " + dayNum + " " + monthNames[currentMonth] + " " + currentYear);
                        }
                    });
                    
                    JLabel dNum = new JLabel(String.valueOf(dayNum), SwingConstants.CENTER);
                    dNum.setForeground((dayNum == selectedDay || dayNum == 16 || dayNum == 27) ? Color.WHITE : TEXT_DIM);
                    dNum.setFont(new Font("SansSerif", (dayNum == selectedDay) ? Font.BOLD : Font.PLAIN, 14));
                    cell.add(dNum, BorderLayout.CENTER);
                    grid.add(cell);
                }
            }
            grid.revalidate();
            grid.repaint();
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────
    private String getInitials(String name) {
        if (name == null || name.isBlank()) return "?";
        String[] parts = name.trim().split("\\s+");
        if (parts.length == 1) return parts[0].substring(0, Math.min(2, parts[0].length())).toUpperCase();
        return ("" + parts[0].charAt(0) + parts[parts.length-1].charAt(0)).toUpperCase();
    }

    // ── Shared inner classes ───────────────────────────────────────────────────

    /** Glassmorphism card */
    static class GlassCard extends JPanel {
        private final int radius;
        GlassCard(int radius) {
            this.radius = radius;
            setOpaque(false);
        }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(GLASS);
            g2.fillRoundRect(0,0,getWidth(),getHeight(),radius,radius);
            g2.setColor(GLASS_BD);
            g2.setStroke(new BasicStroke(1f));
            g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,radius,radius);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    /** Same animated blob background as LoginFrame */
    static class BlobBackground extends JPanel {
        private final List<float[]> blobs = new ArrayList<>();
        private final float[] dx, dy;
        private float[] bx, by;

        BlobBackground() {
            setBackground(BG_DARK);
            float[][] init = {
                {100, 100, 700, 38, 208, 206, 120},
                {620, 310, 800, 138, 43, 226, 100},
                {900, -60, 650, 30, 144, 255, 110},
            };
            bx  = new float[init.length];
            by  = new float[init.length];
            dx  = new float[]{2.5f, -1.8f, -2.5f};
            dy  = new float[]{1.8f, -2.5f,  1.5f};
            for (int i = 0; i < init.length; i++) {
                bx[i] = init[i][0]; by[i] = init[i][1];
                blobs.add(init[i]);
            }
            new Timer(16, e -> {
                for (int i = 0; i < blobs.size(); i++) {
                    bx[i] += dx[i]; by[i] += dy[i];
                    float r = blobs.get(i)[2];
                    if (bx[i] <= -r/2 || bx[i] >= getWidth()  - r/2) dx[i] *= -1;
                    if (by[i] <= -r/2 || by[i] >= getHeight() - r/2) dy[i] *= -1;
                }
                repaint();
            }).start();
        }

        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            for (int i = 0; i < blobs.size(); i++) {
                float[] b = blobs.get(i);
                float r = b[2];
                Color c = new Color((int)b[3],(int)b[4],(int)b[5],(int)b[6]);
                float[] dist  = {0f, 1f};
                Color[] cols  = {c, new Color(c.getRed(),c.getGreen(),c.getBlue(),0)};
                RadialGradientPaint rg = new RadialGradientPaint(
                    new Point2D.Float(bx[i]+r/2, by[i]+r/2), r/2, dist, cols);
                g2.setPaint(rg);
                g2.fillOval((int)bx[i],(int)by[i],(int)r,(int)r);
            }
            g2.dispose();
        }
    }
}