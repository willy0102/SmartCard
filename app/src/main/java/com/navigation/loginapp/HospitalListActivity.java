package com.navigation.loginapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HospitalListActivity extends AppCompatActivity {

    private static final String TAG = "HospitalListActivity";
    private static final int LOCATION_REQUEST_CODE = 100;
    private static final int CALL_REQUEST_CODE = 101; // Request code for call permission

    private List<Hospital> hospitals;
    private FirebaseFirestore firestore;
    private ArrayAdapter<String> adapter;
    private List<String> hospitalNames;
    private FusedLocationProviderClient fusedLocationClient;
    private Location userLocation;
    private int selectedPosition; // Track the selected position in the list

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospital_list);

        // Initialize Firestore and Location Client
        firestore = FirebaseFirestore.getInstance();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        ListView hospitalListView = findViewById(R.id.hospital_list_view);

        // Initialize lists
        hospitals = new ArrayList<>();
        hospitalNames = new ArrayList<>();

        // Create an adapter to display the hospital names
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, hospitalNames);
        hospitalListView.setAdapter(adapter);

        // Check for location permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
        } else {
            // Fetch the user's location and then fetch hospitals
            fetchUserLocation();
        }

        // Set an item click listener to trigger a call when a hospital name is clicked
        hospitalListView.setOnItemClickListener((parent, view, position, id) -> {
            selectedPosition = position; // Store the position of the clicked item
            Hospital selectedHospital = hospitals.get(position);
            String phoneNumber = selectedHospital.getPhoneNumber();

            // Check if phone number is valid
            if (phoneNumber != null && !phoneNumber.isEmpty()) {
                // Check for CALL_PHONE permission before making the call
                if (ContextCompat.checkSelfPermission(HospitalListActivity.this, Manifest.permission.CALL_PHONE)
                        != PackageManager.PERMISSION_GRANTED) {
                    // Request permission
                    ActivityCompat.requestPermissions(HospitalListActivity.this,
                            new String[]{Manifest.permission.CALL_PHONE}, CALL_REQUEST_CODE);
                } else {
                    // Permission already granted, make the call
                    makePhoneCall(phoneNumber);
                }
            } else {
                // Show error if phone number is not available
                Toast.makeText(HospitalListActivity.this, "Phone number not available", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Method to fetch the user's location
    private void fetchUserLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
            return;
        }

        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                userLocation = location;
                // Now fetch hospitals from Firestore
                fetchHospitals();
            } else {
                Toast.makeText(HospitalListActivity.this, "Unable to get location", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Method to fetch hospitals from Firestore
    private void fetchHospitals() {
        firestore.collection("hospitals")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null) {
                            for (DocumentSnapshot document : querySnapshot) {
                                String name = document.getId();  // Assuming document ID is the hospital name
                                String phoneNumber = document.getString("phone_number");
                                Double latitude = document.getDouble("latitude");
                                Double longitude = document.getDouble("longitude");

                                if (phoneNumber != null && latitude != null && longitude != null) {
                                    // Create a new Hospital object with name, phone number, latitude, and longitude
                                    Hospital hospital = new Hospital(name, phoneNumber, latitude, longitude);
                                    hospitals.add(hospital);
                                    hospitalNames.add(name);  // Add name to the list
                                } else {
                                    Log.e(TAG, "Missing data in document: " + document.getId());
                                }
                            }
                            // Sort hospitals by distance
                            sortHospitalsByDistance();

                            // Notify the adapter that the data has changed
                            adapter.notifyDataSetChanged();
                        }
                    } else {
                        Log.e(TAG, "Error fetching hospitals: ", task.getException());
                        Toast.makeText(HospitalListActivity.this, "Failed to fetch hospitals", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Method to sort hospitals by distance from user location
    private void sortHospitalsByDistance() {
        if (userLocation != null) {
            Collections.sort(hospitals, (h1, h2) -> {
                float[] result1 = new float[1];
                Location.distanceBetween(userLocation.getLatitude(), userLocation.getLongitude(),
                        h1.getLatitude(), h1.getLongitude(), result1);

                float[] result2 = new float[1];
                Location.distanceBetween(userLocation.getLatitude(), userLocation.getLongitude(),
                        h2.getLatitude(), h2.getLongitude(), result2);

                return Float.compare(result1[0], result2[0]);
            });

            // Clear and update the hospital names list to reflect the sorted order
            hospitalNames.clear();
            for (Hospital hospital : hospitals) {
                hospitalNames.add(hospital.getName());
            }
        } else {
            Toast.makeText(this, "User location not available", Toast.LENGTH_SHORT).show();
        }
    }

    // Method to make a phone call
    private void makePhoneCall(String phoneNumber) {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + phoneNumber));
        startActivity(callIntent);
    }

    // Handle the result of location permission request
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                fetchUserLocation();
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == CALL_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, call the phone number
                Hospital selectedHospital = hospitals.get(selectedPosition);  // Use the selected position
                makePhoneCall(selectedHospital.getPhoneNumber());
            } else {
                Toast.makeText(this, "Call permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
