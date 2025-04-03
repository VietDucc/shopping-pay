package com.example.uitpay.ui.notifications;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

public class NotificationsViewModel extends ViewModel {

    private final MutableLiveData<List<ChatMessage>> messageList;
    private final MutableLiveData<String> messageInput;

    public NotificationsViewModel() {
        messageList = new MutableLiveData<>();
        messageList.setValue(new ArrayList<>());
        
        messageInput = new MutableLiveData<>();
        messageInput.setValue("");
        
        // Thêm tin nhắn chào mừng
        List<ChatMessage> initialMessages = new ArrayList<>();
        initialMessages.add(new ChatMessage("Xin chào! Tôi là ChatBot. Tôi có thể giúp gì cho bạn?", ChatMessage.TYPE_BOT));
        messageList.setValue(initialMessages);
    }

    public LiveData<List<ChatMessage>> getMessageList() {
        return messageList;
    }
    
    public LiveData<String> getMessageInput() {
        return messageInput;
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
        
        // Giả lập phản hồi từ chatbot
        currentMessages.add(new ChatMessage("Đây là phản hồi từ ChatBot. Trong tương lai, phản hồi sẽ được tạo bằng ChatGPT.", ChatMessage.TYPE_BOT));
        
        messageList.setValue(currentMessages);
        
        // Xóa nội dung input
        messageInput.setValue("");
    }
}