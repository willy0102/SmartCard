package com.navigation.loginapp;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class QRCodeGenerator extends AppCompatActivity {

    private ImageView qrCodeImageView;
    private String userId;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_code_generator);

        // Retrieve user ID passed from HomeActivity
        userId = getIntent().getStringExtra("USER_ID");
        Log.d("QRCodeGenerator", "Received User ID: " + userId);

        // Initialize Firestore
        firestore = FirebaseFirestore.getInstance();

        // Bind views
        qrCodeImageView = findViewById(R.id.qr_code_image_view);

        // Fetch user health information
        fetchUserHealthInfo();
    }

    private void fetchUserHealthInfo() {
        // Show a loading dialog while fetching data
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading health information...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        Log.d("QRCodeGenerator", "Fetching health info for User ID: " + userId);

        firestore.collection("users").document(userId).get()
                .addOnCompleteListener(task -> {
                    progressDialog.dismiss(); // Dismiss the dialog once data is fetched
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        Log.d("QRCodeGenerator", "Fetch successful. Document exists: " + (document != null && document.exists()));

                        if (document != null && document.exists()) {
                            UserHealthInfo healthInfo = document.toObject(UserHealthInfo.class);
                            if (healthInfo != null) {
                                generateQRCode(healthInfo);  // Generate the QR code using the user ID
                            } else {
                                showToast("Error retrieving health info");
                                Log.e("QRCodeGenerator", "HealthInfo object is null");
                            }
                        } else {
                            showToast("No such document");
                            Log.e("QRCodeGenerator", "Document does not exist");
                        }
                    } else {
                        Log.w("QRCodeGenerator", "Error getting documents.", task.getException());
                        showToast("Error retrieving information: " + task.getException().getMessage());
                    }
                });
    }

    private void generateQRCode(UserHealthInfo healthInfo) {
        // Format the QR code data with the link and userID
        String qrData = "https://smartcard.free.nf/?userID=" + userId;

        Log.d("QRCodeGenerator", "QR Data: " + qrData);

        try {
            // Generate the QR code with the formatted data
            BitMatrix bitMatrix = new MultiFormatWriter().encode(qrData, BarcodeFormat.QR_CODE, 400, 400);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            qrCodeImageView.setImageBitmap(barcodeEncoder.createBitmap(bitMatrix));
            Log.d("QRCodeGenerator", "QR Code generated successfully.");
        } catch (Exception e) {
            Log.e("QRCodeGenerator", "Error generating QR code", e);
            showToast("Error generating QR code: " + e.getMessage());
        }
    }

    private void showToast(String message) {
        Toast.makeText(QRCodeGenerator.this, message, Toast.LENGTH_SHORT).show();
    }
}
