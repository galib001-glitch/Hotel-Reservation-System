package com.hotel.dao;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.hotel.model.Room;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class RoomDAO {
    private final Firestore firestore;
    private final String COLLECTION_NAME = "rooms";

    public RoomDAO() {
        this.firestore = FirebaseConnection.getFirestore();
    }

    public boolean addRoom(Room room) {
        try {
            DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(room.getRoomNumber());
            ApiFuture<WriteResult> result = docRef.set(room);
            result.get();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public Room getRoom(String roomNumber) {
        try {
            DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(roomNumber);
            ApiFuture<DocumentSnapshot> future = docRef.get();
            DocumentSnapshot document = future.get();

            if (document.exists()) {
                return document.toObject(Room.class);
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Room> getAllRooms() {
        List<Room> rooms = new ArrayList<>();
        try {
            ApiFuture<QuerySnapshot> future = firestore.collection(COLLECTION_NAME).get();
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();

            for (QueryDocumentSnapshot document : documents) {
                rooms.add(document.toObject(Room.class));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rooms;
    }

    public List<Room> getAvailableRooms() {
        List<Room> rooms = new ArrayList<>();
        try {
            ApiFuture<QuerySnapshot> future = firestore.collection(COLLECTION_NAME)
                    .whereEqualTo("status", Room.RoomStatus.AVAILABLE)
                    .get();

            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            for (QueryDocumentSnapshot document : documents) {
                rooms.add(document.toObject(Room.class));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rooms;
    }

    public boolean updateRoom(Room room) {
        try {
            DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(room.getRoomNumber());
            ApiFuture<WriteResult> result = docRef.set(room);
            result.get();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteRoom(String roomNumber) {
        try {
            ApiFuture<WriteResult> result = firestore.collection(COLLECTION_NAME).document(roomNumber).delete();
            result.get();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}