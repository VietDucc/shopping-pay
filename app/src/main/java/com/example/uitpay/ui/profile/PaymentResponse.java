package com.example.uitpay.ui.profile;

public class PaymentResponse {
    private String orderId;
    private String paymentUrl;

    public PaymentResponse() {}

    public PaymentResponse(String orderId, String paymentUrl) {
        this.orderId = orderId;
        this.paymentUrl = paymentUrl;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getPaymentUrl() {
        return paymentUrl;
    }

    public void setPaymentUrl(String paymentUrl) {
        this.paymentUrl = paymentUrl;
    }
} 