import javax.swing.*;
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
    
    public MenuFrame(Player player) {
        this.player = player;
        
        setTitle("Minesweeper - Menu");
        setSize(500, 420);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        
        initComponents();
        setVisible(true);
    }
    
    private void initComponents() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));
        
        // --- WELCOME LABEL ---
        JLabel welcomeLabel = new JLabel("Welcome, " + player.getUsername() + "!");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // --- HIGH SCORE LABEL (Global Variable) ---
        // Menampilkan skor yang ada di objek player saat ini
        highScoreLabel = new JLabel("High Score: " + player.getHighScore());
        highScoreLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        highScoreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        highScoreLabel.setForeground(new Color(0, 100, 0)); // Warna hijau tua agar menonjol
        
        JLabel selectLabel = new JLabel("Select Difficulty:");
        selectLabel.setFont(new Font("Arial", Font.BOLD, 18));
        selectLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // --- DIFFICULTY BUTTONS ---
        easyButton = createDifficultyButton("EASY", "9x9 Grid, 10 Bombs");
        normalButton = createDifficultyButton("NORMAL", "16x16 Grid, 40 Bombs");
        hardButton = createDifficultyButton("HARD", "16x30 Grid, 99 Bombs");
        
        // --- BOTTOM PANEL (Leaderboard & Logout) ---
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        leaderboardButton = new JButton("Leaderboard");
        logoutButton = new JButton("Logout");
        
        leaderboardButton.setPreferredSize(new Dimension(130, 35));
        logoutButton.setPreferredSize(new Dimension(130, 35));
        
        bottomPanel.add(leaderboardButton);
        bottomPanel.add(logoutButton);
        
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
        mainPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        mainPanel.add(bottomPanel);
        
        add(mainPanel);
        
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
    
    private JButton createDifficultyButton(String title, String description) {
        JButton button = new JButton("<html><center><b>" + title + "</b><br>" + description + "</center></html>");
        button.setPreferredSize(new Dimension(300, 50));
        button.setMaximumSize(new Dimension(300, 50));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setFont(new Font("Arial", Font.PLAIN, 14));
        button.setFocusPainted(false); // Menghilangkan border fokus saat diklik
        return button;
    }
    
    private void startGame(Difficulty difficulty) {
        this.setVisible(false); 
        new GameFrame(player, difficulty);
    }
}