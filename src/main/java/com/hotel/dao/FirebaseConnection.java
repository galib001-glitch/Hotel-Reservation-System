package com.hotel.dao;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

public class FirebaseConnection {
    private static Firestore firestore;

    public static void initialize() {
        try {
            if (FirebaseApp.getApps().isEmpty()) {
                // Try multiple ways to load the config file
                InputStream serviceAccount = getConfigInputStream();

                if (serviceAccount == null) {
                    throw new RuntimeException("Firebase config file not found. Please make sure 'firebase-config.json' is in the classpath or project root.");
                }

                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                        .build();

                FirebaseApp.initializeApp(options);
            }
            firestore = FirestoreClient.getFirestore();
            System.out.println("Firebase connected successfully!");
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to initialize Firebase: " + e.getMessage(), e);
        }
    }

    private static InputStream getConfigInputStream() {
        try {
            // Try 1: Load from classpath (src/main/resources)
            InputStream inputStream = FirebaseConnection.class
                    .getClassLoader()
                    .getResourceAsStream("firebase-config.json");

            if (inputStream != null) {
                System.out.println("Loaded firebase-config.json from classpath");
                return inputStream;
            }

            // Try 2: Load from project root directory
            File configFile = new File("firebase-config.json");
            if (configFile.exists()) {
                System.out.println("Loaded firebase-config.json from project root");
                return new FileInputStream(configFile);
            }

            // Try 3: Load from src/main/resources directory
            configFile = new File("src/main/resources/firebase-config.json");
            if (configFile.exists()) {
                System.out.println("Loaded firebase-config.json from src/main/resources");
                return new FileInputStream(configFile);
            }

            System.err.println("firebase-config.json not found in any location");
            return null;

        } catch (Exception e) {
            System.err.println("Error loading firebase config: " + e.getMessage());
            return null;
        }
    }

    public static Firestore getFirestore() {
        if (firestore == null) {
            initialize();
        }
        return firestore;
    }
}