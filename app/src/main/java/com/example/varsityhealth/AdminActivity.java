package com.example.varsityhealth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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

    // Image Buttons
    LinearLayout ViewUserBookings, ViewExistingUsers;

    // Regular Button
    Button AdminLogout;

    // Dashboard Name
    TextView DashboardName;

    // The Database
    private FirebaseAuth auth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        // Image Buttons
        ViewUserBookings = findViewById(R.id.view_appointment_adm);
        ViewExistingUsers = findViewById(R.id.view_users);

        // Logout button
        AdminLogout = findViewById(R.id.signout_btn_adm);

        // Dashboard Name TextView
        DashboardName = findViewById(R.id.dashboard_name);

        // Initialize FirebaseAuth instance
        auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            String userId = auth.getCurrentUser().getUid();

            // Initialize Firebase Database reference
            mDatabase = FirebaseDatabase.getInstance().getReference().child("users");

            // Retrieve and display user's full name from the database
            mDatabase.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String fullName = dataSnapshot.child("full_name").getValue(String.class);
                        DashboardName.setText(fullName != null ? fullName : "User");  // Display user's name
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Handle any errors
                }
            });
        }

        // Set click listeners for the buttons
        ViewUserBookings.setOnClickListener(v -> {
            Intent intent = new Intent(AdminActivity.this, AdminViewAppointmentsActivity.class);
            startActivity(intent);
        });

        ViewExistingUsers.setOnClickListener(v -> {
            Intent intent = new Intent(AdminActivity.this, AdminViewUserActivity.class);
            startActivity(intent);
        });

        AdminLogout.setOnClickListener(v -> {
            Intent intent = new Intent(AdminActivity.this, IntroActivity.class);
            startActivity(intent);
            finish();
        });

        // Enable edge-to-edge layout
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        View rootView = findViewById(R.id.admin_screen);

        // Handle window insets with WindowInsetsControllerCompat
        rootView.setOnApplyWindowInsetsListener((view, windowInsets) -> {
            WindowInsetsCompat insets = WindowInsetsCompat.toWindowInsetsCompat(windowInsets);
            view.setPadding(
                    insets.getInsets(WindowInsetsCompat.Type.systemBars()).left,
                    insets.getInsets(WindowInsetsCompat.Type.systemBars()).top,
                    insets.getInsets(WindowInsetsCompat.Type.systemBars()).right,
                    insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom
            );
            return windowInsets;
        });
    }
}
