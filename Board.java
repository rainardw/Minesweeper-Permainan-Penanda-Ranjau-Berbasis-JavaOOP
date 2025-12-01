import java.util.Random;

public class Board {
    private Cell[][] grid;
    private Difficulty difficulty;
    private int rows;
    private int cols;
    private int bombCount;
    private int revealedCells;
    private int flaggedCells;
    
    public Board(Difficulty difficulty) {
        this.difficulty = difficulty;
        this.rows = difficulty.getRows();
        this.cols = difficulty.getCols();
        this.bombCount = difficulty.getBombCount();
        this.revealedCells = 0;
        this.flaggedCells = 0;
        
        initializeGrid();
    }
    
    private void initializeGrid() {
        grid = new Cell[rows][cols];
        
        // Create all cells
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                grid[i][j] = new Cell(i, j);
            }
        }
    }
    
    public void placeBombs(int excludeRow, int excludeCol) {
        Random random = new Random();
        int bombsPlaced = 0;
        
        while (bombsPlaced < bombCount) {
            int row = random.nextInt(rows);
            int col = random.nextInt(cols);
            if ((row == excludeRow && col == excludeCol) || grid[row][col].isBomb()) {
                continue;
            }
            
            grid[row][col].setBomb(true);
            bombsPlaced++;
            updateAdjacentCounts(row, col);
        }
    }
    
    private void updateAdjacentCounts(int bombRow, int bombCol) {
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                int newRow = bombRow + i;
                int newCol = bombCol + j;
                
                if (isValidCell(newRow, newCol) && !grid[newRow][newCol].isBomb()) {
                    grid[newRow][newCol].incrementAdjacentBombs();
                }
            }
        }
    }
    
    public boolean isValidCell(int row, int col) {
        return row >= 0 && row < rows && col >= 0 && col < cols;
    }
    
    public Cell getCell(int row, int col) {
        if (isValidCell(row, col)) {
            return grid[row][col];
        }
        return null;
    }
    
    public void revealCell(int row, int col) {
        if (!isValidCell(row, col)) return;
        
        Cell cell = grid[row][col];
        
        if (cell.isRevealed() || cell.isFlagged()) return;
        
        cell.setRevealed(true);
        revealedCells++;
        
        if (cell.getAdjacentBombs() == 0 && !cell.isBomb()) {
            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    if (i == 0 && j == 0) continue;
                    revealCell(row + i, col + j);
                }
            }
        }
    }
    
    public void toggleFlag(int row, int col) {
        if (!isValidCell(row, col)) return;
        
        Cell cell = grid[row][col];
        
        if (cell.isRevealed()) return;
        
        if (cell.isFlagged()) {
            cell.setFlagged(false);
            flaggedCells--;
        } else {
            cell.setFlagged(true);
            flaggedCells++;
        }
    }
    
        public boolean isWin() {
         
        int totalCells = rows * cols;
        return revealedCells == (totalCells - bombCount);
    }
    
    public void revealAllBombs() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (grid[i][j].isBomb()) {
                    grid[i][j].setRevealed(true);
                }
            }
        }
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
    
    public int getRevealedCells() {
        return revealedCells;
    }
    
    public int getFlaggedCells() {
        return flaggedCells;
    }
    
    public Difficulty getDifficulty() {
        return difficulty;
    }
    
    public Cell[][] getGrid() {
        return grid;
    }
}