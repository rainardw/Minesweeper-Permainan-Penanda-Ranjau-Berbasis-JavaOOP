public class Player {
    private int idPlayer;
    private String username;
    private String password;
    private int highScore;
    
    public Player(int idPlayer, String username, String password) {
        this.idPlayer = idPlayer;
        this.username = username;
        this.password = password;
        this.highScore = 0;
    }
    
    // Getters
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
    
    // Setters
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