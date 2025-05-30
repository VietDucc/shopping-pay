package com.example.uitpay.ui.notifications;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uitpay.R;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<ChatMessage> messageList;

    public ChatAdapter(List<ChatMessage> messageList) {
        this.messageList = messageList;
    }

    @Override
    public int getItemViewType(int position) {
        return messageList.get(position).getMessageType();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == ChatMessage.TYPE_USER) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_right, parent, false);
            return new UserMessageViewHolder(view);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_left, parent, false);
            return new BotMessageViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatMessage message = messageList.get(position);
        
        if (holder.getItemViewType() == ChatMessage.TYPE_USER) {
            UserMessageViewHolder viewHolder = (UserMessageViewHolder) holder;
            viewHolder.messageText.setText(message.getMessageText());
            viewHolder.timestamp.setText(message.getTimestamp());
        } else {
            BotMessageViewHolder viewHolder = (BotMessageViewHolder) holder;
            viewHolder.messageText.setText(message.getMessageText());
            viewHolder.timestamp.setText(message.getTimestamp());
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public void addMessage(ChatMessage message) {
        messageList.add(message);
        notifyItemInserted(messageList.size() - 1);
    }

    public void updateMessages(List<ChatMessage> newMessages) {
        this.messageList = newMessages;
        notifyDataSetChanged();
    }

    static class UserMessageViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView messageText;
        TextView timestamp;

        UserMessageViewHolder(View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardView_message);
            messageText = itemView.findViewById(R.id.textView_message);
            timestamp = itemView.findViewById(R.id.textView_timestamp);
        }
    }

    static class BotMessageViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView messageText;
        TextView timestamp;

        BotMessageViewHolder(View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardView_message);
            messageText = itemView.findViewById(R.id.textView_message);
            timestamp = itemView.findViewById(R.id.textView_timestamp);
        }
    }
} 