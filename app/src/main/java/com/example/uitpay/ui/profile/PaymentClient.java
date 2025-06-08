package com.example.uitpay.ui.profile;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PaymentClient {
    
    private static final String BASE_URL = "https://duc-spring.ngodat0103.live/";
    private static PaymentClient instance;
    private final PaymentService paymentService;
    
    private PaymentClient() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build();
        
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        
        paymentService = retrofit.create(PaymentService.class);
    }
    
    public static synchronized PaymentClient getInstance() {
        if (instance == null) {
            instance = new PaymentClient();
        }
        return instance;
    }
    
    public PaymentService getPaymentService() {
        return paymentService;
    }
} 