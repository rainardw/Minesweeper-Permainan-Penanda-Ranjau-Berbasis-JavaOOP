class Game {
    private Player player;
    private Board board;
    private GameTimer timer;
    private Difficulty difficulty;
    private GameState gameState;
    private boolean firstClick;
    
    private int currentScore; // Skor saat ini (akumulasi kotak terbuka)
    private int finalScore;   // Skor akhir (setelah dikali rumus)
    
    private GameListener listener;
    
    // Konstanta Logika Baru
    private int GAME_DURATION = 600; // 10 Menit
    private static final int POINTS_PER_CELL = 10; // Poin per kotak
    
    public enum GameState {
        NOT_STARTED,
        PLAYING,
        WON,
        LOST,
        TIME_UP
    }
    
    public interface GameListener {
        void onGameStateChanged(GameState newState);
        void onScoreUpdated(int score); // Listener untuk update UI real-time
        void onCellRevealed(int row, int col);
        void onFlagsUpdated(int flags);
    }
    
    public Game(Player player, Difficulty difficulty, GameListener listener) {
        this.player = player;
        this.difficulty = difficulty;
        this.listener = listener;
        this.gameState = GameState.NOT_STARTED;
        this.firstClick = true;
        this.currentScore = 0;
        this.finalScore = 0;
        this.GAME_DURATION = Math.max(1, this.difficulty.getTimeLimit());
        
        // Initialize board
        board = new Board(difficulty);
        
        // Initialize timer (Fixed 600 detik)
        timer = new GameTimer(GAME_DURATION, new GameTimer.GameTimerListener() {
            @Override
            public void onTimerUpdate(int remainingTime) {
                // UI update ditangani oleh GameFrame melalui polling atau listener terpisah
            }
            
            @Override
            public void onTimeUp() {
                handleTimeUp();
            }
        });
    }
    
    public void handleCellClick(int row, int col, boolean isRightClick) {
        if (gameState != GameState.PLAYING && gameState != GameState.NOT_STARTED) {
            return;
        }
        
        Cell cell = board.getCell(row, col);
        if (cell == null) return;
        
        // Klik Kanan (Flag)
        if (isRightClick) {
            board.toggleFlag(row, col);
            if (listener != null) {
                listener.onFlagsUpdated(board.getFlaggedCells());
            }
            return;
        }
        
        // Jangan lakukan apa-apa jika sudah terbuka atau dibendera
        if (cell.isRevealed() || cell.isFlagged()) return;
        
        // Klik Pertama: Generate Bom & Mulai Timer
        if (firstClick) {
            board.placeBombs(row, col);
            timer.start();
            gameState = GameState.PLAYING;
            firstClick = false;
            notifyGameStateChanged();
        }
        
        // Cek kena Bom
        if (cell.isBomb()) {
            handleLoss();
            return;
        }
        
        // --- LOGIKA SKOR REAL-TIME ---
        int cellsBefore = board.getRevealedCells();
        
        // Buka sel (bisa recursive flood fill)
        board.revealCell(row, col);
        
        int cellsAfter = board.getRevealedCells();
        int openedCount = cellsAfter - cellsBefore;
        
        if (openedCount > 0) {
            // Update skor: 10 poin per kotak
            currentScore += (openedCount * POINTS_PER_CELL);
            
            // Beritahu UI untuk update label skor
            if (listener != null) {
                listener.onScoreUpdated(currentScore);
                listener.onCellRevealed(row, col);
            }
        }
        
        // Cek Menang
        if (board.isWin()) {
            handleWin();
        }
    }
    
    private void handleWin() {
        gameState = GameState.WON;
        timer.stopTimer();
        calculateFinalScore();
        saveScore(); // Simpan ke Database
        notifyGameStateChanged();
    }
    
    private void handleLoss() {
        gameState = GameState.LOST;
        timer.stopTimer();
        board.revealAllBombs();
        finalScore = 0; // Kalah dapat 0
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
    
    // Rumus: (Sisa Waktu + Skor Saat Ini) * 5
    private void calculateFinalScore() {
        int remainingTime = timer.getRemainingTime();
        finalScore = (remainingTime + currentScore) * 5;
    }
    
    private void saveScore() {
        PlayerDatabase database = PlayerDatabase.getInstance();
        
        // 1. Simpan History
        database.addGameHistory(player.getIdPlayer(), finalScore);
        
        // 2. Update High Score Lokal & DB
        int latestHighScore = database.getHighScoreFromHistory(player.getIdPlayer());
        player.setHighScore(latestHighScore);
        
        System.out.println("Saved Score: " + finalScore);
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
    public Player getPlayer() { return player; }
    public Board getBoard() { return board; }
    public GameTimer getTimer() { return timer; }
    public GameState getGameState() { return gameState; }
    public int getCurrentScore() { return currentScore; }
    public int getFinalScore() { return finalScore; }
    public Difficulty getDifficulty() { return difficulty; }
}