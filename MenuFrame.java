import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

class MenuFrame extends JFrame {
    private Player player;
    private static final Color MINESWEEPER_GREEN = Color.decode("#96dd11");
    private static final Color DARK_GREEN = Color.decode("#376238");
    
    // Warna Emas Pilihanmu untuk Leaderboard
    private static final Color GOLD_COLOR = Color.decode("#f39c12"); 
    
    public MenuFrame(Player player) {
        this.player = player;
        
        setTitle("Minesweeper - Menu");
        try {
            setIconImage(new ImageIcon("assets/logo.png").getImage());
        } catch (Exception e) {}
        
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        
        initComponents();
        
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }
    
    private void initComponents() {
        // === PANEL UTAMA (Background Gradient) ===
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                GradientPaint gradient = new GradientPaint(
                    0, 0, DARK_GREEN,
                    0, getHeight(), MINESWEEPER_GREEN
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setLayout(new BorderLayout(0, 20));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        mainPanel.setPreferredSize(new Dimension(500, 650)); // Tinggi disesuaikan biar muat
        
        // === HEADER PANEL ===
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setOpaque(false);
        
        // Welcome Text
        JLabel welcomeLabel = new JLabel("Welcome, " + player.getUsername() + "!") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Shadow
                g2d.setColor(new Color(0, 0, 0, 100));
                g2d.drawString(getText(), 2, getHeight() - 2);
                // Text
                super.paintComponent(g);
            }
        };
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 32));
        welcomeLabel.setForeground(Color.WHITE);
        welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Score Panel
        JPanel scorePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        scorePanel.setOpaque(false);
        
        JLabel trophyLabel = new JLabel("🏆");
        trophyLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
        
        JLabel scoreLabel = new JLabel("High Score: " + player.getHighScore());
        scoreLabel.setFont(new Font("Monospaced", Font.BOLD, 20));
        scoreLabel.setForeground(Color.decode("#FFD700")); 
        
        scorePanel.add(trophyLabel);
        scorePanel.add(scoreLabel);
        
        headerPanel.add(welcomeLabel);
        headerPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        headerPanel.add(scorePanel);
        
        // === DIFFICULTY PANEL ===
        JPanel difficultyPanel = new JPanel();
        difficultyPanel.setLayout(new BoxLayout(difficultyPanel, BoxLayout.Y_AXIS));
        difficultyPanel.setOpaque(false);
        difficultyPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        
        JLabel selectLabel = new JLabel("Select Difficulty:");
        selectLabel.setFont(new Font("Arial", Font.BOLD, 24));
        selectLabel.setForeground(Color.WHITE);
        selectLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        difficultyPanel.add(selectLabel);
        difficultyPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        JButton easyButton = createDifficultyButton("EASY", "9x9 Grid, 10 Bombs", "😊", Difficulty.EASY, new Color(46, 204, 113));
        JButton normalButton = createDifficultyButton("NORMAL", "16x16 Grid, 40 Bombs", "😐", Difficulty.NORMAL, new Color(230, 126, 34));
        JButton hardButton = createDifficultyButton("HARD", "16x30 Grid, 99 Bombs", "😰", Difficulty.HARD, new Color(231, 76, 60));
        
        difficultyPanel.add(easyButton);
        difficultyPanel.add(Box.createRigidArea(new Dimension(0, 12)));
        difficultyPanel.add(normalButton);
        difficultyPanel.add(Box.createRigidArea(new Dimension(0, 12)));
        difficultyPanel.add(hardButton);
        
        // === BOTTOM PANEL ===
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        bottomPanel.setOpaque(false);
        
        // === BAGIAN INI SUDAH DIPERBAIKI (WARNA EMAS) ===
        JButton leaderboardButton = createStyledButton("Leaderboard", GOLD_COLOR);
        leaderboardButton.addActionListener(e -> {
            new LeaderboardFrame(this);
        });
        
        JButton logoutButton = createActionButton("Logout", new Color(231, 76, 60));
        logoutButton.addActionListener(e -> {
            if (showConfirmDialog("Logout Confirmation", "Are you sure you want to logout?")) {
                dispose();
                new LoginFrame();
            }
        });
        
        bottomPanel.add(leaderboardButton);
        bottomPanel.add(logoutButton);
        
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(difficultyPanel, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
    }
    
    // === HELPER: DIFFICULTY BUTTON ===
    private JButton createDifficultyButton(String title, String subtitle, String emoji, Difficulty difficulty, Color accentColor) {
        JButton button = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (getModel().isPressed()) {
                    g2d.setColor(accentColor.darker());
                } else if (getModel().isRollover()) {
                    GradientPaint gp = new GradientPaint(0, 0, Color.WHITE, 0, getHeight(), accentColor.brighter());
                    g2d.setPaint(gp);
                } else {
                    g2d.setColor(Color.WHITE);
                }
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                
                g2d.setColor(accentColor);
                g2d.setStroke(new BasicStroke(3));
                g2d.drawRoundRect(2, 2, getWidth()-4, getHeight()-4, 15, 15);
                
                // Draw Text & Emoji
                g2d.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
                g2d.drawString(emoji, 15, (getHeight() + g2d.getFontMetrics().getAscent())/2 - 5);
                
                g2d.setColor(accentColor.darker());
                g2d.setFont(new Font("Arial", Font.BOLD, 18));
                g2d.drawString(title, (getWidth() - g2d.getFontMetrics().stringWidth(title))/2, getHeight()/2 - 5);
                
                g2d.setColor(Color.GRAY);
                g2d.setFont(new Font("Arial", Font.PLAIN, 13));
                g2d.drawString(subtitle, (getWidth() - g2d.getFontMetrics().stringWidth(subtitle))/2, getHeight()/2 + 18);
            }
        };
        button.setPreferredSize(new Dimension(350, 75));
        button.setMaximumSize(new Dimension(350, 75));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        button.addActionListener(e -> {
            dispose();
            new GameFrame(player, difficulty);
        });
        
        return button;
    }
    
    // === HELPER: ACTION BUTTON (Leaderboard & Logout) ===
    private JButton createStyledButton(String text, Color bgColor) { // Nama method disamakan
        return createActionButton(text, bgColor);
    }

    private JButton createActionButton(String text, Color bgColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (getModel().isPressed()) g2d.setColor(bgColor.darker().darker());
                else if (getModel().isRollover()) g2d.setColor(bgColor.brighter());
                else g2d.setColor(bgColor);
                
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                
                if (getModel().isRollover()) {
                    g2d.setColor(Color.WHITE);
                    g2d.setStroke(new BasicStroke(2));
                    g2d.drawRoundRect(1, 1, getWidth()-2, getHeight()-2, 15, 15);
                }
                
                g2d.setColor(Color.WHITE);
                g2d.setFont(getFont());
                FontMetrics fm = g2d.getFontMetrics();
                g2d.drawString(getText(), (getWidth() - fm.stringWidth(getText()))/2, (getHeight() + fm.getAscent())/2 - 2);
            }
        };
        button.setPreferredSize(new Dimension(160, 45));
        button.setFont(new Font("Arial", Font.BOLD, 15));
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }
    
    // === HELPER: CONFIRM DIALOG ===
    private boolean showConfirmDialog(String title, String message) {
        final JDialog dialog = new JDialog(this, "Confirm", true);
        dialog.setUndecorated(true);
        dialog.setLayout(new BorderLayout());
        final boolean[] result = {false};
        
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(Color.WHITE);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2d.setColor(DARK_GREEN);
                g2d.setStroke(new BasicStroke(3));
                g2d.drawRoundRect(1, 1, getWidth()-2, getHeight()-2, 20, 20);
            }
        };
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        
        JLabel msgLabel = new JLabel("<html><center>" + message + "</center></html>", SwingConstants.CENTER);
        msgLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        msgLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JPanel btns = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btns.setOpaque(false);
        
        JButton yes = createActionButton("Yes", new Color(46, 204, 113));
        yes.setPreferredSize(new Dimension(80, 35));
        yes.addActionListener(e -> { result[0] = true; dialog.dispose(); });
        
        JButton no = createActionButton("No", new Color(231, 76, 60));
        no.setPreferredSize(new Dimension(80, 35));
        no.addActionListener(e -> { result[0] = false; dialog.dispose(); });
        
        btns.add(yes);
        btns.add(no);
        
        panel.add(msgLabel);
        panel.add(Box.createVerticalStrut(20));
        panel.add(btns);
        
        dialog.add(panel);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
        return result[0];
    }
}