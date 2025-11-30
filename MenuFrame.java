import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;
import java.awt.event.*;

class MenuFrame extends JFrame {
    private Player player;
    private JLabel highScoreLabel; 
    private JButton easyButton;
    private JButton normalButton;
    private JButton hardButton;
    private JButton leaderboardButton;
    private JButton logoutButton;

    private final Color minesweeperGreen = Color.decode("#96dd11");
    private final Color pressedGreen = minesweeperGreen.darker();
    private final Color defaultBg = UIManager.getColor("Button.background");
    private final Color DIFF_TEXT = Color.decode("#bfc5cd");
    private final Color EASY_OUTLINE = Color.decode("#22d86c");
    private final Color EASY_FILL = Color.decode("#005f00");
    private final Color NORMAL_OUTLINE = Color.decode("#7282a8");
    private final Color NORMAL_FILL = Color.decode("#24375e");
    private final Color HARD_OUTLINE = Color.decode("#f75a26");
    private final Color HARD_FILL = Color.decode("#951d00");



    private final MouseAdapter hoverPress = new MouseAdapter() {
        @Override
        public void mouseEntered(MouseEvent e) {
            ((JButton) e.getComponent()).setBackground(minesweeperGreen);
        }
        @Override
        public void mouseExited(MouseEvent e) {
            ((JButton) e.getComponent()).setBackground(defaultBg);
        }
        @Override
        public void mousePressed(MouseEvent e) {
            ((JButton) e.getComponent()).setBackground(pressedGreen);
        }
        @Override
        public void mouseReleased(MouseEvent e) {
            JButton b = (JButton) e.getComponent();
            Point p = SwingUtilities.convertPoint(b, e.getPoint(), b);
            if (b.contains(p)) b.setBackground(minesweeperGreen);
            else b.setBackground(defaultBg);
        }
    };
    
    public MenuFrame(Player player) {
        this.player = player;
        
        setTitle("Minesweeper - Menu");
        setIconImage(new ImageIcon("assets/logo.png").getImage());
        setSize(500, 420);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        
        initComponents();
        setVisible(true);
    }
    
    private void initComponents() {
        LoginPanel background = new LoginPanel();
        background.setLayout(new GridBagLayout());
        setContentPane(background);
        JPanel mainPanel = new JPanel();

        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));
        mainPanel.setOpaque(false);

        
        // --- WELCOME LABEL ---
        JLabel welcomeLabel = new JLabel("Welcome, " + player.getUsername() + "!");
        welcomeLabel.setFont(new Font("Monospaced", Font.BOLD, 24));
        welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // --- HIGH SCORE LABEL (Global Variable) ---
        // Menampilkan skor yang ada di objek player saat ini
        highScoreLabel = new JLabel("High Score: " + player.getHighScore());
        highScoreLabel.setFont(new Font("Monospaced", Font.BOLD, 16));
        highScoreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        highScoreLabel.setForeground(HARD_FILL);
        
        JLabel selectLabel = new JLabel("Select Difficulty:");
        selectLabel.setFont(new Font("Monospaced", Font.BOLD, 18));
        selectLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // --- DIFFICULTY BUTTONS ---
        easyButton = createDifficultyButton("EASY", "9x9 Grid, 10 Bombs", EASY_OUTLINE, EASY_FILL);
        normalButton = createDifficultyButton("NORMAL", "16x16 Grid, 40 Bombs", NORMAL_OUTLINE, NORMAL_FILL);
        hardButton = createDifficultyButton("HARD", "24x24 Grid, 99 Bombs", HARD_OUTLINE, HARD_FILL);
        
        // --- ACTION PANEL (Leaderboard & Logout) ---
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 0));
        actionPanel.setOpaque(false);
        leaderboardButton = new JButton("Leaderboard");
        logoutButton = new JButton("Logout");

        leaderboardButton.setPreferredSize(new Dimension(120, 32));
        leaderboardButton.setFont(new Font("Monospaced", Font.PLAIN, 13));
        logoutButton.setPreferredSize(new Dimension(120, 32));
        logoutButton.setFont(new Font("Monospaced", Font.PLAIN, 13));

        for (JButton b : new JButton[]{leaderboardButton, logoutButton}) {
            b.setOpaque(true);
            b.setFocusPainted(false);
            b.setBorderPainted(false);
            b.setBackground(defaultBg);
            b.setForeground(Color.BLACK);
            b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            b.addMouseListener(hoverPress);
        }
        actionPanel.add(leaderboardButton);
        actionPanel.add(logoutButton);

        
        // --- ADDING COMPONENTS TO MAIN PANEL ---
        mainPanel.add(welcomeLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        mainPanel.add(highScoreLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        mainPanel.add(selectLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        mainPanel.add(easyButton);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        mainPanel.add(normalButton);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        mainPanel.add(hardButton);
        mainPanel.add(Box.createRigidArea(new Dimension(300, 15)));
        mainPanel.add(actionPanel);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        getContentPane().add(mainPanel, gbc);

        
        // --- ACTION LISTENERS ---
        easyButton.addActionListener(e -> startGame(Difficulty.EASY));
        normalButton.addActionListener(e -> startGame(Difficulty.NORMAL));
        hardButton.addActionListener(e -> startGame(Difficulty.HARD));
        
        leaderboardButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new LeaderboardFrame(MenuFrame.this);
            }
        });
        
        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int confirm = JOptionPane.showConfirmDialog(
                    MenuFrame.this,
                    "Are you sure you want to logout?",
                    "Logout",
                    JOptionPane.YES_NO_OPTION
                );
                
                if (confirm == JOptionPane.YES_OPTION) {
                    dispose();
                    new LoginFrame();
                }
            }
        });
    }
    
    // --- METHOD PENTING UNTUK REFRESH DATA DARI DATABASE ---
    @Override
    public void setVisible(boolean b) {
        // Jika window dimunculkan kembali (misal setelah game selesai)
        if (b && player != null) {
            try {
                // 1. Ambil skor tertinggi terbaru langsung dari database history
                int latestHighScore = PlayerDatabase.getInstance()
                        .getHighScoreFromHistory(player.getIdPlayer());
                
                // 2. Update objek player
                player.setHighScore(latestHighScore);
                
                // 3. Update tampilan Label
                if (highScoreLabel != null) {
                    highScoreLabel.setText("High Score: " + latestHighScore);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        super.setVisible(b);
    }
    
    private JButton createDifficultyButton(String title, String description, Color outline, Color fill) {
        JButton button = new JButton("<html><center><b>" + title + "</b><br>" + description + "</center></html>");
        button.setPreferredSize(new Dimension(300, 40));
        button.setMaximumSize(new Dimension(300, 40));
        button.setMinimumSize(new Dimension(300, 40));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setFont(new Font("Monospaced", Font.PLAIN, 14));
        button.setFocusPainted(false);

        button.setUI(new BasicButtonUI());
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setBorderPainted(true);

        button.setBackground(fill);
        button.setForeground(DIFF_TEXT);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        Border outerTransparent = BorderFactory.createLineBorder(new Color(0, 0, 0, 0), 3, true);
        Border insets = BorderFactory.createEmptyBorder(6, 12, 6, 12);
        Border defaultBorder = BorderFactory.createCompoundBorder(outerTransparent, insets);
        button.setBorder(defaultBorder);

        Border hoverOutline = BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(outline, 3, true),
                insets);


        final Dimension normalSize = new Dimension(button.getPreferredSize());
        final Dimension pressedSize = new Dimension(Math.max(1, normalSize.width - 6), Math.max(1, normalSize.height - 4));

        MouseAdapter diffHoverPress = new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBorder(hoverOutline);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBorder(defaultBorder);
            }
            @Override
            public void mousePressed(MouseEvent e) {
                
                button.setPreferredSize(pressedSize);
                button.setMaximumSize(pressedSize);
                button.setMinimumSize(pressedSize);
                
                Container parent = button.getParent();
                if (parent != null) parent.revalidate();
                button.repaint();
            }
            @Override
            public void mouseReleased(MouseEvent e) {
                button.setPreferredSize(normalSize);
                button.setMaximumSize(normalSize);
                button.setMinimumSize(normalSize);
                Container parent = button.getParent();
                if (parent != null) parent.revalidate();
                button.repaint();

                Point p = SwingUtilities.convertPoint(button, e.getPoint(), button);
                if (button.contains(p)) {
                    button.setBorder(hoverOutline);
                } else {
                    button.setBorder(defaultBorder);
                }
            }
        };
        button.addMouseListener(diffHoverPress);

        return button;
    }
    
    private void startGame(Difficulty difficulty) {
        this.setVisible(false); 
        dispose();
        new GameFrame(player, difficulty);
    }
}