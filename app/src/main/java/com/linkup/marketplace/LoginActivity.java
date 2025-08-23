package com.linkup.marketplace;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.linkup.marketplace.dao.DatabaseHelper;


import com.linkup.marketplace.dao.SharedPreferencesHelper;
import com.linkup.marketplace.dao.UserDao;
import com.linkup.marketplace.model.User;

public class LoginActivity extends AppCompatActivity {

    EditText etEmail, etPassword;
    Button btnLogin;
   UserDao db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);

        db = new UserDao(this);

        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            User user = db.getUserByEmail(email);

            if (user != null && user.getPassword().equals(password)) {
                Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show();


                SharedPreferencesHelper sharedPrefs = new SharedPreferencesHelper(this);
                sharedPrefs.saveUser(user.getName(), user.getEmail(), user.getProfilePhoto());


                Intent intent = new Intent(this, ViewProfileActivity.class);
                intent.putExtra("username", user.getUsername());
                intent.putExtra("name", user.getName());
                intent.putExtra("email", user.getEmail());
                intent.putExtra("mobile", user.getMobile());
                intent.putExtra("address", user.getAddress());
                intent.putExtra("bio", user.getBio());
                intent.putExtra("birthdate", user.getBirthdate());
                intent.putExtra("profilePhoto", user.getProfilePhoto());
                intent.putExtra("coverPhoto", user.getCoverPhoto());

                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Invalid Email or Password", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
