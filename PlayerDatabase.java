import java.util.ArrayList;
import java.util.List;
import java.util.Comparator;

public class PlayerDatabase {
    private static PlayerDatabase instance;
    private List<Player> players;
    private int nextId;
    
    private PlayerDatabase() {
        players = new ArrayList<>();
        nextId = 1;
        
        // Add some demo players for testing
        addDemoPlayers();
    }
    
    public static PlayerDatabase getInstance() {
        if (instance == null) {
            instance = new PlayerDatabase();
        }
        return instance;
    }
    
    private void addDemoPlayers() {
        // Optional: add demo data
        registerPlayer("admin", "admin123");
    }
    
    public Player registerPlayer(String username, String password) {
        // Check if username already exists
        if (findPlayerByUsername(username) != null) {
            return null; // Username already taken
        }
        
        Player newPlayer = new Player(nextId++, username, password);
        players.add(newPlayer);
        return newPlayer;
    }
    
    public Player login(String username, String password) {
        Player player = findPlayerByUsername(username);
        
        if (player != null && player.getPassword().equals(password)) {
            return player;
        }
        
        return null; // Invalid credentials
    }
    
    public Player findPlayerByUsername(String username) {
        for (Player player : players) {
            if (player.getUsername().equalsIgnoreCase(username)) {
                return player;
            }
        }
        return null;
    }
    
    public Player findPlayerById(int id) {
        for (Player player : players) {
            if (player.getIdPlayer() == id) {
                return player;
            }
        }
        return null;
    }
    
    public void updatePlayerScore(int playerId, int newScore) {
        Player player = findPlayerById(playerId);
        if (player != null) {
            player.setHighScore(newScore);
        }
    }
    
    public List<Player> getLeaderboard(int limit) {
        List<Player> sortedPlayers = new ArrayList<>(players);
        
        // Sort by high score descending
        sortedPlayers.sort(new Comparator<Player>() {
            @Override
            public int compare(Player p1, Player p2) {
                return Integer.compare(p2.getHighScore(), p1.getHighScore());
            }
        });
        
        // Return top N players
        if (limit > 0 && limit < sortedPlayers.size()) {
            return sortedPlayers.subList(0, limit);
        }
        
        return sortedPlayers;
    }
    
    public List<Player> getAllPlayers() {
        return new ArrayList<>(players);
    }
    
    public boolean isUsernameAvailable(String username) {
        return findPlayerByUsername(username) == null;
    }
    
    public int getTotalPlayers() {
        return players.size();
    }
}

// ScoreCalculator.java
public class ScoreCalculator {
    
    public static int calculateScore(int remainingTime, int revealedCells) {
        // Formula: (remaining time + revealed cells) * 5
        return (remainingTime + revealedCells) * 5;
    }
    
    public static int calculateScore(Game game) {
        int remainingTime = game.getTimer().getRemainingTime();
        int revealedCells = game.getBoard().getRevealedCells();
        return calculateScore(remainingTime, revealedCells);
    }
    
    public static String getScoreRank(int score, Difficulty difficulty) {
        int threshold = 0;
        
        switch (difficulty) {
            case EASY:
                if (score >= 2000) return "Master";
                if (score >= 1500) return "Expert";
                if (score >= 1000) return "Advanced";
                if (score >= 500) return "Intermediate";
                return "Beginner";
                
            case NORMAL:
                if (score >= 5000) return "Master";
                if (score >= 3500) return "Expert";
                if (score >= 2000) return "Advanced";
                if (score >= 1000) return "Intermediate";
                return "Beginner";
                
            case HARD:
                if (score >= 10000) return "Master";
                if (score >= 7000) return "Expert";
                if (score >= 4000) return "Advanced";
                if (score >= 2000) return "Intermediate";
                return "Beginner";
                
            default:
                return "Beginner";
        }
    }
}