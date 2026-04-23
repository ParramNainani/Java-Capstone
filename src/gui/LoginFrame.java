package gui;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import dao.UserDAO;
import model.User;

public class LoginFrame extends JFrame {
    private final CardLayout leftCardLayout = new CardLayout();
    private final JPanel leftContainer = new JPanel(leftCardLayout);
    
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
    
    private String selectedRole = "Student";

    public LoginFrame() {
        setTitle("EXAMIFY - Login");
        setSize(1200, 720);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);
        
        AnimatedBackground root = new AnimatedBackground();
        root.setLayout(new GridLayout(1, 2));
        
        leftContainer.setOpaque(false);
        leftContainer.add(buildLandingCard(), "landing");
        leftContainer.add(buildAuthCard(), "auth");
        
        JPanel rightPanel = buildIllustrationPanel();
        
        root.add(leftContainer);
        root.add(rightPanel);
        
        setContentPane(root);
        
        leftCardLayout.show(leftContainer, "landing");
    }
    
    private JPanel buildLandingCard() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(60, 60, 60, 40));
        
        JLabel brand = new JLabel("EXAMIFY");
        brand.setFont(new Font("SansSerif", Font.BOLD, 56));
        brand.setForeground(Color.WHITE);
        panel.add(brand, BorderLayout.NORTH);
        
        JPanel center = new JPanel(new GridBagLayout());
        center.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        
        // Top Glue for vertical centering
        gbc.weighty = 1.0;
        center.add(Box.createVerticalGlue(), gbc);
        
        gbc.gridy++;
        gbc.weighty = 0.0; // Reset weight
        gbc.insets = new Insets(10, 0, 5, 0);
        
        JLabel title = new JLabel("Assessment Portal");
        title.setFont(new Font("SansSerif", Font.BOLD, 42));
        title.setForeground(Color.WHITE);
        center.add(title, gbc);
        
        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 25, 0);
        JLabel subtitle = new JLabel("Please select your role to continue");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 18));
        subtitle.setForeground(new Color(200, 210, 230));
        center.add(subtitle, gbc);
        
        gbc.gridy++;
        gbc.insets = new Insets(10, 0, 15, 0);
        AnimatedButton studentBtn = new AnimatedButton("Student", new Color(38, 208, 206), new Color(60, 230, 225));
        studentBtn.setPreferredSize(new Dimension(320, 55));
        studentBtn.setForeground(new Color(10, 15, 30));
        studentBtn.setFont(new Font("SansSerif", Font.BOLD, 20));
        studentBtn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        studentBtn.addActionListener(e -> showAuth("Student"));
        center.add(studentBtn, gbc);
        
        gbc.gridy++;
        gbc.insets = new Insets(5, 0, 10, 0);
        AnimatedButton teacherBtn = new AnimatedButton("Teacher", new Color(255, 255, 255, 40), new Color(255, 255, 255, 70));
        teacherBtn.setPreferredSize(new Dimension(320, 55));
        teacherBtn.setForeground(Color.WHITE);
        teacherBtn.setFont(new Font("SansSerif", Font.BOLD, 20));
        teacherBtn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        teacherBtn.addActionListener(e -> showAuth("Teacher"));
        center.add(teacherBtn, gbc);
        
        // Bottom Glue for vertical centering
        gbc.gridy++;
        gbc.weighty = 1.0;
        center.add(Box.createVerticalGlue(), gbc);
        
        panel.add(center, BorderLayout.CENTER);
        return panel;
    }
    
    private void showAuth(String role) {
        selectedRole = role;
        setMode("login");
        // Fade animation simulation via CardLayout standard switch (fast but clean)
        leftCardLayout.show(leftContainer, "auth");
    }
    
    private JPanel buildAuthCard() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(40, 40, 40, 40));
        
        AnimatedButton backBtn = new AnimatedButton("← Back", new Color(0, 0, 0, 0), new Color(255, 255, 255, 40));
        backBtn.setForeground(Color.WHITE);
        backBtn.setFont(new Font("SansSerif", Font.BOLD, 16));
        backBtn.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        backBtn.setHorizontalAlignment(SwingConstants.LEFT);
        backBtn.addActionListener(e -> leftCardLayout.show(leftContainer, "landing"));
        
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setOpaque(false);
        topPanel.add(backBtn);
        panel.add(topPanel, BorderLayout.NORTH);
        
        JPanel center = new JPanel(new GridBagLayout());
        center.setOpaque(false);
        
        JPanel authBox = new JPanel(new BorderLayout());
        authBox.setOpaque(false);
        authBox.setPreferredSize(new Dimension(400, 630));
        
        JPanel tabs = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
        tabs.setOpaque(false);
        styleTabButton(loginTab);
        styleTabButton(signupTab);
        loginTab.addActionListener(e -> setMode("login"));
        signupTab.addActionListener(e -> setMode("signup"));
        tabs.add(loginTab);
        tabs.add(signupTab);
        
        formContainer.setOpaque(false);
        formContainer.add(buildLoginForm(), "login");
        formContainer.add(buildSignupForm(), "signup");
        
        authBox.add(tabs, BorderLayout.NORTH);
        authBox.add(formContainer, BorderLayout.CENTER);
        
        center.add(authBox);
        panel.add(center, BorderLayout.CENTER);
        
        return panel;
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

        AnimatedButton loginButton = new AnimatedButton("Login", Color.WHITE, new Color(220, 230, 255));
        loginButton.setForeground(new Color(30, 40, 60));
        loginButton.setFont(new Font("SansSerif", Font.BOLD, 16));
        loginButton.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        loginButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        loginButton.addActionListener(this::handleLogin);

        JButton switchButton = createLinkButton("Don't have an account? Sign up");
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
        panel.add(createField("Date of Birth", signupDobField));
        panel.add(Box.createVerticalStrut(16));

        AnimatedButton signupButton = new AnimatedButton("Sign Up", Color.WHITE, new Color(220, 230, 255));
        signupButton.setForeground(new Color(30, 40, 60));
        signupButton.setFont(new Font("SansSerif", Font.BOLD, 16));
        signupButton.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        signupButton.setAlignmentX(Component.LEFT_ALIGNMENT);
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
        comp.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        comp.setPreferredSize(new Dimension(340, 40));
        // Add a subtle drop shadow-like border by layering CompoundBorders
        comp.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(255, 255, 255, 120), 1),
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(0, 0, 0, 20), 2),
                        new EmptyBorder(6, 12, 6, 12)
                )
        ));
        comp.setBackground(new Color(255, 255, 255, 30));
        comp.setForeground(Color.WHITE);
        comp.setFont(new Font("SansSerif", Font.PLAIN, 15));
        comp.setOpaque(true);
        // Make caret visible (white) when user starts typing
        if (comp instanceof JTextField) {
            ((JTextField) comp).setCaretColor(Color.WHITE);
        }
    }

    private JButton createLinkButton(String text) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setForeground(new Color(220, 230, 255));
        button.setFont(new Font("SansSerif", Font.BOLD, 13));
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void styleTabButton(JButton button) {
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFont(new Font("SansSerif", Font.BOLD, 22));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void setMode(String mode) {
        boolean isLogin = mode.equals("login");
        formCardLayout.show(formContainer, mode);
        loginTab.setForeground(isLogin ? Color.WHITE : new Color(255, 255, 255, 120));
        signupTab.setForeground(!isLogin ? Color.WHITE : new Color(255, 255, 255, 120));
        loginMsg.setText(" ");
        signupMsg.setText(" ");
    }
    
    private BufferedImage makeTransparentBackground(BufferedImage img) {
        BufferedImage newImg = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                int argb = img.getRGB(x, y);
                int r = (argb >> 16) & 0xff;
                int g = (argb >> 8) & 0xff;
                int b = argb & 0xff;
                // Make the light grey / white checkerboard squares transparent
                // The AI checkerboard and white edges need an aggressive threshold
                // to blend cleanly with the dark animated background
                if (r > 175 && g > 175 && b > 175) {
                    newImg.setRGB(x, y, 0x00FFFFFF); // fully transparent
                } else {
                    newImg.setRGB(x, y, argb);
                }
            }
        }
        return newImg;
    }
    
    private JPanel buildIllustrationPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        
        try {
            // Try multiple paths to find the image regardless of working directory
            String[] candidatePaths = {
                "gui/illustration.png",
                "src/gui/illustration.png",
                "illustration.png",
                "/Users/niyatijha/Desktop/Java-Capstone/src/gui/illustration.png"
            };
            // Also try relative to the class file location
            File classFile = new File(LoginFrame.class.getProtectionDomain()
                    .getCodeSource().getLocation().toURI());
            File classDir = classFile.isDirectory() ? classFile : classFile.getParentFile();
            candidatePaths = java.util.Arrays.copyOf(candidatePaths, candidatePaths.length + 2);
            candidatePaths[candidatePaths.length - 2] = new File(classDir, "illustration.png").getAbsolutePath();
            candidatePaths[candidatePaths.length - 1] = new File(classDir, "gui/illustration.png").getAbsolutePath();

            File imgFile = null;
            for (String path : candidatePaths) {
                File f = new File(path);
                if (f.exists()) { imgFile = f; break; }
            }
            if(imgFile != null) {
                BufferedImage originalImg = ImageIO.read(imgFile);
                BufferedImage transparentImg = makeTransparentBackground(originalImg);
                
                JPanel imgContainer = new JPanel(new BorderLayout()) {
                    @Override
                    protected void paintComponent(Graphics g) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        // Draw a soft white glowing aura to backlight the black formulas
                        float[] dist = {0.0f, 1.0f};
                        Color[] colors = {new Color(255, 255, 255, 140), new Color(255, 255, 255, 0)};
                        RadialGradientPaint p = new RadialGradientPaint(
                                new Point(getWidth()/2, getHeight()/2), 
                                getWidth()/2.2f, dist, colors);
                        g2.setPaint(p);
                        g2.fillOval(30, 30, getWidth()-60, getHeight()-60);
                        g2.dispose();
                        super.paintComponent(g);
                    }
                };
                imgContainer.setOpaque(false);
                
                JLabel picLabel = new JLabel(new ImageIcon(transparentImg.getScaledInstance(750, 750, Image.SCALE_SMOOTH)));
                imgContainer.add(picLabel, BorderLayout.CENTER);
                panel.add(imgContainer, BorderLayout.CENTER);
            } else {
                JLabel placeholder = new JLabel("Illustration Missing", SwingConstants.CENTER);
                placeholder.setForeground(Color.WHITE);
                panel.add(placeholder, BorderLayout.CENTER);
            }
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
        return panel;
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
        UserDAO dao = new UserDAO();
        User existingUsername = dao.getUserByUsername(username);
        if (existingUsername != null) {
            setError(signupMsg, "Username already exists.");
            return;
        }

        User newUser = new User(username, password, email);
        newUser.setRole(selectedRole); // Use the role selected on the landing screen

        boolean success = dao.insertUser(newUser);
        if (success) {
            setSuccess(signupMsg, "Signup successful. You can login now.");
            Timer timer = new Timer(900, evt -> setMode("login"));
            timer.setRepeats(false);
            timer.start();
        } else {
            setError(signupMsg, "Database error during signup.");
        }
    }

    private void handleLogin(ActionEvent e) {
        String identifier = loginIdentifierField.getText().trim().toLowerCase();
        String password = new String(loginPasswordField.getPassword());

        if (identifier.isEmpty() || password.isEmpty()) {
            setError(loginMsg, "Enter username/email and password.");
            return;
        }

        UserDAO dao = new UserDAO();
        User found = dao.getUserByUsername(identifier);
        
        if (found == null || !found.getPassword().equals(password)) {
            setError(loginMsg, "Invalid login credentials.");
            return;
        }

        setSuccess(loginMsg, "Welcome back, " + found.getUsername() + "!");

        User finalFound = found;
        Timer timer = new Timer(500, evt -> {
            dispose();
            if (selectedRole.equals("Teacher")) {
                new TeacherDashboard(finalFound).setVisible(true);
            } else {
                new StudentDashboard(finalFound).setVisible(true);
            }
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

    // Removed Local File Storage Persistence logic and User class

    static class AnimatedBackground extends JPanel {
        private final List<Blob> blobs = new ArrayList<>();
        private final Timer timer;

        public AnimatedBackground() {
            // Dark professional base background
            setBackground(new Color(10, 15, 30));
            
            // Add 3 softly glowing animated blobs with increased speed
            blobs.add(new Blob(100, 100, 2.5f, 1.8f, 700, new Color(38, 208, 206, 120))); // Cyan
            blobs.add(new Blob(600, 300, -1.8f, -2.5f, 800, new Color(138, 43, 226, 100))); // Purple
            blobs.add(new Blob(800, -50, -2.5f, 1.5f, 650, new Color(30, 144, 255, 110))); // Blue

            timer = new Timer(16, e -> { // ~60 FPS for smoother, faster animation
                for (Blob b : blobs) {
                    b.update(getWidth(), getHeight());
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

            for (Blob b : blobs) {
                float[] dist = {0.0f, 1.0f};
                Color[] colors = {b.color, new Color(b.color.getRed(), b.color.getGreen(), b.color.getBlue(), 0)};
                RadialGradientPaint p = new RadialGradientPaint(
                        new Point((int)(b.x + b.radius/2), (int)(b.y + b.radius/2)), 
                        b.radius/2, dist, colors);
                g2.setPaint(p);
                g2.fillOval((int)b.x, (int)b.y, (int)b.radius, (int)b.radius);
            }
            g2.dispose();
        }

        private static class Blob {
            float x, y, dx, dy, radius;
            Color color;
            Blob(float x, float y, float dx, float dy, float radius, Color color) {
                this.x = x; this.y = y; this.dx = dx; this.dy = dy;
                this.radius = radius; this.color = color;
            }
            void update(int width, int height) {
                x += dx; y += dy;
                // Bounce off edges gently
                if (x <= -radius/2 || x >= width - radius/2) dx *= -1;
                if (y <= -radius/2 || y >= height - radius/2) dy *= -1;
            }
        }
    }
    
    // Custom button with hover animation
    static class AnimatedButton extends JButton {
        private Color normalColor;
        private Color hoverColor;
        private float hoverFraction = 0f;
        private Timer animationTimer;
        private boolean isHovered = false;

        AnimatedButton(String text, Color normalColor, Color hoverColor) {
            super(text);
            this.normalColor = normalColor;
            this.hoverColor = hoverColor;
            
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            
            animationTimer = new Timer(15, e -> {
                if (isHovered && hoverFraction < 1f) {
                    hoverFraction += 0.1f;
                    if (hoverFraction > 1f) hoverFraction = 1f;
                    repaint();
                } else if (!isHovered && hoverFraction > 0f) {
                    hoverFraction -= 0.1f;
                    if (hoverFraction < 0f) hoverFraction = 0f;
                    repaint();
                } else {
                    animationTimer.stop();
                }
            });

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent evt) {
                    isHovered = true;
                    animationTimer.start();
                }
                @Override
                public void mouseExited(MouseEvent evt) {
                    isHovered = false;
                    animationTimer.start();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int r = (int)(normalColor.getRed() + (hoverColor.getRed() - normalColor.getRed()) * hoverFraction);
            int gr = (int)(normalColor.getGreen() + (hoverColor.getGreen() - normalColor.getGreen()) * hoverFraction);
            int b = (int)(normalColor.getBlue() + (hoverColor.getBlue() - normalColor.getBlue()) * hoverFraction);
            g2.setColor(new Color(r, gr, b, normalColor.getAlpha())); 
            // Respect alpha if normalColor is transparent
            if(normalColor.getAlpha() == 0 && hoverFraction > 0) {
                 g2.setColor(new Color(hoverColor.getRed(), hoverColor.getGreen(), hoverColor.getBlue(), (int)(hoverColor.getAlpha() * hoverFraction)));
            }
            
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
            super.paintComponent(g);
            g2.dispose();
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}
        
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}