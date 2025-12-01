import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

class LeaderboardFrame extends JDialog {
    // Palet Warna Minesweeper
    private static final Color MINESWEEPER_GREEN = Color.decode("#96dd11");
    private static final Color DARK_GREEN = Color.decode("#376238");
    
    // WARNA BARU (ORANGE EMAS) untuk baris tabel
    private static final Color STRIPE_COLOR = Color.decode("#f39c12");
    
    private Leaderboard leaderboard;
    private JTable table;
    
    public LeaderboardFrame(JFrame parent) {
        super(parent, "Leaderboard", true);
        
        leaderboard = new Leaderboard();
        
        try {
            setIconImage(new ImageIcon("assets/logo.png").getImage());
        } catch (Exception e) {}
        
        setSize(650, 600);
        setLocationRelativeTo(parent);
        setResizable(false);
        
        initComponents();
        setVisible(true);
    }
    
    private void initComponents() {
        // === BACKGROUND GRADIENT ===
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                GradientPaint gradient = new GradientPaint(
                    0, 0, DARK_GREEN,
                    0, getHeight(), MINESWEEPER_GREEN
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setLayout(new BorderLayout(15, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        
        // === HEADER JUDUL ===
        JLabel titleLabel = new JLabel("🏆 LEADERBOARD", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Monospaced", Font.BOLD, 32));
        titleLabel.setForeground(Color.WHITE);
        
        // === TABEL ===
        String[] columns = {"Rank", "Username", "Score", "Level"};
        
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable() {
                return false; // Semua sel tidak dapat diedit
            }
        };
        
        List<LeaderboardEntry> entries = leaderboard.getTopEntries(10);

        for (LeaderboardEntry entry : entries) {
            model.addRow(new Object[]{
                getRankEmoji(entry.getRank()) + " " + entry.getRank(),
                entry.getUsername(),
                entry.getScore(),
                entry.getScoreRank()
            });
        }
        
        table = new JTable(model);
        table.setFont(new Font("Monospaced", Font.PLAIN, 14));
        table.setRowHeight(40);
        table.setShowGrid(true);
        table.setIntercellSpacing(new Dimension(0, 2));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setFocusable(true);
        
        // Header Table
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Monospaced", Font.BOLD, 15));
        header.setBackground(DARK_GREEN);
        header.setPreferredSize(new Dimension(0, 45));
        
        // Kolom
        TableColumnModel cm = table.getColumnModel();
        cm.getColumn(0).setPreferredWidth(80);
        cm.getColumn(1).setPreferredWidth(200);
        cm.getColumn(2).setPreferredWidth(100);
        cm.getColumn(3).setPreferredWidth(120);
        
        // RENDERER WARNA-WARNI (Menggunakan STRIPE_COLOR / Emas)
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (row % 2 == 0) {
                    // Baris genap pakai warna Emas Pilihanmu
                    c.setBackground(STRIPE_COLOR); 
                } else {
                    // Baris ganjil Putih
                    c.setBackground(Color.WHITE);
                }
                
                c.setForeground(Color.BLACK);
                
                if (column == 1) setHorizontalAlignment(LEFT);
                else setHorizontalAlignment(CENTER);
                
                setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
                return c;
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        
        // === STATS PANEL ===
        LeaderboardStats stats = leaderboard.getStats();
        
        JPanel statsPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        statsPanel.setOpaque(false);
        statsPanel.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));
        
        statsPanel.add(createStatLabel("👥 Players: " + stats.getTotalPlayers()));
        statsPanel.add(createStatLabel("🔥 Highest: " + stats.getHighestScore()));
        statsPanel.add(createStatLabel("📊 Average: " + stats.getAverageScore()));
        statsPanel.add(createStatLabel("📉 Lowest: " + stats.getLowestScore()));
        
        // === TOMBOL CLOSE ===
        JButton closeButton = createStyledButton("Close");
        closeButton.addActionListener(e -> dispose());
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setOpaque(false);
        buttonPanel.add(closeButton);
        
        // Assembly Bawah
        JPanel bottomContainer = new JPanel(new BorderLayout(0, 15));
        bottomContainer.setOpaque(false);
        bottomContainer.add(statsPanel, BorderLayout.CENTER);
        bottomContainer.add(buttonPanel, BorderLayout.SOUTH);
        
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(bottomContainer, BorderLayout.SOUTH);
        
        add(mainPanel);
    }
    
    private JLabel createStatLabel(String text) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(new Font("Monospaced", Font.BOLD, 14));
        label.setForeground(Color.WHITE);
        // Background hitam transparan agar tulisan terbaca
        label.setOpaque(true);
        label.setBackground(new Color(0, 0, 0, 80)); 
        label.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        return label;
    }
    
    private String getRankEmoji(int rank) {
        if (rank == 1) return "🥇";
        if (rank == 2) return "🥈";
        if (rank == 3) return "🥉";
        return "🏅";
    }
    
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isPressed()) g2d.setColor(Color.LIGHT_GRAY);
                else if (getModel().isRollover()) g2d.setColor(Color.WHITE);
                else g2d.setColor(new Color(240, 240, 240));
                
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                g2d.setColor(DARK_GREEN);
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRoundRect(1, 1, getWidth()-2, getHeight()-2, 15, 15);
                
                g2d.setFont(getFont());
                FontMetrics fm = g2d.getFontMetrics();
                g2d.drawString(getText(), (getWidth() - fm.stringWidth(getText())) / 2, (getHeight() + fm.getAscent()) / 2 - 2);
            }
        };
        button.setPreferredSize(new Dimension(120, 45));
        button.setFont(new Font("Monospaced", Font.BOLD, 16));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        return button;
    }
}