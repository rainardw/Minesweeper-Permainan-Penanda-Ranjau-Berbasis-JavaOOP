import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

class MenuFrame extends JFrame {
    private Player player;
    private JButton easyButton;
    private JButton normalButton;
    private JButton hardButton;
    private JButton leaderboardButton;
    private JButton logoutButton;
    
    public MenuFrame(Player player) {
        this.player = player;
        
        setTitle("Minesweeper - Menu");
        setSize(500, 400);
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
        
        JLabel welcomeLabel = new JLabel("Welcome, " + player.getUsername() + "!");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel highScoreLabel = new JLabel("High Score: " + player.getHighScore());
        highScoreLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        highScoreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel selectLabel = new JLabel("Select Difficulty:");
        selectLabel.setFont(new Font("Arial", Font.BOLD, 18));
        selectLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        easyButton = createDifficultyButton("EASY", "9x9 Grid, 10 Bombs");
        normalButton = createDifficultyButton("NORMAL", "16x16 Grid, 40 Bombs");
        hardButton = createDifficultyButton("HARD", "16x30 Grid, 99 Bombs");
        
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        leaderboardButton = new JButton("Leaderboard");
        logoutButton = new JButton("Logout");
        
        leaderboardButton.setPreferredSize(new Dimension(130, 35));
        logoutButton.setPreferredSize(new Dimension(130, 35));
        
        bottomPanel.add(leaderboardButton);
        bottomPanel.add(logoutButton);
        
        mainPanel.add(welcomeLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        mainPanel.add(highScoreLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        mainPanel.add(selectLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        mainPanel.add(easyButton);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        mainPanel.add(normalButton);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        mainPanel.add(hardButton);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        mainPanel.add(bottomPanel);
        
        add(mainPanel);
        
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
                dispose();
                new LoginFrame();
            }
        });
    }
    
    private JButton createDifficultyButton(String title, String description) {
        JButton button = new JButton("<html><center><b>" + title + "</b><br>" + description + "</center></html>");
        button.setPreferredSize(new Dimension(300, 50));
        button.setMaximumSize(new Dimension(300, 50));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setFont(new Font("Arial", Font.PLAIN, 14));
        return button;
    }
    
    private void startGame(Difficulty difficulty) {
        dispose();
        new GameFrame(player, difficulty);
    }
}