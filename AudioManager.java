import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

class AudioManager {
    private static AudioManager instance;
    
    private Clip backgroundMusic;
    private Clip clickSound;
    private Clip explosionSound;
    private Clip winSound;
    private Clip loseSound;
    private Clip flagSound;
    
    private boolean musicEnabled = true;
    private boolean soundEnabled = true;
    private float musicVolume = 0.8f;
    private float soundVolume = 0.8f; 
    
    private AudioManager() {
        loadAudio();
    }
    
    public static AudioManager getInstance() {
        if (instance == null) {
            instance = new AudioManager();
        }
        return instance;
    }
    
    private void loadAudio() {
        try {
            backgroundMusic = loadClip("assets/audio/background.wav");
            
            clickSound = loadClip("assets/audio/click.wav");
            explosionSound = loadClip("assets/audio/explosion.wav");
            winSound = loadClip("assets/audio/win.wav");
            loseSound = loadClip("assets/audio/lose.wav");
            
            flagSound = loadClip("assets/audio/click.wav"); 
            
            System.out.println("Audio loaded successfully!");
        } catch (Exception e) {
            System.err.println("Warning: Could not load audio files - " + e.getMessage());
            System.err.println("Game will continue without sound.");
        }
    }
    
    private Clip loadClip(String path) {
        try {
            File audioFile = new File(path);
            if (!audioFile.exists()) {
                System.err.println("Audio file not found: " + path);
                return null;
            }
            
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            
            return clip;
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.err.println("Could not load: " + path + " - " + e.getMessage());
            return null;
        }
    }
    
    public void playBackgroundMusic() {
        if (backgroundMusic != null && musicEnabled) {
            if (backgroundMusic.isRunning()) {
                backgroundMusic.stop();
            }
            backgroundMusic.setFramePosition(0);
            backgroundMusic.loop(Clip.LOOP_CONTINUOUSLY);
            setVolume(backgroundMusic, musicVolume);
        }
    }
    
    public void stopBackgroundMusic() {
        if (backgroundMusic != null && backgroundMusic.isRunning()) {
            backgroundMusic.stop();
        }
    }
    
    public void pauseBackgroundMusic() {
        if (backgroundMusic != null && backgroundMusic.isRunning()) {
            backgroundMusic.stop();
        }
    }
    
    public void resumeBackgroundMusic() {
        if (backgroundMusic != null && musicEnabled && !backgroundMusic.isRunning()) {
            backgroundMusic.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }
    
    public void playClickSound() {
        playSound(clickSound);
    }
    

    public void playExplosionSound() {
        playSound(explosionSound);
    }
    
    public void playWinSound() {
        playSound(winSound);
    }
    
    public void playLoseSound() {
        playSound(loseSound);
    }
    
    public void playFlagSound() {
        playSound(flagSound);
    }
    
    private void playSound(Clip clip) {
        if (clip != null && soundEnabled) {
            if (clip.isRunning()) {
                clip.stop();
            }
            clip.setFramePosition(0);
            setVolume(clip, soundVolume);
            clip.start();
        }
    }
    
    private void setVolume(Clip clip, float volume) {
        if (clip != null) {
            try {
                FloatControl volumeControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                float min = volumeControl.getMinimum();
                float max = volumeControl.getMaximum();
                float gain = min + (max - min) * volume;
                volumeControl.setValue(gain);
            } catch (Exception e) {
                
            }
        }
    }
    
    // ===== GETTER & SETTER =====
    
    public void setMusicEnabled(boolean enabled) {
        this.musicEnabled = enabled;
        if (!enabled) {
            stopBackgroundMusic();
        } else {
            playBackgroundMusic();
        }
    }
    
    public void setSoundEnabled(boolean enabled) {
        this.soundEnabled = enabled;
    }
    
    public void setMusicVolume(float volume) {
        this.musicVolume = Math.max(0.0f, Math.min(1.0f, volume));
        if (backgroundMusic != null) {
            setVolume(backgroundMusic, musicVolume);
        }
    }
    
    public void setSoundVolume(float volume) {
        this.soundVolume = Math.max(0.0f, Math.min(1.0f, volume));
    }
    
    public boolean isMusicEnabled() {
        return musicEnabled;
    }
    
    public boolean isSoundEnabled() {
        return soundEnabled;
    }
    
    public float getMusicVolume() {
        return musicVolume;
    }
    
    public float getSoundVolume() {
        return soundVolume;
    }
    
    public void cleanup() {
        if (backgroundMusic != null) {
            backgroundMusic.close();
        }
        if (clickSound != null) {
            clickSound.close();
        }
        if (explosionSound != null) {
            explosionSound.close();
        }
        if (winSound != null) {
            winSound.close();
        }
        if (loseSound != null) {
            loseSound.close();
        }
        if (flagSound != null) {
            flagSound.close();
        }
    }
}