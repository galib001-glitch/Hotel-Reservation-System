package com.hotel.ui;

import com.hotel.dao.RoomDAO;
import com.hotel.model.Room;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class RoomManagementPanel extends JPanel {
    private RoomDAO roomDAO;
    private JTable roomTable;
    private DefaultTableModel tableModel;

    public RoomManagementPanel() {
        roomDAO = new RoomDAO();
        initializeUI();
        loadRoomData();
    }

    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));

        // Title
        JLabel titleLabel = new JLabel("Room Management", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        add(titleLabel, BorderLayout.NORTH);

        // Table
        String[] columns = {"Room Number", "Type", "Status", "Price/Night", "Description"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        roomTable = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(roomTable);

        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton addButton = new JButton("Add Room");
        JButton editButton = new JButton("Edit Room");
        JButton deleteButton = new JButton("Delete Room");
        JButton refreshButton = new JButton("Refresh");
        JButton availableButton = new JButton("Show Available");
        JButton allButton = new JButton("Show All");

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(availableButton);
        buttonPanel.add(allButton);

        // Add components to main panel
        add(tableScrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // Event listeners
        addButton.addActionListener(e -> addRoom());
        editButton.addActionListener(e -> editRoom());
        deleteButton.addActionListener(e -> deleteRoom());
        refreshButton.addActionListener(e -> loadRoomData());
        availableButton.addActionListener(e -> showAvailableRooms());
        allButton.addActionListener(e -> loadRoomData());
    }

    private void loadRoomData() {
        tableModel.setRowCount(0);
        List<Room> rooms = roomDAO.getAllRooms();

        for (Room room : rooms) {
            Object[] rowData = {
                    room.getRoomNumber(),
                    room.getType(),
                    room.getStatus(),
                    String.format("$%.2f", room.getPricePerNight()),
                    room.getDescription()
            };
            tableModel.addRow(rowData);
        }
    }

    private void showAvailableRooms() {
        tableModel.setRowCount(0);
        List<Room> rooms = roomDAO.getAvailableRooms();

        for (Room room : rooms) {
            Object[] rowData = {
                    room.getRoomNumber(),
                    room.getType(),
                    room.getStatus(),
                    String.format("$%.2f", room.getPricePerNight()),
                    room.getDescription()
            };
            tableModel.addRow(rowData);
        }
    }

    private void addRoom() {
        JTextField roomNumberField = new JTextField();
        JComboBox<Room.RoomType> typeComboBox = new JComboBox<>(Room.RoomType.values());
        JTextField priceField = new JTextField();
        JTextField descriptionField = new JTextField();

        Object[] message = {
                "Room Number:", roomNumberField,
                "Room Type:", typeComboBox,
                "Price per Night:", priceField,
                "Description:", descriptionField
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Add New Room",
                JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            try {
                Room room = new Room(
                        roomNumberField.getText(),
                        (Room.RoomType) typeComboBox.getSelectedItem(),
                        Double.parseDouble(priceField.getText()),
                        descriptionField.getText()
                );

                if (roomDAO.addRoom(room)) {
                    JOptionPane.showMessageDialog(this, "Room added successfully!");
                    loadRoomData();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to add room!", "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Please enter a valid price!", "Error",
                        JOptionPane.ERROR_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error adding room: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void editRoom() {
        int selectedRow = roomTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a room to edit!",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String roomNumber = tableModel.getValueAt(selectedRow, 0).toString();
        Room room = roomDAO.getRoom(roomNumber);

        if (room != null) {
            JTextField priceField = new JTextField(String.valueOf(room.getPricePerNight()));
            JTextField descriptionField = new JTextField(room.getDescription());
            JComboBox<Room.RoomStatus> statusComboBox = new JComboBox<>(Room.RoomStatus.values());
            statusComboBox.setSelectedItem(room.getStatus());

            Object[] message = {
                    "Room: " + room.getRoomNumber() + " (" + room.getType() + ")",
                    "Price per Night:", priceField,
                    "Status:", statusComboBox,
                    "Description:", descriptionField
            };

            int option = JOptionPane.showConfirmDialog(this, message, "Edit Room",
                    JOptionPane.OK_CANCEL_OPTION);

            if (option == JOptionPane.OK_OPTION) {
                try {
                    room.setPricePerNight(Double.parseDouble(priceField.getText()));
                    room.setStatus((Room.RoomStatus) statusComboBox.getSelectedItem());
                    room.setDescription(descriptionField.getText());

                    if (roomDAO.updateRoom(room)) {
                        JOptionPane.showMessageDialog(this, "Room updated successfully!");
                        loadRoomData();
                    } else {
                        JOptionPane.showMessageDialog(this, "Failed to update room!", "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, "Please enter a valid price!", "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private void deleteRoom() {
        int selectedRow = roomTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a room to delete!",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String roomNumber = tableModel.getValueAt(selectedRow, 0).toString();

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete room: " + roomNumber + "?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (roomDAO.deleteRoom(roomNumber)) {
                JOptionPane.showMessageDialog(this, "Room deleted successfully!");
                loadRoomData();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete room!", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}