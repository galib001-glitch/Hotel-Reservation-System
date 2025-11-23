package com.hotel.ui;

import com.hotel.dao.GuestDAO;
import com.hotel.model.Guest;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class GuestManagementPanel extends JPanel {
    private GuestDAO guestDAO;
    private JTable guestTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;

    public GuestManagementPanel() {
        guestDAO = new GuestDAO();
        initializeUI();
        loadGuestData();
    }

    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));

        // Title
        JLabel titleLabel = new JLabel("Guest Management", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        add(titleLabel, BorderLayout.NORTH);

        // Search panel
        JPanel searchPanel = new JPanel(new FlowLayout());
        searchField = new JTextField(20);
        JButton searchButton = new JButton("Search");
        JButton clearButton = new JButton("Clear");

        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.add(clearButton);

        // Table
        String[] columns = {"Guest ID", "First Name", "Last Name", "Email", "Phone", "Address"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        guestTable = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(guestTable);

        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton addButton = new JButton("Add Guest");
        JButton editButton = new JButton("Edit Guest");
        JButton deleteButton = new JButton("Delete Guest");
        JButton refreshButton = new JButton("Refresh");

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);

        // Add components to main panel
        add(searchPanel, BorderLayout.NORTH);
        add(tableScrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // Event listeners
        addButton.addActionListener(e -> addGuest());
        editButton.addActionListener(e -> editGuest());
        deleteButton.addActionListener(e -> deleteGuest());
        refreshButton.addActionListener(e -> loadGuestData());
        searchButton.addActionListener(e -> searchGuests());
        clearButton.addActionListener(e -> {
            searchField.setText("");
            loadGuestData();
        });
    }

    private void loadGuestData() {
        tableModel.setRowCount(0); // Clear existing data
        List<Guest> guests = guestDAO.getAllGuests();

        for (Guest guest : guests) {
            Object[] rowData = {
                    guest.getGuestId(),
                    guest.getFirstName(),
                    guest.getLastName(),
                    guest.getEmail(),
                    guest.getPhone(),
                    guest.getAddress()
            };
            tableModel.addRow(rowData);
        }
    }

    private void searchGuests() {
        String searchTerm = searchField.getText().trim().toLowerCase();
        if (searchTerm.isEmpty()) {
            loadGuestData();
            return;
        }

        tableModel.setRowCount(0);
        List<Guest> guests = guestDAO.getAllGuests();

        for (Guest guest : guests) {
            if (guest.getFirstName().toLowerCase().contains(searchTerm) ||
                    guest.getLastName().toLowerCase().contains(searchTerm) ||
                    guest.getEmail().toLowerCase().contains(searchTerm) ||
                    guest.getPhone().contains(searchTerm)) {

                Object[] rowData = {
                        guest.getGuestId(),
                        guest.getFirstName(),
                        guest.getLastName(),
                        guest.getEmail(),
                        guest.getPhone(),
                        guest.getAddress()
                };
                tableModel.addRow(rowData);
            }
        }
    }

    private void addGuest() {
        // Create a dialog for adding new guest
        JTextField firstNameField = new JTextField();
        JTextField lastNameField = new JTextField();
        JTextField emailField = new JTextField();
        JTextField phoneField = new JTextField();
        JTextField addressField = new JTextField();
        JTextField idNumberField = new JTextField();

        Object[] message = {
                "First Name:", firstNameField,
                "Last Name:", lastNameField,
                "Email:", emailField,
                "Phone:", phoneField,
                "Address:", addressField,
                "ID Number:", idNumberField
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Add New Guest",
                JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            try {
                Guest guest = new Guest(
                        null, // ID will be generated by DAO
                        firstNameField.getText(),
                        lastNameField.getText(),
                        emailField.getText(),
                        phoneField.getText(),
                        addressField.getText(),
                        idNumberField.getText()
                );

                String guestId = guestDAO.addGuest(guest);
                if (guestId != null) {
                    JOptionPane.showMessageDialog(this, "Guest added successfully!");
                    loadGuestData();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to add guest!", "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error adding guest: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void editGuest() {
        int selectedRow = guestTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a guest to edit!",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String guestId = tableModel.getValueAt(selectedRow, 0).toString();
        Guest guest = guestDAO.getGuest(guestId);

        if (guest != null) {
            JTextField firstNameField = new JTextField(guest.getFirstName());
            JTextField lastNameField = new JTextField(guest.getLastName());
            JTextField emailField = new JTextField(guest.getEmail());
            JTextField phoneField = new JTextField(guest.getPhone());
            JTextField addressField = new JTextField(guest.getAddress());
            JTextField idNumberField = new JTextField(guest.getIdNumber());

            Object[] message = {
                    "First Name:", firstNameField,
                    "Last Name:", lastNameField,
                    "Email:", emailField,
                    "Phone:", phoneField,
                    "Address:", addressField,
                    "ID Number:", idNumberField
            };

            int option = JOptionPane.showConfirmDialog(this, message, "Edit Guest",
                    JOptionPane.OK_CANCEL_OPTION);

            if (option == JOptionPane.OK_OPTION) {
                guest.setFirstName(firstNameField.getText());
                guest.setLastName(lastNameField.getText());
                guest.setEmail(emailField.getText());
                guest.setPhone(phoneField.getText());
                guest.setAddress(addressField.getText());
                guest.setIdNumber(idNumberField.getText());

                if (guestDAO.updateGuest(guest)) {
                    JOptionPane.showMessageDialog(this, "Guest updated successfully!");
                    loadGuestData();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to update guest!", "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private void deleteGuest() {
        int selectedRow = guestTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a guest to delete!",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String guestId = tableModel.getValueAt(selectedRow, 0).toString();
        String guestName = tableModel.getValueAt(selectedRow, 1) + " " +
                tableModel.getValueAt(selectedRow, 2);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete guest: " + guestName + "?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (guestDAO.deleteGuest(guestId)) {
                JOptionPane.showMessageDialog(this, "Guest deleted successfully!");
                loadGuestData();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete guest!", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}