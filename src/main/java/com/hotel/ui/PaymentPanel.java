package com.hotel.ui;

import com.hotel.dao.BookingDAO;
import com.hotel.dao.PaymentDAO;
import com.hotel.model.Booking;
import com.hotel.model.Payment;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;

public class PaymentPanel extends JPanel {
    private PaymentDAO paymentDAO;
    private BookingDAO bookingDAO;
    private JTable paymentTable;
    private DefaultTableModel tableModel;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    public PaymentPanel() {
        paymentDAO = new PaymentDAO();
        bookingDAO = new BookingDAO();
        initializeUI();
        loadPaymentData();
    }

    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));

        // Title
        JLabel titleLabel = new JLabel("Payment Management", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        add(titleLabel, BorderLayout.NORTH);

        // Table
        String[] columns = {"Payment ID", "Booking ID", "Amount", "Method", "Status", "Date", "Transaction ID"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        paymentTable = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(paymentTable);

        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton addPaymentButton = new JButton("Record Payment");
        JButton updateStatusButton = new JButton("Update Status");
        JButton refreshButton = new JButton("Refresh");
        JButton generateBillButton = new JButton("Generate Bill");

        buttonPanel.add(addPaymentButton);
        buttonPanel.add(updateStatusButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(generateBillButton);

        // Add components to main panel
        add(tableScrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // Event listeners
        addPaymentButton.addActionListener(e -> recordPayment());
        updateStatusButton.addActionListener(e -> updatePaymentStatus());
        refreshButton.addActionListener(e -> loadPaymentData());
        generateBillButton.addActionListener(e -> generateBill());
    }

    private void loadPaymentData() {
        tableModel.setRowCount(0);
        List<Payment> payments = getAllPayments();

        for (Payment payment : payments) {
            Object[] rowData = {
                    payment.getPaymentId(),
                    payment.getBookingId(),
                    String.format("$%.2f", payment.getAmount()),
                    payment.getPaymentMethod(),
                    payment.getStatus(),
                    payment.getPaymentDate() != null ? dateFormat.format(payment.getPaymentDate()) : "N/A",
                    payment.getTransactionId() != null ? payment.getTransactionId() : "N/A"
            };
            tableModel.addRow(rowData);
        }
    }

    private List<Payment> getAllPayments() {
        // This would normally come from PaymentDAO
        // For now, we'll get payments for all bookings
        List<Payment> allPayments = new java.util.ArrayList<>();
        List<Booking> bookings = bookingDAO.getAllBookings();

        for (Booking booking : bookings) {
            List<Payment> payments = paymentDAO.getPaymentsByBooking(booking.getBookingId());
            allPayments.addAll(payments);
        }

        return allPayments;
    }

    private void recordPayment() {
        List<Booking> bookings = bookingDAO.getAllBookings();

        if (bookings.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No bookings available for payment!", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        JComboBox<Booking> bookingComboBox = new JComboBox<>(bookings.toArray(new Booking[0]));
        JTextField amountField = new JTextField();
        JComboBox<Payment.PaymentMethod> methodComboBox = new JComboBox<>(Payment.PaymentMethod.values());
        JTextField transactionIdField = new JTextField();

        // Set custom renderer for booking combo box
        bookingComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Booking) {
                    Booking booking = (Booking) value;
                    setText("Booking " + booking.getBookingId() + " - Room " + booking.getRoomNumber() +
                            " - $" + booking.getTotalAmount());
                }
                return this;
            }
        });

        Object[] message = {
                "Booking:", bookingComboBox,
                "Amount:", amountField,
                "Payment Method:", methodComboBox,
                "Transaction ID:", transactionIdField
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Record Payment",
                JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            try {
                Booking selectedBooking = (Booking) bookingComboBox.getSelectedItem();
                double amount = Double.parseDouble(amountField.getText());

                Payment payment = new Payment(
                        null, // ID will be generated
                        selectedBooking.getBookingId(),
                        amount,
                        (Payment.PaymentMethod) methodComboBox.getSelectedItem()
                );

                payment.setStatus(Payment.PaymentStatus.COMPLETED);
                payment.setTransactionId(transactionIdField.getText().trim());

                String paymentId = paymentDAO.addPayment(payment);
                if (paymentId != null) {
                    JOptionPane.showMessageDialog(this, "Payment recorded successfully!");
                    loadPaymentData();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to record payment!", "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Please enter a valid amount!", "Error",
                        JOptionPane.ERROR_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error recording payment: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void updatePaymentStatus() {
        int selectedRow = paymentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a payment to update!",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String paymentId = tableModel.getValueAt(selectedRow, 0).toString();
        Payment payment = paymentDAO.getPayment(paymentId);

        if (payment != null) {
            JComboBox<Payment.PaymentStatus> statusComboBox = new JComboBox<>(Payment.PaymentStatus.values());
            statusComboBox.setSelectedItem(payment.getStatus());

            Object[] message = {
                    "Payment ID: " + payment.getPaymentId(),
                    "New Status:", statusComboBox
            };

            int option = JOptionPane.showConfirmDialog(this, message, "Update Payment Status",
                    JOptionPane.OK_CANCEL_OPTION);

            if (option == JOptionPane.OK_OPTION) {
                payment.setStatus((Payment.PaymentStatus) statusComboBox.getSelectedItem());

                if (paymentDAO.updatePayment(payment)) {
                    JOptionPane.showMessageDialog(this, "Payment status updated successfully!");
                    loadPaymentData();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to update payment status!", "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private void generateBill() {
        int selectedRow = paymentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a payment to generate bill!",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String paymentId = tableModel.getValueAt(selectedRow, 0).toString();
        Payment payment = paymentDAO.getPayment(paymentId);

        if (payment != null) {
            Booking booking = bookingDAO.getBooking(payment.getBookingId());

            String bill = generateBillContent(payment, booking);

            // Show bill in a dialog
            JTextArea billArea = new JTextArea(bill, 20, 50);
            billArea.setEditable(false);
            billArea.setFont(new Font("Monospaced", Font.PLAIN, 12));

            JScrollPane scrollPane = new JScrollPane(billArea);
            JOptionPane.showMessageDialog(this, scrollPane, "Hotel Bill", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private String generateBillContent(Payment payment, Booking booking) {
        StringBuilder bill = new StringBuilder();
        bill.append("========================================\n");
        bill.append("           HOTEL GRAND BILL\n");
        bill.append("========================================\n\n");
        bill.append("Payment ID: ").append(payment.getPaymentId()).append("\n");
        bill.append("Booking ID: ").append(payment.getBookingId()).append("\n");

        if (booking != null) {
            bill.append("Room: ").append(booking.getRoomNumber()).append("\n");
            bill.append("Check-In: ").append(dateFormat.format(booking.getCheckInDate())).append("\n");
            bill.append("Check-Out: ").append(dateFormat.format(booking.getCheckOutDate())).append("\n");
        }

        bill.append("\n----------------------------------------\n");
        bill.append("PAYMENT DETAILS:\n");
        bill.append("----------------------------------------\n");
        bill.append("Amount: $").append(String.format("%.2f", payment.getAmount())).append("\n");
        bill.append("Method: ").append(payment.getPaymentMethod()).append("\n");
        bill.append("Status: ").append(payment.getStatus()).append("\n");
        bill.append("Date: ").append(dateFormat.format(payment.getPaymentDate())).append("\n");

        if (payment.getTransactionId() != null) {
            bill.append("Transaction ID: ").append(payment.getTransactionId()).append("\n");
        }

        bill.append("\n========================================\n");
        bill.append("Thank you for staying with us!\n");
        bill.append("========================================\n");

        return bill.toString();
    }
}