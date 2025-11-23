package com.hotel.dao;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.hotel.model.Booking;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class BookingDAO {
    private final Firestore firestore;
    private final String COLLECTION_NAME = "bookings";

    public BookingDAO() {
        this.firestore = FirebaseConnection.getFirestore();
    }

    public String addBooking(Booking booking) {
        try {
            String bookingId = UUID.randomUUID().toString();
            booking.setBookingId(bookingId);

            DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(bookingId);
            ApiFuture<WriteResult> result = docRef.set(booking);
            result.get();
            return bookingId;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Booking getBooking(String bookingId) {
        try {
            DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(bookingId);
            ApiFuture<DocumentSnapshot> future = docRef.get();
            DocumentSnapshot document = future.get();

            if (document.exists()) {
                return document.toObject(Booking.class);
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Booking> getAllBookings() {
        List<Booking> bookings = new ArrayList<>();
        try {
            ApiFuture<QuerySnapshot> future = firestore.collection(COLLECTION_NAME).get();
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();

            for (QueryDocumentSnapshot document : documents) {
                bookings.add(document.toObject(Booking.class));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bookings;
    }

    public boolean updateBooking(Booking booking) {
        try {
            DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(booking.getBookingId());
            ApiFuture<WriteResult> result = docRef.set(booking);
            result.get();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Booking> getBookingsByGuest(String guestId) {
        List<Booking> bookings = new ArrayList<>();
        try {
            ApiFuture<QuerySnapshot> future = firestore.collection(COLLECTION_NAME)
                    .whereEqualTo("guestId", guestId)
                    .get();

            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            for (QueryDocumentSnapshot document : documents) {
                bookings.add(document.toObject(Booking.class));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bookings;
    }

    public List<Booking> getActiveBookings() {
        List<Booking> bookings = new ArrayList<>();
        try {
            // Get bookings that are confirmed or checked in
            ApiFuture<QuerySnapshot> future = firestore.collection(COLLECTION_NAME)
                    .whereIn("status",
                            List.of(Booking.BookingStatus.CONFIRMED, Booking.BookingStatus.CHECKED_IN))
                    .get();

            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            for (QueryDocumentSnapshot document : documents) {
                bookings.add(document.toObject(Booking.class));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bookings;
    }
}