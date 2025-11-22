import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

class LoginFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;
    private PlayerDatabase database;
    
    public LoginFrame() {
        database = PlayerDatabase.getInstance();
        
        setTitle("Minesweeper - Login");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        
        initComponents();
        setVisible(true);
    }
    
    private void initComponents() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        
        JLabel titleLabel = new JLabel("MINESWEEPER");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel subtitleLabel = new JLabel("Login to Play");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JPanel usernamePanel = new JPanel(new BorderLayout(10, 0));
        JLabel usernameLabel = new JLabel("Username:");
        usernameField = new JTextField();
        usernamePanel.add(usernameLabel, BorderLayout.WEST);
        usernamePanel.add(usernameField, BorderLayout.CENTER);
        
        JPanel passwordPanel = new JPanel(new BorderLayout(10, 0));
        JLabel passwordLabel = new JLabel("Password:");
        passwordField = new JPasswordField();
        passwordPanel.add(passwordLabel, BorderLayout.WEST);
        passwordPanel.add(passwordField, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        loginButton = new JButton("Login");
        registerButton = new JButton("Register");
        
        loginButton.setPreferredSize(new Dimension(100, 30));
        registerButton.setPreferredSize(new Dimension(100, 30));
        
        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);
        
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        mainPanel.add(subtitleLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        mainPanel.add(usernamePanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        mainPanel.add(passwordPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        mainPanel.add(buttonPanel);
        
        add(mainPanel);
        
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleLogin();
            }
        });
        
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleRegister();
            }
        });
        
        passwordField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleLogin();
            }
        });
    }
    
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Please enter username and password!", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        Player player = database.login(username, password);
        
        if (player != null) {
            JOptionPane.showMessageDialog(this, 
                "Welcome back, " + username + "!", 
                "Login Successful", 
                JOptionPane.INFORMATION_MESSAGE);
            
            dispose();
            new MenuFrame(player);
        } else {
            JOptionPane.showMessageDialog(this, 
                "Invalid username or password!", 
                "Login Failed", 
                JOptionPane.ERROR_MESSAGE);
            passwordField.setText("");
        }
    }
    
    private void handleRegister() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Please enter username and password!", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (username.length() < 3) {
            JOptionPane.showMessageDialog(this, 
                "Username must be at least 3 characters!", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (password.length() < 4) {
            JOptionPane.showMessageDialog(this, 
                "Password must be at least 4 characters!", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        Player player = database.registerPlayer(username, password);
        
        if (player != null) {
            JOptionPane.showMessageDialog(this, 
                "Registration successful! You can now login.", 
                "Success", 
                JOptionPane.INFORMATION_MESSAGE);
            
            passwordField.setText("");
        } else {
            JOptionPane.showMessageDialog(this, 
                "Username already taken!", 
                "Registration Failed", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
}
