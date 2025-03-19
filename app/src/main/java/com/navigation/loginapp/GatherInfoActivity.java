package com.navigation.loginapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class GatherInfoActivity extends AppCompatActivity {

    private EditText phoneRelatives;
    private EditText healthIssues;
    private EditText prescriptions;
    private EditText allergy;
    private EditText notes;
    private EditText phoneNumber;
    private Button saveButton,btnBack;
    private String userId;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gather_information); // Ensure this matches your layout file name

        // Retrieve the user ID passed from HomeActivity
        userId = getIntent().getStringExtra("USER_ID");

        // Initialize Firestore
        firestore = FirebaseFirestore.getInstance();

        // Initialize views
        phoneRelatives = findViewById(R.id.phone_relatives);
        healthIssues = findViewById(R.id.health_issues);
        prescriptions = findViewById(R.id.prescriptions);
        allergy = findViewById(R.id.allergy);
        notes = findViewById(R.id.notes);
        phoneNumber = findViewById(R.id.telephone);
        saveButton = findViewById(R.id.save_button);
        btnBack = findViewById(R.id.btnBack);
        // Set a click listener on the save button
        saveButton.setOnClickListener(v -> {
            saveInfo();  // Call your save method to save data
            Intent intent = new Intent(GatherInfoActivity.this, HomeActivity.class);
            intent.putExtra("USER_ID", userId);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);  // Clear activity stack to go back to HomeActivity
            startActivity(intent);
            finish();  // Finish the current activity to remove it from the back stack
        });

        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(GatherInfoActivity.this, HomeActivity.class);
            intent.putExtra("USER_ID", userId);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);

        });
    }

    private void saveInfo() {
        String relativesPhone = phoneRelatives.getText().toString().trim();
        String PhoneNumber = phoneNumber.getText().toString().trim();
        String healthIssuesText = healthIssues.getText().toString().trim();
        String prescriptionsText = prescriptions.getText().toString().trim();
        String allergyText = allergy.getText().toString().trim();
        String notesText = notes.getText().toString().trim();


        // Validate input
        if (relativesPhone.isEmpty() || healthIssuesText.isEmpty() || prescriptionsText.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a map of data to save
        Map<String, Object> healthInfo = new HashMap<>();
        healthInfo.put("relativesPhone", relativesPhone);
        healthInfo.put("phoneNumber", PhoneNumber);
        healthInfo.put("healthIssues", healthIssuesText);
        healthInfo.put("prescriptions", prescriptionsText);
        healthInfo.put("allergy", allergyText);
        healthInfo.put("notes", notesText);

        // Save data to Firestore using update to avoid overwriting existing data
        firestore.collection("users")
                .document(userId) // Use userId as document ID to uniquely identify the user
                .update(healthInfo)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(GatherInfoActivity.this, "Information saved", Toast.LENGTH_SHORT).show();
                    // Optionally, finish this activity or navigate back
                    finish();
                })
                .addOnFailureListener(e -> {
                    Log.w("GatherInfoActivity", "Error saving document", e);
                    Toast.makeText(GatherInfoActivity.this, "Error saving information", Toast.LENGTH_SHORT).show();
                });
    }
}
