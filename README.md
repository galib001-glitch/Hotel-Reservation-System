Hotel Reservation System (HRS)

This is a desktop application designed to manage the core operations of a hotel, including guest reservations, room status updates, payment processing, and administrative reporting. It is built using Java Swing for the graphical user interface and leverages Firebase Firestore for a cloud-based, real-time data backend.

🚀 Features

The system is built around the following functional modules:

1. Guest Management

CRUD Operations: Add new guests, view details, edit information, and delete profiles.

2. Room Management

Room Types: Supports Single, Double, Deluxe, and Suite room definitions.

Status Tracking: Tracks room status as Available, Booked, or Under Maintenance.

Pricing: Allows setting dynamic pricing based on room type.

3. Booking & Reservation System

Book new rooms based on availability and date.

Search for available rooms between specified check-in/check-out dates.

Check-in/Check-out: Automated processes to update room status upon guest arrival and departure.

4. Payment Management

Record payments and track payment types (Cash, Card, Online).

Generate comprehensive guest bills upon check-out.

5. Multi-Level User Authentication

Secure Admin Login system with role-based access control.

User Roles: Admin, Manager, and Receptionist, each with specific permissions.

6. Reports

Generate daily revenue reports and monthly booking summaries.

View a list of guests currently staying at the hotel.

🛠️ Technology Stack

Frontend

Technology: Java Swing

Notes: Standard desktop UI framework.

Backend/Core

Technology: Java Development Kit (JDK)

Version: 25

Notes: Latest long-term support version.

Database

Technology: Firebase Firestore (Latest SDK)

Notes: Real-time, cloud-based NoSQL database, accessed via the Firebase Admin SDK.

Dependency Management

Technology: Maven

Notes: Used to manage all Java and Firebase dependencies.

⚙️ Setup and Installation

Prerequisites

JDK 25 installed and configured.

Maven installed.

IntelliJ IDEA or another compatible Java IDE.

A Firebase Project with Firestore enabled (see Database Setup).

Database Setup

Create Firebase Project: Set up a new project in the Firebase Console named Hotel Reservation System.

Enable Firestore: Initialize the Firestore Database in Production Mode.

Security Rules: Apply the required security rules to allow read/write access to authenticated users in the artifacts/{appId}/public/data/... path.

Initial Data: Run the DataSeeder.java class once (or use the main application entry point during initial setup) to populate the database with initial users, rooms, and bookings.

Project Configuration

Since this application is designed for a collaborative environment, it relies on specific global context variables for authentication and configuration:

__firebase_config (JSON string containing Firebase project details).

__initial_auth_token (Custom token for initial user authentication).

__app_id (The unique ID for the application instance).

Note: If running locally outside of the Canvas environment, you would typically replace the usage of these global variables in FirestoreManager.java with a local service account key file.

Run the Application

Clone the repository.

Open the project in IntelliJ IDEA.

Ensure Maven dependencies are downloaded (mvn clean install).

Run the com.hotel.hrs.Main class.

🔑 Initial Login Credentials (Seeded Data)

The DataSeeder.java creates three test user accounts for initial login:

Admin Account (Full Access)

Username: 

Password: 

Manager Account (Reports, Guest/Room Management)

Username: 

Password: 

Receptionist Account (Daily Operations: Bookings, Payments, Check-in/out)

Username: 

Password: 
