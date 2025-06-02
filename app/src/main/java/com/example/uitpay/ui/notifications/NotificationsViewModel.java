package com.example.uitpay.ui.notifications;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationsViewModel extends AndroidViewModel {

    private final MutableLiveData<List<ChatMessage>> messageList;
    private final MutableLiveData<String> messageInput;
    private final MutableLiveData<Boolean> isLoading;
    private ProductCsvReader productCsvReader;
    private static final String TAG = "NotificationsViewModel";

    public NotificationsViewModel(@NonNull Application application) {
        super(application);
        messageList = new MutableLiveData<>();
        messageList.setValue(new ArrayList<>());
        
        messageInput = new MutableLiveData<>();
        messageInput.setValue("");
        
        isLoading = new MutableLiveData<>(false);
        
        // Khởi tạo ProductCsvReader
        productCsvReader = new ProductCsvReader(application.getApplicationContext());
        
        // Thêm tin nhắn chào mừng
        List<ChatMessage> initialMessages = new ArrayList<>();
        initialMessages.add(new ChatMessage("Xin chào! Tôi là ChatBot. Tôi có thể giúp bạn tìm thông tin về sản phẩm trong cửa hàng. Hãy hỏi tôi về vị trí của sản phẩm bạn cần tìm.", ChatMessage.TYPE_BOT));
        messageList.setValue(initialMessages);
    }

    public LiveData<List<ChatMessage>> getMessageList() {
        return messageList;
    }
    
    public LiveData<String> getMessageInput() {
        return messageInput;
    }
    
    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }
    
    public void setMessageInput(String input) {
        messageInput.setValue(input);
    }
    
    public void sendMessage(String messageText) {
        if (messageText == null || messageText.trim().isEmpty()) {
            return;
        }
        
        List<ChatMessage> currentMessages = messageList.getValue();
        if (currentMessages == null) {
            currentMessages = new ArrayList<>();
        }
        
        // Thêm tin nhắn của người dùng
        currentMessages.add(new ChatMessage(messageText, ChatMessage.TYPE_USER));
        messageList.setValue(new ArrayList<>(currentMessages));
        
        // Hiển thị trạng thái đang tải
        isLoading.setValue(true);
        
        // Chuẩn bị thông tin sản phẩm từ CSV
        String productInfo = productCsvReader.getProductsAsString();
        
        // Chuẩn bị system prompt với thông tin sản phẩm
        String systemPrompt = "Bạn là trợ lý ảo của cửa hàng và nhân viên UIT, mỗi lần rep hãy phản hồi những câu như 'UIT xin chào','UIT xin giúp đỡ',.... Hãy giúp khách hàng tìm thông tin về sản phẩm " +
                "dựa trên dữ liệu sau đây. Nếu khách hỏi về vị trí sản phẩm, hãy cung cấp thông tin chính xác. " +
                "Nếu khách hỏi về thông tin không có trong dữ liệu, hãy lịch sự giới thiệu họ đến nhân viên cửa hàng. " +
                "Đây là dữ liệu sản phẩm:\n\n" + productInfo;
        
        // Tạo request đến ChatGPT
        ChatGptRequest request = ChatGptRequest.createRequest(messageText, systemPrompt, currentMessages);
        
        // Gọi API
        OpenAIClient.getInstance().getOpenAIService().getChatCompletion(request).enqueue(new Callback<ChatGptResponse>() {
            @Override
            public void onResponse(Call<ChatGptResponse> call, Response<ChatGptResponse> response) {
                isLoading.setValue(false);
                
                if (response.isSuccessful() && response.body() != null) {
                    String botResponse = response.body().getFirstResponse();
                    
                    // Thêm phản hồi của bot
                    List<ChatMessage> updatedMessages = messageList.getValue();
                    if (updatedMessages == null) {
                        updatedMessages = new ArrayList<>();
                    }
                    updatedMessages.add(new ChatMessage(botResponse, ChatMessage.TYPE_BOT));
                    messageList.setValue(new ArrayList<>(updatedMessages));
                } else {
                    int statusCode = response.code();
                    String errorBody = "";
                    try {
                        if (response.errorBody() != null) {
                            errorBody = response.errorBody().string();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Không thể đọc error body", e);
                    }
                    
                    Log.e(TAG, "API error: Code=" + statusCode + ", Body=" + errorBody);
                    handleApiError("Lỗi kết nối: " + statusCode);
                }
            }
            
            @Override
            public void onFailure(Call<ChatGptResponse> call, Throwable t) {
                Log.e(TAG, "API call failed", t);
                isLoading.setValue(false);
                handleApiError("Lỗi kết nối: " + t.getMessage());
            }
        });
        
        // Xóa nội dung input
        messageInput.setValue("");
    }
    
    private void handleApiError(String errorDetail) {
        List<ChatMessage> currentMessages = messageList.getValue();
        if (currentMessages == null) {
            currentMessages = new ArrayList<>();
        }
        currentMessages.add(new ChatMessage("Xin lỗi, tôi đang gặp vấn đề kết nối. Vui lòng thử lại sau.\n" + errorDetail, ChatMessage.TYPE_BOT));
        messageList.setValue(new ArrayList<>(currentMessages));
    }
    
    private void handleApiError() {
        handleApiError("Không có thông tin chi tiết");
    }
}