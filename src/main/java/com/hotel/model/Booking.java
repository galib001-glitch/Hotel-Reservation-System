package com.hotel.model;

import java.util.Date;

public class Booking {
    public enum BookingStatus { CONFIRMED, CHECKED_IN, CHECKED_OUT, CANCELLED }

    private String bookingId;
    private String guestId;
    private String roomNumber;
    private Date checkInDate;
    private Date checkOutDate;
    private int numberOfGuests;
    private double totalAmount;
    private BookingStatus status;
    private Date createdAt;
    private Date actualCheckIn;
    private Date actualCheckOut;

    public Booking() {}

    public Booking(String bookingId, String guestId, String roomNumber,
                   Date checkInDate, Date checkOutDate, int numberOfGuests, double totalAmount) {
        this.bookingId = bookingId;
        this.guestId = guestId;
        this.roomNumber = roomNumber;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.numberOfGuests = numberOfGuests;
        this.totalAmount = totalAmount;
        this.status = BookingStatus.CONFIRMED;
        this.createdAt = new Date();
    }

    // Getters and Setters
    public String getBookingId() { return bookingId; }
    public void setBookingId(String bookingId) { this.bookingId = bookingId; }

    public String getGuestId() { return guestId; }
    public void setGuestId(String guestId) { this.guestId = guestId; }

    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }

    public Date getCheckInDate() { return checkInDate; }
    public void setCheckInDate(Date checkInDate) { this.checkInDate = checkInDate; }

    public Date getCheckOutDate() { return checkOutDate; }
    public void setCheckOutDate(Date checkOutDate) { this.checkOutDate = checkOutDate; }

    public int getNumberOfGuests() { return numberOfGuests; }
    public void setNumberOfGuests(int numberOfGuests) { this.numberOfGuests = numberOfGuests; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

    public BookingStatus getStatus() { return status; }
    public void setStatus(BookingStatus status) { this.status = status; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public Date getActualCheckIn() { return actualCheckIn; }
    public void setActualCheckIn(Date actualCheckIn) { this.actualCheckIn = actualCheckIn; }

    public Date getActualCheckOut() { return actualCheckOut; }
    public void setActualCheckOut(Date actualCheckOut) { this.actualCheckOut = actualCheckOut; }
}