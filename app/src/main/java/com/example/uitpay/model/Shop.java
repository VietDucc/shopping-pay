package com.example.uitpay.model;

public class Shop {
    private String id;
    private String name;
    private String mapUrl;
    private String imageUrl;
    private String address;

    public Shop() {
        // Constructor mặc định cho Firebase
    }

    public Shop(String id, String name, String mapUrl, String imageUrl, String address) {
        this.id = id;
        this.name = name;
        this.mapUrl = mapUrl;
        this.imageUrl = imageUrl;
        this.address = address;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getMapUrl() {
        return mapUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getAddress() {
        return address;
    }

    // Setters
    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setMapUrl(String mapUrl) {
        this.mapUrl = mapUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setAddress(String address) {
        this.address = address;
    }
} 