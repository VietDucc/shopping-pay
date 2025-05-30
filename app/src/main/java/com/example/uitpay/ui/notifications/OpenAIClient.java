package com.example.uitpay.ui.notifications;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class OpenAIClient {
    // TODO: Thay API key này bằng API key hợp lệ từ OpenAI (https://platform.openai.com/)
    // API key hiện tại là của Together.ai, không phải OpenAI
    private static final String API_KEY = "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";
    // URL API của OpenAI
    private static final String BASE_URL = "https://api.openai.com/";
    
    private static OpenAIClient instance;
    private final OpenAIService openAIService;
    
    private OpenAIClient() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    Request original = chain.request();
                    Request request = original.newBuilder()
                            .header("Authorization", "Bearer " + API_KEY)
                            .method(original.method(), original.body())
                            .build();
                    return chain.proceed(request);
                })
                .addInterceptor(loggingInterceptor)
                .build();
        
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        
        openAIService = retrofit.create(OpenAIService.class);
    }
    
    public static synchronized OpenAIClient getInstance() {
        if (instance == null) {
            instance = new OpenAIClient();
        }
        return instance;
    }
    
    public OpenAIService getOpenAIService() {
        return openAIService;
    }
} 