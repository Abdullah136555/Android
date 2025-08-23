package com.linkup.marketplace.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.linkup.marketplace.model.Product;

import java.util.ArrayList;
import java.util.List;

public class ProductDao {
    private SQLiteDatabase db;

    public ProductDao(Context context) {
        DatabaseHelper helper = new DatabaseHelper(context);
        db = helper.getWritableDatabase();
    }

    public long insertProduct(Product product) {
        ContentValues values = new ContentValues();
        values.put("productName", product.getProductName());
        values.put("productDescription", product.getProductDescription());
        values.put("productPrice", product.getProductPrice());
        values.put("productImage", product.getProductImage());
        values.put("userName", product.getUserName());
        values.put("userProfilePhoto", product.getUserProfilePhoto());
        values.put("uploadDate", product.getUploadDate());
        values.put("contactNumber", product.getContactNumber());
        values.put("category", product.getCategory());
        return db.insert(DatabaseHelper.PRODUCT_TABLE, null, values);
    }

    public List<Product> getAllProducts() {
        List<Product> productList = new ArrayList<>();
        Cursor cursor = db.query(DatabaseHelper.PRODUCT_TABLE, null, null, null, null, null, "uploadDate DESC");

        if (cursor != null && cursor.moveToFirst()) {
            do {
                Product product = new Product();
                product.setProductId(cursor.getInt(cursor.getColumnIndexOrThrow("productId")));
                product.setProductName(cursor.getString(cursor.getColumnIndexOrThrow("productName")));
                product.setProductDescription(cursor.getString(cursor.getColumnIndexOrThrow("productDescription")));
                product.setProductPrice(cursor.getString(cursor.getColumnIndexOrThrow("productPrice")));
                product.setProductImage(cursor.getString(cursor.getColumnIndexOrThrow("productImage")));
                product.setUserName(cursor.getString(cursor.getColumnIndexOrThrow("userName")));
                product.setUserProfilePhoto(cursor.getString(cursor.getColumnIndexOrThrow("userProfilePhoto")));
                product.setUploadDate(cursor.getString(cursor.getColumnIndexOrThrow("uploadDate")));
                product.setContactNumber(cursor.getString(cursor.getColumnIndexOrThrow("contactNumber")));
                product.setCategory(cursor.getString(cursor.getColumnIndexOrThrow("category")));
                productList.add(product);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return productList;
    }

    public int updateProduct(Product product) {
        ContentValues values = new ContentValues();
        values.put("productName", product.getProductName());
        values.put("productDescription", product.getProductDescription());
        values.put("productPrice", product.getProductPrice());
        values.put("productImage", product.getProductImage());
        values.put("userName", product.getUserName());
        values.put("userProfilePhoto", product.getUserProfilePhoto());
        values.put("uploadDate", product.getUploadDate());
        values.put("contactNumber", product.getContactNumber());
        values.put("category", product.getCategory());

        return db.update(DatabaseHelper.PRODUCT_TABLE, values, "productId = ?", new String[]{String.valueOf(product.getProductId())});
    }

    public int deleteProduct(int productId) {
        return db.delete(DatabaseHelper.PRODUCT_TABLE, "productId = ?", new String[]{String.valueOf(productId)});
    }
}
