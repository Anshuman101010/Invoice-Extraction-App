package com.example.incoiceextraction.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.incoiceextraction.R;
import com.example.incoiceextraction.database.DatabaseHelper;

public class SignupActivity extends AppCompatActivity {

    EditText email, password;
    Button signupBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        signupBtn = findViewById(R.id.signupBtn);

        signupBtn.setOnClickListener(v -> {

            DatabaseHelper db = new DatabaseHelper(this);

            String userEmail = email.getText().toString().trim();
            String userPassword = password.getText().toString().trim();

            // ✅ EMPTY CHECK
            if (userEmail.isEmpty() || userPassword.isEmpty()) {
                Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // ✅ EMAIL VALIDATION
            if (!Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()) {
                email.setError("Enter valid email");
                email.requestFocus();
                return;
            }

            // ✅ PASSWORD CHECK (optional but recommended)
            if (userPassword.length() < 4) {
                password.setError("Password must be at least 4 characters");
                password.requestFocus();
                return;
            }

            // ✅ EXISTING FUNCTION (UNCHANGED)
            boolean success = db.registerUser(userEmail, userPassword);

            if (success) {
                Toast.makeText(this, "Signup Successful", Toast.LENGTH_SHORT).show();
                finish(); // go back to login
            } else {
                Toast.makeText(this, "User already exists", Toast.LENGTH_SHORT).show();
            }

        });
    }
}