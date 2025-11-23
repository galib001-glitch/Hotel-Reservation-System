package com.hotel.dao;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.hotel.model.Guest;

import java.util.*;
import java.util.concurrent.ExecutionException;

public class GuestDAO {
    private final Firestore firestore;
    private final String COLLECTION_NAME = "guests";

    public GuestDAO() {
        this.firestore = FirebaseConnection.getFirestore();
    }

    public String addGuest(Guest guest) {
        try {
            String guestId = UUID.randomUUID().toString();
            guest.setGuestId(guestId);

            DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(guestId);
            ApiFuture<WriteResult> result = docRef.set(guest);
            result.get();
            return guestId;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Guest getGuest(String guestId) {
        try {
            DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(guestId);
            ApiFuture<DocumentSnapshot> future = docRef.get();
            DocumentSnapshot document = future.get();

            if (document.exists()) {
                return document.toObject(Guest.class);
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Guest> getAllGuests() {
        List<Guest> guests = new ArrayList<>();
        try {
            ApiFuture<QuerySnapshot> future = firestore.collection(COLLECTION_NAME).get();
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();

            for (QueryDocumentSnapshot document : documents) {
                guests.add(document.toObject(Guest.class));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return guests;
    }

    public boolean updateGuest(Guest guest) {
        try {
            DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(guest.getGuestId());
            ApiFuture<WriteResult> result = docRef.set(guest);
            result.get();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteGuest(String guestId) {
        try {
            ApiFuture<WriteResult> result = firestore.collection(COLLECTION_NAME).document(guestId).delete();
            result.get();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public Guest findGuestByEmail(String email) {
        try {
            ApiFuture<QuerySnapshot> future = firestore.collection(COLLECTION_NAME)
                    .whereEqualTo("email", email)
                    .get();

            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            if (!documents.isEmpty()) {
                return documents.get(0).toObject(Guest.class);
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}