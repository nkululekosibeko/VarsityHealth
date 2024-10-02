package com.example.varsityhealth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignupActivity extends AppCompatActivity {

    // Buttons
    Button signUp_btn;

    // Text Edits
    EditText fullName_signUp, email_signUp, pass_signUp, passConf_signUp;

    // Role Spinner
    Spinner roleSpinner;

    // Firebase Use
    FirebaseAuth mAuth;

    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Enable edge-to-edge display
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

        setContentView(R.layout.activity_signup);

        // Handle system bars insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.signup_screen), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Edit Texts
        fullName_signUp = findViewById(R.id.fullname_singup);
        email_signUp = findViewById(R.id.email_signup);
        pass_signUp = findViewById(R.id.signup_password);
        passConf_signUp = findViewById(R.id.signup_conf_pass);

        // Role Picker
        roleSpinner = findViewById(R.id.signup_role);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.roles_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        roleSpinner.setAdapter(adapter);

        // Button
        signUp_btn = findViewById(R.id.signup_button);
        signUp_btn.setOnClickListener(view -> {
            String fullName = fullName_signUp.getText().toString().trim();
            String email = email_signUp.getText().toString().trim();
            String password = pass_signUp.getText().toString().trim();
            String confirmPassword = passConf_signUp.getText().toString().trim();
            String role = roleSpinner.getSelectedItem().toString();

            // Check if any field is empty
            if (fullName.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(SignupActivity.this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Check if passwords match
            if (!password.equals(confirmPassword)) {
                Toast.makeText(SignupActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            // Create user in Firebase Auth
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            saveUserInfoToDatabase(fullName, email, role, user.getUid());
                        } else {
                            Toast.makeText(SignupActivity.this, "Signup failed. Please try again.", Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }

    private void saveUserInfoToDatabase(String fullName, String email, String role, String userId) {
        reference = FirebaseDatabase.getInstance().getReference("users");

        HelperClass helperClass = new HelperClass(fullName, email, role);

        // Save user information to Firebase Realtime Database
        reference.child(userId).setValue(helperClass).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(SignupActivity.this, "You have signed up successfully", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(SignupActivity.this, "Signup failed. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
