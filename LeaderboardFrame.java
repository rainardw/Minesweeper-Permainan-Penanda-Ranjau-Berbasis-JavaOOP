import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.border.EmptyBorder;
import java.awt.*;

class LeaderboardFrame extends JDialog {
    private Leaderboard leaderboard;
    private JTable table;

    // theme colors
    private final Color HEADER_BG = Color.decode("#005f00");
    private final Color ROW_BG = Color.decode("#96dd11");
    private final Color MINESWEEPER_GREEN = Color.decode("#96dd11");
    private final Color defaultBg = UIManager.getColor("Button.background");

    public LeaderboardFrame(JFrame parent) {
        super(parent, "Leaderboard", true);
        setIconImage(new ImageIcon("assets/logo.png").getImage());

        leaderboard = new Leaderboard();

        setSize(540, 460);
        setLocationRelativeTo(parent);
        setResizable(false);

        initComponents();
        setVisible(true);
    }

    private void initComponents() {
        // Use same background panel as Login/Menu
        LoginPanel background = new LoginPanel();
        background.setLayout(new BorderLayout(10, 10));
        setContentPane(background);

        // Title
        JLabel titleLabel = new JLabel("LEADERBOARD", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Monospaced", Font.BOLD, 26));
        titleLabel.setBorder(new EmptyBorder(12, 0, 12, 0));
        titleLabel.setForeground(Color.BLACK);

        String[] columns = {"Rank", "Username", "Score", "Level"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };

        java.util.List<LeaderboardEntry> entries = leaderboard.getTopEntries(50);
        for (LeaderboardEntry entry : entries) {
            model.addRow(new Object[]{
                entry.getRank(),
                entry.getUsername(),
                entry.getScore(),
                entry.getScoreRank()
            });
        }

        table = new JTable(model);
        table.setFont(new Font("Monospaced", Font.PLAIN, 14));
        table.setRowHeight(30);
        table.setShowGrid(true);
        table.setIntercellSpacing(new Dimension(1, 1));
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        table.setGridColor(HEADER_BG.darker());

        table.setSelectionBackground(ROW_BG.darker());
        table.setSelectionForeground(Color.WHITE);
        table.setForeground(Color.BLACK);
        table.setBackground(ROW_BG);
        table.setOpaque(false);
        table.setFillsViewportHeight(true);

        // Header styling
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Monospaced", Font.BOLD, 14));
        header.setOpaque(true);
        header.setBackground(HEADER_BG);
        header.setForeground(Color.WHITE);
        header.setReorderingAllowed(false);
        header.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 36));

        // custom header renderer to ensure background always applied
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel c = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                c.setHorizontalAlignment(SwingConstants.CENTER);
                c.setBackground(HEADER_BG);
                c.setForeground(Color.WHITE);
                c.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, HEADER_BG.darker()));
                return c;
            }
        });

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            private final Color BORDER_COLOR = HEADER_BG.darker();
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                           boolean hasFocus, int row, int column) {
                JLabel c = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                c.setHorizontalAlignment(SwingConstants.CENTER);
                if (isSelected) {
                    c.setBackground(table.getSelectionBackground());
                    c.setForeground(table.getSelectionForeground());
                } else {
                    c.setBackground(ROW_BG);
                    c.setForeground(Color.BLACK);
                }
                // apply subtle border so cell edges are visible (table shape)
                c.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, BORDER_COLOR));
                return c;
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.DARK_GRAY, 0),
            BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));
        // make upper-left corner same color as header
        JPanel corner = new JPanel();
        corner.setBackground(HEADER_BG);
        scrollPane.setCorner(JScrollPane.UPPER_LEFT_CORNER, corner);
         scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setWheelScrollingEnabled(true);
        table.setAutoscrolls(true);

 

        // Stats panel (transparent background so checkered shows)
        LeaderboardStats stats = leaderboard.getStats();
        JPanel statsPanel = new JPanel(new GridLayout(2, 2, 8, 6));
        statsPanel.setBorder(new EmptyBorder(12, 12, 12, 12));
        statsPanel.setOpaque(false);
        JLabel totalPlayers = new JLabel("Total Players: " + stats.getTotalPlayers());
        JLabel highestScore = new JLabel("Highest Score: " + stats.getHighestScore());
        JLabel avgScore = new JLabel("Average Score: " + stats.getAverageScore());
        JLabel lowestScore = new JLabel("Lowest Score: " + stats.getLowestScore());
        // style labels
        for (JLabel l : new JLabel[]{totalPlayers, highestScore, avgScore, lowestScore}) {
            l.setFont(new Font("Monospaced", Font.PLAIN, 13));
            l.setForeground(Color.BLACK);
        }
        statsPanel.add(totalPlayers);
        statsPanel.add(highestScore);
        statsPanel.add(avgScore);
        statsPanel.add(lowestScore);

        // Close button - minesweeper green with hover/press
        JButton closeButton = new JButton("Close");
        closeButton.setPreferredSize(new Dimension(90, 34));
        closeButton.setFont(new Font("Monospaced", Font.PLAIN, 13));
        closeButton.setBackground(defaultBg);
        closeButton.setOpaque(true);
        closeButton.setFocusPainted(false);
        closeButton.setBorderPainted(false);
        closeButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        closeButton.addActionListener(e -> dispose());
        // hover/press effect
        closeButton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) { closeButton.setBackground(MINESWEEPER_GREEN); }
            @Override public void mouseExited(java.awt.event.MouseEvent e) { closeButton.setBackground(defaultBg); }
            @Override public void mousePressed(java.awt.event.MouseEvent e) { closeButton.setBackground(MINESWEEPER_GREEN.darker().darker()); }
            @Override public void mouseReleased(java.awt.event.MouseEvent e) { closeButton.setBackground(MINESWEEPER_GREEN); }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.add(closeButton);

        // layout
        add(titleLabel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);
        bottomPanel.add(statsPanel, BorderLayout.CENTER);
        bottomPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(bottomPanel, BorderLayout.SOUTH);
    }
}