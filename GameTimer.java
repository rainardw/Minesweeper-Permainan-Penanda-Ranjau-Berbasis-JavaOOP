public class GameTimer extends Thread {
    private int elapsedTime;
    private int timeLimit;
    private boolean running;
    private boolean paused;
    private GameTimerListener listener;
    
    public interface GameTimerListener {
        void onTimerUpdate(int elapsedTime);
        void onTimeUp();
    }
    
    public GameTimer(int timeLimit, GameTimerListener listener) {
        this.timeLimit = timeLimit;
        this.elapsedTime = 0;
        this.running = false;
        this.paused = false;
        this.listener = listener;
    }
    
    @Override
    public void run() {
        running = true;
        
        while (running && elapsedTime < timeLimit) {
            if (!paused) {
                try {
                    Thread.sleep(1000); // 1 second
                    elapsedTime++;
                    
                    if (listener != null) {
                        listener.onTimerUpdate(elapsedTime);
                    }
                    
                    if (elapsedTime >= timeLimit) {
                        if (listener != null) {
                            listener.onTimeUp();
                        }
                    }
                } catch (InterruptedException e) {
                    running = false;
                }
            } else {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    running = false;
                }
            }
        }
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
    
    public int getElapsedTime() {
        return elapsedTime;
    }
    
    public int getRemainingTime() {
        return timeLimit - elapsedTime;
    }
    
    public String getFormattedTime() {
        int minutes = elapsedTime / 60;
        int seconds = elapsedTime % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }
    
    public String getFormattedRemainingTime() {
        int remaining = getRemainingTime();
        int minutes = remaining / 60;
        int seconds = remaining % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }
    
    public boolean isRunning() {
        return running;
    }
    
    public boolean isPaused() {
        return paused;
    }
}