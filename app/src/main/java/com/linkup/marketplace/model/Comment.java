package com.linkup.marketplace.model;

public class Comment {
    private int id;
    private int productId;
    private String userName;
    private String commentText;

    public Comment(int id, int productId, String userName, String commentText) {
        this.id = id;
        this.productId = productId;
        this.userName = userName;
        this.commentText = commentText;
    }

    public Comment(int productId, String userName, String commentText) {
        this.productId = productId;
        this.userName = userName;
        this.commentText = commentText;
    }

    // Getter and Setter
    public int getId() { return id; }
    public int getProductId() { return productId; }
    public String getUserName() { return userName; }
    public String getCommentText() { return commentText; }


    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
