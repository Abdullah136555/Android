package com.linkup.marketplace;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import android.widget.EditText;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.linkup.marketplace.dao.DatabaseHelper;
import com.linkup.marketplace.dao.UserDao;
import com.linkup.marketplace.model.User;

public class RegistrationActivity extends AppCompatActivity {

    EditText etUsername, etName, etEmail, etPassword, etMobile, etAddress, etBio, etBirthdate;
    Button btnRegister;
    Button btnLoginInstead;
   UserDao db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        db = new UserDao(this);

        // Initialize all EditText fields
        etUsername = findViewById(R.id.etUsername);
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etMobile = findViewById(R.id.etMobile);
        etAddress = findViewById(R.id.etAddress);
        etBio = findViewById(R.id.etBio);
        etBirthdate = findViewById(R.id.etBirthdate);
        btnRegister = findViewById(R.id.btnRegister);
        btnLoginInstead = findViewById(R.id.btnLoginInstead);

        btnLoginInstead.setOnClickListener(v -> {
            // LoginActivity-তে যাও
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
        btnRegister.setOnClickListener(v -> {
            // Form validation (basic)
            if (
                    etUsername.getText().toString().isEmpty() ||
                            etName.getText().toString().isEmpty() ||
                            etEmail.getText().toString().isEmpty() ||
                            etPassword.getText().toString().isEmpty()
            ) {
                Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Create user object
            User user = new User(
                    0,
                    etUsername.getText().toString().trim(),
                    etName.getText().toString().trim(),
                    etEmail.getText().toString().trim(),
                    etPassword.getText().toString().trim(),
                    etMobile.getText().toString().trim(),
                    etAddress.getText().toString().trim(),
                    etBio.getText().toString().trim(),
                    etBirthdate.getText().toString().trim(),
                    "default_profile.jpg", // default value, replace with image picker later
                    "default_cover.jpg"
            );

            // Insert user into DB
            long result = db.insertUser(user);
            if (result > 0) {
                Toast.makeText(this, "Registration Successful", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, LoginActivity.class));
                finish();
            } else {
                Toast.makeText(this, "Registration Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
