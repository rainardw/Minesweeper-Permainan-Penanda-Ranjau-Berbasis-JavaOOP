public class GameTimer extends Thread {
    private int remainingTime;
    private boolean running;
    private boolean paused;
    private GameTimerListener listener;
    // private GameTimerListener externalListener;
    
    public interface GameTimerListener {
        void onTimerUpdate(int remainingTime);
        void onTimeUp();
    }
    
    public GameTimer(int timeLimitInSeconds, GameTimerListener listener) {
        this.remainingTime = timeLimitInSeconds;
        this.running = false;
        this.paused = false;
        this.listener = listener;
        // this.externalListener = null;
    }
    
    @Override
    public void run() {
        running = true;
        
        while (running && remainingTime > 0) {
            if (!paused) {
                try {
                    Thread.sleep(1000); 
                    remainingTime--;    
                    
                    if (listener != null) {
                        listener.onTimerUpdate(remainingTime);
                    }
                    
                    // if (externalListener != null) {
                    //     externalListener.onTimerUpdate(remainingTime);
                    // }
                    
                    if (remainingTime <= 0) {
                        running = false;
                        
                        if (listener != null) {
                            listener.onTimeUp();
                        }
                        
                        // if (externalListener != null) {
                        //     externalListener.onTimeUp();
                        // }
                    }
                } catch (InterruptedException e) {
                    running = false;
                    Thread.currentThread().interrupt(); 
                }
            } else {
                try {
                    Thread.sleep(100); 
                } catch (InterruptedException e) {
                    running = false;
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    // public void setExternalListener(GameTimerListener externalListener) {
    //     this.externalListener = externalListener;
    // }
    
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
    
    public boolean isPaused() {
        return paused;
    }
}