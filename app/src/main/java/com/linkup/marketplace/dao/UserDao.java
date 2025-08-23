package com.linkup.marketplace.dao;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.linkup.marketplace.model.User;

public class UserDao {
    private SQLiteDatabase db;

    public UserDao(Context context) {
        DatabaseHelper helper = new DatabaseHelper(context);
        db = helper.getWritableDatabase();
    }

    public long insertUser(User user) {
        ContentValues cv = new ContentValues();
        cv.put("username", user.getUsername());
        cv.put("name", user.getName());
        cv.put("email", user.getEmail());
        cv.put("password", user.getPassword());
        cv.put("mobile", user.getMobile());
        cv.put("address", user.getAddress());
        cv.put("bio", user.getBio());
        cv.put("birthdate", user.getBirthdate());
        cv.put("profilePhoto", user.getProfilePhoto());
        cv.put("coverPhoto", user.getCoverPhoto());
        return db.insert(DatabaseHelper.USER_TABLE, null, cv);
    }

    public User getUserByEmail(String email) {
        Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseHelper.USER_TABLE + " WHERE email=?", new String[]{email});
        if (cursor.moveToFirst()) {
            User user = new User(
                    cursor.getInt(0), cursor.getString(1), cursor.getString(2),
                    cursor.getString(3), cursor.getString(4), cursor.getString(5),
                    cursor.getString(6), cursor.getString(7), cursor.getString(8),
                    cursor.getString(9), cursor.getString(10)
            );
            cursor.close();
            return user;
        }
        cursor.close();
        return null;
    }

    public int updateUserProfile(String email, String name, String mobile, String address,
                                 String bio, String birthdate, String profilePhoto, String coverPhoto) {
        ContentValues cv = new ContentValues();
        cv.put("name", name);
        cv.put("mobile", mobile);
        cv.put("address", address);
        cv.put("bio", bio);
        cv.put("birthdate", birthdate);
        cv.put("profilePhoto", profilePhoto);
        cv.put("coverPhoto", coverPhoto);
        return db.update(DatabaseHelper.USER_TABLE, cv, "email=?", new String[]{email});
    }

    public int updateUserByEmail(String email, ContentValues values) {
        return db.update(DatabaseHelper.USER_TABLE, values, "email=?", new String[]{email});
    }

}
