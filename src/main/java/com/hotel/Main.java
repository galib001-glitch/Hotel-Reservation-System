package com.hotel;

import com.hotel.auth.LoginFrame;
import com.hotel.dao.FirebaseConnection;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {

        // Check if we should run the seeder
        if (args.length > 0 && "seed".equalsIgnoreCase(args[0])) {
            runSeeder();
            return;
        }

        // Initialize Firebase connection
        try {
            FirebaseConnection.initialize();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                    "Failed to connect to Firebase: " + e.getMessage(),
                    "Connection Error",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        // Set Look and Feel (Corrected)
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            try {
                // Fallback
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            } catch (Exception ex) {
                System.err.println("Could not set look and feel: " + ex.getMessage());
            }
        }

        // Start application
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }

    // Seeder runs only when "seed" argument is passed
    private static void runSeeder() {
        try {
            System.out.println("Starting Database Seeder...");

            FirebaseConnection.initialize();

            com.hotel.dao.DatabaseSeeder seeder = new com.hotel.dao.DatabaseSeeder();
            seeder.seedAllData();
            seeder.displaySeedSummary();

            System.out.println("Seeder completed successfully!");

        } catch (Exception e) {
            System.err.println("Seeder failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
