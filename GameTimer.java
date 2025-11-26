public class GameTimer extends Thread {
    private int remainingTime;
    // private int timeLimit;
    private boolean running;
    private boolean paused;
    private GameTimerListener listener;
    
    public interface GameTimerListener {
        void onTimerUpdate(int remainingTime);
        void onTimeUp();
    }
    
    public GameTimer(int timeLimitInSeconds, GameTimerListener listener) {
        // this.timeLimit = timeLimitInSeconds;
        this.remainingTime = timeLimitInSeconds;
        this.running = false;
        this.paused = false;
        this.listener = listener;
    }
    
    @Override
    public void run() {
        running = true;
        
        while (running && remainingTime > 0) {
            if (!paused) {
                try {
                    Thread.sleep(1000); // Tunggu 1 detik
                    remainingTime--;    // Kurangi waktu
                    
                    if (listener != null) {
                        listener.onTimerUpdate(remainingTime);
                    }
                    
                    if (remainingTime <= 0) {
                        running = false;
                        if (listener != null) {
                            listener.onTimeUp();
                        }
                    }
                } catch (InterruptedException e) {
                    running = false;
                }
            } else {
                try {
                    Thread.sleep(100); // Sedikit delay saat pause
                } catch (InterruptedException e) {
                    running = false;
                }
            }
        }
    }

    public void setListener(GameTimerListener listener) {
        this.listener = listener;
    }
    
    public void pauseTimer() {
        paused = true;
    }
    
    public void resumeTimer() {
        paused = false;
    }
    
    public void stopTimer() {
        running = false;
    }
    
    public int getRemainingTime() {
        return remainingTime;
    }
    
    public String getFormattedTime() {
        int minutes = remainingTime / 60;
        int seconds = remainingTime % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }
    
    public boolean isRunning() {
        return running;
    }
}