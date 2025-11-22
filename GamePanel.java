import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

class GamePanel extends JPanel {
    private Game game;
    private Board board;
    private static final int CELL_SIZE = 30;
    private static final int CELL_PADDING = 2;
    
    private static final Color COLOR_COVERED = new Color(189, 189, 189);
    private static final Color COLOR_REVEALED = new Color(215, 215, 215);
    private static final Color COLOR_BOMB = new Color(255, 100, 100);
    private static final Color COLOR_FLAG = new Color(255, 200, 0);
    private static final Color[] NUMBER_COLORS = {
        Color.BLACK,
        new Color(0, 0, 255),
        new Color(0, 128, 0),
        new Color(255, 0, 0),
        new Color(0, 0, 128),
        new Color(128, 0, 0),
        new Color(0, 128, 128),
        Color.BLACK,
        Color.GRAY
    };
    
    public GamePanel(Game game) {
        this.game = game;
        this.board = game.getBoard();
        
        int width = board.getCols() * CELL_SIZE;
        int height = board.getRows() * CELL_SIZE;
        setPreferredSize(new Dimension(width, height));
        
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                handleMouseClick(e);
            }
        });
    }
    
    public void setGame(Game game) {
        this.game = game;
        this.board = game.getBoard();
    }
    
    private void handleMouseClick(MouseEvent e) {
        int col = e.getX() / CELL_SIZE;
        int row = e.getY() / CELL_SIZE;
        
        if (board.isValidCell(row, col)) {
            boolean isRightClick = SwingUtilities.isRightMouseButton(e);
            game.handleCellClick(row, col, isRightClick);
            repaint();
        }
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        Cell[][] grid = board.getGrid();
        
        for (int row = 0; row < board.getRows(); row++) {
            for (int col = 0; col < board.getCols(); col++) {
                drawCell(g2d, grid[row][col], row, col);
            }
        }
    }
    
    private void drawCell(Graphics2D g, Cell cell, int row, int col) {
        int x = col * CELL_SIZE;
        int y = row * CELL_SIZE;
        
        if (cell.isRevealed()) {
            if (cell.isBomb()) {
                g.setColor(COLOR_BOMB);
            } else {
                g.setColor(COLOR_REVEALED);
            }
        } else {
            g.setColor(COLOR_COVERED);
        }
        
        g.fillRect(x + CELL_PADDING, y + CELL_PADDING, 
                   CELL_SIZE - CELL_PADDING * 2, CELL_SIZE - CELL_PADDING * 2);
        
        g.setColor(Color.GRAY);
        g.drawRect(x + CELL_PADDING, y + CELL_PADDING, 
                   CELL_SIZE - CELL_PADDING * 2, CELL_SIZE - CELL_PADDING * 2);
        
        if (cell.isRevealed()) {
            if (cell.isBomb()) {
                drawBomb(g, x, y);
            } else if (cell.getAdjacentBombs() > 0) {
                drawNumber(g, cell.getAdjacentBombs(), x, y);
            }
        } else if (cell.isFlagged()) {
            drawFlag(g, x, y);
        }
    }
    
    private void drawBomb(Graphics2D g, int x, int y) {
        g.setColor(Color.BLACK);
        int centerX = x + CELL_SIZE / 2;
        int centerY = y + CELL_SIZE / 2;
        int radius = 8;
        
        g.fillOval(centerX - radius, centerY - radius, radius * 2, radius * 2);
        
        g.setStroke(new BasicStroke(2));
        g.drawLine(centerX, centerY - radius - 3, centerX, centerY + radius + 3);
        g.drawLine(centerX - radius - 3, centerY, centerX + radius + 3, centerY);
    }
    
    private void drawFlag(Graphics2D g, int x, int y) {
        g.setColor(COLOR_FLAG);
        int centerX = x + CELL_SIZE / 2;
        int centerY = y + CELL_SIZE / 2;
        
        g.setColor(Color.BLACK);
        g.setStroke(new BasicStroke(2));
        g.drawLine(centerX, centerY - 8, centerX, centerY + 8);
        
        g.setColor(COLOR_FLAG);
        int[] xPoints = {centerX, centerX, centerX + 10};
        int[] yPoints = {centerY - 8, centerY + 2, centerY - 3};
        g.fillPolygon(xPoints, yPoints, 3);
    }
    
    private void drawNumber(Graphics2D g, int number, int x, int y) {
        g.setColor(NUMBER_COLORS[number]);
        g.setFont(new Font("Arial", Font.BOLD, 18));
        
        FontMetrics fm = g.getFontMetrics();
        String text = String.valueOf(number);
        int textX = x + (CELL_SIZE - fm.stringWidth(text)) / 2;
        int textY = y + (CELL_SIZE + fm.getAscent()) / 2 - 2;
        
        g.drawString(text, textX, textY);
    }
}
