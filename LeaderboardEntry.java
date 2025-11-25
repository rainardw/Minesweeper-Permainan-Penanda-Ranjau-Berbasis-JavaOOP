public class LeaderboardEntry {
    private int rank;
    private String username;
    private int score;
    private String scoreRank; // Contoh: "Master", "Beginner"

    public LeaderboardEntry(int rank, String username, int score, String scoreRank) {
        this.rank = rank;
        this.username = username;
        this.score = score;
        this.scoreRank = scoreRank;
    }

    public int getRank() { return rank; }
    public String getUsername() { return username; }
    public int getScore() { return score; }
    public String getScoreRank() { return scoreRank; }
}