package com.linkup.marketplace.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.linkup.marketplace.model.Comment;

import java.util.ArrayList;
import java.util.List;

public class CommentDao {
    private SQLiteDatabase db;

    public CommentDao(SQLiteDatabase db) {
        this.db = db;
    }

    // Add Comment
    public void addComment(Comment comment) {
        ContentValues values = new ContentValues();
        values.put("productId", comment.getProductId());
        values.put("username", comment.getUserName());
        values.put("commentText", comment.getCommentText());
        db.insert("comment_table", null, values);
    }

    // Get Comments by Product ID
    public List<Comment> getCommentsByProductId(int productId) {
        List<Comment> comments = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM comment_table WHERE productId = ?", new String[]{String.valueOf(productId)});
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String username = cursor.getString(cursor.getColumnIndexOrThrow("userName"));
                String text = cursor.getString(cursor.getColumnIndexOrThrow("commentText"));
                comments.add(new Comment(id, productId, username, text));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return comments;
    }

    public int getCommentCountByProductId(int productId) {
        Cursor cursor = db.rawQuery(
                "SELECT COUNT(*) FROM comment_table WHERE productId = ?",
                new String[]{String.valueOf(productId)}
        );
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }

}
