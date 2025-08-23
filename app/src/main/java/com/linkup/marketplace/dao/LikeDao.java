package com.linkup.marketplace.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class LikeDao {
    private SQLiteDatabase database;

    public LikeDao(SQLiteDatabase db) {
        this.database = db;
    }

    // ✅ Check if a product is liked by a user
    public boolean isProductLikedByUser(int userId, int productId) {
        Cursor cursor = database.rawQuery("SELECT * FROM likes WHERE user_id = ? AND product_id = ?",
                new String[]{String.valueOf(userId), String.valueOf(productId)});
        boolean exists = cursor.moveToFirst();
        cursor.close();
        return exists;
    }

    // ✅ Add a like to the database
    public boolean addLike(int userId, int productId) {
        ContentValues values = new ContentValues();
        values.put("user_id", userId);
        values.put("product_id", productId);

        long result = database.insert("likes", null, values);
        return result != -1;
    }

    // ✅ Remove a like from the database
    public boolean removeLike(int userId, int productId) {
        int result = database.delete("likes", "user_id=? AND product_id=?",
                new String[]{String.valueOf(userId), String.valueOf(productId)});
        return result > 0;
    }

    // ✅ Count total likes for a product
    public int countLikesForProduct(int productId) {
        Cursor cursor = database.rawQuery("SELECT COUNT(*) FROM likes WHERE product_id = ?",
                new String[]{String.valueOf(productId)});
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }
}

