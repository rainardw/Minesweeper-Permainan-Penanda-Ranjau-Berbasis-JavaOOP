import java.util.List;

public class Leaderboard {
    private PlayerDatabase database;

    public Leaderboard() {
        this.database = PlayerDatabase.getInstance();
    }

    public List<LeaderboardEntry> getTopEntries(int limit) {
        return database.getLeaderboardEntries(limit);
    }

    public LeaderboardStats getStats() {
        return database.getLeaderboardStats();
    }
}