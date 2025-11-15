/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package javasecurity;


import java.util.Scanner;
import java.util.ArrayList;

public class SecuritySystem {
    private final PasswordManager passwordManager;
    private final ContactManager contactManager;
    private final SecurityChecker securityChecker;
    private final AppProtector appProtector;
    private final Scanner scanner;

    // Menu constants
    private static final int GENERATE_PASSWORD = 1;
    private static final int STORE_PASSWORD = 2;
    private static final int VIEW_PASSWORDS = 3;
    private static final int ADD_CONTACT = 4;
    private static final int VIEW_CONTACTS = 5;
    private static final int EMERGENCY_ACCESS = 6;
    private static final int CHECK_SECURITY = 7;
    private static final int PROTECT_APP = 8;
    private static final int EXIT = 0;

    public SecuritySystem() {
        passwordManager = new PasswordManager();
        contactManager = new ContactManager();
        securityChecker = new SecurityChecker();
        appProtector = new AppProtector();
        scanner = new Scanner(System.in);
    }

    public void start() {
        System.out.println("=== SECURITY APP STARTED ===");
        System.out.println("Radial transparency enabled - UI secured\n");
        showMenu();
    }

    private void showMenu() {
        int choice = -1;

        while (choice != EXIT) {
            System.out.println("\n========= MAIN MENU =========");
            System.out.println("1. Generate Password Suggestions");
            System.out.println("2. Store a Password");
            System.out.println("3. View Stored Passwords");
            System.out.println("4. Add Trusted Contact");
            System.out.println("5. View Trusted Contacts");
            System.out.println("6. Grant Emergency Access");
            System.out.println("7. Check Website Security");
            System.out.println("8. Protect App (Block Screenshot)");
            System.out.println("0. Exit");
            System.out.print("Choose an option: ");

            try {
                choice = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
                continue;
            }

            switch (choice) {
                case GENERATE_PASSWORD -> generatePasswordMenu();
                case STORE_PASSWORD -> storePasswordMenu();
                case VIEW_PASSWORDS -> passwordManager.showStoredPasswords();
                case ADD_CONTACT -> addContactMenu();
                case VIEW_CONTACTS -> contactManager.showContacts();
                case EMERGENCY_ACCESS -> contactManager.grantEmergencyAccess(passwordManager);
                case CHECK_SECURITY -> checkSecurityMenu();
                case PROTECT_APP -> {
                    appProtector.simulateBlockScreenshot();
                    appProtector.simulateBlockScreenRecording();
                }
                case EXIT -> System.out.println("Exiting... Stay safe!");
                default -> System.out.println("Invalid choice. Try again.");
            }
        }
    }

    private void generatePasswordMenu() {
        System.out.print("Enter password guideline (e.g., 'summer vacation'): ");
        String guideline = scanner.nextLine();

        System.out.print("How many suggestions do you want? ");
        int count = Integer.parseInt(scanner.nextLine());

        ArrayList<String> suggestions = passwordManager.generatePasswords(guideline, count);

        System.out.println("\n--- PASSWORD SUGGESTIONS ---");
        for (String s : suggestions) {
            System.out.println("• " + s);
        }
    }

    private void storePasswordMenu() {
        System.out.print("Enter account name: ");
        String account = scanner.nextLine();

        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        passwordManager.storePassword(new PasswordEntry(account, password));
    }

    private void addContactMenu() {
        System.out.print("Enter contact name: ");
        String name = scanner.nextLine();

        System.out.print("Enter contact phone: ");
        String phone = scanner.nextLine();

        contactManager.addContact(new Contact(name, phone));
    }

    private void checkSecurityMenu() {
        System.out.print("Enter website URL: ");
        String url = scanner.nextLine();
        securityChecker.checkSecurity(url);
    }
}