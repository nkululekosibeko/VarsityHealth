package com.example.varsityhealth;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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

    Button Login_btn;
    EditText LogIn_email, LogInPass;
    TextView SignupRedirectText;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        LogIn_email = findViewById(R.id.login_email);
        LogInPass = findViewById(R.id.login_pass);
        SignupRedirectText = findViewById(R.id.signup_redirect);
        Login_btn = findViewById(R.id.login_btn);

        Login_btn.setOnClickListener(view -> {
            if (!validateEmail()) {
                LogIn_email.requestFocus();
            } else if (!validatePassword()) {
                LogInPass.requestFocus();
            } else {
                // Check network connectivity before attempting to authenticate
                if (isNetworkAvailable()) {
                    checkUser();
                } else {
                    Toast.makeText(LoginActivity.this, "No internet connection. Please check your network and try again.", Toast.LENGTH_LONG).show();
                }
            }
        });

        SignupRedirectText.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
            startActivity(intent);
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.login_screen), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public Boolean validateEmail() {
        String val = LogIn_email.getText().toString();
        if (val.isEmpty()) {
            LogIn_email.setError("Email cannot be empty");
            return false;
        } else {
            LogIn_email.setError(null);
            return true;
        }
    }

    public Boolean validatePassword() {
        String val = LogInPass.getText().toString();
        if (val.isEmpty()) {
            LogInPass.setError("Password cannot be empty");
            return false;
        } else {
            LogInPass.setError(null);
            return true;
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
            return capabilities != null && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
        }
        return false;
    }

    public void checkUser() {
        String userEmail = LogIn_email.getText().toString().trim();
        String userPassword = LogInPass.getText().toString().trim();

        mAuth.signInWithEmailAndPassword(userEmail, userPassword)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser currentUser = mAuth.getCurrentUser();
                        if (currentUser != null) {
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
                    finish();
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
