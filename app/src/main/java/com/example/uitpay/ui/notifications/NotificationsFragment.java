package com.example.uitpay.ui.notifications;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ScrollView;
import androidx.cardview.widget.CardView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uitpay.R;
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
    private ProgressBar progressBar;
    private ScrollView suggestedQuestionsContainer;
    private CardView question1, question2, question3, question4;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(this).get(NotificationsViewModel.class);

        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Khởi tạo RecyclerView
        recyclerView = binding.chatRecyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        
        // Khởi tạo Adapter
        chatAdapter = new ChatAdapter(new ArrayList<>());
        recyclerView.setAdapter(chatAdapter);
        
        // Khởi tạo các view khác
        editTextMessage = binding.messageEditText;
        buttonSend = binding.sendButton;
        suggestedQuestionsContainer = root.findViewById(R.id.suggestedQuestionsContainer);
        
        // Khởi tạo câu hỏi mẫu
        question1 = root.findViewById(R.id.question1);
        question2 = root.findViewById(R.id.question2);
        question3 = root.findViewById(R.id.question3);
        question4 = root.findViewById(R.id.question4);
        
        // Thêm ProgressBar để hiển thị trạng thái đang tải
        progressBar = root.findViewById(R.id.progressBar);
        if (progressBar == null) {
            // Nếu chưa có ProgressBar trong layout, thêm nó vào
            progressBar = new ProgressBar(getContext(), null, android.R.attr.progressBarStyleSmall);
            progressBar.setId(R.id.progressBar);
            progressBar.setVisibility(View.GONE);
            
            ViewGroup bottomLayout = root.findViewById(R.id.bottomLayout);
            if (bottomLayout != null && bottomLayout instanceof ViewGroup) {
                ((ViewGroup) bottomLayout).addView(progressBar);
            }
        }
        
        // Thiết lập sự kiện click cho nút gửi
        buttonSend.setOnClickListener(v -> sendMessage());
        
        // Thiết lập sự kiện click cho câu hỏi mẫu
        setupSuggestedQuestions();
        
        // Thiết lập sự kiện Enter trên EditText
        editTextMessage.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND || 
                    (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN)) {
                sendMessage();
                return true;
            }
            return false;
        });
        
        // Quan sát dữ liệu từ ViewModel
        viewModel.getMessageList().observe(getViewLifecycleOwner(), this::updateMessages);
        
        // Quan sát trạng thái đang tải
        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (progressBar != null) {
                progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            }
            buttonSend.setEnabled(!isLoading);
        });
        
        return root;
    }
    
    private void setupSuggestedQuestions() {
        question1.setOnClickListener(v -> {
            sendMessageFromSuggestion("Quy trình mua hàng như thế nào?");
        });
        
        question2.setOnClickListener(v -> {
            sendMessageFromSuggestion("Có những phương thức thanh toán nào?");
        });
        
        question3.setOnClickListener(v -> {
            sendMessageFromSuggestion("Thời gian giao hàng và chi phí?");
        });
        
        question4.setOnClickListener(v -> {
            sendMessageFromSuggestion("Có chương trình khuyến mãi nào không?");
        });
    }
    
    private void sendMessageFromSuggestion(String message) {
        // Ẩn suggested questions và hiển thị chat
        suggestedQuestionsContainer.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        
        // Gửi tin nhắn
        viewModel.sendMessage(message);
    }
    
    private void sendMessage() {
        String message = editTextMessage.getText().toString().trim();
        if (!message.isEmpty()) {
            // Ẩn suggested questions khi user bắt đầu chat
            if (suggestedQuestionsContainer.getVisibility() == View.VISIBLE) {
                suggestedQuestionsContainer.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            }
            
            viewModel.sendMessage(message);
            editTextMessage.setText("");
        }
    }
    
    private void updateMessages(List<ChatMessage> messages) {
        chatAdapter.updateMessages(messages);
        
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