import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

class LeaderboardFrame extends JDialog {
    private Leaderboard leaderboard;
    private JTable table;
    
    public LeaderboardFrame(JFrame parent) {
        super(parent, "Leaderboard", true);
        
        leaderboard = new Leaderboard();
        
        setSize(500, 400);
        setLocationRelativeTo(parent);
        setResizable(false);
        
        initComponents();
        setVisible(true);
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        
        JLabel titleLabel = new JLabel("LEADERBOARD", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        
        String[] columns = {"Rank", "Username", "Score", "Level"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        java.util.List<LeaderboardEntry> entries = leaderboard.getTopEntries(10);
        for (LeaderboardEntry entry : entries) {
            model.addRow(new Object[]{
                entry.getRank(),
                entry.getUsername(),
                entry.getScore(),
                entry.getScoreRank()
            });
        }
        
        table = new JTable(model);
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        table.setRowHeight(25);
        table.getColumnModel().getColumn(0).setPreferredWidth(50);
        table.getColumnModel().getColumn(1).setPreferredWidth(150);
        table.getColumnModel().getColumn(2).setPreferredWidth(100);
        table.getColumnModel().getColumn(3).setPreferredWidth(100);
        
        JScrollPane scrollPane = new JScrollPane(table);
        
        LeaderboardStats stats = leaderboard.getStats();
        JPanel statsPanel = new JPanel(new GridLayout(2, 2, 10, 5));
        statsPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        statsPanel.add(new JLabel("Total Players: " + stats.getTotalPlayers()));
        statsPanel.add(new JLabel("Highest Score: " + stats.getHighestScore()));
        statsPanel.add(new JLabel("Average Score: " + stats.getAverageScore()));
        statsPanel.add(new JLabel("Lowest Score: " + stats.getLowestScore()));
        
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dispose());
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(closeButton);
        
        add(titleLabel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(statsPanel, BorderLayout.CENTER);
        bottomPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(bottomPanel, BorderLayout.SOUTH);
    }
}