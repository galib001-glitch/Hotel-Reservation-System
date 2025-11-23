package com.hotel.dao;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.hotel.model.Payment;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class PaymentDAO {
    private final Firestore firestore;
    private final String COLLECTION_NAME = "payments";

    public PaymentDAO() {
        this.firestore = FirebaseConnection.getFirestore();
    }

    public String addPayment(Payment payment) {
        try {
            String paymentId = UUID.randomUUID().toString();
            payment.setPaymentId(paymentId);

            DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(paymentId);
            ApiFuture<WriteResult> result = docRef.set(payment);
            result.get();
            return paymentId;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Payment getPayment(String paymentId) {
        try {
            DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(paymentId);
            ApiFuture<DocumentSnapshot> future = docRef.get();
            DocumentSnapshot document = future.get();

            if (document.exists()) {
                return document.toObject(Payment.class);
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Payment> getPaymentsByBooking(String bookingId) {
        List<Payment> payments = new ArrayList<>();
        try {
            ApiFuture<QuerySnapshot> future = firestore.collection(COLLECTION_NAME)
                    .whereEqualTo("bookingId", bookingId)
                    .get();

            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            for (QueryDocumentSnapshot document : documents) {
                payments.add(document.toObject(Payment.class));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return payments;
    }

    public boolean updatePayment(Payment payment) {
        try {
            DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(payment.getPaymentId());
            ApiFuture<WriteResult> result = docRef.set(payment);
            result.get();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}