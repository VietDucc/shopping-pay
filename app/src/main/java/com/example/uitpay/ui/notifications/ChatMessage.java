package com.example.uitpay.ui.notifications;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ChatMessage {
    public static final int TYPE_USER = 0;
    public static final int TYPE_BOT = 1;

    private String messageText;
    private int messageType;
    private String timestamp;

    public ChatMessage(String messageText, int messageType) {
        this.messageText = messageText;
        this.messageType = messageType;
        this.timestamp = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public int getMessageType() {
        return messageType;
    }

    public void setMessageType(int messageType) {
        this.messageType = messageType;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
} 