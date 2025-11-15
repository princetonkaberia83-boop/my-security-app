/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package javasecurity;

/**
 *
 * @author kaber
 */
public class PasswordEntry {

  

 
    private final String account;
    private final String password;

    public PasswordEntry(String account, String password) {
        this.account = account;
        this.password = password;
    }

    public String getAccount() { return account; }
    public String getPassword() { return password; }
}