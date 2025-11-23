package com.hotel.ui;

import com.hotel.auth.AuthService;
import com.hotel.auth.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainDashboard extends JFrame {
    private AuthService authService;
    private JTabbedPane tabbedPane;

    public MainDashboard(AuthService authService) {
        this.authService = authService;
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Hotel Reservation System - Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);

        // Create menu bar
        createMenuBar();

        // Create main content
        tabbedPane = new JTabbedPane();

        // Add panels based on user role
        User currentUser = authService.getCurrentUser();

        // Guest Management - Available for all roles
        tabbedPane.addTab("Guest Management", new GuestManagementPanel());

        // Room Management - Available for receptionist and above
        if (authService.hasPermission(User.UserRole.RECEPTIONIST)) {
            tabbedPane.addTab("Room Management", new RoomManagementPanel());
        }

        // Booking System - Available for receptionist and above
        if (authService.hasPermission(User.UserRole.RECEPTIONIST)) {
            tabbedPane.addTab("Booking System", new BookingPanel());
        }

        // Payment Management - Available for receptionist and above
        if (authService.hasPermission(User.UserRole.RECEPTIONIST)) {
            tabbedPane.addTab("Payment Management", new PaymentPanel());
        }

        // Reports - Available for manager and above
        if (authService.hasPermission(User.UserRole.MANAGER)) {
            tabbedPane.addTab("Reports", new ReportsPanel());
        }

        add(tabbedPane);

        // Status bar
        add(createStatusBar(), BorderLayout.SOUTH);
    }

    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // File menu
        JMenu fileMenu = new JMenu("File");
        JMenuItem logoutItem = new JMenuItem("Logout");
        JMenuItem exitItem = new JMenuItem("Exit");

        logoutItem.addActionListener(e -> logout());
        exitItem.addActionListener(e -> System.exit(0));

        fileMenu.add(logoutItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);

        // Help menu
        JMenu helpMenu = new JMenu("Help");
        JMenuItem aboutItem = new JMenuItem("About");

        aboutItem.addActionListener(e -> showAboutDialog());

        helpMenu.add(aboutItem);

        menuBar.add(fileMenu);
        menuBar.add(helpMenu);

        setJMenuBar(menuBar);
    }

    private JPanel createStatusBar() {
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBorder(BorderFactory.createEtchedBorder());

        User currentUser = authService.getCurrentUser();
        JLabel userLabel = new JLabel("Logged in as: " + currentUser.getFullName() +
                " (" + currentUser.getRole() + ")");

        JLabel timeLabel = new JLabel(new java.util.Date().toString());

        statusBar.add(userLabel, BorderLayout.WEST);
        statusBar.add(timeLabel, BorderLayout.EAST);

        return statusBar;
    }

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to logout?",
                "Confirm Logout",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            authService.logout();
            SwingUtilities.invokeLater(() -> {
                new com.hotel.auth.LoginFrame().setVisible(true);
                dispose();
            });
        }
    }

    private void showAboutDialog() {
        JOptionPane.showMessageDialog(this,
                "Hotel Reservation System\nVersion 1.0\n\n" +
                        "A comprehensive hotel management solution\n" +
                        "Built with Java Swing and Firebase Firestore",
                "About",
                JOptionPane.INFORMATION_MESSAGE);
    }
}