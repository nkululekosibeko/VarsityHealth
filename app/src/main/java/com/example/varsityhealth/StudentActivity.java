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

        // TextView
        DashBoardName = findViewById(R.id.dashboard_name);

        // Layout View
        BookAppointment = findViewById(R.id.book_appointment);
        ViewAppointment = findViewById(R.id.view_appointment_std);

        //Buttons
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
                        if (fullName != null) {
                            DashBoardName.setText(fullName);  // Display user's name
                        } else {
                            DashBoardName.setText("User");
                        }
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

        ViewAppointment.setOnClickListener(v -> {
            Intent intent = new Intent(StudentActivity.this, ViewBookingActivity.class);
            startActivity(intent);
        });

        // Logout button
        StdLogout.setOnClickListener(view -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(StudentActivity.this, IntroActivity.class);
            startActivity(intent);
            finish();
        });

        // Handle system bars insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.student_screen), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}
