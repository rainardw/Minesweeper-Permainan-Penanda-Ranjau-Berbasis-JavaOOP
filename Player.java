public class Player {
    private int idPlayer; 
    private String username;
    private String password;
    private int highScore;
    private int gamesPlayed;
    
    public Player(int idPlayer, String username, String password) {
        this.idPlayer = idPlayer;
        this.username = username;
        this.password = password;
        this.highScore = 0;
        this.gamesPlayed = 0;
    }
    
    public int getIdPlayer() {
        return idPlayer;
    }
    
    public String getUsername() {
        return username;
    }
    
    public String getPassword() {
        return password;
    }
    
    public int getHighScore() {
        return highScore;
    }

    public int getGamesPlayed() {
        return gamesPlayed;
    }

    public void setGamesPlayed(int gamesPlayed) {
        this.gamesPlayed = gamesPlayed;
    }
    
    public void setHighScore(int score) {
        if (score > this.highScore) {
            this.highScore = score;
        }
    }
    
    @Override
    public String toString() {
        return "Player{" +
                "id=" + idPlayer +
                ", username='" + username + '\'' +
                ", highScore=" + highScore +
                '}';
    }
}