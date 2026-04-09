package com.example.incoiceextraction.activities;

import android.os.Bundle;
import android.widget.*;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import com.example.incoiceextraction.R;
import com.example.incoiceextraction.database.DatabaseHelper;

public class LoginActivity extends AppCompatActivity {

    EditText email, password;
    Button login, signupRedirect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        login = findViewById(R.id.loginBtn);
        signupRedirect = findViewById(R.id.signupRedirect);

        DatabaseHelper db = new DatabaseHelper(this);

        // 🔐 LOGIN BUTTON
        login.setOnClickListener(v -> {

            String userEmail = email.getText().toString().trim();
            String userPass = password.getText().toString().trim();

            if (userEmail.isEmpty() || userPass.isEmpty()) {
                Toast.makeText(this, "Please enter all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // ✅ Check user in DB
            if (db.checkUser(userEmail, userPass)) {

                int userId = db.getUserId(userEmail);

                Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(this, DashboardActivity.class);
                intent.putExtra("userId", userId); // 🔥 pass user id
                startActivity(intent);

                finish();

            } else {

                Toast.makeText(this, "Invalid Credentials", Toast.LENGTH_SHORT).show();
            }
        });

        // 🔄 GO TO SIGNUP
        signupRedirect.setOnClickListener(v -> {
            startActivity(new Intent(this, SignupActivity.class));
        });
    }
}