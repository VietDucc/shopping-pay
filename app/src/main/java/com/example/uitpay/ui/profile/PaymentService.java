package com.example.uitpay.ui.profile;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface PaymentService {
    
    @POST("demo/api/app/order")
    Call<PaymentResponse> createPaymentOrder(@Body PaymentRequest request);
    
    @GET("demo/api/app/order/{orderId}")
    Call<PaymentStatusResponse> getPaymentStatus(@Path("orderId") String orderId);
} 