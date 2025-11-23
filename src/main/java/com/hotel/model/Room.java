package com.hotel.model;

public class Room {
    public enum RoomType { SINGLE, DOUBLE, DELUXE, SUITE }
    public enum RoomStatus { AVAILABLE, BOOKED, MAINTENANCE }

    private String roomNumber;
    private RoomType type;
    private RoomStatus status;
    private double pricePerNight;
    private String description;

    public Room() {}

    public Room(String roomNumber, RoomType type, double pricePerNight, String description) {
        this.roomNumber = roomNumber;
        this.type = type;
        this.pricePerNight = pricePerNight;
        this.description = description;
        this.status = RoomStatus.AVAILABLE;
    }

    // Getters and Setters
    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }

    public RoomType getType() { return type; }
    public void setType(RoomType type) { this.type = type; }

    public RoomStatus getStatus() { return status; }
    public void setStatus(RoomStatus status) { this.status = status; }

    public double getPricePerNight() { return pricePerNight; }
    public void setPricePerNight(double pricePerNight) { this.pricePerNight = pricePerNight; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    @Override
    public String toString() {
        return "Room " + roomNumber + " (" + type + ") - $" + pricePerNight + "/night";
    }
}