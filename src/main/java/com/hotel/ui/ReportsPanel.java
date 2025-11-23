package com.hotel.ui;

import com.hotel.dao.BookingDAO;
import com.hotel.dao.GuestDAO;
import com.hotel.dao.PaymentDAO;
import com.hotel.dao.RoomDAO;
import com.hotel.model.Booking;
import com.hotel.model.Guest;
import com.hotel.model.Payment;
import com.hotel.model.Room;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ReportsPanel extends JPanel {
    private BookingDAO bookingDAO;
    private GuestDAO guestDAO;
    private RoomDAO roomDAO;
    private PaymentDAO paymentDAO;
    private JTextArea reportArea;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public ReportsPanel() {
        bookingDAO = new BookingDAO();
        guestDAO = new GuestDAO();
        roomDAO = new RoomDAO();
        paymentDAO = new PaymentDAO();
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));

        // Title
        JLabel titleLabel = new JLabel("Reports Dashboard", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        add(titleLabel, BorderLayout.NORTH);

        // Report buttons panel
        JPanel buttonPanel = new JPanel(new GridLayout(2, 3, 10, 10));
        JButton dailyRevenueBtn = new JButton("Daily Revenue Report");
        JButton monthlyBookingsBtn = new JButton("Monthly Booking Summary");
        JButton currentGuestsBtn = new JButton("Current Guests");
        JButton occupancyBtn = new JButton("Occupancy Report");
        JButton guestReportBtn = new JButton("Guest Report");
        JButton paymentReportBtn = new JButton("Payment Report");

        buttonPanel.add(dailyRevenueBtn);
        buttonPanel.add(monthlyBookingsBtn);
        buttonPanel.add(currentGuestsBtn);
        buttonPanel.add(occupancyBtn);
        buttonPanel.add(guestReportBtn);
        buttonPanel.add(paymentReportBtn);

        // Report display area
        reportArea = new JTextArea(20, 60);
        reportArea.setEditable(false);
        reportArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(reportArea);

        // Clear button
        JButton clearButton = new JButton("Clear Report");

        JPanel southPanel = new JPanel(new FlowLayout());
        southPanel.add(clearButton);

        // Add components to main panel
        add(buttonPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(southPanel, BorderLayout.SOUTH);

        // Event listeners
        dailyRevenueBtn.addActionListener(e -> generateDailyRevenueReport());
        monthlyBookingsBtn.addActionListener(e -> generateMonthlyBookingSummary());
        currentGuestsBtn.addActionListener(e -> generateCurrentGuestsReport());
        occupancyBtn.addActionListener(e -> generateOccupancyReport());
        guestReportBtn.addActionListener(e -> generateGuestReport());
        paymentReportBtn.addActionListener(e -> generatePaymentReport());
        clearButton.addActionListener(e -> reportArea.setText(""));
    }

    private void generateDailyRevenueReport() {
        Date today = new Date();
        List<Payment> payments = getAllPayments();

        double dailyRevenue = 0;
        int paymentCount = 0;

        for (Payment payment : payments) {
            if (payment.getPaymentDate() != null &&
                    isSameDay(payment.getPaymentDate(), today) &&
                    payment.getStatus() == Payment.PaymentStatus.COMPLETED) {
                dailyRevenue += payment.getAmount();
                paymentCount++;
            }
        }

        StringBuilder report = new StringBuilder();
        report.append("DAILY REVENUE REPORT\n");
        report.append("====================\n\n");
        report.append("Date: ").append(dateFormat.format(today)).append("\n");
        report.append("Total Revenue: $").append(String.format("%.2f", dailyRevenue)).append("\n");
        report.append("Number of Payments: ").append(paymentCount).append("\n");
        report.append("Average Payment: $").append(paymentCount > 0 ?
                String.format("%.2f", dailyRevenue / paymentCount) : "0.00").append("\n");

        reportArea.setText(report.toString());
    }

    private void generateMonthlyBookingSummary() {
        Calendar cal = Calendar.getInstance();
        int currentMonth = cal.get(Calendar.MONTH);
        int currentYear = cal.get(Calendar.YEAR);

        List<Booking> bookings = bookingDAO.getAllBookings();

        int confirmed = 0, checkedIn = 0, checkedOut = 0, cancelled = 0;
        double totalRevenue = 0;

        for (Booking booking : bookings) {
            cal.setTime(booking.getCreatedAt());
            int bookingMonth = cal.get(Calendar.MONTH);
            int bookingYear = cal.get(Calendar.YEAR);

            if (bookingMonth == currentMonth && bookingYear == currentYear) {
                switch (booking.getStatus()) {
                    case CONFIRMED: confirmed++; break;
                    case CHECKED_IN: checkedIn++; break;
                    case CHECKED_OUT:
                        checkedOut++;
                        totalRevenue += booking.getTotalAmount();
                        break;
                    case CANCELLED: cancelled++; break;
                }
            }
        }

        StringBuilder report = new StringBuilder();
        report.append("MONTHLY BOOKING SUMMARY\n");
        report.append("=======================\n\n");
        report.append("Month: ").append(getMonthName(currentMonth)).append(" ").append(currentYear).append("\n\n");
        report.append("Booking Statistics:\n");
        report.append("Confirmed:    ").append(confirmed).append("\n");
        report.append("Checked In:   ").append(checkedIn).append("\n");
        report.append("Checked Out:  ").append(checkedOut).append("\n");
        report.append("Cancelled:    ").append(cancelled).append("\n");
        report.append("Total:        ").append(confirmed + checkedIn + checkedOut + cancelled).append("\n\n");
        report.append("Total Revenue: $").append(String.format("%.2f", totalRevenue)).append("\n");

        reportArea.setText(report.toString());
    }

    private void generateCurrentGuestsReport() {
        List<Booking> activeBookings = bookingDAO.getActiveBookings();

        StringBuilder report = new StringBuilder();
        report.append("CURRENT GUESTS REPORT\n");
        report.append("=====================\n\n");
        report.append("Generated: ").append(new Date()).append("\n\n");

        if (activeBookings.isEmpty()) {
            report.append("No current guests staying at the hotel.\n");
        } else {
            report.append(String.format("%-15s %-20s %-10s %-12s %-12s\n",
                    "Room", "Guest", "Check-In", "Check-Out", "Status"));
            report.append("-".repeat(75)).append("\n");

            for (Booking booking : activeBookings) {
                Guest guest = guestDAO.getGuest(booking.getGuestId());
                String guestName = (guest != null) ?
                        guest.getFirstName() + " " + guest.getLastName() : "Unknown";

                report.append(String.format("%-15s %-20s %-12s %-12s %-12s\n",
                        booking.getRoomNumber(),
                        guestName.length() > 19 ? guestName.substring(0, 19) : guestName,
                        dateFormat.format(booking.getCheckInDate()),
                        dateFormat.format(booking.getCheckOutDate()),
                        booking.getStatus()));
            }

            report.append("\nTotal Current Guests: ").append(activeBookings.size()).append("\n");
        }

        reportArea.setText(report.toString());
    }

    private void generateOccupancyReport() {
        List<Room> allRooms = roomDAO.getAllRooms();
        List<Booking> activeBookings = bookingDAO.getActiveBookings();

        int totalRooms = allRooms.size();
        int occupiedRooms = (int) activeBookings.stream()
                .filter(b -> b.getStatus() == Booking.BookingStatus.CHECKED_IN)
                .count();
        int availableRooms = (int) allRooms.stream()
                .filter(r -> r.getStatus() == Room.RoomStatus.AVAILABLE)
                .count();
        int maintenanceRooms = (int) allRooms.stream()
                .filter(r -> r.getStatus() == Room.RoomStatus.MAINTENANCE)
                .count();

        double occupancyRate = totalRooms > 0 ? (occupiedRooms * 100.0) / totalRooms : 0;

        StringBuilder report = new StringBuilder();
        report.append("OCCUPANCY REPORT\n");
        report.append("================\n\n");
        report.append("Generated: ").append(new Date()).append("\n\n");
        report.append("Room Statistics:\n");
        report.append("Total Rooms:      ").append(totalRooms).append("\n");
        report.append("Occupied Rooms:   ").append(occupiedRooms).append("\n");
        report.append("Available Rooms:  ").append(availableRooms).append("\n");
        report.append("Maintenance:      ").append(maintenanceRooms).append("\n");
        report.append("Occupancy Rate:   ").append(String.format("%.1f%%", occupancyRate)).append("\n");

        reportArea.setText(report.toString());
    }

    private void generateGuestReport() {
        List<Guest> guests = guestDAO.getAllGuests();

        StringBuilder report = new StringBuilder();
        report.append("GUEST REPORT\n");
        report.append("============\n\n");
        report.append("Total Guests: ").append(guests.size()).append("\n\n");

        report.append(String.format("%-20s %-25s %-15s\n", "Name", "Email", "Phone"));
        report.append("-".repeat(65)).append("\n");

        for (Guest guest : guests) {
            report.append(String.format("%-20s %-25s %-15s\n",
                    (guest.getFirstName() + " " + guest.getLastName()).substring(0,
                            Math.min(19, (guest.getFirstName() + " " + guest.getLastName()).length())),
                    guest.getEmail().length() > 24 ? guest.getEmail().substring(0, 24) : guest.getEmail(),
                    guest.getPhone()));
        }

        reportArea.setText(report.toString());
    }

    private void generatePaymentReport() {
        List<Payment> payments = getAllPayments();
        double totalRevenue = payments.stream()
                .filter(p -> p.getStatus() == Payment.PaymentStatus.COMPLETED)
                .mapToDouble(Payment::getAmount)
                .sum();

        long cashPayments = payments.stream()
                .filter(p -> p.getPaymentMethod() == Payment.PaymentMethod.CASH &&
                        p.getStatus() == Payment.PaymentStatus.COMPLETED)
                .count();
        long cardPayments = payments.stream()
                .filter(p -> p.getPaymentMethod() == Payment.PaymentMethod.CARD &&
                        p.getStatus() == Payment.PaymentStatus.COMPLETED)
                .count();
        long onlinePayments = payments.stream()
                .filter(p -> p.getPaymentMethod() == Payment.PaymentMethod.ONLINE &&
                        p.getStatus() == Payment.PaymentStatus.COMPLETED)
                .count();

        StringBuilder report = new StringBuilder();
        report.append("PAYMENT REPORT\n");
        report.append("==============\n\n");
        report.append("Total Payments: ").append(payments.size()).append("\n");
        report.append("Total Revenue: $").append(String.format("%.2f", totalRevenue)).append("\n\n");
        report.append("Payment Methods:\n");
        report.append("Cash:    ").append(cashPayments).append(" payments\n");
        report.append("Card:    ").append(cardPayments).append(" payments\n");
        report.append("Online:  ").append(onlinePayments).append(" payments\n");

        reportArea.setText(report.toString());
    }

    private List<Payment> getAllPayments() {
        List<Payment> allPayments = new java.util.ArrayList<>();
        List<Booking> bookings = bookingDAO.getAllBookings();

        for (Booking booking : bookings) {
            List<Payment> payments = paymentDAO.getPaymentsByBooking(booking.getBookingId());
            allPayments.addAll(payments);
        }

        return allPayments;
    }

    private boolean isSameDay(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(date1);
        cal2.setTime(date2);
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) &&
                cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH);
    }

    private String getMonthName(int month) {
        String[] monthNames = {"January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"};
        return monthNames[month];
    }
}