
package gui;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.HashMap;
import java.util.Random;
import javax.swing.*;
import javax.swing.border.*;

public class RegisterFrame extends JFrame {
    // Color constants (match LoginFrame)
    private static final Color PRIMARY = new Color(18, 32, 47);
    private static final Color ACCENT = new Color(52, 152, 219);
    private static final Color CYAN_GLOW = new Color(0, 255, 255, 80);
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 38);
    private static final Font SUBTITLE_FONT = new Font("Segoe UI", Font.PLAIN, 18);
    private static final Font LABEL_FONT = new Font("Segoe UI", Font.PLAIN, 17);
    private static final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 17);
    private static final Font BRAND_BG_FONT = new Font("Segoe UI", Font.BOLD, 120);

    private JTextField nameField, emailField, dobField, studentIdField;
    private JPasswordField passwordField;
    private JButton saveBtn, proceedBtn, backBtn;
    private JLabel errorLabel;
    private LoginFrame.StudentData registeredStudent = null;

    public RegisterFrame() {
        setTitle("EXAMIFY – Student Registration");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1200, 800);
        setMinimumSize(new Dimension(1000, 700));
        setLocationRelativeTo(null);
        setResizable(true);

        setLayout(new BorderLayout());

        // Animated background panel
        AnimatedBackgroundPanel bgPanel = new AnimatedBackgroundPanel();
        bgPanel.setLayout(new BorderLayout());
        setContentPane(bgPanel);

        // Main split layout
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setOpaque(false);
        bgPanel.add(mainPanel, BorderLayout.CENTER);

        // Left: Registration card panel
        JPanel leftPanel = new JPanel(new GridBagLayout());
        leftPanel.setOpaque(false);
        leftPanel.setPreferredSize(new Dimension(480, 100));
        mainPanel.add(leftPanel, BorderLayout.WEST);

        GlassCardPanel card = new GlassCardPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setPreferredSize(new Dimension(420, 600));
        card.setMaximumSize(new Dimension(480, 700));
        card.setAlignmentY(Component.CENTER_ALIGNMENT);

        // Branding
        JLabel logo = new JLabel("EXAMIFY");
        logo.setFont(TITLE_FONT);
        logo.setForeground(ACCENT);
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);
        logo.setBorder(new EmptyBorder(18, 0, 0, 0));

        JLabel tagline = new JLabel("Examify – Online Quiz & Assessment Platform");
        tagline.setFont(SUBTITLE_FONT);
        tagline.setForeground(new Color(60, 60, 60));
        tagline.setAlignmentX(Component.CENTER_ALIGNMENT);
        tagline.setBorder(new EmptyBorder(0, 0, 18, 0));

        card.add(logo);
        card.add(tagline);

        JLabel regTitle = new JLabel("Create Student Account");
        regTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        regTitle.setForeground(PRIMARY);
        regTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        regTitle.setBorder(new EmptyBorder(0, 0, 18, 0));
        card.add(regTitle);

        // Registration form
        JPanel formPanel = new JPanel();
        formPanel.setOpaque(false);
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        formPanel.add(buildFormRow("Full Name", nameField = new JTextField()));
        formPanel.add(buildFormRow("Email", emailField = new JTextField()));
        formPanel.add(buildFormRow("Password", passwordField = new JPasswordField()));
        formPanel.add(buildFormRow("Date of Birth (YYYY-MM-DD)", dobField = new JTextField()));
        formPanel.add(buildFormRow("Student ID", studentIdField = new JTextField()));

        card.add(formPanel);

        card.add(Box.createVerticalStrut(10));

        errorLabel = new JLabel(" ");
        errorLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        errorLabel.setForeground(new Color(192, 57, 43));
        errorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(errorLabel);

        card.add(Box.createVerticalStrut(10));

        JPanel btnPanel = new JPanel();
        btnPanel.setOpaque(false);
        btnPanel.setLayout(new BoxLayout(btnPanel, BoxLayout.X_AXIS));

        saveBtn = createStyledButton("Save");
        proceedBtn = createStyledButton("Proceed");
        proceedBtn.setEnabled(false);
        backBtn = createStyledButton("Back to Login");

        btnPanel.add(saveBtn);
        btnPanel.add(Box.createHorizontalStrut(12));
        btnPanel.add(proceedBtn);
        btnPanel.add(Box.createHorizontalStrut(12));
        btnPanel.add(backBtn);

        card.add(btnPanel);
        card.add(Box.createVerticalStrut(16));

        leftPanel.add(card);

        // Responsive layout on resize
        addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                bgPanel.repaint();
            }
        });

        // Action listeners
        saveBtn.addActionListener(e -> handleSave());
        proceedBtn.addActionListener(e -> handleProceed());
        backBtn.addActionListener(e -> handleBack());

        setVisible(true);
    }

    private JPanel buildFormRow(String label, JComponent field) {
        JPanel row = new JPanel();
        row.setLayout(new BoxLayout(row, BoxLayout.Y_AXIS));
        row.setOpaque(false);
        JLabel lbl = new JLabel(label);
        lbl.setFont(LABEL_FONT);
        field.setFont(LABEL_FONT);
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        field.setPreferredSize(new Dimension(320, 44));
        field.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(200, 200, 200), 1, true),
                new EmptyBorder(8, 16, 8, 16)));
        if (field instanceof JTextField) {
            field.setBackground(new Color(245, 245, 245));
        }
        row.add(lbl);
        row.add(field);
        row.add(Box.createVerticalStrut(10));
        return row;
    }

    private JButton createStyledButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(BUTTON_FONT);
        btn.setBackground(ACCENT);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(new LineBorder(ACCENT, 1, true));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(140, 44));
        btn.setMaximumSize(new Dimension(180, 44));
        btn.setMinimumSize(new Dimension(100, 44));
        btn.setOpaque(true);
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(41, 128, 185));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(ACCENT);
            }
        });
        return btn;
    }

    private void handleSave() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim().toLowerCase();
        String password = new String(passwordField.getPassword());
        String dob = dobField.getText().trim();
        String studentId = studentIdField.getText().trim();

        // Validation
        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || dob.isEmpty() || studentId.isEmpty()) {
            errorLabel.setText("Please fill in all fields.");
            return;
        }
        if (!isValidEmail(email)) {
            errorLabel.setText("Please enter a valid email address.");
            return;
        }
        if (password.length() < 6) {
            errorLabel.setText("Password must be at least 6 characters.");
            return;
        }
        if (!isValidDOB(dob)) {
            errorLabel.setText("Date of Birth must be in YYYY-MM-DD format.");
            return;
        }
        // Check for duplicate email or student ID
        HashMap<String, LoginFrame.StudentData> store = LoginFrame.studentStore;
        if (store.containsKey(email)) {
            errorLabel.setText("This email is already registered.");
            return;
        }
        for (LoginFrame.StudentData s : store.values()) {
            if (s.studentId.equalsIgnoreCase(studentId)) {
                errorLabel.setText("This student ID is already registered.");
                return;
            }
        }
        // Save student
        registeredStudent = new LoginFrame.StudentData(name, email, password, dob, studentId);
        store.put(email, registeredStudent);
        JOptionPane.showMessageDialog(this, "Registration successful! You may now proceed.", "Success", JOptionPane.INFORMATION_MESSAGE);
        proceedBtn.setEnabled(true);
        saveBtn.setEnabled(false);
        emailField.setEditable(false);
        studentIdField.setEditable(false);
        errorLabel.setText(" ");
    }

    private void handleProceed() {
        if (registeredStudent == null) {
            errorLabel.setText("Please complete registration first.");
            return;
        }
        this.dispose();
        new gui.StudentDashboard(registeredStudent);
    }

    private void handleBack() {
        this.dispose();
        new gui.LoginFrame();
    }

    private boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    }

    private boolean isValidDOB(String dob) {
        return dob.matches("^\\d{4}-\\d{2}-\\d{2}$");
    }

    /**
     * Animated background panel with gradient, particles, and branding.
     * (Copied from LoginFrame for visual consistency)
     */
    private static class AnimatedBackgroundPanel extends JPanel {
        private static final int PARTICLE_COUNT = 18;
        private final Particle[] particles;
        private final Timer timer;
        private float wavePhase = 0f;
        private final Random rand = new Random();

        public AnimatedBackgroundPanel() {
            setOpaque(true);
            particles = new Particle[PARTICLE_COUNT];
            for (int i = 0; i < PARTICLE_COUNT; i++) {
                particles[i] = new Particle();
            }
            timer = new Timer(32, e -> {
                for (Particle p : particles) p.move(getWidth(), getHeight());
                wavePhase += 0.012f;
                repaint();
            });
            timer.start();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Animated gradient background
            int w = getWidth(), h = getHeight();
            float t = (System.currentTimeMillis() % 8000) / 8000f;
            Color grad1 = new Color(18, 32, 47);
            Color grad2 = new Color(36, 123, 160);
            Color grad3 = new Color(0, 210, 255);
            GradientPaint gp = new GradientPaint(0, 0, grad1, w, h, grad2);
            g2.setPaint(gp);
            g2.fillRect(0, 0, w, h);

            // Animated wave
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.18f));
            g2.setColor(new Color(0, 210, 255));
            Path2D.Float wave = new Path2D.Float();
            wave.moveTo(0, h * 0.7);
            for (int x = 0; x <= w; x += 8) {
                double y = h * 0.7 + Math.sin((x / 180.0) + wavePhase) * 28 + Math.cos((x / 90.0) + wavePhase * 1.5) * 12;
                wave.lineTo(x, y);
            }
            wave.lineTo(w, h);
            wave.lineTo(0, h);
            wave.closePath();
            g2.fill(wave);

            // Animated particles
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.22f));
            for (Particle p : particles) {
                g2.setColor(new Color(0, 255, 255, 90 + rand.nextInt(60)));
                g2.fillOval((int) p.x, (int) p.y, (int) p.size, (int) p.size);
            }

            // Large faint EXAMIFY branding
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.10f));
            g2.setFont(BRAND_BG_FONT);
            FontMetrics fm = g2.getFontMetrics();
            String brand = "EXAMIFY";
            int strW = fm.stringWidth(brand);
            int strH = fm.getAscent();
            g2.setColor(Color.WHITE);
            g2.drawString(brand, (w - strW) / 2, (int) (h * 0.42));
        }

        private static class Particle {
            float x, y, vx, vy, size;
            private final Random rand = new Random();
            public Particle() {
                reset(1440, 900);
            }
            public void reset(int w, int h) {
                x = rand.nextInt(w);
                y = rand.nextInt(h);
                vx = (rand.nextFloat() - 0.5f) * 1.2f;
                vy = (rand.nextFloat() - 0.5f) * 1.2f;
                size = 38 + rand.nextFloat() * 32;
            }
            public void move(int w, int h) {
                x += vx;
                y += vy;
                if (x < -size || x > w + size || y < -size || y > h + size) reset(w, h);
            }
        }
    }

    /**
     * Glassmorphism card panel for registration UI.
     */
    private static class GlassCardPanel extends JPanel {
        public GlassCardPanel() {
            setOpaque(false);
            setBorder(new CompoundBorder(
                    new LineBorder(new Color(255,255,255,80), 2, true),
                    new EmptyBorder(24, 32, 32, 32)));
        }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth(), h = getHeight();
            // Glass effect
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.60f));
            g2.setColor(new Color(255,255,255,230));
            g2.fillRoundRect(0, 0, w, h, 36, 36);
            // Shadow
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.18f));
            g2.setColor(new Color(0,0,0,180));
            g2.fillRoundRect(8, 8, w-16, h-16, 36, 36);
            g2.dispose();
            super.paintComponent(g);
        }
    }
}
