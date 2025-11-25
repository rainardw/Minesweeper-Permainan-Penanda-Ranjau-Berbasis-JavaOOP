import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PlayerDatabase {
    private static PlayerDatabase instance;
    
    // Konfigurasi Database (Sesuaikan password jika ada)
    private static final String DB_URL = "jdbc:mysql://localhost:3306/minesweeper_db";
    private static final String DB_USER = "root";
    private static final String DB_PASS = ""; 

    private PlayerDatabase() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.err.println("Driver MySQL tidak ditemukan! Pastikan library mysql-connector-j sudah ditambahkan.");
        }
    }

    public static PlayerDatabase getInstance() {
        if (instance == null) {
            instance = new PlayerDatabase();
        }
        return instance;
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
    }

    // --- LOGIN ---
    public Player login(String username, String password) {
        String sql = "SELECT * FROM players WHERE username = ? AND password = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                int id = rs.getInt("player_id");
                // Ambil High Score terkini dari tabel history
                int currentHighScore = getHighScoreFromHistory(id);
                
                Player player = new Player(id, username, password);
                player.setHighScore(currentHighScore); 
                return player;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Login gagal
    }

    // --- REGISTER ---
    public Player registerPlayer(String username, String password) {
        if (!isUsernameAvailable(username)) return null;

        String sql = "INSERT INTO players (username, password) VALUES (?, ?)";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int id = generatedKeys.getInt(1);
                        return new Player(id, username, password);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean isUsernameAvailable(String username) {
        String sql = "SELECT player_id FROM players WHERE username = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            return !rs.next(); // True jika tidak ditemukan
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // --- SIMPAN SKOR (INSERT HISTORY) ---
    public void addGameHistory(int playerId, int score) {
        String sql = "INSERT INTO game_history (player_id, score) VALUES (?, ?)";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, playerId);
            pstmt.setInt(2, score);
            pstmt.executeUpdate();
            System.out.println("History saved: PlayerID=" + playerId + ", Score=" + score);
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // --- AMBIL SKOR TERTINGGI (MAX) ---
    public int getHighScoreFromHistory(int playerId) {
        String sql = "SELECT MAX(score) as max_score FROM game_history WHERE player_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, playerId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("max_score");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0; // Jika belum pernah main
    }

    // --- LEADERBOARD ---
    public List<LeaderboardEntry> getLeaderboardEntries(int limit) {
        List<LeaderboardEntry> list = new ArrayList<>();
        
        String sql = "SELECT p.username, MAX(gh.score) as highest_score " +
                     "FROM players p " +
                     "JOIN game_history gh ON p.player_id = gh.player_id " +
                     "GROUP BY p.player_id, p.username " +
                     "ORDER BY highest_score DESC " +
                     "LIMIT ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, limit);
            ResultSet rs = pstmt.executeQuery();
            
            int rank = 1;
            while (rs.next()) {
                String username = rs.getString("username");
                int score = rs.getInt("highest_score");
                
                // [FIX] Menggunakan method helper internal getScoreRank()
                // Tidak lagi memanggil ScoreCalculator eksternal
                String rankTitle = getScoreRank(score);
                
                list.add(new LeaderboardEntry(rank++, username, score, rankTitle));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public LeaderboardStats getLeaderboardStats() {
        String sql = "SELECT COUNT(DISTINCT player_id) as total_p, " + 
                     "MAX(score) as max_s, AVG(score) as avg_s, MIN(score) as min_s " + 
                     "FROM game_history";
        
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return new LeaderboardStats(
                    rs.getInt("total_p"),
                    rs.getInt("max_s"),
                    rs.getDouble("avg_s"),
                    rs.getInt("min_s")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new LeaderboardStats(0, 0, 0, 0);
    }
    
    // --- [BARU] HELPER METHOD UNTUK MENENTUKAN RANK ---
    // Ini menggantikan fungsi ScoreCalculator.getScoreRank() yang hilang
    private String getScoreRank(int score) {
        if (score >= 5000) return "Master";
        if (score >= 3500) return "Expert";
        if (score >= 2000) return "Advanced";
        if (score >= 1000) return "Intermediate";
        return "Beginner";
    }
}