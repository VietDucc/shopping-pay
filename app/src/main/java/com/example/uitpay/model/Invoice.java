package com.example.uitpay.model;

import java.util.Map;

public class Invoice {
    private String invoiceID;
    private double cost;
    private String date;
    private String description;
    private String userid;
    private Map<String, InvoiceProduct> product;

    public Invoice() {
        // Constructor mặc định cho Firebase
    }

    public Invoice(String invoiceID, double cost, String date, String description, String userid, Map<String, InvoiceProduct> product) {
        this.invoiceID = invoiceID;
        this.cost = cost;
        this.date = date;
        this.description = description;
        this.userid = userid;
        this.product = product;
    }

    // Getters and Setters
    public String getInvoiceID() {
        return invoiceID;
    }

    public void setInvoiceID(String invoiceID) {
        this.invoiceID = invoiceID;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public Map<String, InvoiceProduct> getProduct() {
        return product;
    }

    public void setProduct(Map<String, InvoiceProduct> product) {
        this.product = product;
    }

    // Inner class cho Product trong Invoice
    public static class InvoiceProduct {
        private String name;
        private double price;
        private int quantity;
        
        // Thông tin bổ sung từ bảng product
        private String description;
        private String productImage;

        public InvoiceProduct() {
            // Constructor mặc định cho Firebase
        }

        public InvoiceProduct(String name, double price, int quantity) {
            this.name = name;
            this.price = price;
            this.quantity = quantity;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public double getPrice() {
            return price;
        }

        public void setPrice(double price) {
            this.price = price;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getProductImage() {
            return productImage;
        }

        public void setProductImage(String productImage) {
            this.productImage = productImage;
        }
    }
} 