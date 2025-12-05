import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
                // Run GUI on Event Dispatch Thread
        // SwingUtilities.invokeLater(new Runnable() {
        //     @Override
        //     public void run() {
        //         new LoginFrame();
        //     }
        // });
        
        SwingUtilities.invokeLater(() -> new LoginFrame());
    }
}
