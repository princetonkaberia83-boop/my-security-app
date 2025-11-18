
package javasecurity;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.Random;

public class SecurityApp extends JFrame {
    private JTextField websiteField, usernameField, passwordField, searchField;
    private JTextArea notesArea;
    private JTextArea displayArea;
    private JButton addButton, viewButton, updateButton, deleteButton, searchButton, generateButton;
    private Connection connection;
    
    // Database connection details
    private String url = "jdbc:mysql://localhost:3306/password_manager";
    private String user = "root";
    private String dbPassword = "";  // Changed to avoid variable conflict
    
    public SecurityApp() {
        createGUI();
        connectToDatabase();
    }
    
    public static void main(String[] args) {
          
        // Create and show the application window
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                SecurityApp app = new SecurityApp();
                app.setVisible(true);
            }
        });
    }
    
    private void connectToDatabase() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(url, user, dbPassword);
            showMessage("Connected to database successfully!");
        } catch (Exception e) {
            showMessage("Database connection failed: " + e.getMessage());
        }
    }
    
    private void createGUI() {
        setTitle("Password Manager - Security App");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        
        // Main panel with tabs
        JTabbedPane tabbedPane = new JTabbedPane();
        
        // Add Password Tab
        JPanel addPanel = createAddPanel();
        tabbedPane.addTab("Add Password", addPanel);
        
        // View Passwords Tab
        JPanel viewPanel = createViewPanel();
        tabbedPane.addTab("View Passwords", viewPanel);
        
        add(tabbedPane);
    }
    
    private JPanel createAddPanel() {
        JPanel panel = new JPanel(new GridLayout(6, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Website
        panel.add(new JLabel("Website:"));
        websiteField = new JTextField();
        panel.add(websiteField);
        
        // Username
        panel.add(new JLabel("Username:"));
        usernameField = new JTextField();
        panel.add(usernameField);
        
        // Password
        panel.add(new JLabel("Password:"));
        JPanel passwordPanel = new JPanel(new BorderLayout());
        passwordField = new JTextField();
        generateButton = new JButton("Generate");
        passwordPanel.add(passwordField, BorderLayout.CENTER);
        passwordPanel.add(generateButton, BorderLayout.EAST);
        panel.add(passwordPanel);
        
        // Notes
        panel.add(new JLabel("Notes:"));
        notesArea = new JTextArea(3, 20);
        JScrollPane notesScroll = new JScrollPane(notesArea);
        panel.add(notesScroll);
        
        // Buttons
        addButton = new JButton("Add Password");
        panel.add(addButton);
        
        // Empty cell for layout
        panel.add(new JLabel());
        
        // Add action listeners
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addPassword();
            }
        });
        
        generateButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                generatePassword();
            }
        });
        
        return panel;
    }
    
    private JPanel createViewPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Search panel
        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.add(new JLabel("Search:"), BorderLayout.WEST);
        searchField = new JTextField();
        searchPanel.add(searchField, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        searchButton = new JButton("Search");
        viewButton = new JButton("View All");
        updateButton = new JButton("Update");
        deleteButton = new JButton("Delete");
        
        buttonPanel.add(searchButton);
        buttonPanel.add(viewButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        
        searchPanel.add(buttonPanel, BorderLayout.EAST);
        
        // Display area
        displayArea = new JTextArea(20, 50);
        displayArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(displayArea);
        
        panel.add(searchPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Add action listeners
        viewButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                viewAllPasswords();
            }
        });
        
        searchButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                searchPasswords();
            }
        });
        
        updateButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updatePassword();
            }
        });
        
        deleteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                deletePassword();
            }
        });
        
        return panel;
    }
    
    private void addPassword() {
        String website = websiteField.getText().trim();
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();
        String notes = notesArea.getText().trim();
        
        if (website.isEmpty() || username.isEmpty() || password.isEmpty()) {
            showMessage("Please fill in website, username, and password!");
            return;
        }
        
        try {
            String encryptedPassword = simpleEncrypt(password);
            
            String sql = "INSERT INTO passwords (website, username, password, notes) VALUES (?, ?, ?, ?)";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, website);
            stmt.setString(2, username);
            stmt.setString(3, encryptedPassword);
            stmt.setString(4, notes);
            
            int rows = stmt.executeUpdate();
            if (rows > 0) {
                showMessage("Password added successfully!");
                clearFields();
            }
            stmt.close();
        } catch (SQLException e) {
            showMessage("Error adding password: " + e.getMessage());
        }
    }
    
    private void viewAllPasswords() {
        try {
            String sql = "SELECT * FROM passwords ORDER BY website";
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            
            displayArea.setText("");
            while (rs.next()) {
                int id = rs.getInt("id");
                String website = rs.getString("website");
                String username = rs.getString("username");
                String encryptedPassword = rs.getString("password");
                String notes = rs.getString("notes");
                
                String password = simpleDecrypt(encryptedPassword);
                
                displayArea.append("ID: " + id + "\n");
                displayArea.append("Website: " + website + "\n");
                displayArea.append("Username: " + username + "\n");
                displayArea.append("Password: " + password + "\n");
                displayArea.append("Notes: " + (notes != null ? notes : "") + "\n");
                displayArea.append("------------------------\n");
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            showMessage("Error viewing passwords: " + e.getMessage());
        }
    }
    
    private void searchPasswords() {
        String searchTerm = searchField.getText().trim();
        if (searchTerm.isEmpty()) {
            showMessage("Please enter a search term!");
            return;
        }
        
        try {
            String sql = "SELECT * FROM passwords WHERE website LIKE ? OR username LIKE ? ORDER BY website";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, "%" + searchTerm + "%");
            stmt.setString(2, "%" + searchTerm + "%");
            
            ResultSet rs = stmt.executeQuery();
            
            displayArea.setText("Search Results:\n\n");
            boolean found = false;
            while (rs.next()) {
                found = true;
                int id = rs.getInt("id");
                String website = rs.getString("website");
                String username = rs.getString("username");
                String encryptedPassword = rs.getString("password");
                String notes = rs.getString("notes");
                
                String password = simpleDecrypt(encryptedPassword);
                
                displayArea.append("ID: " + id + "\n");
                displayArea.append("Website: " + website + "\n");
                displayArea.append("Username: " + username + "\n");
                displayArea.append("Password: " + password + "\n");
                displayArea.append("Notes: " + (notes != null ? notes : "") + "\n");
                displayArea.append("------------------------\n");
            }
            
            if (!found) {
                displayArea.append("No passwords found for: " + searchTerm);
            }
            
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            showMessage("Error searching passwords: " + e.getMessage());
        }
    }
    
    private void updatePassword() {
        String input = JOptionPane.showInputDialog(this, "Enter ID of password to update:");
        if (input == null || input.trim().isEmpty()) return;
        
        try {
            int id = Integer.parseInt(input.trim());
            
            String sql = "SELECT * FROM passwords WHERE id = ?";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                JTextField updateWebsiteField = new JTextField(rs.getString("website"));
                JTextField updateUsernameField = new JTextField(rs.getString("username"));
                String currentPassword = simpleDecrypt(rs.getString("password"));
                JTextField updatePasswordField = new JTextField(currentPassword);
                JTextField updateNotesField = new JTextField(rs.getString("notes"));
                
                JPanel panel = new JPanel(new GridLayout(5, 2));
                panel.add(new JLabel("Website:"));
                panel.add(updateWebsiteField);
                panel.add(new JLabel("Username:"));
                panel.add(updateUsernameField);
                panel.add(new JLabel("Password:"));
                panel.add(updatePasswordField);
                panel.add(new JLabel("Notes:"));
                panel.add(updateNotesField);
                
                int result = JOptionPane.showConfirmDialog(this, panel, 
                    "Update Password", JOptionPane.OK_CANCEL_OPTION);
                
                if (result == JOptionPane.OK_OPTION) {
                    String updateSql = "UPDATE passwords SET website=?, username=?, password=?, notes=? WHERE id=?";
                    PreparedStatement updateStmt = connection.prepareStatement(updateSql);
                    updateStmt.setString(1, updateWebsiteField.getText());
                    updateStmt.setString(2, updateUsernameField.getText());
                    updateStmt.setString(3, simpleEncrypt(updatePasswordField.getText()));
                    updateStmt.setString(4, updateNotesField.getText());
                    updateStmt.setInt(5, id);
                    
                    int rows = updateStmt.executeUpdate();
                    if (rows > 0) {
                        showMessage("Password updated successfully!");
                        viewAllPasswords();
                    }
                    updateStmt.close();
                }
            } else {
                showMessage("Password with ID " + id + " not found!");
            }
            
            rs.close();
            stmt.close();
        } catch (Exception e) {
            showMessage("Error updating password: " + e.getMessage());
        }
    }
    
    private void deletePassword() {
        String input = JOptionPane.showInputDialog(this, "Enter ID of password to delete:");
        if (input == null || input.trim().isEmpty()) return;
        
        try {
            int id = Integer.parseInt(input.trim());
            
            int confirm = JOptionPane.showConfirmDialog(this, 
                "Are you sure you want to delete password with ID " + id + "?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                String sql = "DELETE FROM passwords WHERE id = ?";
                PreparedStatement stmt = connection.prepareStatement(sql);
                stmt.setInt(1, id);
                
                int rows = stmt.executeUpdate();
                if (rows > 0) {
                    showMessage("Password deleted successfully!");
                    viewAllPasswords();
                } else {
                    showMessage("Password with ID " + id + " not found!");
                }
                stmt.close();
            }
        } catch (Exception e) {
            showMessage("Error deleting password: " + e.getMessage());
        }
    }
    //yuh
    private void generatePassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%";
        Random random = new Random();
        StringBuilder password = new StringBuilder();
        
        for (int i = 0; i < 12; i++) {
            password.append(chars.charAt(random.nextInt(chars.length())));
        }
        
        passwordField.setText(password.toString());
    }
    
    // Simple encryption (for demonstration only)
    private String simpleEncrypt(String text) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            result.append((char)(c + 1));
        }
        return result.toString();
    }
    
    // Simple decryption
    private String simpleDecrypt(String text) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            result.append((char)(c - 1));
        }
        return result.toString();
    }
    
    private void clearFields() {
        websiteField.setText("");
        usernameField.setText("");
        passwordField.setText("");
        notesArea.setText("");
    }
    
    private void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message);
    }
}

