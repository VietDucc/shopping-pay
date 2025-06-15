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
        
        // Không thêm tin nhắn chào mừng ban đầu vì đã có suggested questions
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
        String systemPrompt = "Bạn là trợ lý ảo thông minh của UIT Shopping - ứng dụng mua sắm tiện lợi tại các cửa hàng vật lý UIT. " +
                "Hãy luôn bắt đầu câu trả lời với 'UIT Shopping xin chào' hoặc 'UIT Shopping rất vui được hỗ trợ'.\n\n" +
                
                "MÔ HÌNH KINH DOANH UIT SHOPPING:\n" +
                "- Cửa hàng vật lý hiện đại với công nghệ NFC thanh toán tiên tiến\n" +
                "- Quẹt thẻ NFC để mua hàng và thêm sản phẩm vào giỏ hàng tự động\n" +
                "- Kiểm soát phiên mua hàng tiện lợi và thông minh\n\n" +
                
                "QUY TRÌNH MUA HÀNG CHI TIẾT:\n" +
                "1. Quẹt sản phẩm bằng NFC để thêm vào giỏ hàng\n" +
                "2. Xem thông tin sản phẩm đầy đủ sau khi quẹt\n" +
                "3. Bước Recheck thủ công: Kiểm tra giỏ hàng có thiếu/thừa sản phẩm không\n" +
                "4. Chọn phương thức thanh toán\n" +
                "5. Hoàn tất giao dịch\n\n" +
                
                "PHƯƠNG THỨC THANH TOÁN:\n" +
                "- Ví VNPAY: Ví điện tử VNPAY với đầy đủ tính năng\n" +
                "- Ví UITPAY: Ví nội bộ riêng của UIT Shopping app\n\n" +
                
                "THỜI GIAN GIAO HÀNG & CHI PHÍ:\n" +
                "- Hiện tại: Mua hàng tại cửa hàng vật lý\n" +
                "- Tương lai: Sẽ phát triển mô hình COD và bán hàng trực tuyến\n" +
                "- Đang nghiên cứu dịch vụ giao hàng toàn diện\n\n" +
                
                "CHƯƠNG TRÌNH KHUYẾN MÃI:\n" +
                "- Hiện tại: Đang có các chương trình khuyến mãi hấp dẫn\n" +
                "- Tương lai: Sẽ tích hợp sâu hơn vào app với nhiều ưu đãi\n" +
                "- Hệ thống tích điểm và voucher thông minh\n\n" +
                
                "- Hỗ trợ 24/7 qua hotline: 0898856496\n\n" +
                
                "Nếu khách hỏi về sản phẩm cụ thể, hãy sử dụng dữ liệu sau:\n" + productInfo + "\n\n" +
                
                "Hãy trả lời một cách thân thiện, chi tiết và hữu ích. Nhấn mạnh tính tiện lợi của công nghệ NFC và trải nghiệm mua sắm hiện đại. Nếu không biết thông tin, hãy hướng dẫn khách liên hệ hotline.";
        
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