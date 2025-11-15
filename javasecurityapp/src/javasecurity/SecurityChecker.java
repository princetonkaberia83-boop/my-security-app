/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package javasecurity;

/**
 *
 * @author kaber
 */


public class SecurityChecker {
    public void checkSecurity(String website) {
        System.out.print("Checking " + website + " → ");

        if (website.startsWith("https://")) {
            System.out.println("✓ SECURE (encrypted)");
        } else {
            System.out.println("✗ UNSECURE - Possible phishing!");
            System.out.println("  Warning: Site lacks HTTPS encryption.");
        }
    }
}