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
    
    public GameFrame(Player player, Difficulty difficulty) {
        this.player = player;
        this.difficulty = difficulty;
        
        setTitle("Minesweeper - " + difficulty.name() + " [" + player.getUsername() + "]");
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
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        topPanel.setBackground(new Color(230, 230, 230));
        
        timerLabel = new JLabel("Time: 10:00"); // Default start
        timerLabel.setFont(new Font("Monospaced", Font.BOLD, 18));
        
        bombsLabel = new JLabel("Bombs: " + difficulty.getBombCount());
        bombsLabel.setFont(new Font("Arial", Font.BOLD, 16));
        
        scoreLabel = new JLabel("Score: 0");
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 16));
        scoreLabel.setForeground(new Color(0, 100, 0));
        
        topPanel.add(timerLabel);
        topPanel.add(bombsLabel);
        topPanel.add(scoreLabel);
        
        // --- GAME PANEL ---
        gamePanel = new GamePanel(game);
        
        // --- BOTTOM PANEL ---
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        
        restartButton = new JButton("Restart");
        menuButton = new JButton("Menu");
        
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
        
        timerLabel.setText("Time: 10:00");
        scoreLabel.setText("Score: 0");
        bombsLabel.setText("Bombs: " + difficulty.getBombCount());
    }
    
    // --- IMPLEMENTASI GameListener ---
    
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
                gamePanel.repaint();
                JOptionPane.showMessageDialog(this,
                    "Time is up!\nScore: 0",
                    "Game Over",
                    JOptionPane.WARNING_MESSAGE);
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
        // Handled by onGameStateChanged via Game class
    }
    
    private void updateBombsLabel() {
        int remainingBombs = difficulty.getBombCount() - game.getBoard().getFlaggedCells();
        bombsLabel.setText("Bombs: " + remainingBombs);
    }
}