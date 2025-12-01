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
    final Color darkGreen = Color.decode("#376238");
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
        game = new Game(player, difficulty, this);
        game.getTimer().setListener(this); 
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        
        // --- TOP PANEL ---
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        topPanel.setBackground(darkGreen);
        
        timerLabel = new JLabel("Time: " + game.getTimer().getFormattedTime());
        timerLabel.setFont(new Font("Segoe UI Emoji", Font.BOLD, 18));

        ImageIcon bombIcon = null;
        try {
            bombIcon = new ImageIcon("assets/mine.png");
            Image scaled = bombIcon.getImage().getScaledInstance(15, 15, Image.SCALE_SMOOTH);
            bombIcon = new ImageIcon(scaled);
        } catch (Exception e) {
            bombIcon = null;
        }
    
        int remainingBombs = difficulty.getBombCount();
        bombsLabel = new JLabel(" " + remainingBombs);
        if (bombIcon != null) bombsLabel.setIcon(bombIcon);
        bombsLabel.setFont(new Font("Segoe UI Emoji", Font.BOLD, 18));
        bombsLabel.setForeground(Color.BLACK);
        
        scoreLabel = new JLabel("Score: 0");
        scoreLabel.setFont(new Font("Segoe UI Emoji", Font.BOLD, 18));
        scoreLabel.setForeground(Color.BLACK);
        
        topPanel.add(timerLabel);
        topPanel.add(scoreLabel);
        topPanel.add(bombsLabel);
        
        // --- GAME PANEL ---
        gamePanel = new GamePanel(game);
        
        // --- BOTTOM PANEL ---
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        bottomPanel.setBackground(darkGreen);
        
        restartButton = new JButton("Restart");
        menuButton = new JButton("Menu");

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
            boolean confirm = showConfirmDialog("Quit to Menu?", "Your current progress will be lost.");
            
            if (confirm) {
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
            Point p = SwingUtilities.convertPoint(b, e.getPoint(), b);
            if (b.contains(p)) b.setBackground(minesweeperGreen);
            else b.setBackground(defaultBg);
        }
    };
    
    @Override
    public void onGameStateChanged(Game.GameState newState) {
        gamePanel.repaint();
        
        switch (newState) {
            case WON:
                showGameOverDialog(
                   "VICTORY!",
                    String.format(
                        "Cells Opened: %d\n" +
                        "Base Score: %d\n" +
                        "Time Bonus: %d seconds\n" +
                        "FINAL SCORE: %d",
                        game.getBoard().getRevealedCells(),
                        game.getCurrentScore(),
                        game.getTimer().getRemainingTime(),
                        game.getFinalScore()
                    ),
                    new Color(46, 204, 113),
                    "🏆"
                );
                break;
                
            case LOST:
                showGameOverDialog(
                    "GAME OVER",
                    String.format(
                        "You hit a bomb!\n\n" +
                        "Cells Opened: %d\n" +
                        "Base Score: %d\n" +
                        "FINAL SCORE: %d",
                        game.getBoard().getRevealedCells(),
                        game.getCurrentScore(),
                        game.getFinalScore()
                    ),
                    new Color(231, 76, 60),
                    "💣"
                );
                break;
                
            case TIME_UP:
                showGameOverDialog(
                    "TIME'S UP!",
                    String.format(
                        "Time ran out!\n\n" +
                        "Cells Opened: %d\n" +
                        "Base Score: %d\n" +
                        "FINAL SCORE: %d",
                        game.getBoard().getRevealedCells(),
                        game.getCurrentScore(),
                        game.getFinalScore()
                    ),
                    new Color(230, 126, 34),
                    "⏱️"
                );
                break;
        }
    }

    private boolean showConfirmDialog(String title, String message) {
        final JDialog dialog = new JDialog(this, "Confirm", true);
        dialog.setUndecorated(true);
        dialog.setLayout(new BorderLayout());
        
        final boolean[] result = {false};
        
        // Main panel dengan gradient
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Gradient background
                GradientPaint gradient = new GradientPaint(
                    0, 0, darkGreen,
                    0, getHeight(), minesweeperGreen
                );
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                
                // Border
                g2d.setColor(darkGreen.darker());
                g2d.setStroke(new BasicStroke(3));
                g2d.drawRoundRect(1, 1, getWidth()-2, getHeight()-2, 20, 20);
            }
        };
        mainPanel.setOpaque(false);
        mainPanel.setLayout(new BorderLayout(15, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        
        // Content panel
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);
        
        // Title
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Message
        JLabel messageLabel = new JLabel("<html><div style='text-align: center;'>" + message + "</div></html>");
        messageLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        messageLabel.setForeground(Color.WHITE.darker());
        messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        contentPanel.add(titleLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        contentPanel.add(messageLabel);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setOpaque(false);
        
        JButton yesButton = createDialogButton("Yes", new Color(46, 204, 113));
        yesButton.addActionListener(e -> {
            result[0] = true;
            dialog.dispose();
        });
        
        JButton noButton = createDialogButton("No", new Color(231, 76, 60));
        noButton.addActionListener(e -> {
            result[0] = false;
            dialog.dispose();
        });
        
        buttonPanel.add(yesButton);
        buttonPanel.add(noButton);
        
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.add(mainPanel);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
        
        return result[0];
    }
    
    private void showGameOverDialog(String title, String message, Color accentColor, String emoji) {
        final JDialog dialog = new JDialog(this, "Game Over", true);
        dialog.setUndecorated(true);
        dialog.setLayout(new BorderLayout());
        
        // Main panel dengan gradient
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Gradient background
                GradientPaint gradient = new GradientPaint(
                    0, 0, accentColor.darker(),
                    0, getHeight(), accentColor
                );
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);
                
                // Border
                g2d.setColor(accentColor.darker().darker());
                g2d.setStroke(new BasicStroke(4));
                g2d.drawRoundRect(2, 2, getWidth()-4, getHeight()-4, 25, 25);
            }
        };
        mainPanel.setOpaque(false);
        mainPanel.setLayout(new BorderLayout(20, 20));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(35, 45, 35, 45));
        
        // Header panel
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setOpaque(false);
        
        // Emoji
        JLabel emojiLabel = new JLabel(emoji);
        emojiLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
        emojiLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Title
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        headerPanel.add(emojiLabel);
        headerPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        headerPanel.add(titleLabel);
        
        // Message panel dengan background semi-transparent
        JPanel messagePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Semi-transparent white background
                g2d.setColor(new Color(255, 255, 255, 200));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
            }
        };
        messagePanel.setOpaque(false);
        messagePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        messagePanel.setLayout(new BorderLayout());
        
        JTextArea messageArea = new JTextArea(message);
        messageArea.setEditable(false);
        messageArea.setOpaque(false);
        messageArea.setFont(new Font("Monospaced", Font.BOLD, 14));
        messageArea.setForeground(Color.decode("#2c3e50"));
        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);
        
        messagePanel.add(messageArea);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setOpaque(false);
        
        JButton playAgainButton = createDialogButton("Play Again", Color.WHITE);
        playAgainButton.addActionListener(e -> {
            dialog.dispose();
            restartGame();
        });
        
        JButton menuButton = createDialogButton("Main Menu", Color.WHITE);
        menuButton.addActionListener(e -> {
            dialog.dispose();
            game.getTimer().stopTimer();
            dispose();
            new MenuFrame(player);
        });
        
        buttonPanel.add(playAgainButton);
        buttonPanel.add(menuButton);
        
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(messagePanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.add(mainPanel);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
    
    private JButton createDialogButton(String text, Color bgColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Background
                if (getModel().isPressed()) {
                    g2d.setColor(bgColor.darker().darker());
                } else if (getModel().isRollover()) {
                    g2d.setColor(bgColor.darker());
                } else {
                    g2d.setColor(bgColor);
                }
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                
                // Border on hover
                if (getModel().isRollover()) {
                    g2d.setColor(Color.WHITE);
                    g2d.setStroke(new BasicStroke(2));
                    g2d.drawRoundRect(1, 1, getWidth()-2, getHeight()-2, 15, 15);
                }
                
                // Text
                g2d.setColor(bgColor.equals(Color.WHITE) ? darkGreen : Color.WHITE);
                g2d.setFont(getFont());
                FontMetrics fm = g2d.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent()) / 2 - 2;
                g2d.drawString(getText(), x, y);
            }
        };
        
        button.setPreferredSize(new Dimension(130, 45));
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        return button;
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
    
    @Override
    public void onTimerUpdate(int remainingTime) {
        SwingUtilities.invokeLater(() -> {
            timerLabel.setText("Time: " + game.getTimer().getFormattedTime());
            
            if (remainingTime <= 60) {
                timerLabel.setForeground(Color.RED);
            } else {
                timerLabel.setForeground(Color.BLACK);
            }
        });
    }
    
    @Override
    public void onTimeUp() {
    }
    
    private void updateBombsLabel() {
        int remainingBombs = difficulty.getBombCount() - game.getBoard().getFlaggedCells();
        bombsLabel.setText(" " + remainingBombs);
    }
}