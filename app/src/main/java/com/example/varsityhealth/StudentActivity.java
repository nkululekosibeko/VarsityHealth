package com.example.varsityhealth;

import android.content.Intent;
import android.os.Bundle;
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

public class StudentActivity extends AppCompatActivity {

    // Text View
    TextView DashBoardName;

    // Layout
    LinearLayout BookAppointment, ViewAppointment;

    // Button
    Button StdLogout;

    private FirebaseAuth auth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.activity_student);

        // Initializing views
        DashBoardName = findViewById(R.id.dashboard_name);
        BookAppointment = findViewById(R.id.book_appointment);
        ViewAppointment = findViewById(R.id.view_appointment_std);
        StdLogout = findViewById(R.id.signout_btn_std);

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
                        DashBoardName.setText(fullName != null ? fullName : "Student User");  // Fallback if full name is null
                    } else {
                        DashBoardName.setText("Student User");  // Fallback for missing data
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Handle any errors
                }
            });
        }

        // Book appointment button
        BookAppointment.setOnClickListener(v -> {
            Intent intent = new Intent(StudentActivity.this, BookAppointmentActivity.class);
            startActivity(intent);
        });

        // View appointments button
        ViewAppointment.setOnClickListener(v -> {
            Intent intent = new Intent(StudentActivity.this, ViewBookingActivity.class);
            startActivity(intent);
        });

        // Logout button
        StdLogout.setOnClickListener(view -> {
            auth.signOut();  // Sign out from Firebase
            Intent intent = new Intent(StudentActivity.this, IntroActivity.class);
            startActivity(intent);
            finish();  // Close the StudentActivity
        });

        // Handle system bars insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.student_screen), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}
