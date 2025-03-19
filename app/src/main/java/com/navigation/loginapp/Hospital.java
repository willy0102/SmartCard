package com.navigation.loginapp;

import android.location.Location;

import androidx.appcompat.app.AppCompatActivity;

public class Hospital extends AppCompatActivity {
    private String name;
    private String phoneNumber;
    private double latitude;
    private double longitude;

    // Constructor with phone number
    public Hospital(String name, String phoneNumber, double latitude, double longitude) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    // Getters
    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    // Method to calculate distance to another location
    public double calculateDistance(double userLatitude, double userLongitude) {
        float[] results = new float[1];
        Location.distanceBetween(latitude, longitude, userLatitude, userLongitude, results);
        return results[0]; // Distance in meters
    }
}
