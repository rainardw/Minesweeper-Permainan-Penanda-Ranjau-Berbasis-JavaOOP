import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

class LoginFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;
    private PlayerDatabase database;
    
    final Color minesweeperGreen = Color.decode("#96dd11");
    final Color pressedGreen = minesweeperGreen.darker();
    final Color defaultBg = UIManager.getColor("Button.background");

    public LoginFrame() {
        database = PlayerDatabase.getInstance();
        
        setTitle("Minesweeper - Login");
        setIconImage(new ImageIcon("assets/logo.png").getImage());
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        
        initComponents();
        setVisible(true);
    }
    
    private void initComponents() {
        LoginPanel content = new LoginPanel();
        content.setLayout(new GridBagLayout());
        setContentPane(content);
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 40, 10, 40));
        mainPanel.setOpaque(false);

        ImageIcon logoIcon = null;
        try {
            logoIcon = new ImageIcon("assets/logo.png");
            Image scaled = logoIcon.getImage().getScaledInstance(44, 44, Image.SCALE_SMOOTH);
            logoIcon = new ImageIcon(scaled);
        } catch (Exception e) {
            logoIcon = null;
        }
        
        ImageIcon smileIcon = null;
        try {
            smileIcon = new ImageIcon("assets/smile.png");
            Image scaled = smileIcon.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH);
            smileIcon = new ImageIcon(scaled);
        } catch (Exception e) {
            smileIcon = null;
        }

        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        titlePanel.setOpaque(false);
        
        JLabel titleLabel = new JLabel("MINESWEEPER");
        titleLabel.setFont(new Font("Monospaced", Font.BOLD, 28));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel subtitleLabel = new JLabel("Login to Play");
        subtitleLabel.setFont(new Font("Monospaced", Font.PLAIN, 14));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel logoLabel = new JLabel();
        if (logoIcon != null) {
            logoLabel.setIcon(logoIcon);
        } else {
            logoLabel.setText(" ");
        }
        logoLabel.setOpaque(false);
        
        JPanel subtitlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 0));
        subtitlePanel.setOpaque(false);
        JLabel smileLabel = new JLabel();
        if (smileIcon != null) {
            smileLabel.setIcon(smileIcon);
        } else {
            smileLabel.setText(" ");
        }
        smileLabel.setOpaque(false);

        subtitlePanel.add(smileLabel);
        subtitlePanel.add(subtitleLabel);
        titlePanel.add(logoLabel);
        titlePanel.add(titleLabel);
        
        JPanel usernamePanel = new JPanel(new BorderLayout(10, 0));
        usernamePanel.setOpaque(false);
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(new Font("Monospaced", Font.PLAIN, 14));
        usernameField = new JTextField();
        usernamePanel.add(usernameLabel, BorderLayout.WEST);
        usernamePanel.add(usernameField, BorderLayout.CENTER);
        
        JPanel passwordPanel = new JPanel(new BorderLayout(10, 0));
        passwordPanel.setOpaque(false);
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Monospaced", Font.PLAIN, 14));
        passwordField = new JPasswordField();
        passwordPanel.add(passwordLabel, BorderLayout.WEST);
        passwordPanel.add(passwordField, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setOpaque(false);
        loginButton = new JButton("Login");
        loginButton.setPreferredSize(new Dimension(100, 30));
        loginButton.setFont(new Font("Monospaced", Font.PLAIN, 12));
        loginButton.setOpaque(true);
        loginButton.setFocusPainted(false);
        loginButton.setBorderPainted(false);
        loginButton.setBackground(defaultBg);
        loginButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        registerButton = new JButton("Register");
        registerButton.setPreferredSize(new Dimension(100, 30));
        registerButton.setFont(new Font("Monospaced", Font.PLAIN, 12));
        registerButton.setOpaque(true);
        registerButton.setFocusPainted(false);
        registerButton.setBorderPainted(false);
        registerButton.setBackground(defaultBg);
        registerButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        loginButton.addMouseListener(hoverPress);
        registerButton.addMouseListener(hoverPress);
        
        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);
        
        mainPanel.add(titlePanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        mainPanel.add(subtitlePanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        mainPanel.add(usernamePanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        mainPanel.add(passwordPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        mainPanel.add(buttonPanel);
        
        // tambahkan mainPanel ke contentPane yang menggunakan GridBagLayout agar ter-center
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        getContentPane().add(mainPanel, gbc);
        
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

    MouseAdapter hoverPress = new MouseAdapter() {
        @Override
        public void mouseEntered(MouseEvent e) {
            ((JButton) e.getComponent()).setBackground(minesweeperGreen);
        }
        @Override
        public void mouseExited(MouseEvent e) {
            ((JButton) e.getComponent()).setBackground(defaultBg);
        }
        @Override
        public void mousePressed(MouseEvent e) {
            ((JButton) e.getComponent()).setBackground(pressedGreen);
        }
        @Override
        public void mouseReleased(MouseEvent e) {
            JButton b = (JButton) e.getComponent();
            // jika kursor masih di atas tombol setelah release, kembali ke hover warna
            Point p = SwingUtilities.convertPoint(b, e.getPoint(), b);
            if (b.contains(p)) b.setBackground(minesweeperGreen);
            else b.setBackground(defaultBg);
        }
    };
    
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
