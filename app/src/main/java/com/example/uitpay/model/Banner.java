package com.example.uitpay.model;

public class Banner {
    private String id;
    private String imageUrl;

    public Banner() {
        // Empty constructor needed for Firestore
    }

    public Banner(String id, String imageUrl) {
        this.id = id;
        this.imageUrl = imageUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
} 