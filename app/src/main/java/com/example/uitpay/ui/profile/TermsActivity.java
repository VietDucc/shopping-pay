package com.example.uitpay.ui.profile;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.uitpay.R;

public class TermsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms);
        
        setupViews();
    }

    private void setupViews() {
        // Back button
        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());
        
        // Terms content
        TextView termsContent = findViewById(R.id.termsContent);
        termsContent.setText(getTermsContent());
    }

    private String getTermsContent() {
        return "ĐIỀU KHOẢN DỊCH VỤ SHOPPING PAY\n\n" +
                "Chào mừng bạn đến với Shopping Pay! Bằng việc sử dụng ứng dụng này, bạn đồng ý với các điều khoản sau:\n\n" +
                
                "1. VỀ DỊCH VỤ\n" +
                "• Shopping Pay là ứng dụng thanh toán điện tử giúp người dùng quản lý và thực hiện các giao dịch mua sắm.\n" +
                "• Chúng tôi cung cấp dịch vụ ví điện tử, thanh toán online và quản lý hóa đơn.\n" +
                "• Dịch vụ khả dụng 24/7 trừ thời gian bảo trì hệ thống.\n\n" +
                
                "2. TÀI KHOẢN NGƯỜI DÙNG\n" +
                "• Bạn có trách nhiệm bảo mật thông tin đăng nhập của mình.\n" +
                "• Không chia sẻ tài khoản với người khác.\n" +
                "• Thông báo ngay cho chúng tôi nếu phát hiện hoạt động bất thường.\n" +
                "• Cập nhật thông tin cá nhân chính xác và kịp thời.\n\n" +
                
                "3. GIAO DỊCH & THANH TOÁN\n" +
                "• Mọi giao dịch đều được mã hóa và bảo mật tối đa.\n" +
                "• Phí dịch vụ sẽ được thông báo rõ ràng trước khi thực hiện.\n" +
                "• Giao dịch hoàn tất không thể hoàn tác trừ trường hợp đặc biệt.\n" +
                "• Chúng tôi không chịu trách nhiệm với các giao dịch ngoài hệ thống.\n\n" +
                
                "4. BẢO MẬT THÔNG TIN\n" +
                "• Thông tin cá nhân được bảo vệ theo chuẩn quốc tế.\n" +
                "• Không chia sẻ dữ liệu với bên thứ ba khi chưa có sự đồng ý.\n" +
                "• Sử dụng công nghệ mã hóa AES-256 để bảo vệ dữ liệu.\n" +
                "• Quyền truy cập dữ liệu chỉ dành cho nhân viên được ủy quyền.\n\n" +
                
                "5. QUYỀN VÀ NGHĨA VỤ\n" +
                "• Người dùng có quyền yêu cầu xóa tài khoản bất kỳ lúc nào.\n" +
                "• Chúng tôi có quyền tạm ngưng dịch vụ khi phát hiện vi phạm.\n" +
                "• Người dùng phải tuân thủ pháp luật Việt Nam khi sử dụng.\n" +
                "• Báo cáo ngay các lỗ hổng bảo mật nếu phát hiện.\n\n" +
                
                "6. CHÍNH SÁCH HOÀN TIỀN\n" +
                "• Hoàn tiền trong vòng 24h nếu giao dịch bị lỗi từ hệ thống.\n" +
                "• Yêu cầu hoàn tiền phải có bằng chứng và lý do hợp lệ.\n" +
                "• Thời gian xử lý hoàn tiền từ 3-7 ngày làm việc.\n" +
                "• Không hoàn tiền cho các giao dịch đã hoàn tất thành công.\n\n" +
                
                "7. HỖ TRỢ KHÁCH HÀNG\n" +
                "• Tổng đài hỗ trợ: 1900-123-456 (24/7)\n" +
                "• Email: support@shoppingpay.vn\n" +
                "• Thời gian phản hồi tối đa 2 giờ trong giờ hành chính.\n" +
                "• Chatbot AI hỗ trợ tức thì mọi lúc.\n\n" +
                
                "8. THAY ĐỔI ĐIỀU KHOẢN\n" +
                "• Chúng tôi có quyền cập nhật điều khoản khi cần thiết.\n" +
                "• Thông báo trước tối thiểu 30 ngày qua ứng dụng và email.\n" +
                "• Việc tiếp tục sử dụng được coi là đồng ý với điều khoản mới.\n\n" +
                
                "9. LIÊN HỆ\n" +
                "Shopping Pay Co., Ltd\n" +
                "Địa chỉ: 123 Đường ABC, Quận 1, TP.HCM\n" +
                "Điện thoại: (028) 1234-5678\n" +
                "Email: legal@shoppingpay.vn\n\n" +
                
                "Cập nhật lần cuối: " + java.text.DateFormat.getDateInstance().format(new java.util.Date()) + "\n\n" +
                
                "Cảm ơn bạn đã tin tưởng và sử dụng Shopping Pay! 🛍️💳";
    }
} 