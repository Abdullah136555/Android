package com.linkup.marketplace;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.linkup.marketplace.dao.DatabaseHelper;
import com.linkup.marketplace.dao.UserDao;
import com.linkup.marketplace.model.User;

import java.io.File;

public class ViewProfileActivity extends AppCompatActivity {

    TextView tvUsername, tvEmail, tvName, tvMobile, tvAddress, tvBio, tvBirthdate;
    ImageView ivProfilePhoto, ivCoverPhoto;
    UserDao db;
    String email = "naeem@gmail.com";
    User user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);

        // Bind views
        tvUsername = findViewById(R.id.tvUsername);
        tvEmail = findViewById(R.id.tvEmail);
        tvName = findViewById(R.id.tvName);
        tvMobile = findViewById(R.id.tvMobile);
        tvAddress = findViewById(R.id.tvAddress);
        tvBio = findViewById(R.id.tvBio);
        tvBirthdate = findViewById(R.id.tvBirthdate);
        ivProfilePhoto = findViewById(R.id.ivProfilePhoto);
        ivCoverPhoto = findViewById(R.id.ivCoverPhoto);

        // ✅ Get email from intent
        email = getIntent().getStringExtra("email");

        db = new UserDao(this);
        loadProfile();

        findViewById(R.id.btnEditProfile).setOnClickListener(v -> {
            Intent intent = new Intent(ViewProfileActivity.this, EditProfileActivity.class);
            intent.putExtra("email", email);
            startActivity(intent);
        });

        findViewById(R.id.btnUploadProduct).setOnClickListener(v -> {
            Intent intent = new Intent(ViewProfileActivity.this, UploadProductActivity.class);
            intent.putExtra("userName", user.getName());
            intent.putExtra("userProfilePhoto", user.getProfilePhoto());
            startActivity(intent);
        });
    }

    protected void onResume() {
        super.onResume();
        loadProfile();
    }


    private void loadProfile() {
        user = db.getUserByEmail(email);

        if (user != null) {
            tvUsername.setText("Username: " + user.getUsername());
            tvEmail.setText("Email: " + user.getEmail());
            tvName.setText(user.getName());
            tvMobile.setText(user.getMobile());
            tvAddress.setText(user.getAddress());
            tvBio.setText(user.getBio());
            tvBirthdate.setText(user.getBirthdate());

            // Profile Photo load
            if (user.getProfilePhoto() != null && !user.getProfilePhoto().isEmpty()) {
                if (user.getProfilePhoto().startsWith("content://")) {
                    Glide.with(this)
                            .load(Uri.parse(user.getProfilePhoto()))
                            .into(ivProfilePhoto);
                } else {
                    Glide.with(this)
                            .load(new File(user.getProfilePhoto()))
                            .into(ivProfilePhoto);
                }
            } else {
                ivProfilePhoto.setImageResource(R.drawable.default_profile);
            }

            // Cover Photo load
            if (user.getCoverPhoto() != null && !user.getCoverPhoto().isEmpty()) {
                if (user.getCoverPhoto().startsWith("content://")) {
                    Glide.with(this)
                            .load(Uri.parse(user.getCoverPhoto()))
                            .into(ivCoverPhoto);
                } else {
                    Glide.with(this)
                            .load(new File(user.getCoverPhoto()))
                            .into(ivCoverPhoto);
                }
            } else {
                ivCoverPhoto.setImageResource(R.drawable.default_cover); // default_cover.png drawable ফাইল থাকতে হবে
            }
        }
    }

}

