
package gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

public class LoginFrame extends JFrame {

    private final CardLayout formCardLayout = new CardLayout();
    private final JPanel formContainer = new JPanel(formCardLayout);

    private final JButton loginTab = new JButton("Login");
    private final JButton signupTab = new JButton("Sign Up");

    private final JLabel loginMsg = new JLabel(" ");
    private final JLabel signupMsg = new JLabel(" ");

    private JTextField loginIdentifierField;
    private JPasswordField loginPasswordField;

    private JTextField signupNameField;
    private JTextField signupUsernameField;
    private JTextField signupEmailField;
    private JPasswordField signupPasswordField;
    private JPasswordField signupConfirmField;
    private JTextField signupCollegeIdField;
    private JTextField signupDobField;

    private final List<User> users = new ArrayList<>();

    public LoginFrame() {
        setTitle("EXAMIFY - Login");
        setSize(1200, 720);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);

        AnimatedBackgroundPanel root = new AnimatedBackgroundPanel();
        root.setLayout(new GridLayout(1, 2));

        JPanel leftPanel = buildBrandPanel();
        JPanel rightPanel = buildAuthPanel();

        root.add(leftPanel);
        root.add(rightPanel);

        setContentPane(root);

        seedDemoUser();
        setMode("login");
        
        // DEV BYPASS
        Timer bypassTimer = new Timer(500, e -> {
            dispose();
            new StudentDashboard(users.get(0)).setVisible(true);
        });
        bypassTimer.setRepeats(false);
        bypassTimer.start();
    }

    private void seedDemoUser() {
        users.add(new User(
                "Niyati Jha",
                "niyati",
                "niyati@example.com",
                "123456",
                "UPES001",
                "2004-01-01"
        ));
    }

    private JPanel buildBrandPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);

        AnimatedBrandLabel brand = new AnimatedBrandLabel("EXAMIFY");
        brand.setForeground(Color.WHITE);
        brand.setFont(new Font("Serif", Font.BOLD, 60));

        JPanel wrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 40, 0));
        wrapper.setOpaque(false);
        wrapper.add(brand);

        panel.add(wrapper);
        return panel;
    }

    private JPanel buildAuthPanel() {
        JPanel outer = new JPanel(new GridBagLayout());
        outer.setOpaque(false);

        JPanel card = new RoundedPanel(28, new Color(255, 255, 255, 35));
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(25, 25, 25, 25));
        card.setPreferredSize(new Dimension(430, 560));

        JPanel tabPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 18, 0));
        tabPanel.setOpaque(false);

        styleTabButton(loginTab);
        styleTabButton(signupTab);

        loginTab.addActionListener(e -> setMode("login"));
        signupTab.addActionListener(e -> setMode("signup"));

        tabPanel.add(loginTab);
        tabPanel.add(signupTab);

        formContainer.setOpaque(false);
        formContainer.add(buildLoginForm(), "login");
        formContainer.add(buildSignupForm(), "signup");

        card.add(tabPanel, BorderLayout.NORTH);
        card.add(formContainer, BorderLayout.CENTER);

        outer.add(card);
        return outer;
    }

    private JPanel buildLoginForm() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(20, 0, 0, 0));

        loginMsg.setForeground(new Color(255, 150, 170));
        loginMsg.setAlignmentX(Component.LEFT_ALIGNMENT);

        loginIdentifierField = createTextField();
        loginPasswordField = createPasswordField();

        panel.add(loginMsg);
        panel.add(Box.createVerticalStrut(8));
        panel.add(createField("Username / Email", loginIdentifierField));
        panel.add(Box.createVerticalStrut(12));
        panel.add(createField("Password", loginPasswordField));
        panel.add(Box.createVerticalStrut(18));

        JButton loginButton = createPrimaryButton("Login");
        loginButton.addActionListener(this::handleLogin);

        JButton switchButton = createLinkButton("Don’t have an account? Sign up");
        switchButton.addActionListener(e -> setMode("signup"));

        panel.add(loginButton);
        panel.add(Box.createVerticalStrut(14));
        panel.add(switchButton);

        return panel;
    }

    private JPanel buildSignupForm() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(20, 0, 0, 0));

        signupMsg.setForeground(new Color(255, 150, 170));
        signupMsg.setAlignmentX(Component.LEFT_ALIGNMENT);

        signupNameField = createTextField();
        signupUsernameField = createTextField();
        signupEmailField = createTextField();
        signupPasswordField = createPasswordField();
        signupConfirmField = createPasswordField();
        signupCollegeIdField = createTextField();
        signupDobField = createTextField();

        panel.add(signupMsg);
        panel.add(Box.createVerticalStrut(8));
        panel.add(createField("Full Name", signupNameField));
        panel.add(Box.createVerticalStrut(10));
        panel.add(createField("Username", signupUsernameField));
        panel.add(Box.createVerticalStrut(10));
        panel.add(createField("Email", signupEmailField));
        panel.add(Box.createVerticalStrut(10));
        panel.add(createField("Password", signupPasswordField));
        panel.add(Box.createVerticalStrut(10));
        panel.add(createField("Confirm Password", signupConfirmField));
        panel.add(Box.createVerticalStrut(10));
        panel.add(createField("College ID", signupCollegeIdField));
        panel.add(Box.createVerticalStrut(10));
        panel.add(createField("Date of Birth (yyyy-mm-dd)", signupDobField));
        panel.add(Box.createVerticalStrut(16));

        JButton signupButton = createPrimaryButton("Sign Up");
        signupButton.addActionListener(this::handleSignup);

        JButton switchButton = createLinkButton("Already have an account? Login");
        switchButton.addActionListener(e -> setMode("login"));

        panel.add(signupButton);
        panel.add(Box.createVerticalStrut(12));
        panel.add(switchButton);

        return panel;
    }

    private JPanel createField(String labelText, JComponent field) {
        JPanel wrapper = new JPanel();
        wrapper.setOpaque(false);
        wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS));
        wrapper.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel label = new JLabel(labelText);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("SansSerif", Font.BOLD, 14));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        field.setAlignmentX(Component.LEFT_ALIGNMENT);

        wrapper.add(label);
        wrapper.add(Box.createVerticalStrut(7));
        wrapper.add(field);
        return wrapper;
    }

    private JTextField createTextField() {
        JTextField field = new JTextField();
        styleField(field);
        return field;
    }

    private JPasswordField createPasswordField() {
        JPasswordField field = new JPasswordField();
        styleField(field);
        return field;
    }

    private void styleField(JComponent comp) {
        comp.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        comp.setPreferredSize(new Dimension(340, 46));
        comp.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 230, 255, 80), 1),
                new EmptyBorder(10, 12, 10, 12)
        ));
        comp.setBackground(new Color(255, 255, 255, 35));
        comp.setForeground(Color.WHITE);
        comp.setFont(new Font("SansSerif", Font.PLAIN, 14));
    }

    private JButton createPrimaryButton(String text) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setForeground(new Color(35, 24, 71));
        button.setBackground(new Color(183, 171, 255));
        button.setFont(new Font("SansSerif", Font.BOLD, 16));
        button.setBorder(new EmptyBorder(12, 20, 12, 20));
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private JButton createLinkButton(String text) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setForeground(new Color(220, 213, 255));
        button.setFont(new Font("SansSerif", Font.BOLD, 13));
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void styleTabButton(JButton button) {
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFont(new Font("SansSerif", Font.BOLD, 18));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void setMode(String mode) {
        boolean isLogin = mode.equals("login");
        formCardLayout.show(formContainer, mode);

        loginTab.setForeground(isLogin ? Color.WHITE : new Color(209, 200, 255));
        signupTab.setForeground(!isLogin ? Color.WHITE : new Color(209, 200, 255));

        loginMsg.setText(" ");
        signupMsg.setText(" ");
    }

    private void handleSignup(ActionEvent e) {
        String fullName = signupNameField.getText().trim();
        String username = signupUsernameField.getText().trim();
        String email = signupEmailField.getText().trim().toLowerCase();
        String password = new String(signupPasswordField.getPassword());
        String confirm = new String(signupConfirmField.getPassword());
        String collegeId = signupCollegeIdField.getText().trim();
        String dob = signupDobField.getText().trim();

        if (fullName.isEmpty() || username.isEmpty() || email.isEmpty() || password.isEmpty()
                || confirm.isEmpty() || collegeId.isEmpty() || dob.isEmpty()) {
            setError(signupMsg, "Please fill all fields.");
            return;
        }

        if (!email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            setError(signupMsg, "Enter a valid email address.");
            return;
        }

        if (password.length() < 6) {
            setError(signupMsg, "Password must be at least 6 characters.");
            return;
        }

        if (!password.equals(confirm)) {
            setError(signupMsg, "Passwords do not match.");
            return;
        }

        for (User u : users) {
            if (u.email.equalsIgnoreCase(email)) {
                setError(signupMsg, "Email already exists.");
                return;
            }
            if (u.username.equalsIgnoreCase(username)) {
                setError(signupMsg, "Username already exists.");
                return;
            }
            if (u.collegeId.equalsIgnoreCase(collegeId)) {
                setError(signupMsg, "College ID already exists.");
                return;
            }
        }

        users.add(new User(fullName, username, email, password, collegeId, dob));
        setSuccess(signupMsg, "Signup successful. You can login now.");

        signupNameField.setText("");
        signupUsernameField.setText("");
        signupEmailField.setText("");
        signupPasswordField.setText("");
        signupConfirmField.setText("");
        signupCollegeIdField.setText("");
        signupDobField.setText("");

        Timer timer = new Timer(900, evt -> setMode("login"));
        timer.setRepeats(false);
        timer.start();
    }

    private void handleLogin(ActionEvent e) {
        String identifier = loginIdentifierField.getText().trim().toLowerCase();
        String password = new String(loginPasswordField.getPassword());

        if (identifier.isEmpty() || password.isEmpty()) {
            setError(loginMsg, "Enter username/email and password.");
            return;
        }

        User found = null;
        for (User u : users) {
            if ((u.email.equalsIgnoreCase(identifier) || u.username.equalsIgnoreCase(identifier))
                    && u.password.equals(password)) {
                found = u;
                break;
            }
        }

        if (found == null) {
            setError(loginMsg, "Invalid login credentials.");
            return;
        }

        setSuccess(loginMsg, "Welcome back, " + found.fullName + "!");

        User finalFound = found;
        Timer timer = new Timer(500, evt -> {
            dispose();
            new StudentDashboard(finalFound).setVisible(true);
        });
        timer.setRepeats(false);
        timer.start();
    }

    private void setError(JLabel label, String text) {
        label.setForeground(new Color(255, 142, 168));
        label.setText(text);
    }

    private void setSuccess(JLabel label, String text) {
        label.setForeground(new Color(116, 224, 187));
        label.setText(text);
    }

    static class User {
        String fullName;
        String username;
        String email;
        String password;
        String collegeId;
        String dob;

        User(String fullName, String username, String email, String password, String collegeId, String dob) {
            this.fullName = fullName;
            this.username = username;
            this.email = email;
            this.password = password;
            this.collegeId = collegeId;
            this.dob = dob;
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

    static class AnimatedBackgroundPanel extends JPanel {
        private final List<Particle> particles = new ArrayList<>();
        private final Timer timer;

        AnimatedBackgroundPanel() {
            setOpaque(true);
            setBackground(new Color(21, 48, 83));

            for (int i = 0; i < 120; i++) {
                particles.add(new Particle());
            }

            timer = new Timer(30, e -> {
                for (Particle p : particles) {
                    p.update(getWidth(), getHeight());
                }
                repaint();
            });
            timer.start();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            GradientPaint gp = new GradientPaint(
                    0, 0, new Color(21, 48, 83),
                    getWidth(), getHeight(), new Color(48, 95, 145)
            );
            g2.setPaint(gp);
            g2.fillRect(0, 0, getWidth(), getHeight());

            g2.setColor(new Color(180, 167, 255, 30));
            g2.fillOval(60, 60, 250, 250);

            g2.setColor(new Color(126, 220, 223, 25));
            g2.fillOval(getWidth() - 300, getHeight() - 280, 230, 230);

            for (Particle p : particles) {
                p.draw(g2);
            }

            g2.dispose();
        }
    }

    static class Particle {
        double x = Math.random() * 1200;
        double y = Math.random() * 800;
        double vx = -1 + Math.random() * 2;
        double vy = -1 + Math.random() * 2;
        int size = 3 + (int) (Math.random() * 6);
        Color color = new Color(220, 230, 255, 120);

        void update(int w, int h) {
            x += vx * 0.5;
            y += vy * 0.5;

            if (x < -20) x = w + 20;
            if (x > w + 20) x = -20;
            if (y < -20) y = h + 20;
            if (y > h + 20) y = -20;
        }

        void draw(Graphics2D g2) {
            g2.setColor(color);
            g2.fillOval((int) x, (int) y, size, size);
        }
    }

    static class AnimatedBrandLabel extends JLabel {
        private int phase = 0;
        private int alpha = 255;

        AnimatedBrandLabel(String text) {
            super(text);
            Timer timer = new Timer(90, e -> {
                phase = (phase + 1) % 80;
                int wave = phase % 40;
                alpha = wave < 20 ? 150 + wave * 5 : 250 - (wave - 20) * 5;
                repaint();
            });
            timer.start();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.setFont(getFont());

            String text = getText();
            FontMetrics fm = g2.getFontMetrics();
            int x = 0;
            int y = fm.getAscent() + 10;

            g2.setColor(new Color(180, 167, 255, 70));
            g2.drawString(text, x + 4, y + 4);

            g2.setColor(new Color(255, 255, 255, alpha));
            g2.drawString(text, x, y);

            g2.dispose();
        }

        @Override
        public Dimension getPreferredSize() {
            FontMetrics fm = getFontMetrics(getFont());
            return new Dimension(fm.stringWidth(getText()) + 20, fm.getHeight() + 20);
        }
    }
}