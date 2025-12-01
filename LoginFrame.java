import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

class LoginFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;
    private PlayerDatabase database;
    
    private static final Color MINESWEEPER_GREEN = Color.decode("#96dd11");
    private static final Color DARK_GREEN = Color.decode("#376238");
    private static final Color LIGHT_BEIGE = Color.decode("#e4c894");
    private static final Color DARK_BEIGE = Color.decode("#d4bc94");

    public LoginFrame() {
        database = PlayerDatabase.getInstance();
        
        setTitle("Minesweeper - Login");
        setIconImage(new ImageIcon("assets/logo.png").getImage());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        
        initComponents();
        
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }
    
    private void initComponents() {

        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                int tileSize = 24;
                int w = getWidth();
                int h = getHeight();
                for (int y = 0; y < h; y += tileSize) {
                    for (int x = 0; x < w; x += tileSize) {
                        boolean even = ((x / tileSize) + (y / tileSize)) % 2 == 0;
                        g.setColor(even ? LIGHT_BEIGE : DARK_BEIGE);
                        g.fillRect(x, y, tileSize, tileSize);
                    }
                }
            }
        };
        mainPanel.setPreferredSize(new Dimension(600, 650)); 
        mainPanel.setLayout(new GridBagLayout());
        
        // Content panel yang akan di-center
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50)); // Padding lebih besar
        
        // === LOGO & TITLE ===
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setOpaque(false);
        
        // Logo + Title Row
        JPanel titleRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        titleRow.setOpaque(false);
        
        JLabel logoLabel = new JLabel();
        try {
            ImageIcon logoIcon = new ImageIcon("assets/logo.png");
            Image scaled = logoIcon.getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH);
            logoLabel.setIcon(new ImageIcon(scaled));
        } catch (Exception e) {
            logoLabel.setText("💣");
            logoLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 50));
        }
        
        JLabel titleLabel = new JLabel("MINESWEEPER");
        titleLabel.setFont(new Font("Monospaced", Font.BOLD, 36)); 
        titleLabel.setForeground(DARK_GREEN);
        
        titleRow.add(logoLabel);
        titleRow.add(titleLabel);
        
        // Subtitle with smile icon
        JPanel subtitleRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 8));
        subtitleRow.setOpaque(false);
        
        JLabel smileLabel = new JLabel();
        try {
            ImageIcon smileIcon = new ImageIcon("assets/smile.png");
            Image scaled = smileIcon.getImage().getScaledInstance(28, 28, Image.SCALE_SMOOTH);
            smileLabel.setIcon(new ImageIcon(scaled));
        } catch (Exception e) {
            smileLabel.setText("😊");
            smileLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
        }
        
        JLabel subtitleLabel = new JLabel("Login to Play");
        subtitleLabel.setFont(new Font("Monospaced", Font.BOLD, 18)); 
        subtitleLabel.setForeground(DARK_GREEN.darker());
        
        subtitleRow.add(smileLabel);
        subtitleRow.add(subtitleLabel);
        
        headerPanel.add(titleRow);
        headerPanel.add(subtitleRow);
        
        // FORM PANEL 
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setOpaque(false);
        formPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 25, 30)); 
        
        // Username field
        JPanel usernamePanel = createFieldPanel("👤 Username:", true);
        usernameField = (JTextField) usernamePanel.getComponent(1);
        
        // Password field
        JPanel passwordPanel = createFieldPanel("🔒 Password:", false);
        passwordField = (JPasswordField) passwordPanel.getComponent(1);
        
        formPanel.add(usernamePanel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 18))); 
        formPanel.add(passwordPanel);
        
        // === BUTTON PANEL ===
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 18, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(25, 0, 0, 0));
        
        loginButton = createStyledButton("LOGIN", MINESWEEPER_GREEN);
        registerButton = createStyledButton("REGISTER", DARK_GREEN);
        
        loginButton.addActionListener(e -> handleLogin());
        registerButton.addActionListener(e -> handleRegister());
        passwordField.addActionListener(e -> handleLogin());
        
        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);
        
        // === ASSEMBLY ===
        contentPanel.add(headerPanel);
        contentPanel.add(formPanel);
        contentPanel.add(buttonPanel);
        
        // Center contentPanel in mainPanel
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        mainPanel.add(contentPanel, gbc);
        
        setContentPane(mainPanel);
    }
    
    private JPanel createFieldPanel(String labelText, boolean isTextField) {
        JPanel panel = new JPanel(new BorderLayout(12, 0));
        panel.setOpaque(false);
        panel.setMaximumSize(new Dimension(420, 42)); 
        
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Monospaced", Font.BOLD, 16)); 
        label.setForeground(DARK_GREEN);
        label.setPreferredSize(new Dimension(150, 36));
        
        JTextField field;
        if (isTextField) {
            field = new JTextField();
        } else {
            field = new JPasswordField();
        }
        
        field.setFont(new Font("Monospaced", Font.PLAIN, 15)); 
        field.setPreferredSize(new Dimension(240, 36)); 
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(DARK_GREEN, 2),
            BorderFactory.createEmptyBorder(6, 12, 6, 12)
        ));
        
        panel.add(label, BorderLayout.WEST);
        panel.add(field, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Background
                if (getModel().isPressed()) {
                    g2d.setColor(bgColor.darker().darker());
                } else if (getModel().isRollover()) {
                    g2d.setColor(bgColor.brighter());
                } else {
                    g2d.setColor(bgColor);
                }
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                
                // Border
                g2d.setColor(bgColor.darker());
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRoundRect(1, 1, getWidth()-2, getHeight()-2, 10, 10);
                
                // Text
                g2d.setColor(Color.WHITE);
                g2d.setFont(getFont());
                FontMetrics fm = g2d.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent()) / 2 - 2;
                g2d.drawString(getText(), x, y);
            }
        };
        
        button.setPreferredSize(new Dimension(140, 48)); // Lebih besar
        button.setFont(new Font("Monospaced", Font.BOLD, 15)); // Font lebih besar
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        return button;
    }
    
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        
        if (username.isEmpty() || password.isEmpty()) {
            showCustomDialog(
                "⚠️ Error",
                "Please enter username and password!",
                new Color(231, 76, 60)
            );
            return;
        }
        
        Player player = database.login(username, password);
        
        if (player != null) {
            showCustomDialog(
                "✅ Success",
                "Welcome back, " + username + "!",
                MINESWEEPER_GREEN
            );
            
            dispose();
            MenuFrame menu = new MenuFrame(player);
            menu.setVisible(true);
        } else {
            showCustomDialog(
                "❌ Login Failed",
                "Invalid username or password!",
                new Color(231, 76, 60)
            );
            passwordField.setText("");
        }
    }
    
    private void handleRegister() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        
        if (username.isEmpty() || password.isEmpty()) {
            showCustomDialog(
                "⚠️ Error",
                "Please enter username and password!",
                new Color(231, 76, 60)
            );
            return;
        }
        
        if (username.length() < 3) {
            showCustomDialog(
                "⚠️ Error",
                "Username must be at least 3 characters!",
                new Color(230, 126, 34)
            );
            return;
        }
        
        if (password.length() < 4) {
            showCustomDialog(
                "⚠️ Error",
                "Password must be at least 4 characters!",
                new Color(230, 126, 34)
            );
            return;
        }
        
        Player player = database.registerPlayer(username, password);
        
        if (player != null) {
            showCustomDialog(
                "✅ Success",
                "Registration successful! You can now login.",
                MINESWEEPER_GREEN
            );
            passwordField.setText("");
        } else {
            showCustomDialog(
                "❌ Registration Failed",
                "Username already taken!",
                new Color(231, 76, 60)
            );
        }
    }
    
    private void showCustomDialog(String title, String message, Color accentColor) {
        final JDialog dialog = new JDialog(this, "Message", true);
        dialog.setUndecorated(true);
        dialog.setLayout(new BorderLayout());
        
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Background
                g2d.setColor(Color.WHITE);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                
                // Accent bar at top
                g2d.setColor(accentColor);
                g2d.fillRoundRect(0, 0, getWidth(), 50, 20, 20);
                g2d.fillRect(0, 30, getWidth(), 20);
                
                // Border
                g2d.setColor(accentColor.darker());
                g2d.setStroke(new BasicStroke(3));
                g2d.drawRoundRect(1, 1, getWidth()-2, getHeight()-2, 20, 20);
            }
        };
        mainPanel.setOpaque(false);
        mainPanel.setLayout(new BorderLayout(0, 20));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 30, 25, 30));
        mainPanel.setPreferredSize(new Dimension(350, 200));
        
        // Title
        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Monospaced", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        
        // Message
        JLabel messageLabel = new JLabel(
            "<html><div style='text-align: center;'>" + message + "</div></html>",
            SwingConstants.CENTER
        );
        messageLabel.setFont(new Font("Monospaced", Font.PLAIN, 14));
        messageLabel.setForeground(DARK_GREEN);
        
        // OK Button
        JButton okButton = createStyledButton("OK", accentColor);
        okButton.setPreferredSize(new Dimension(100, 35));
        okButton.addActionListener(e -> dialog.dispose());
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setOpaque(false);
        buttonPanel.add(okButton);
        
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(messageLabel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.add(mainPanel);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
}