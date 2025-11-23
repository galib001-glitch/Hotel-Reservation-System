package com.hotel.ui;

import com.hotel.dao.BookingDAO;
import com.hotel.dao.GuestDAO;
import com.hotel.dao.RoomDAO;
import com.hotel.model.Booking;
import com.hotel.model.Guest;
import com.hotel.model.Room;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class BookingPanel extends JPanel {
    private BookingDAO bookingDAO;
    private GuestDAO guestDAO;
    private RoomDAO roomDAO;
    private JTable bookingTable;
    private DefaultTableModel tableModel;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public BookingPanel() {
        bookingDAO = new BookingDAO();
        guestDAO = new GuestDAO();
        roomDAO = new RoomDAO();
        initializeUI();
        loadBookingData();
    }

    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));

        // Title
        JLabel titleLabel = new JLabel("Booking Management", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        add(titleLabel, BorderLayout.NORTH);

        // Table
        String[] columns = {"Booking ID", "Guest", "Room", "Check-In", "Check-Out", "Status", "Amount"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        bookingTable = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(bookingTable);

        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton newBookingButton = new JButton("New Booking");
        JButton checkInButton = new JButton("Check In");
        JButton checkOutButton = new JButton("Check Out");
        JButton cancelButton = new JButton("Cancel Booking");
        JButton refreshButton = new JButton("Refresh");

        buttonPanel.add(newBookingButton);
        buttonPanel.add(checkInButton);
        buttonPanel.add(checkOutButton);
        buttonPanel.add(cancelButton);
        buttonPanel.add(refreshButton);

        // Add components to main panel
        add(tableScrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // Event listeners
        newBookingButton.addActionListener(e -> createNewBooking());
        checkInButton.addActionListener(e -> checkIn());
        checkOutButton.addActionListener(e -> checkOut());
        cancelButton.addActionListener(e -> cancelBooking());
        refreshButton.addActionListener(e -> loadBookingData());
    }

    private void loadBookingData() {
        tableModel.setRowCount(0);
        List<Booking> bookings = bookingDAO.getAllBookings();

        for (Booking booking : bookings) {
            Guest guest = guestDAO.getGuest(booking.getGuestId());
            String guestName = (guest != null) ? guest.getFirstName() + " " + guest.getLastName() : "Unknown";

            Object[] rowData = {
                    booking.getBookingId(),
                    guestName,
                    booking.getRoomNumber(),
                    dateFormat.format(booking.getCheckInDate()),
                    dateFormat.format(booking.getCheckOutDate()),
                    booking.getStatus(),
                    String.format("$%.2f", booking.getTotalAmount())
            };
            tableModel.addRow(rowData);
        }
    }

    private void createNewBooking() {
        // Get available guests and rooms
        List<Guest> guests = guestDAO.getAllGuests();
        List<Room> availableRooms = roomDAO.getAvailableRooms();

        if (guests.isEmpty() || availableRooms.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No available guests or rooms for booking!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Create form components
        JComboBox<Guest> guestComboBox = new JComboBox<>(guests.toArray(new Guest[0]));
        JComboBox<Room> roomComboBox = new JComboBox<>(availableRooms.toArray(new Room[0]));
        JTextField checkInField = new JTextField(dateFormat.format(new Date()));
        JTextField checkOutField = new JTextField(getNextWeekDate());
        JSpinner guestsSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 10, 1));

        // Set custom renderers for combo boxes
        guestComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Guest) {
                    Guest guest = (Guest) value;
                    setText(guest.getFirstName() + " " + guest.getLastName() + " (" + guest.getEmail() + ")");
                }
                return this;
            }
        });

        roomComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Room) {
                    Room room = (Room) value;
                    setText("Room " + room.getRoomNumber() + " (" + room.getType() + ") - $" + room.getPricePerNight());
                }
                return this;
            }
        });

        Object[] message = {
                "Guest:", guestComboBox,
                "Room:", roomComboBox,
                "Check-In Date (yyyy-mm-dd):", checkInField,
                "Check-Out Date (yyyy-mm-dd):", checkOutField,
                "Number of Guests:", guestsSpinner
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Create New Booking",
                JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            try {
                Guest selectedGuest = (Guest) guestComboBox.getSelectedItem();
                Room selectedRoom = (Room) roomComboBox.getSelectedItem();

                Date checkInDate = dateFormat.parse(checkInField.getText());
                Date checkOutDate = dateFormat.parse(checkOutField.getText());
                int numberOfGuests = (Integer) guestsSpinner.getValue();

                // Calculate total amount
                long days = (checkOutDate.getTime() - checkInDate.getTime()) / (1000 * 60 * 60 * 24);
                double totalAmount = days * selectedRoom.getPricePerNight();

                Booking booking = new Booking(
                        null, // ID will be generated
                        selectedGuest.getGuestId(),
                        selectedRoom.getRoomNumber(),
                        checkInDate,
                        checkOutDate,
                        numberOfGuests,
                        totalAmount
                );

                String bookingId = bookingDAO.addBooking(booking);
                if (bookingId != null) {
                    // Update room status
                    selectedRoom.setStatus(Room.RoomStatus.BOOKED);
                    roomDAO.updateRoom(selectedRoom);

                    JOptionPane.showMessageDialog(this, "Booking created successfully!\nTotal Amount: $" + totalAmount);
                    loadBookingData();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to create booking!", "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error creating booking: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void checkIn() {
        int selectedRow = bookingTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a booking to check in!",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String bookingId = tableModel.getValueAt(selectedRow, 0).toString();
        Booking booking = bookingDAO.getBooking(bookingId);

        if (booking != null && booking.getStatus() == Booking.BookingStatus.CONFIRMED) {
            booking.setStatus(Booking.BookingStatus.CHECKED_IN);
            booking.setActualCheckIn(new Date());

            if (bookingDAO.updateBooking(booking)) {
                JOptionPane.showMessageDialog(this, "Guest checked in successfully!");
                loadBookingData();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to check in guest!", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Cannot check in this booking!", "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void checkOut() {
        int selectedRow = bookingTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a booking to check out!",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String bookingId = tableModel.getValueAt(selectedRow, 0).toString();
        Booking booking = bookingDAO.getBooking(bookingId);

        if (booking != null && booking.getStatus() == Booking.BookingStatus.CHECKED_IN) {
            booking.setStatus(Booking.BookingStatus.CHECKED_OUT);
            booking.setActualCheckOut(new Date());

            // Update room status back to available
            Room room = roomDAO.getRoom(booking.getRoomNumber());
            if (room != null) {
                room.setStatus(Room.RoomStatus.AVAILABLE);
                roomDAO.updateRoom(room);
            }

            if (bookingDAO.updateBooking(booking)) {
                JOptionPane.showMessageDialog(this, "Guest checked out successfully!");
                loadBookingData();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to check out guest!", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Cannot check out this booking!", "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cancelBooking() {
        int selectedRow = bookingTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a booking to cancel!",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String bookingId = tableModel.getValueAt(selectedRow, 0).toString();
        Booking booking = bookingDAO.getBooking(bookingId);

        if (booking != null && booking.getStatus() == Booking.BookingStatus.CONFIRMED) {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to cancel this booking?",
                    "Confirm Cancel", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                booking.setStatus(Booking.BookingStatus.CANCELLED);

                // Update room status back to available
                Room room = roomDAO.getRoom(booking.getRoomNumber());
                if (room != null) {
                    room.setStatus(Room.RoomStatus.AVAILABLE);
                    roomDAO.updateRoom(room);
                }

                if (bookingDAO.updateBooking(booking)) {
                    JOptionPane.showMessageDialog(this, "Booking cancelled successfully!");
                    loadBookingData();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to cancel booking!", "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Cannot cancel this booking!", "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private String getNextWeekDate() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, 7);
        return dateFormat.format(cal.getTime());
    }
}