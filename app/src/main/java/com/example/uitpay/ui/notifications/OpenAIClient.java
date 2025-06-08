package com.example.uitpay.ui.notifications;

import com.example.uitpay.BuildConfig;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class OpenAIClient {
    // TODO: Thêm API key vào gradle.properties hoặc biến môi trường
    // Để bảo mật, không hard-code API key trong source code
    // private static final String API_KEY = "YOUR_OPENAI_API_KEY_HERE";
    private static final String API_KEY = getApiKey();
    // URL API của OpenAI
    private static final String BASE_URL = "https://api.openai.com/";
    
    private static OpenAIClient instance;
    private final OpenAIService openAIService;
    
    private static String getApiKey() {
        // API key được lấy từ BuildConfig (từ gradle.properties)
        return BuildConfig.OPENAI_API_KEY;
    }
    
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