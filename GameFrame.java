import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

class GameFrame extends JFrame implements Game.GameListener, GameTimer.GameTimerListener {
    private Player player;
    private Difficulty difficulty;
    private Game game;
    private GamePanel gamePanel;
    
    private JLabel timerLabel;
    private JLabel bombsLabel;
    private JLabel scoreLabel;
    private JButton restartButton;
    private JButton menuButton;
    
    // Warna hover / pressed
    final Color minesweeperGreen = Color.decode("#96dd11");
    final Color pressedGreen = minesweeperGreen.darker();
    final Color defaultBg = UIManager.getColor("Button.background");

    public GameFrame(Player player, Difficulty difficulty) {
        this.player = player;
        this.difficulty = difficulty;
        
        setTitle("Minesweeper - " + difficulty.name() + " [" + player.getUsername() + "]");
        setIconImage(new ImageIcon("assets/logo.png").getImage());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        
        initGame();
        initComponents();
        
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }
    
    private void initGame() {
        // Timer listener dihandle oleh Frame ini
        game = new Game(player, difficulty, this);
        // Daftarkan frame ini sebagai listener timer juga untuk update label waktu
        game.getTimer().setListener(this); 
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        
        // --- TOP PANEL ---
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        topPanel.setBackground(Color.decode("#376238"));
        
        timerLabel = new JLabel("Time: " + game.getTimer().getFormattedTime());
        timerLabel.setFont(new Font("Monospaced", Font.BOLD, 18));

        ImageIcon bombIcon = null;
        try {
            bombIcon = new ImageIcon("assets/mine.png");
            Image scaled = bombIcon.getImage().getScaledInstance(15, 15, Image.SCALE_SMOOTH);
            bombIcon = new ImageIcon(scaled);
        } catch (Exception e) {
            bombIcon = null;
        }
    
        // Left: bombs (mine) count
        int remainingBombs = difficulty.getBombCount();
        bombsLabel = new JLabel(" " + remainingBombs);
        if (bombIcon != null) bombsLabel.setIcon(bombIcon);
        bombsLabel.setFont(new Font("Monospaced", Font.BOLD, 18));
        bombsLabel.setForeground(Color.BLACK);
        
        scoreLabel = new JLabel("Score: 0");
        scoreLabel.setFont(new Font("Monospaced", Font.BOLD, 18));
        scoreLabel.setForeground(Color.BLACK);
        
        topPanel.add(timerLabel);
        topPanel.add(scoreLabel);
        topPanel.add(bombsLabel);
        
        // --- GAME PANEL ---
        gamePanel = new GamePanel(game);
        
        // --- BOTTOM PANEL ---
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        bottomPanel.setBackground(Color.decode("#376238"));
        
        restartButton = new JButton("Restart");
        menuButton = new JButton("Menu");


        // Basic button look
        restartButton.setOpaque(true);
        restartButton.setFocusPainted(false);
        restartButton.setBorderPainted(false);
        restartButton.setForeground(Color.BLACK);
        restartButton.setBackground(defaultBg);
        menuButton.setOpaque(true);
        menuButton.setFocusPainted(false);
        menuButton.setBorderPainted(false);
        menuButton.setForeground(Color.BLACK);
        menuButton.setBackground(defaultBg);

        restartButton.addMouseListener(hoverPress);
        menuButton.addMouseListener(hoverPress);
        
        
        restartButton.addActionListener(e -> restartGame());
        
        menuButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                GameFrame.this,
                "Quit to Menu?",
                "Confirm",
                JOptionPane.YES_NO_OPTION
            );
            
            if (confirm == JOptionPane.YES_OPTION) {
                game.getTimer().stopTimer();
                dispose();
                new MenuFrame(player);
            }
        });
        
        bottomPanel.add(restartButton);
        bottomPanel.add(menuButton);
        
        add(topPanel, BorderLayout.NORTH);
        add(gamePanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }
    
    private void restartGame() {
        game.getTimer().stopTimer();
        initGame();
        gamePanel.setGame(game);
        gamePanel.repaint();
        
        timerLabel.setText("Time: " + game.getTimer().getFormattedTime());
        scoreLabel.setText("Score: 0");
        bombsLabel.setText("" + difficulty.getBombCount());
    }
    
    // --- IMPLEMENTASI GameListener ---

    // Helper untuk memasang efek hover/press
    MouseAdapter hoverPress = new MouseAdapter() {
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
            // jika kursor masih di atas tombol setelah release, kembali ke hover warna
            Point p = SwingUtilities.convertPoint(b, e.getPoint(), b);
            if (b.contains(p)) b.setBackground(minesweeperGreen);
            else b.setBackground(defaultBg);
        }
    };
    
    @Override
    public void onGameStateChanged(Game.GameState newState) {
        switch (newState) {
            case WON:
                gamePanel.repaint();
                JOptionPane.showMessageDialog(this,
                    "VICTORY!\n\n" +
                    "Base Score: " + game.getCurrentScore() + "\n" +
                    "Time Bonus: " + game.getTimer().getRemainingTime() + "\n" +
                    "Formula: (Score + Time) * 5\n" +
                    "--------------------\n" +
                    "FINAL SCORE: " + game.getFinalScore(),
                    "Congratulations",
                    JOptionPane.INFORMATION_MESSAGE);
                break;
                
            case LOST:
                gamePanel.repaint();
                JOptionPane.showMessageDialog(this,
                    "BOOM! You hit a bomb.\nScore: 0",
                    "Game Over",
                    JOptionPane.ERROR_MESSAGE);
                break;
                
            case TIME_UP:
                onTimeUp();
                break;
        }
    }
    
    @Override
    public void onScoreUpdated(int score) {
        scoreLabel.setText("Score: " + score);
    }
    
    @Override
    public void onCellRevealed(int row, int col) {
        gamePanel.repaint();
    }

    @Override
    public void onFlagsUpdated(int flags) {
        updateBombsLabel();
    }
    
    // --- IMPLEMENTASI GameTimerListener ---
    
    @Override
    public void onTimerUpdate(int remainingTime) {
        // Pastikan update UI dilakukan di Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            timerLabel.setText("Time: " + game.getTimer().getFormattedTime());
            
            // Ubah warna merah jika waktu < 1 menit
            if (remainingTime <= 60) {
                timerLabel.setForeground(Color.RED);
            } else {
                timerLabel.setForeground(Color.BLACK);
            }
        });
    }
    
    @Override
    public void onTimeUp() {
        gamePanel.repaint();
                JOptionPane.showMessageDialog(this,
                    "Time is up!\nScore: 0",
                    "Game Over",
                    JOptionPane.WARNING_MESSAGE);
    }
    
    private void updateBombsLabel() {
        int remainingBombs = difficulty.getBombCount() - game.getBoard().getFlaggedCells();
        bombsLabel.setText(" " + remainingBombs);
    }
}