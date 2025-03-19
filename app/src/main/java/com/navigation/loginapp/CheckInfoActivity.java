package com.navigation.loginapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class CheckInfoActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private TextView txtNote, txtAllergy, txtHealthIssues, txtPrescriptions, txtRelativesPhone, txtPhoneNumber;
    private String userID;  // Get this from intent

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_info);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Get userID from intent
        userID = getIntent().getStringExtra("userID");

        // Initialize UI components
        txtNote = findViewById(R.id.txtNote);
        txtPhoneNumber = findViewById(R.id.txtPhoneNumber);
        txtAllergy = findViewById(R.id.txtAllergy);
        txtHealthIssues = findViewById(R.id.txtHealthIssues);
        txtPrescriptions = findViewById(R.id.txtPrescriptions);
        txtRelativesPhone = findViewById(R.id.txtRelativesPhone);

        Button btnBack = findViewById(R.id.btnBack);

        // Handle back button click
        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(CheckInfoActivity.this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("USER_ID", userID);
            startActivity(intent);

        });

        // Fetch user info
        getUserInfo(userID);
    }

    private void getUserInfo(String userID) {
        if (userID == null || userID.isEmpty()) {
            Log.e("Firestore", "Invalid userID");
            return;
        }

        DocumentReference docRef = db.collection("users").document(userID);
        docRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String note = documentSnapshot.getString("notes");
                String allergy = documentSnapshot.getString("allergy");
                String healthIssues = documentSnapshot.getString("healthIssues");
                String prescriptions = documentSnapshot.getString("prescriptions");
                String relativesPhone = documentSnapshot.getString("relativesPhone");
                String phoneNumber = documentSnapshot.getString("phoneNumber");

                // Update UI
                txtNote.setText(note);
                txtAllergy.setText(allergy);
                txtHealthIssues.setText(healthIssues);
                txtPrescriptions.setText(prescriptions);
                txtRelativesPhone.setText(relativesPhone);
                txtPhoneNumber.setText(phoneNumber);
            } else {
                Log.e("Firestore", "No such document");
            }
        }).addOnFailureListener(e -> Log.e("Firestore", "Failed to fetch data", e));
    }
}
