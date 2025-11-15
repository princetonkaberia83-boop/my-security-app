/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package javasecurity;

/**
 *
 * @author kaber
 */

import java.util.ArrayList;

public class ContactManager {
    private final ArrayList<Contact> trustedContacts;

    public ContactManager() {
        trustedContacts = new ArrayList<>();
    }

    public void addContact(Contact contact) {
        trustedContacts.add(contact);
        System.out.println("✓ Added trusted contact: " + contact.getName());
    }

    public void showContacts() {
        System.out.println("\n--- TRUSTED CONTACTS ---");

        if (trustedContacts.isEmpty()) {
            System.out.println("No contacts added.");
            return;
        }

        for (Contact c : trustedContacts) {
            System.out.println("• " + c.getName() + " - " + c.getPhone());
        }
    }

    public void grantEmergencyAccess(PasswordManager manager) {
        System.out.println("EMERGENCY ACCESS GRANTED!");
        System.out.println("Trusted contacts can now view stored passwords:");
        manager.showStoredPasswords();
    }
}