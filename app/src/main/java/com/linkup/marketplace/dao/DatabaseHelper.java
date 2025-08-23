package com.linkup.marketplace.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DB_NAME = "marketplace.db";
    public static final int DB_VERSION = 3;

    public static final String USER_TABLE = "users";
    public static final String PRODUCT_TABLE = "products";
    public static final String likes_table = "likes";
    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createUserTable = "CREATE TABLE " + USER_TABLE + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "username TEXT, name TEXT, email TEXT, password TEXT, " +
                "mobile TEXT, address TEXT, bio TEXT, birthdate TEXT, " +
                "profilePhoto TEXT, coverPhoto TEXT)";
        db.execSQL(createUserTable);

        String createProductTable = "CREATE TABLE " + PRODUCT_TABLE + " (" +
                "productId INTEGER PRIMARY KEY AUTOINCREMENT," +
                "productName TEXT, productDescription TEXT, productPrice TEXT, " +
                "productImage TEXT, userName TEXT, userProfilePhoto TEXT, " +
                "uploadDate TEXT, contactNumber TEXT, category TEXT)";
        db.execSQL(createProductTable);

        String createLikesTable = "CREATE TABLE " + likes_table + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user_id INTEGER, " +
                "product_id INTEGER, " +
                "UNIQUE(user_id, product_id))";
        db.execSQL(createLikesTable);

        String createCommentsTable = "CREATE TABLE comment_table (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "productId INTEGER," +
                "userName TEXT," +
                "commentText TEXT)";
        db.execSQL(createCommentsTable);
    }



    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + USER_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + PRODUCT_TABLE);

        db.execSQL("DROP TABLE IF EXISTS " + likes_table);
        db.execSQL("DROP TABLE IF EXISTS comment_table");
        onCreate(db);
    }
}