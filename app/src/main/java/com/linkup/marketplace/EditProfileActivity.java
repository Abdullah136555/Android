
package com.linkup.marketplace;

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.linkup.marketplace.dao.UserDao;
import com.linkup.marketplace.model.User;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;


public class EditProfileActivity extends AppCompatActivity {

    EditText etName, etMobile, etAddress, etBio, etBirthdate;
    ImageView ivProfilePhoto, ivCoverPhoto;
    Uri profileUri, coverUri;
    UserDao db;
    String email;
    User user;

    static final int PICK_PROFILE = 100;
    static final int PICK_COVER = 101;

    String profilePhotoPath = null;
    String coverPhotoPath = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        email = getIntent().getStringExtra("email");
        db = new UserDao(this);
        user = db.getUserByEmail(email);

        etName = findViewById(R.id.etName);
        etMobile = findViewById(R.id.etMobile);
        etAddress = findViewById(R.id.etAddress);
        etBio = findViewById(R.id.etBio);
        etBirthdate = findViewById(R.id.etBirthdate);
        ivProfilePhoto = findViewById(R.id.ivProfilePhoto);
        ivCoverPhoto = findViewById(R.id.ivCoverPhoto);

        if (user != null) {
            etName.setText(user.getName());
            etMobile.setText(user.getMobile());
            etAddress.setText(user.getAddress());
            etBio.setText(user.getBio());
            etBirthdate.setText(user.getBirthdate());

            if (user.getProfilePhoto() != null && !user.getProfilePhoto().equals("default_profile.jpg")) {
                profilePhotoPath = user.getProfilePhoto();
                Glide.with(this).load(new File(profilePhotoPath)).into(ivProfilePhoto);
            } else {
                ivProfilePhoto.setImageResource(R.drawable.default_profile);
            }

            if (user.getCoverPhoto() != null && !user.getCoverPhoto().equals("default_cover.jpg")) {
                coverPhotoPath = user.getCoverPhoto();
                Glide.with(this).load(new File(coverPhotoPath)).into(ivCoverPhoto);
            } else {
                ivCoverPhoto.setImageResource(R.drawable.default_cover);
            }
        }

        ivProfilePhoto.setOnClickListener(v -> selectPhoto(PICK_PROFILE));
        ivCoverPhoto.setOnClickListener(v -> selectPhoto(PICK_COVER));
    }

void selectPhoto(int requestCode) {
    Intent intent = new Intent(Intent.ACTION_PICK);
    intent.setType("image/*");
    startActivityForResult(intent, requestCode);
}


@Override
protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    if (resultCode == RESULT_OK && data != null && data.getData() != null) {
        Uri selectedImageUri = data.getData();

        // Try taking permission (may fail silently if from gallery)
        try {
            getContentResolver().takePersistableUriPermission(
                    selectedImageUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String filename = "IMG_" + System.currentTimeMillis() + ".jpg";
        String path = copyUriToInternalStorage(selectedImageUri, filename);

        if (requestCode == PICK_PROFILE && path != null) {
            profilePhotoPath = path;
            Glide.with(this).load(new File(path)).into(ivProfilePhoto);
        } else if (requestCode == PICK_COVER && path != null) {
            coverPhotoPath = path;
            Glide.with(this).load(new File(path)).into(ivCoverPhoto);
        }
    }
}
    private String copyUriToInternalStorage(Uri uri, String filename) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            File file = new File(getFilesDir(), filename);
            FileOutputStream outputStream = new FileOutputStream(file);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
            outputStream.close();
            inputStream.close();
            return file.getAbsolutePath(); // Save this to DB
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void saveProfile(android.view.View view) {
        ContentValues values = new ContentValues();
        values.put("name", etName.getText().toString());
        values.put("mobile", etMobile.getText().toString());
        values.put("address", etAddress.getText().toString());
        values.put("bio", etBio.getText().toString());
        values.put("birthdate", etBirthdate.getText().toString());

        if (profilePhotoPath != null) values.put("profilePhoto", profilePhotoPath);
        if (coverPhotoPath != null) values.put("coverPhoto", coverPhotoPath);

        int result = db.updateUserByEmail(email, values);

        if (result > 0) {
            Toast.makeText(this, "Profile updated", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Failed to update profile", Toast.LENGTH_SHORT).show();
        }
    }
}
