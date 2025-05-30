package com.example.uitpay.ui.notifications;

public class Product {
    private int id;
    private String name;
    private double price;
    private String location;
    private String description;
    
    public Product(int id, String name, double price, String location, String description) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.location = location;
        this.description = description;
    }
    
    public int getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }
    
    public double getPrice() {
        return price;
    }
    
    public String getLocation() {
        return location;
    }
    
    public String getDescription() {
        return description;
    }
    
    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", location='" + location + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
} 