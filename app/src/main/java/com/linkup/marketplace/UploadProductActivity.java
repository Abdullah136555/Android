package com.linkup.marketplace;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.linkup.marketplace.dao.ProductDao;
import com.linkup.marketplace.dao.SharedPreferencesHelper;
import com.linkup.marketplace.model.Product;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class UploadProductActivity extends AppCompatActivity {

    // UI Components
    private EditText etProductName, etDescription, etPrice, etContact, etCategory;
    private ImageView imgProduct;
    private Button btnChooseImage, btnUpload, btnViewProducts;

    // Image selection and storage
    private Uri selectedImageUri;
    private String imagePath;
    private static final int PICK_IMAGE = 1;

    // DAO
    private ProductDao dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_product);

        // Initialize DB helper
        dbHelper = new ProductDao(this);

        // Initialize Views
        etProductName = findViewById(R.id.etProductName);
        etDescription = findViewById(R.id.etDescription);
        etPrice = findViewById(R.id.etPrice);
        etContact = findViewById(R.id.etContact);
        etCategory = findViewById(R.id.etCategory);
        imgProduct = findViewById(R.id.imgProduct);
        btnChooseImage = findViewById(R.id.btnChooseImage);
        btnUpload = findViewById(R.id.btnUpload);
        btnViewProducts = findViewById(R.id.btnViewProducts);

        // Open Gallery when Choose Image is clicked
        btnChooseImage.setOnClickListener(v -> openGallery());

        // Upload product on button click
        btnUpload.setOnClickListener(v -> uploadProduct());

        // View product list
        btnViewProducts.setOnClickListener(v -> {
            Intent intent = new Intent(UploadProductActivity.this, DisplayProductListActivity.class);
            startActivity(intent);
        });
    }

    // Launch gallery picker
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE);
    }

    // Handle selected image from gallery
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            imgProduct.setImageURI(selectedImageUri);

            // Save image to internal storage
            imagePath = saveImageToInternalStorage(selectedImageUri);

            if (imagePath == null) {
                Toast.makeText(this, "Image save failed!", Toast.LENGTH_SHORT).show();
            } else {
                Log.d("ImagePath", "Saved Image Path: " + imagePath);
            }
        }
    }

    // Save selected image to internal storage and return file path
    private String saveImageToInternalStorage(Uri imageUri) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
            String fileName = "IMG_" + System.currentTimeMillis() + ".jpg";
            File file = new File(getFilesDir(), fileName);

            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.close();

            return file.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Deprecated, not used anymore (optional to remove)
    private String getRealPathFromURI(Uri uri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(uri, proj, null, null, null);
        if (cursor == null) return null;
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String path = cursor.getString(column_index);
        cursor.close();
        return path;
    }

    // Upload product to database
    private void uploadProduct() {
        String name = etProductName.getText().toString().trim();
        String desc = etDescription.getText().toString().trim();
        String price = etPrice.getText().toString().trim();
        String contact = etContact.getText().toString().trim();
        String category = etCategory.getText().toString().trim();

        // Check all fields are filled
        if (name.isEmpty() || desc.isEmpty() || price.isEmpty() || contact.isEmpty() || category.isEmpty() || imagePath == null) {
            Toast.makeText(this, "Please fill all fields and select image", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get user details from SharedPreferences
        SharedPreferencesHelper helper = new SharedPreferencesHelper(this);
        String userName = helper.getName();
        String userProfilePhoto = helper.getProfilePhoto();

        // Create and populate Product model
        Product product = new Product();
        product.setProductName(name);
        product.setProductDescription(desc);
        product.setProductPrice(price);
        product.setProductImage(imagePath);
        product.setUserName(userName);
        product.setUserProfilePhoto(userProfilePhoto);
        product.setUploadDate(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date()));
        product.setContactNumber(contact);
        product.setCategory(category);

        // Insert product into DB
        long id = dbHelper.insertProduct(product);

        if (id > 0) {
            Toast.makeText(this, "Product Uploaded!", Toast.LENGTH_SHORT).show();
            finish(); // Go back
        } else {
            Toast.makeText(this, "Upload Failed!", Toast.LENGTH_SHORT).show();
        }
    }
}
