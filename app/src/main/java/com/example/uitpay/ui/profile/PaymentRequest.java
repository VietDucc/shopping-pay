package com.example.uitpay.ui.profile;

public class PaymentRequest {
    private long amount;
    private String orderInfo;

    public PaymentRequest(long amount, String orderInfo) {
        this.amount = amount;
        this.orderInfo = orderInfo;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public String getOrderInfo() {
        return orderInfo;
    }

    public void setOrderInfo(String orderInfo) {
        this.orderInfo = orderInfo;
    }
} 