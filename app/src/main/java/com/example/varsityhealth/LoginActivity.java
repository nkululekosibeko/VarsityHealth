package com.example.varsityhealth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    // Buttons
    Button login_btn;

    // Text Edits
    EditText logIn_email, logInPass;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Enable edge-to-edge display
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.activity_login);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Edit Text
        logIn_email = findViewById(R.id.login_email);
        logInPass = findViewById(R.id.login_pass);

        // Button
        login_btn = findViewById(R.id.login_btn);

        login_btn.setOnClickListener(view -> {
            if (!validateEmail()) {
                logIn_email.requestFocus();
            } else if (!validatePassword()) {
                logInPass.requestFocus();
            } else {
                checkUser();
            }
        });

        // Handle system bars insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.login_screen), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public Boolean validateEmail() {
        String val = logIn_email.getText().toString();
        if (val.isEmpty()) {
            logIn_email.setError("Email cannot be empty");
            return false;
        } else {
            logIn_email.setError(null);
            return true;
        }
    }

    public Boolean validatePassword() {
        String val = logInPass.getText().toString();
        if (val.isEmpty()) {
            logInPass.setError("Password cannot be empty");
            return false;
        } else {
            logInPass.setError(null);
            return true;
        }
    }

    public void checkUser() {
        String userEmail = logIn_email.getText().toString().trim();
        String userPassword = logInPass.getText().toString().trim();

        // Use Firebase Authentication to sign in
        mAuth.signInWithEmailAndPassword(userEmail, userPassword)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, navigate based on role
                        FirebaseUser currentUser = mAuth.getCurrentUser();
                        if (currentUser != null) {
                            // Fetch user role from Realtime Database
                            fetchUserRole(currentUser.getUid());
                        }
                    } else {
                        Toast.makeText(LoginActivity.this, "Authentication failed. Check your credentials.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void fetchUserRole(String userId) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users").child(userId);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String role = dataSnapshot.child("role").getValue(String.class);
                    Intent intent;
                    if ("Admin".equals(role)) {
                        intent = new Intent(LoginActivity.this, AdminActivity.class);
                    } else {
                        intent = new Intent(LoginActivity.this, StudentActivity.class);
                    }
                    startActivity(intent);
                    finish();  // Close LoginActivity
                } else {
                    Toast.makeText(LoginActivity.this, "User role not found.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(LoginActivity.this, "Failed to retrieve user data: " + databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
