/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package javasecurity;




    

import java.util.ArrayList;
public class PasswordManager
{
    private final ArrayList<PasswordEntry> storedPasswords;

    public PasswordManager() {
        storedPasswords = new ArrayList<>();
    }

    public ArrayList<String> generatePasswords(String guideline, int count) {
        ArrayList<String> list = new ArrayList<>();
        String[] words = guideline.split(" ");

        for (int i = 0; i < count; i++) {
            String base = words.length > 0 ? capitalize(words[i % words.length]) : "Secure";
            int randomNum = 100 + (int)(Math.random() * 900);
            list.add(base + "@" + randomNum);
        }
        return list;
    }

    public void storePassword(PasswordEntry entry) {
        storedPasswords.add(entry);
        System.out.println("✓ Password stored for account: " + entry.getAccount());
    }

    public void showStoredPasswords() {
        System.out.println("\n--- STORED PASSWORDS ---");

        if (storedPasswords.isEmpty()) {
            System.out.println("No passwords stored.");
            return;
        }

        for (PasswordEntry entry : storedPasswords) {
            System.out.println("• " + entry.getAccount() + " → " + entry.getPassword());
        }
    }

    private String capitalize(String word) {
        if (word.isEmpty()) return word;
        return word.substring(0, 1).toUpperCase() + word.substring(1);
    }
}