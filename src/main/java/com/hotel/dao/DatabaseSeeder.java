package com.hotel.dao;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.hotel.model.*;
import com.hotel.auth.User;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;
import java.util.logging.Level;

public class DatabaseSeeder {
    private final Firestore firestore;
    private final GuestDAO guestDAO;
    private final RoomDAO roomDAO;
    private final BookingDAO bookingDAO;
    private final PaymentDAO paymentDAO;
    private static final Logger logger = Logger.getLogger(DatabaseSeeder.class.getName());

    public DatabaseSeeder() {
        this.firestore = FirebaseConnection.getFirestore();
        this.guestDAO = new GuestDAO();
        this.roomDAO = new RoomDAO();
        this.bookingDAO = new BookingDAO();
        this.paymentDAO = new PaymentDAO();
    }

    public void seedAllData() {
        try {
            System.out.println("Starting database seeding...");

            // Initialize collections first
            initializeCollections();

            // Seed data in order
            seedUsers();
            seedRooms();
            seedGuests();
            seedBookings();
            seedPayments();

            System.out.println("Database seeding completed successfully!");

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error during database seeding", e);
        }
    }

    private void initializeCollections() {
        try {
            System.out.println("Initializing Firestore collections...");

            // List of collections we need
            String[] collections = {"users", "guests", "rooms", "bookings", "payments"};

            for (String collectionName : collections) {
                initializeCollection(collectionName);
            }

            System.out.println("Firestore collections initialized successfully!");

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error initializing collections", e);
        }
    }

    private void initializeCollection(String collectionName) {
        try {
            // Try to get a document from the collection to see if it exists
            ApiFuture<QuerySnapshot> future = firestore.collection(collectionName).limit(1).get();
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();

            System.out.println("Collection '" + collectionName + "' is accessible");

        } catch (Exception e) {
            System.out.println("Collection '" + collectionName + "' might not exist, but it will be created automatically when we add data");
        }
    }

    private void seedUsers() {
        try {
            System.out.println("Seeding users...");

            List<User> users = Arrays.asList(
                    new User(
                            UUID.randomUUID().toString(),
                            "admin",
                            "admin123",
                            User.UserRole.ADMIN,
                            "System Administrator",
                            "admin@hotelgrand.com"
                    ),
                    new User(
                            UUID.randomUUID().toString(),
                            "reception",
                            "reception123",
                            User.UserRole.RECEPTIONIST,
                            "Sarah Johnson",
                            "sarah.johnson@hotelgrand.com"
                    ),
                    new User(
                            UUID.randomUUID().toString(),
                            "manager",
                            "manager123",
                            User.UserRole.MANAGER,
                            "Michael Brown",
                            "michael.brown@hotelgrand.com"
                    ),
                    new User(
                            UUID.randomUUID().toString(),
                            "reception2",
                            "reception123",
                            User.UserRole.RECEPTIONIST,
                            "Emily Davis",
                            "emily.davis@hotelgrand.com"
                    )
            );

            for (User user : users) {
                ApiFuture<WriteResult> future = firestore.collection("users")
                        .document(user.getUserId())
                        .set(user);
                future.get();
                System.out.println("✓ Created user: " + user.getUsername());
            }

            System.out.println("✓ Users seeded successfully!");

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error seeding users", e);
        }
    }

    private void seedRooms() {
        try {
            System.out.println("Seeding rooms...");

            List<Room> rooms = Arrays.asList(
                    // Single Rooms
                    new Room("101", Room.RoomType.SINGLE, 89.99, "Cozy single room with city view"),
                    new Room("102", Room.RoomType.SINGLE, 89.99, "Comfortable single room"),
                    new Room("103", Room.RoomType.SINGLE, 79.99, "Standard single room"),
                    new Room("104", Room.RoomType.SINGLE, 79.99, "Economy single room"),

                    // Double Rooms
                    new Room("201", Room.RoomType.DOUBLE, 129.99, "Spacious double room with balcony"),
                    new Room("202", Room.RoomType.DOUBLE, 129.99, "Double room with garden view"),
                    new Room("203", Room.RoomType.DOUBLE, 119.99, "Standard double room"),
                    new Room("204", Room.RoomType.DOUBLE, 119.99, "Comfortable double room"),
                    new Room("205", Room.RoomType.DOUBLE, 119.99, "Double room with workspace"),

                    // Deluxe Rooms
                    new Room("301", Room.RoomType.DELUXE, 199.99, "Luxurious deluxe room with jacuzzi"),
                    new Room("302", Room.RoomType.DELUXE, 199.99, "Deluxe room with panoramic view"),
                    new Room("303", Room.RoomType.DELUXE, 189.99, "Executive deluxe room"),
                    new Room("304", Room.RoomType.DELUXE, 189.99, "Deluxe room with king bed"),

                    // Suites
                    new Room("401", Room.RoomType.SUITE, 299.99, "Presidential suite with living area"),
                    new Room("402", Room.RoomType.SUITE, 279.99, "Executive suite with kitchenette"),
                    new Room("403", Room.RoomType.SUITE, 259.99, "Family suite"),
                    new Room("404", Room.RoomType.SUITE, 259.99, "Honeymoon suite")
            );

            // Set some rooms as under maintenance
            rooms.get(2).setStatus(Room.RoomStatus.MAINTENANCE); // Room 103
            rooms.get(14).setStatus(Room.RoomStatus.MAINTENANCE); // Room 403

            for (Room room : rooms) {
                boolean success = roomDAO.addRoom(room);
                if (success) {
                    System.out.println("✓ Created room: " + room.getRoomNumber() + " - " + room.getType() + " - $" + room.getPricePerNight());
                } else {
                    System.err.println("✗ Failed to create room: " + room.getRoomNumber());
                }
            }

            System.out.println("✓ Rooms seeded successfully!");

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error seeding rooms", e);
        }
    }

    private void seedGuests() {
        try {
            System.out.println("Seeding guests...");

            List<Guest> guests = Arrays.asList(
                    new Guest(
                            UUID.randomUUID().toString(),
                            "John",
                            "Smith",
                            "john.smith@email.com",
                            "+1-555-0101",
                            "123 Main St, New York, NY",
                            "PASS123456"
                    ),
                    new Guest(
                            UUID.randomUUID().toString(),
                            "Maria",
                            "Garcia",
                            "maria.garcia@email.com",
                            "+1-555-0102",
                            "456 Oak Ave, Los Angeles, CA",
                            "PASS123457"
                    ),
                    new Guest(
                            UUID.randomUUID().toString(),
                            "David",
                            "Johnson",
                            "david.johnson@email.com",
                            "+1-555-0103",
                            "789 Pine St, Chicago, IL",
                            "PASS123458"
                    ),
                    new Guest(
                            UUID.randomUUID().toString(),
                            "Sarah",
                            "Williams",
                            "sarah.williams@email.com",
                            "+1-555-0104",
                            "321 Elm St, Houston, TX",
                            "PASS123459"
                    ),
                    new Guest(
                            UUID.randomUUID().toString(),
                            "James",
                            "Brown",
                            "james.brown@email.com",
                            "+1-555-0105",
                            "654 Maple Dr, Phoenix, AZ",
                            "PASS123460"
                    ),
                    new Guest(
                            UUID.randomUUID().toString(),
                            "Lisa",
                            "Davis",
                            "lisa.davis@email.com",
                            "+1-555-0106",
                            "987 Cedar Ln, Philadelphia, PA",
                            "PASS123461"
                    ),
                    new Guest(
                            UUID.randomUUID().toString(),
                            "Robert",
                            "Miller",
                            "robert.miller@email.com",
                            "+1-555-0107",
                            "147 Birch Rd, San Antonio, TX",
                            "PASS123462"
                    ),
                    new Guest(
                            UUID.randomUUID().toString(),
                            "Jennifer",
                            "Wilson",
                            "jennifer.wilson@email.com",
                            "+1-555-0108",
                            "258 Walnut St, San Diego, CA",
                            "PASS123463"
                    ),
                    new Guest(
                            UUID.randomUUID().toString(),
                            "Michael",
                            "Taylor",
                            "michael.taylor@email.com",
                            "+1-555-0109",
                            "369 Spruce Ave, Dallas, TX",
                            "PASS123464"
                    ),
                    new Guest(
                            UUID.randomUUID().toString(),
                            "Emily",
                            "Anderson",
                            "emily.anderson@email.com",
                            "+1-555-0110",
                            "741 Willow Way, San Jose, CA",
                            "PASS123465"
                    )
            );

            for (Guest guest : guests) {
                String guestId = guestDAO.addGuest(guest);
                if (guestId != null) {
                    System.out.println("✓ Created guest: " + guest.getFirstName() + " " + guest.getLastName());
                } else {
                    System.err.println("✗ Failed to create guest: " + guest.getFirstName() + " " + guest.getLastName());
                }
            }

            System.out.println("✓ Guests seeded successfully!");

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error seeding guests", e);
        }
    }

    private void seedBookings() {
        try {
            System.out.println("Seeding bookings...");

            // Get sample guests and rooms
            List<Guest> guests = guestDAO.getAllGuests();
            List<Room> rooms = roomDAO.getAllRooms();

            if (guests.isEmpty() || rooms.isEmpty()) {
                System.err.println("No guests or rooms available for booking creation");
                return;
            }

            Calendar cal = Calendar.getInstance();

            // Create past bookings (checked out)
            Booking booking1 = new Booking(
                    UUID.randomUUID().toString(),
                    guests.get(0).getGuestId(),
                    "101",
                    getDate(cal, -10),
                    getDate(cal, -7),
                    1,
                    269.97
            );
            booking1.setStatus(Booking.BookingStatus.CHECKED_OUT);
            booking1.setActualCheckIn(getDate(cal, -10));
            booking1.setActualCheckOut(getDate(cal, -7));

            Booking booking2 = new Booking(
                    UUID.randomUUID().toString(),
                    guests.get(1).getGuestId(),
                    "201",
                    getDate(cal, -5),
                    getDate(cal, -2),
                    2,
                    389.97
            );
            booking2.setStatus(Booking.BookingStatus.CHECKED_OUT);
            booking2.setActualCheckIn(getDate(cal, -5));
            booking2.setActualCheckOut(getDate(cal, -2));

            // Create current bookings (checked in)
            Booking booking3 = new Booking(
                    UUID.randomUUID().toString(),
                    guests.get(2).getGuestId(),
                    "301",
                    getDate(cal, -2),
                    getDate(cal, 3),
                    2,
                    999.95
            );
            booking3.setStatus(Booking.BookingStatus.CHECKED_IN);
            booking3.setActualCheckIn(getDate(cal, -2));

            Booking booking4 = new Booking(
                    UUID.randomUUID().toString(),
                    guests.get(3).getGuestId(),
                    "202",
                    getDate(cal, -1),
                    getDate(cal, 4),
                    2,
                    649.95
            );
            booking4.setStatus(Booking.BookingStatus.CHECKED_IN);
            booking4.setActualCheckIn(getDate(cal, -1));

            // Create future bookings (confirmed)
            Booking booking5 = new Booking(
                    UUID.randomUUID().toString(),
                    guests.get(4).getGuestId(),
                    "401",
                    getDate(cal, 2),
                    getDate(cal, 7),
                    4,
                    1799.93
            );

            Booking booking6 = new Booking(
                    UUID.randomUUID().toString(),
                    guests.get(5).getGuestId(),
                    "302",
                    getDate(cal, 5),
                    getDate(cal, 10),
                    2,
                    1199.90
            );

            List<Booking> bookings = Arrays.asList(booking1, booking2, booking3, booking4, booking5, booking6);

            for (Booking booking : bookings) {
                String bookingId = bookingDAO.addBooking(booking);
                if (bookingId != null) {
                    // Update room status if booked
                    Room room = roomDAO.getRoom(booking.getRoomNumber());
                    if (room != null && (booking.getStatus() == Booking.BookingStatus.CONFIRMED ||
                            booking.getStatus() == Booking.BookingStatus.CHECKED_IN)) {
                        room.setStatus(Room.RoomStatus.BOOKED);
                        roomDAO.updateRoom(room);
                    }
                    System.out.println("✓ Created booking: " + booking.getBookingId() + " - Room " + booking.getRoomNumber() + " - " + booking.getStatus());
                } else {
                    System.err.println("✗ Failed to create booking for room: " + booking.getRoomNumber());
                }
            }

            System.out.println("✓ Bookings seeded successfully!");

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error seeding bookings", e);
        }
    }

    private void seedPayments() {
        try {
            System.out.println("Seeding payments...");

            List<Booking> bookings = bookingDAO.getAllBookings();

            if (bookings.isEmpty()) {
                System.err.println("No bookings available for payment creation");
                return;
            }

            // Create payments for each booking
            for (Booking booking : bookings) {
                Payment payment = new Payment(
                        UUID.randomUUID().toString(),
                        booking.getBookingId(),
                        booking.getTotalAmount(),
                        getRandomPaymentMethod()
                );

                // Set payment status based on booking status
                if (booking.getStatus() == Booking.BookingStatus.CHECKED_OUT) {
                    payment.setStatus(Payment.PaymentStatus.COMPLETED);
                    payment.setTransactionId("TXN" + System.currentTimeMillis());
                } else if (booking.getStatus() == Booking.BookingStatus.CHECKED_IN) {
                    payment.setStatus(Payment.PaymentStatus.COMPLETED);
                    payment.setTransactionId("TXN" + System.currentTimeMillis());
                } else {
                    payment.setStatus(Payment.PaymentStatus.PENDING);
                }

                String paymentId = paymentDAO.addPayment(payment);
                if (paymentId != null) {
                    System.out.println("✓ Created payment: " + payment.getPaymentId() + " - $" + payment.getAmount() + " - " + payment.getStatus());
                } else {
                    System.err.println("✗ Failed to create payment for booking: " + booking.getBookingId());
                }
            }

            System.out.println("✓ Payments seeded successfully!");

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error seeding payments", e);
        }
    }

    private Date getDate(Calendar cal, int daysFromNow) {
        Calendar newCal = (Calendar) cal.clone();
        newCal.add(Calendar.DAY_OF_MONTH, daysFromNow);
        return newCal.getTime();
    }

    private Payment.PaymentMethod getRandomPaymentMethod() {
        Payment.PaymentMethod[] methods = Payment.PaymentMethod.values();
        Random random = new Random();
        return methods[random.nextInt(methods.length)];
    }

    public void displaySeedSummary() {
        try {
            Thread.sleep(2000); // Wait for all operations to complete
            System.out.println("\n" + "=".repeat(50));
            System.out.println("DATABASE SEEDING SUMMARY");
            System.out.println("=".repeat(50));

            // Count documents in each collection
            countDocuments("users", "Users");
            countDocuments("guests", "Guests");
            countDocuments("rooms", "Rooms");
            countDocuments("bookings", "Bookings");
            countDocuments("payments", "Payments");

            System.out.println("=".repeat(50));
            System.out.println("SEEDING COMPLETED SUCCESSFULLY!");
            System.out.println("=".repeat(50));

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error generating summary", e);
        }
    }

    private void countDocuments(String collectionName, String label) {
        try {
            ApiFuture<QuerySnapshot> future = firestore.collection(collectionName).get();
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            System.out.printf("%-12s: %d documents%n", label, documents.size());
        } catch (Exception e) {
            System.out.printf("%-12s: ERROR - %s%n", label, e.getMessage());
        }
    }

    // Method to run seeder from main
    public static void main(String[] args) {
        try {
            System.out.println("🚀 Starting Hotel Reservation System Database Seeder");
            System.out.println("⏳ Initializing Firebase connection...");

            // Initialize Firebase
            FirebaseConnection.initialize();

            // Create and run seeder
            DatabaseSeeder seeder = new DatabaseSeeder();
            seeder.seedAllData();
            seeder.displaySeedSummary();

            System.out.println("\n🎉 Database seeding completed!");
            System.out.println("📱 You can now run the main application.");
            System.out.println("\n🔑 Default Login Credentials:");
            System.out.println("   Admin:      admin / admin123");
            System.out.println("   Reception:  reception / reception123");
            System.out.println("   Manager:    manager / manager123");

        } catch (Exception e) {
            System.err.println("❌ Failed to seed database: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}