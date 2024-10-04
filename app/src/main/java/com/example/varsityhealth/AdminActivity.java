package com.example.varsityhealth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log; // Add this for logging
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AdminActivity extends AppCompatActivity {
    LinearLayout ViewUserBookings, ViewExistingUsers;
    Button AdminLogout;
    TextView DashboardName;
    private FirebaseAuth auth;
    private DatabaseReference mDatabase;
    private static final String TAG = "AdminActivity"; // Tag for logging

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        // Initializing views
        ViewUserBookings = findViewById(R.id.view_appointment_adm);
        ViewExistingUsers = findViewById(R.id.view_users);
        AdminLogout = findViewById(R.id.signout_btn_adm);
        DashboardName = findViewById(R.id.dashboard_name);

        Log.d(TAG, "onCreate: Views initialized");

        // Firebase auth initialization
        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() != null) {
            String userId = auth.getCurrentUser().getUid();
            Log.d(TAG, "onCreate: Current user ID: " + userId);

            mDatabase = FirebaseDatabase.getInstance().getReference().child("users");
            mDatabase.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        Log.d(TAG, "onDataChange: DataSnapshot exists for userId: " + userId);
                        String fullName = dataSnapshot.child("full_name").getValue(String.class);
                        Log.d(TAG, "onDataChange: Full name fetched: " + fullName);
                        DashboardName.setText(fullName != null ? fullName : "Admin User");  // Fallback if full name is null
                    } else {
                        Log.w(TAG, "onDataChange: DataSnapshot does not exist for userId: " + userId);
                        DashboardName.setText("Admin User");  // Fallback for missing data
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e(TAG, "onCancelled: Database error: " + databaseError.getMessage());
                    // Handle any errors, e.g., logging or showing a message
                }
            });
        } else {
            Log.w(TAG, "onCreate: No current user found in FirebaseAuth");
        }

// Navigate to the View Appointments screen
        ViewUserBookings.setOnClickListener(v -> {
            Log.d(TAG, "ViewUserBookings: Clicked");

            try {
                Intent intent = new Intent(AdminActivity.this, AdminViewAppointmentsActivity.class);
                Log.d(TAG, "ViewUserBookings: Intent created successfully");
                startActivity(intent);
                Log.d(TAG, "ViewUserBookings: Navigated to AdminViewAppointmentsActivity");
            } catch (Exception e) {
                Log.e(TAG, "ViewUserBookings: Exception occurred while navigating to AdminViewAppointmentsActivity", e);
            }
        });


        // Navigate to the View Users screen
        ViewExistingUsers.setOnClickListener(v -> {
            Log.d(TAG, "ViewExistingUsers: Navigating to AdminViewUserActivity");
            Intent intent = new Intent(AdminActivity.this, AdminViewUserActivity.class);
            startActivity(intent);
        });

        // Logout and return to Intro screen
        AdminLogout.setOnClickListener(v -> {
            Log.d(TAG, "AdminLogout: Signing out...");
            auth.signOut();  // Sign out from FirebaseAuth
            Log.d(TAG, "AdminLogout: Signed out. Redirecting to IntroActivity.");
            Intent intent = new Intent(AdminActivity.this, IntroActivity.class);
            startActivity(intent);
            finish();  // Close AdminActivity
        });

        // Handle system window insets for full-screen content
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        View rootView = findViewById(R.id.admin_screen);
        rootView.setOnApplyWindowInsetsListener((view, windowInsets) -> {
            WindowInsetsCompat insets = WindowInsetsCompat.toWindowInsetsCompat(windowInsets);
            Insets systemInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            view.setPadding(systemInsets.left, systemInsets.top, systemInsets.right, systemInsets.bottom);
            Log.d(TAG, "onApplyWindowInsets: Applied system window insets");
            return windowInsets;
        });

        Log.d(TAG, "onCreate: Finished setup");
    }
}
