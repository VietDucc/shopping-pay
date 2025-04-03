package com.example.uitpay.ui.notifications;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uitpay.databinding.FragmentNotificationsBinding;

import java.util.ArrayList;
import java.util.List;

public class NotificationsFragment extends Fragment {

    private FragmentNotificationsBinding binding;
    private NotificationsViewModel viewModel;
    private RecyclerView recyclerView;
    private EditText editTextMessage;
    private ImageButton buttonSend;
    private ChatAdapter chatAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(this).get(NotificationsViewModel.class);

        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Khởi tạo RecyclerView
        recyclerView = binding.recyclerViewChat;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        
        // Khởi tạo Adapter
        chatAdapter = new ChatAdapter(new ArrayList<>());
        recyclerView.setAdapter(chatAdapter);
        
        // Khởi tạo các view khác
        editTextMessage = binding.editTextMessage;
        buttonSend = binding.buttonSend;
        
        // Thiết lập sự kiện click cho nút gửi
        buttonSend.setOnClickListener(v -> {
            String message = editTextMessage.getText().toString().trim();
            if (!message.isEmpty()) {
                viewModel.sendMessage(message);
                editTextMessage.setText("");
            }
        });
        
        // Quan sát dữ liệu từ ViewModel
        viewModel.getMessageList().observe(getViewLifecycleOwner(), this::updateMessages);
        
        return root;
    }
    
    private void updateMessages(List<ChatMessage> messages) {
        // Tạo adapter mới với danh sách cập nhật
        chatAdapter = new ChatAdapter(messages);
        recyclerView.setAdapter(chatAdapter);
        
        // Cuộn xuống tin nhắn mới nhất
        if (messages.size() > 0) {
            recyclerView.smoothScrollToPosition(messages.size() - 1);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}