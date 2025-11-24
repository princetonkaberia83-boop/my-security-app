package javasecurity;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class MasterPassword {
    private Connection connection;
    
    public MasterPassword(Connection connection) {
        this.connection = connection;
    }
    
    public void initializeMasterPassword() {
        createMasterPasswordTable();
    }
    
    private void createMasterPasswordTable() {
        try {
            // Create the table if it doesn't exist
            String createTableSQL = 
                "CREATE TABLE IF NOT EXISTS master_password (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "password_hash VARCHAR(255) NOT NULL)";
            
            Statement createStmt = connection.createStatement();
            createStmt.execute(createTableSQL);
            createStmt.close();
            
            // Check if master password exists using a NEW statement
            String checkSQL = "SELECT COUNT(*) as count FROM master_password";
            Statement checkStmt = connection.createStatement();
            ResultSet rs = checkStmt.executeQuery(checkSQL);
            
            if (rs.next() && rs.getInt("count") == 0) {
                // Set default master password
                String defaultPassword = "admin123";
                String hashedPassword = hashPassword(defaultPassword);
                String insertSQL = "INSERT INTO master_password (password_hash) VALUES (?)";
                PreparedStatement pstmt = connection.prepareStatement(insertSQL);
                pstmt.setString(1, hashedPassword);
                pstmt.executeUpdate();
                pstmt.close();
                
                JOptionPane.showMessageDialog(null, 
                    "Default master password set to: admin123\nPlease change it immediately!",
                    "First Time Setup", JOptionPane.INFORMATION_MESSAGE);
            }
            rs.close();
            checkStmt.close();
        } catch (SQLException e) {
            System.out.println("Error creating master password table: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // Verify master password for login
    public boolean verifyMasterPassword(String password) {
        try {
            String sql = "SELECT password_hash FROM master_password WHERE id = 1";
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            
            if (rs.next()) {
                String storedHash = rs.getString("password_hash");
                String inputHash = hashPassword(password);
                boolean result = storedHash.equals(inputHash);
                
                rs.close();
                stmt.close();
                return result;
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            System.out.println("Error verifying password: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    public void changeMasterPassword() {
        try {
            // Verify current password first
            JPasswordField currentPassField = new JPasswordField(15);
            int result = JOptionPane.showConfirmDialog(null, currentPassField, 
                "Enter Current Master Password:", JOptionPane.OK_CANCEL_OPTION);
            
            if (result == JOptionPane.OK_OPTION) {
                char[] currentInput = currentPassField.getPassword();
                String currentPassword = new String(currentInput);
                
                // Verify current password
                if (!verifyMasterPassword(currentPassword)) {
                    JOptionPane.showMessageDialog(null, "Current password is incorrect!");
                    return;
                }
                
                // Get new password
                JPasswordField newPassField = new JPasswordField(15);
                JPasswordField confirmPassField = new JPasswordField(15);
                
                JPanel panel = new JPanel(new GridLayout(2, 2));
                panel.add(new JLabel("New Password:"));
                panel.add(newPassField);
                panel.add(new JLabel("Confirm Password:"));
                panel.add(confirmPassField);
                
                result = JOptionPane.showConfirmDialog(null, panel, 
                    "Change Master Password", JOptionPane.OK_CANCEL_OPTION);
                
                if (result == JOptionPane.OK_OPTION) {
                    char[] newPass = newPassField.getPassword();
                    char[] confirmPass = confirmPassField.getPassword();
                    
                    String newPassword = new String(newPass);
                    String confirmPassword = new String(confirmPass);
                    
                    if (newPassword.equals(confirmPassword)) {
                        if (newPassword.length() >= 4) {
                            String newHash = hashPassword(newPassword);
                            
                            String updateSQL = "UPDATE master_password SET password_hash = ? WHERE id = 1";
                            PreparedStatement pstmt = connection.prepareStatement(updateSQL);
                            pstmt.setString(1, newHash);
                            pstmt.executeUpdate();
                            pstmt.close();
                            
                            JOptionPane.showMessageDialog(null, "Password changed successfully!");
                        } else {
                            JOptionPane.showMessageDialog(null, "Password must be at least 4 characters long!");
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "Passwords do not match!");
                    }
                    
                    // Clear sensitive data
                    java.util.Arrays.fill(newPass, '0');
                    java.util.Arrays.fill(confirmPass, '0');
                }
                
                // Clear current password
                java.util.Arrays.fill(currentInput, '0');
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error changing password: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private String hashPassword(String password) {
        // Simple hash function for demonstration
        int hash = 7;
        for (int i = 0; i < password.length(); i++) {
            hash = hash * 31 + password.charAt(i);
        }
        return Integer.toString(hash);
    }
}