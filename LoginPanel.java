import javax.swing.*;
import java.awt.*;

class LoginPanel extends JPanel {
        private final Color c1 = Color.decode("#e4c894");
        private final Color c2 = Color.decode("#d4bc94");
        private final int tileSize = 24;
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            int w = getWidth();
            int h = getHeight();
            for (int y = 0; y < h; y += tileSize) {
                for (int x = 0; x < w; x += tileSize) {
                    boolean even = ((x / tileSize) + (y / tileSize)) % 2 == 0;
                    g.setColor(even ? c1 : c2);
                    g.fillRect(x, y, tileSize, tileSize);
                }
            }
        }
    }