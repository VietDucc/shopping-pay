package com.example.uitpay.ui.profile;

import com.google.gson.annotations.SerializedName;

public class PaymentStatusResponse {
    @SerializedName("status")
    private String status; // PAID hoặc UNPAID
    
    @SerializedName("orderId")
    private String orderId;
    
    @SerializedName("amount")
    private long amount;

    public PaymentStatusResponse() {}

    public PaymentStatusResponse(String status, String orderId, long amount) {
        this.status = status;
        this.orderId = orderId;
        this.amount = amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public boolean isPaid() {
        return "PAID".equalsIgnoreCase(status);
    }

    @Override
    public String toString() {
        return "PaymentStatusResponse{" +
                "status='" + status + '\'' +
                ", orderId='" + orderId + '\'' +
                ", amount=" + amount +
                '}';
    }
} 