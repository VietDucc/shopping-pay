package com.example.uitpay.ui.notifications;

import java.util.ArrayList;
import java.util.List;

public class ChatGptRequest {
    private String model;
    private List<Message> messages;
    private double temperature;
    private int max_tokens;
    
    public ChatGptRequest(String model, List<Message> messages, double temperature, int max_tokens) {
        this.model = model;
        this.messages = messages;
        this.temperature = temperature;
        this.max_tokens = max_tokens;
    }
    
    public static class Message {
        private String role;
        private String content;
        
        public Message(String role, String content) {
            this.role = role;
            this.content = content;
        }
    }
    
    public static ChatGptRequest createRequest(String prompt, String systemPrompt, List<ChatMessage> chatHistory) {
        List<Message> messages = new ArrayList<>();
        
        // Add system prompt
        messages.add(new Message("system", systemPrompt));
        
        // Add chat history
        for (ChatMessage chatMessage : chatHistory) {
            String role = (chatMessage.getMessageType() == ChatMessage.TYPE_USER) ? "user" : "assistant";
            messages.add(new Message(role, chatMessage.getMessageText()));
        }
        
        // Add current prompt
        messages.add(new Message("user", prompt));
        
        // Sử dụng model của OpenAI
        return new ChatGptRequest("gpt-3.5-turbo", messages, 0.7, 500);
    }
} 