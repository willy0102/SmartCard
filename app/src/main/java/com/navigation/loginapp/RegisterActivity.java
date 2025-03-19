package com.navigation.loginapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private EditText usernameInput, passwordInput;
    private Button registerButton;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        // Initialize Firestore
        firestore = FirebaseFirestore.getInstance();

        // Bind Views
        usernameInput = findViewById(R.id.register_username_input);
        passwordInput = findViewById(R.id.register_password_input);
        registerButton = findViewById(R.id.register_btn);

        // Register Button OnClickListener
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = usernameInput.getText().toString().trim();
                String password = passwordInput.getText().toString().trim();

                if (!username.isEmpty() && !password.isEmpty()) {
                    checkForDuplicateUsername(username, password);
                } else {
                    Toast.makeText(RegisterActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void checkForDuplicateUsername(final String username, final String password) {
        // Check if the username already exists in Firestore
        firestore.collection("users")
                .whereEqualTo("username", username)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot.isEmpty()) {
                            // Username is not taken, proceed with registration
                            createNewUser(username, password);
                        } else {
                            // Username is already taken
                            Toast.makeText(RegisterActivity.this, "Username already exists, please choose another.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(RegisterActivity.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e("FirestoreError", "Error checking username: ", task.getException());
                    }
                });
    }

    private void createNewUser(String username, String password) {
        // Create a new user document in Firestore with the username as the document ID
        Map<String, Object> user = new HashMap<>();
        user.put("username", username);
        user.put("password", password); // Save the password

        // Use the username as the document ID
        firestore.collection("users").document(username)
                .set(user)
                .addOnSuccessListener(aVoid -> {
                    // Registration successful, move to HomeActivity and pass the username as userID
                    Toast.makeText(RegisterActivity.this, "Registration successful!", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(RegisterActivity.this, HomeActivity.class);
                    intent.putExtra("USER_ID", username); // Pass the username as userID
                    startActivity(intent);
                    finish(); // Close the registration activity
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(RegisterActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("FirestoreError", "Error creating user: ", e);
                });
    }
}
