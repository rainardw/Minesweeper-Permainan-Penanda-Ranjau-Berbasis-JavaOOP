public class Game {
    private Player player;
    private Board board;
    private GameTimer timer;
    private Difficulty difficulty;
    private GameState gameState;
    private boolean firstClick;
    private int finalScore;
    private GameListener listener;
    
    public enum GameState {
        NOT_STARTED,
        PLAYING,
        WON,
        LOST,
        TIME_UP
    }
    
    public interface GameListener {
        void onGameStateChanged(GameState newState);
        void onScoreCalculated(int score);
        void onCellRevealed(int row, int col);
    }
    
    public Game(Player player, Difficulty difficulty, GameListener listener) {
        this.player = player;
        this.difficulty = difficulty;
        this.listener = listener;
        this.gameState = GameState.NOT_STARTED;
        this.firstClick = true;
        this.finalScore = 0;
        
        // Initialize board
        board = new Board(difficulty);
        
        // Initialize timer
        timer = new GameTimer(difficulty.getTimeLimit(), new GameTimer.GameTimerListener() {
            @Override
            public void onTimerUpdate(int elapsedTime) {
                // Timer update handled by UI
            }
            
            @Override
            public void onTimeUp() {
                handleTimeUp();
            }
        });
    }
    
    public void startGame() {
        gameState = GameState.PLAYING;
        notifyGameStateChanged();
    }
    
    public void handleCellClick(int row, int col, boolean isRightClick) {
        if (gameState != GameState.PLAYING && gameState != GameState.NOT_STARTED) {
            return;
        }
        
        Cell cell = board.getCell(row, col);
        if (cell == null) return;
        
        if (isRightClick) {
            // Right click - toggle flag
            board.toggleFlag(row, col);
            return;
        }
        
        // Left click - reveal cell
        if (cell.isFlagged()) return;
        
        // First click - start timer and place bombs
        if (firstClick) {
            board.placeBombs(row, col);
            timer.start();
            gameState = GameState.PLAYING;
            firstClick = false;
            notifyGameStateChanged();
        }
        
        // Check if clicked on bomb
        if (cell.isBomb()) {
            handleLoss();
            return;
        }
        
        // Reveal cell
        board.revealCell(row, col);
        
        if (listener != null) {
            listener.onCellRevealed(row, col);
        }
        
        // Check win condition
        if (board.isWin()) {
            handleWin();
        }
    }
    
    private void handleWin() {
        gameState = GameState.WON;
        timer.stopTimer();
        calculateScore();
        saveScore();
        notifyGameStateChanged();
    }
    
    private void handleLoss() {
        gameState = GameState.LOST;
        timer.stopTimer();
        board.revealAllBombs();
        finalScore = 0;
        notifyGameStateChanged();
    }
    
    private void handleTimeUp() {
        if (gameState == GameState.PLAYING) {
            gameState = GameState.TIME_UP;
            board.revealAllBombs();
            finalScore = 0;
            notifyGameStateChanged();
        }
    }
    
    private void calculateScore() {
        // Score formula: (remaining time + revealed cells) * 5
        int remainingTime = timer.getRemainingTime();
        int revealedCells = board.getRevealedCells();
        
        finalScore = (remainingTime + revealedCells) * 5;
        
        if (listener != null) {
            listener.onScoreCalculated(finalScore);
        }
    }
    
    private void saveScore() {
        PlayerDatabase database = PlayerDatabase.getInstance();
        database.updatePlayerScore(player.getIdPlayer(), finalScore);
        player.setHighScore(finalScore);
    }
    
    public void pauseGame() {
        if (gameState == GameState.PLAYING) {
            timer.pauseTimer();
        }
    }
    
    public void resumeGame() {
        if (gameState == GameState.PLAYING) {
            timer.resumeTimer();
        }
    }
    
    private void notifyGameStateChanged() {
        if (listener != null) {
            listener.onGameStateChanged(gameState);
        }
    }
    
    // Getters
    public Player getPlayer() {
        return player;
    }
    
    public Board getBoard() {
        return board;
    }
    
    public GameTimer getTimer() {
        return timer;
    }
    
    public GameState getGameState() {
        return gameState;
    }
    
    public int getFinalScore() {
        return finalScore;
    }
    
    public Difficulty getDifficulty() {
        return difficulty;
    }
}