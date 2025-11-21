public class Cell {
    private boolean isBomb;
    private boolean isRevealed;
    private boolean isFlagged;
    private int adjacentBombs;
    private int row;
    private int col;
    
    public Cell(int row, int col) {
        this.row = row;
        this.col = col;
        this.isBomb = false;
        this.isRevealed = false;
        this.isFlagged = false;
        this.adjacentBombs = 0;
    }
    
    // Getters
    public boolean isBomb() {
        return isBomb;
    }
    
    public boolean isRevealed() {
        return isRevealed;
    }
    
    public boolean isFlagged() {
        return isFlagged;
    }
    
    public int getAdjacentBombs() {
        return adjacentBombs;
    }
    
    public int getRow() {
        return row;
    }
    
    public int getCol() {
        return col;
    }
    
    // Setters
    public void setBomb(boolean bomb) {
        isBomb = bomb;
    }
    
    public void setRevealed(boolean revealed) {
        isRevealed = revealed;
    }
    
    public void setFlagged(boolean flagged) {
        isFlagged = flagged;
    }
    
    public void setAdjacentBombs(int count) {
        this.adjacentBombs = count;
    }
    
    public void incrementAdjacentBombs() {
        this.adjacentBombs++;
    }
}
