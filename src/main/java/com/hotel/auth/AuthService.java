package com.hotel.auth;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.hotel.dao.FirebaseConnection;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AuthService {
    private final Firestore firestore;
    private final String COLLECTION_NAME = "users";
    private User currentUser;
    private static final Logger logger = Logger.getLogger(AuthService.class.getName());

    public AuthService() {
        this.firestore = FirebaseConnection.getFirestore();
        initializeDefaultUsers();
    }

    private void initializeDefaultUsers() {
        try {
            // Check if admin user exists
            ApiFuture<QuerySnapshot> future = firestore.collection(COLLECTION_NAME)
                    .whereEqualTo("username", "admin")
                    .get();

            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            if (documents.isEmpty()) {
                // Create default users
                createUser("admin", "admin123", User.UserRole.ADMIN, "System Administrator", "admin@hotel.com");
                createUser("reception", "reception123", User.UserRole.RECEPTIONIST, "Front Desk", "reception@hotel.com");
                createUser("manager", "manager123", User.UserRole.MANAGER, "Hotel Manager", "manager@hotel.com");
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error initializing default users", e);
        }
    }

    private void createUser(String username, String password, User.UserRole role, String fullName, String email) {
        try {
            String userId = UUID.randomUUID().toString();
            User user = new User(userId, username, password, role, fullName, email);

            DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(userId);
            docRef.set(user).get();
            logger.info("Created default user: " + username);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error creating user: " + username, e);
        }
    }

    public User login(String username, String password) {
        try {
            ApiFuture<QuerySnapshot> future = firestore.collection(COLLECTION_NAME)
                    .whereEqualTo("username", username)
                    .whereEqualTo("password", password)
                    .get();

            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            if (!documents.isEmpty()) {
                currentUser = documents.get(0).toObject(User.class);
                logger.info("User logged in: " + username);
                return currentUser;
            }
            logger.warning("Login failed for user: " + username);
            return null;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error during login for user: " + username, e);
            return null;
        }
    }

    public void logout() {
        if (currentUser != null) {
            logger.info("User logged out: " + currentUser.getUsername());
            currentUser = null;
        }
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public boolean hasPermission(User.UserRole requiredRole) {
        if (currentUser == null) return false;

        return switch (requiredRole) {
            case RECEPTIONIST ->
                    currentUser.getRole() == User.UserRole.RECEPTIONIST ||
                            currentUser.getRole() == User.UserRole.MANAGER ||
                            currentUser.getRole() == User.UserRole.ADMIN;
            case MANAGER ->
                    currentUser.getRole() == User.UserRole.MANAGER ||
                            currentUser.getRole() == User.UserRole.ADMIN;
            case ADMIN ->
                    currentUser.getRole() == User.UserRole.ADMIN;
        };
    }
}