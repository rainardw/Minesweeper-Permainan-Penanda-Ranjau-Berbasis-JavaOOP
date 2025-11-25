public class LeaderboardStats {
    private int totalPlayers;
    private int highestScore;
    private double averageScore;
    private int lowestScore;

    public LeaderboardStats(int totalPlayers, int highestScore, double averageScore, int lowestScore) {
        this.totalPlayers = totalPlayers;
        this.highestScore = highestScore;
        this.averageScore = averageScore;
        this.lowestScore = lowestScore;
    }

    public int getTotalPlayers() { return totalPlayers; }
    public int getHighestScore() { return highestScore; }
    public String getAverageScore() { return String.format("%.2f", averageScore); }
    public int getLowestScore() { return lowestScore; }
}