package com.example.uitpay.ui.notifications;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface OpenAIService {
    @Headers({
        "Content-Type: application/json"
    })
    @POST("v1/chat/completions")
    Call<ChatGptResponse> getChatCompletion(@Body ChatGptRequest request);
} 