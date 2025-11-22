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
        
        setTitle("Minesweeper - " + difficulty.name());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        
        initGame();
        initComponents();
        
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }
    
    private void initGame() {
        game = new Game(player, difficulty, this);
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        topPanel.setBackground(new Color(200, 200, 200));
        
        timerLabel = new JLabel("Time: 00:00");
        timerLabel.setFont(new Font("Arial", Font.BOLD, 16));
        
        bombsLabel = new JLabel("Bombs: " + difficulty.getBombCount());
        bombsLabel.setFont(new Font("Arial", Font.BOLD, 16));
        
        scoreLabel = new JLabel("Score: 0");
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 16));
        
        topPanel.add(timerLabel);
        topPanel.add(bombsLabel);
        topPanel.add(scoreLabel);
        
        gamePanel = new GamePanel(game);
        
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        
        restartButton = new JButton("Restart");
        menuButton = new JButton("Menu");
        
        restartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                restartGame();
            }
        });
        
        menuButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int confirm = JOptionPane.showConfirmDialog(
                    GameFrame.this,
                    "Are you sure you want to quit to menu?",
                    "Confirm",
                    JOptionPane.YES_NO_OPTION
                );
                
                if (confirm == JOptionPane.YES_OPTION) {
                    game.getTimer().stopTimer();
                    dispose();
                    new MenuFrame(player);
                }
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
        
        timerLabel.setText("Time: 00:00");
        scoreLabel.setText("Score: 0");
        bombsLabel.setText("Bombs: " + difficulty.getBombCount());
    }
    
    @Override
    public void onGameStateChanged(Game.GameState newState) {
        switch (newState) {
            case WON:
                gamePanel.repaint();
                JOptionPane.showMessageDialog(this,
                    "Congratulations! You won!\nScore: " + game.getFinalScore(),
                    "Victory!",
                    JOptionPane.INFORMATION_MESSAGE);
                break;
                
            case LOST:
                gamePanel.repaint();
                JOptionPane.showMessageDialog(this,
                    "Game Over! You hit a bomb!",
                    "Defeat",
                    JOptionPane.ERROR_MESSAGE);
                break;
                
            case TIME_UP:
                gamePanel.repaint();
                JOptionPane.showMessageDialog(this,
                    "Time's up! Game Over!",
                    "Time Up",
                    JOptionPane.WARNING_MESSAGE);
                break;
        }
    }
    
    @Override
    public void onScoreCalculated(int score) {
        scoreLabel.setText("Score: " + score);
    }
    
    @Override
    public void onCellRevealed(int row, int col) {
        gamePanel.repaint();
        updateBombsLabel();
    }
    
    @Override
    public void onTimerUpdate(int elapsedTime) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                timerLabel.setText("Time: " + game.getTimer().getFormattedTime());
            }
        });
    }
    
    @Override
    public void onTimeUp() {
        // Handled in onGameStateChanged
    }
    
    private void updateBombsLabel() {
        int remainingBombs = difficulty.getBombCount() - game.getBoard().getFlaggedCells();
        bombsLabel.setText("Bombs: " + remainingBombs);
    }
}
