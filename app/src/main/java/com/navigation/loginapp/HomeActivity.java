package com.navigation.loginapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

public class HomeActivity extends AppCompatActivity {

    private TextView welcomeTextView;
    private Button emergencyBtn, addInfoBtn, generateQRBtn, checkInfoBtn;
    private FirebaseFirestore firestore;
    private String userId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Get the userID passed from the previous activity
        userId = getIntent().getStringExtra("USER_ID");

        // Initialize Firestore
        firestore = FirebaseFirestore.getInstance();

        // Bind views
        welcomeTextView = findViewById(R.id.welcome_text_view);
        emergencyBtn = findViewById(R.id.emergency_btn);
        addInfoBtn = findViewById(R.id.add_info_btn);
        checkInfoBtn = findViewById(R.id.check_info_btn);
        generateQRBtn = findViewById(R.id.generate_qr_btn);

        // Set welcome message
        welcomeTextView.setText("Xin chào, " + userId + "!");

        // Emergency button: Navigate to HospitalListActivity
        emergencyBtn.setOnClickListener(v -> {
            // Navigate to HospitalListActivity
            Intent intent = new Intent(HomeActivity.this, HospitalListActivity.class);
            // Pass any data to the activity if needed, e.g., userId
            intent.putExtra("USER_ID", userId);
            startActivity(intent);
        });

        // Add Info button: Navigate to GatherInfoActivity
        addInfoBtn.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, GatherInfoActivity.class);
            intent.putExtra("USER_ID", userId);
            startActivity(intent);
        });
        checkInfoBtn.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, CheckInfoActivity.class);
            intent.putExtra("userID", userId);
            startActivity(intent);
        });

        // Generate QR Code button: Navigate to QRCodeGenerator
        generateQRBtn.setOnClickListener(v -> generateQRCode());
    }

    // Method to generate QR code
    private void generateQRCode() {
        // Start QRCodeGenerator activity and pass the userId as an intent extra
        Intent intent = new Intent(HomeActivity.this, QRCodeGenerator.class);
        intent.putExtra("USER_ID", userId);  // Pass the userId
        startActivity(intent);
    }
}
