package com.example.uitpay.ui.notifications;

import java.util.List;

public class ChatGptResponse {
    private List<Choice> choices;
    private String object;
    private String id;
    
    public List<Choice> getChoices() {
        return choices;
    }
    
    public String getFirstResponse() {
        if (choices != null && !choices.isEmpty()) {
            Choice choice = choices.get(0);
            if (choice.getMessage() != null) {
                return choice.getMessage().getContent();
            }
        }
        return "Không có phản hồi từ ChatBot";
    }
    
    public static class Choice {
        private Message message;
        private int index;
        
        public Message getMessage() {
            return message;
        }
    }
    
    public static class Message {
        private String content;
        private String role;
        
        public String getContent() {
            return content;
        }
        
        public String getRole() {
            return role;
        }
    }
} 