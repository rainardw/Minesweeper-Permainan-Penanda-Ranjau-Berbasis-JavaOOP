public enum Difficulty {
    EASY(9, 9, 10),      // 9x9 grid, 10 bombs
    NORMAL(16, 16, 40),   // 16x16 grid, 40 bombs
    HARD(16, 30, 99);     // 16x30 grid, 99 bombs
    
    private final int rows;
    private final int cols;
    private final int bombCount;
    
    Difficulty(int rows, int cols, int bombCount) {
        this.rows = rows;
        this.cols = cols;
        this.bombCount = bombCount;
    }
    
    public int getRows() {
        return rows;
    }
    
    public int getCols() {
        return cols;
    }
    
    public int getBombCount() {
        return bombCount;
    }
    
    public int getTotalCells() {
        return rows * cols;
    }
    
    public int getTimeLimit() {
        switch (this) {
            case EASY: return 300;    // 5 minutes
            case NORMAL: return 600;   // 10 minutes
            case HARD: return 900;     // 15 minutes
            default: return 300;
        }
    }
}
