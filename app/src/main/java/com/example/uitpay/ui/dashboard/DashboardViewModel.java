package com.example.uitpay.ui.dashboard;

import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;
import java.util.HashMap;
import java.util.Map;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.ServerValue;
import com.example.uitpay.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.example.uitpay.model.Product;
import android.view.View;
import androidx.annotation.NonNull;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.Toast;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import com.example.uitpay.ui.profile.PaymentClient;
import com.example.uitpay.ui.profile.PaymentRequest;
import com.example.uitpay.ui.profile.PaymentResponse;
import com.example.uitpay.ui.profile.PaymentStatusResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.google.firebase.firestore.DocumentReference;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class DashboardViewModel extends ViewModel {

    private final MutableLiveData<String> mText;
    private final MutableLiveData<Integer> currentStage;
    private final MutableLiveData<String> buttonText;
    private final MutableLiveData<Integer> stageImageResource;
    private final MutableLiveData<Integer> muahangImageResource;
    private final MutableLiveData<Integer> muahangImageVisibility;
    private final MutableLiveData<Integer> summaryLayoutVisibility;
    private DatabaseReference databaseRef;
    private FirebaseFirestore db;
    private MutableLiveData<Product> newProduct = new MutableLiveData<>();
    private final MutableLiveData<Integer> totalQuantity = new MutableLiveData<>(0);
    private final MutableLiveData<Double> totalPrice = new MutableLiveData<>(0.0);
    private final MutableLiveData<Integer> recyclerViewVisibility = new MutableLiveData<>(View.VISIBLE);
    private final MutableLiveData<String> recheckStatusText = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isChecked = new MutableLiveData<>(false);
    private final MutableLiveData<String> paymentInfoText = new MutableLiveData<>();
    private final MutableLiveData<Integer> paymentCardVisibility = new MutableLiveData<>(View.GONE);
    private final MutableLiveData<String> currentBalanceText = new MutableLiveData<>();
    private final MutableLiveData<String> paymentAmountText = new MutableLiveData<>();
    private final MutableLiveData<String> balanceAfterText = new MutableLiveData<>();
    private final MutableLiveData<String> currentOrderId = new MutableLiveData<>();
    private final MutableLiveData<String> paymentUrl = new MutableLiveData<>();

    public DashboardViewModel() {
        mText = new MutableLiveData<>();
        currentStage = new MutableLiveData<>(1);
        buttonText = new MutableLiveData<>("Bắt đầu mua hàng");
        stageImageResource = new MutableLiveData<>(R.drawable.stage1);
        muahangImageResource = new MutableLiveData<>(R.drawable.muahang1);
        muahangImageVisibility = new MutableLiveData<>(View.VISIBLE);
        summaryLayoutVisibility = new MutableLiveData<>(View.GONE);
        mText.setValue("Đi đến các UIT Store để bắt đầu mua hàng");

        try {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            databaseRef = database.getReference();
            
            // Kiểm tra kết nối
            databaseRef.child(".info/connected").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    boolean connected = snapshot.getValue(Boolean.class);
                    Log.d("Firebase", "Kết nối: " + connected);
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    Log.e("Firebase", "Lỗi kết nối: " + error.getMessage());
                }
            });
        } catch (Exception e) {
            Log.e("Firebase", "Lỗi khởi tạo database: " + e.getMessage());
        }

        db = FirebaseFirestore.getInstance();
    }

    public void writeUserData(String productId) {
        if (databaseRef == null) {
            databaseRef = FirebaseDatabase.getInstance().getReference();
        }

        DatabaseReference productsRef = databaseRef.child("phampho1103").child("products");
        
        // Kiểm tra tất cả các sản phẩm để tìm productId trùng
        productsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean productExists = false;
                
                for (DataSnapshot child : snapshot.getChildren()) {
                    String existingProductId = child.child("productId").getValue(String.class);
                    if (existingProductId != null && existingProductId.equals(productId)) {
                        productExists = true;
                        Log.d("Firebase", "ProductId đã tồn tại: " + productId);
                        break;
                    }
                }
                
                if (!productExists) {
                    addNewProduct(productId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Lỗi kiểm tra productId: " + error.getMessage());
            }
        });
    }

    private void addNewProduct(String productId) {
        String key = databaseRef.child("phampho1103")
            .child("products")
            .push()
            .getKey();
        
        if (key != null) {
            db.collection("product")
                .document(productId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Lấy toàn bộ thông tin sản phẩm từ Firestore
                        String name = documentSnapshot.getString("name");
                        Double price = documentSnapshot.getDouble("price");
                        String image = documentSnapshot.getString("productImage");
                        String origin = documentSnapshot.getString("origin");
                        String description = documentSnapshot.getString("description");

                        Map<String, Object> productData = new HashMap<>();
                        productData.put("productId", productId);
                        productData.put("name", name);
                        productData.put("quantity", 1);
                        productData.put("price", price);
                        productData.put("productImage", image);
                        productData.put("origin", origin);
                        productData.put("description", description);

                        databaseRef.child("phampho1103")
                            .child("products")
                            .child(key)
                            .setValue(productData);
                    }
                });
        }
    }

    public LiveData<String> getText() {
        return mText;
    }

    public LiveData<Integer> getCurrentStage() {
        return currentStage;
    }

    public LiveData<String> getButtonText() {
        return buttonText;
    }

    public LiveData<Integer> getStageImageResource() {
        return stageImageResource;
    }

    public LiveData<Integer> getMuahangImageResource() {
        return muahangImageResource;
    }

    public LiveData<Integer> getMuahangImageVisibility() {
        return muahangImageVisibility;
    }

    public LiveData<Integer> getSummaryLayoutVisibility() {
        return summaryLayoutVisibility;
    }

    public void moveToNextStage() {
        int stage = currentStage.getValue();
        if (stage == 1) {
            currentStage.setValue(2);
            buttonText.setValue("Hoàn thành mua hàng");
            stageImageResource.setValue(R.drawable.stage2);
            muahangImageResource.setValue(R.drawable.muahang2);
            mText.setValue("Hãy quét điện thoại tới các sản phẩm mà bạn muốn mua");
            summaryLayoutVisibility.setValue(View.VISIBLE);
        }
    }

    public void fetchProductById(String productId) {
        db.collection("product")
            .document(productId)
            .get()
            .addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    Product product = documentSnapshot.toObject(Product.class);
                    if (product != null) {
                        product.setProductId(productId);
                        product.setQuantity(1);
                        newProduct.setValue(product);
                        hidemuahangImage();
                        
                        // Cập nhật tổng quantity và price
                        DatabaseReference userRef = databaseRef.child("phampho1103");
                        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                int currentQuantity = snapshot.child("quantity").getValue(Integer.class);
                                double currentTotalPrice = snapshot.child("totalprice").getValue(Double.class);
                                
                                int newQuantity = currentQuantity + 1;
                                double newTotalPrice = currentTotalPrice + product.getPrice();
                                
                                Map<String, Object> updates = new HashMap<>();
                                updates.put("quantity", newQuantity);
                                updates.put("totalprice", newTotalPrice);
                                
                                totalQuantity.setValue(newQuantity);
                                totalPrice.setValue(newTotalPrice);
                                
                                userRef.updateChildren(updates);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Log.e("Firebase", "Lỗi đọc dữ liệu: " + error.getMessage());
                            }
                        });
                        
                        // Thêm sản phẩm vào node products như cũ
                        String key = databaseRef.child("phampho1103")
                            .child("products")
                            .push()
                            .getKey();
                        
                        if (key != null) {
                            Map<String, Object> productData = new HashMap<>();
                            productData.put("productId", productId);
                            productData.put("name", product.getName());
                            productData.put("quantity", product.getQuantity());
                            productData.put("price", product.getPrice());
                            productData.put("productImage", product.getProductImage());
                            productData.put("origin", product.getOrigin());
                            productData.put("description", product.getDescription());
                            
                            databaseRef.child("phampho1103")
                                .child("products")
                                .child(key)
                                .setValue(productData);
                        }
                    }
                }
            });
    }

    public LiveData<Product> getNewProduct() {
        return newProduct;
    }

    public void hidemuahangImage() {
        muahangImageVisibility.setValue(View.GONE);
    }

    public void writeInitialData() {
        if (databaseRef == null) {
            databaseRef = FirebaseDatabase.getInstance().getReference();
        }

        Map<String, Object> initialData = new HashMap<>();
        initialData.put("timestamp", ServerValue.TIMESTAMP);
        initialData.put("quantity", 0);
        initialData.put("totalprice", 0);
        initialData.put("isChecked", false);
        initialData.put("isBuying", true);
        initialData.put("isPaid", false);
        
        totalQuantity.setValue(0);
        totalPrice.setValue(0.0);
        
        databaseRef.child("phampho1103")
            .setValue(initialData);
    }

    public LiveData<Integer> getTotalQuantity() {
        return totalQuantity;
    }

    public LiveData<Double> getTotalPrice() {
        return totalPrice;
    }

    public LiveData<Integer> getRecyclerViewVisibility() {
        return recyclerViewVisibility;
    }

    public void finishShopping() {
        currentStage.setValue(3);
        buttonText.setValue("Hoàn thành recheck");
        stageImageResource.setValue(R.drawable.stage3);
        muahangImageResource.setValue(R.drawable.muahang3);
        muahangImageVisibility.setValue(View.VISIBLE);
        mText.setValue("Đi tới quầy thu ngân để thực hiện Recheck");
        
        recyclerViewVisibility.setValue(View.GONE);
        
        if (databaseRef != null) {
            databaseRef.child("phampho1103")
                .child("isBuying")
                .setValue(false);
        }
    }

    public LiveData<String> getRecheckStatusText() {
        return recheckStatusText;
    }

    public LiveData<Boolean> getIsChecked() {
        return isChecked;
    }

    public void listenToCheckStatus() {
        if (databaseRef != null) {
            databaseRef.child("phampho1103").child("isChecked")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Boolean checked = snapshot.getValue(Boolean.class);
                        isChecked.setValue(checked);
                        
                        if (checked != null) {
                            if (checked) {
                                recheckStatusText.setValue("Đơn hàng đã được Recheck rồi! Bạn hãy thanh toán nhé");
                                muahangImageResource.setValue(R.drawable.muahang4);
                            } else {
                                recheckStatusText.setValue("Đơn hàng vẫn chưa được Recheck. Đi tới quầy thu ngân để Recheck nhé!");
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("Firebase", "Lỗi đọc trạng thái recheck: " + error.getMessage());
                    }
                });
        }
    }

    public LiveData<String> getPaymentInfoText() {
        return paymentInfoText;
    }

    public LiveData<Integer> getPaymentCardVisibility() {
        return paymentCardVisibility;
    }

    public LiveData<String> getCurrentBalanceText() {
        return currentBalanceText;
    }

    public LiveData<String> getPaymentAmountText() {
        return paymentAmountText;
    }

    public LiveData<String> getBalanceAfterText() {
        return balanceAfterText;
    }

    public LiveData<String> getCurrentOrderId() {
        return currentOrderId;
    }

    public LiveData<String> getPaymentUrl() {
        return paymentUrl;
    }

    public void showPaymentInfo() {
        buttonText.setValue(""); // Ẩn button cũ
        muahangImageVisibility.setValue(View.GONE);
        paymentCardVisibility.setValue(View.VISIBLE);
        // Ẩn text cũ khi hiển thị card mới
        mText.setValue("");
        
        FirebaseFirestore.getInstance()
            .collection("user")
            .document("id001")
            .get()
            .addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    Long currentBalance = documentSnapshot.getLong("sotien");
                    
                    DatabaseReference userRef = databaseRef.child("phampho1103");
                    userRef.child("totalprice").get().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Double totalPrice = task.getResult().getValue(Double.class);
                            if (currentBalance != null && totalPrice != null) {
                                long remainingBalance = currentBalance - totalPrice.longValue();
                                
                                // Update payment card data
                                currentBalanceText.setValue(String.format("%,d", currentBalance));
                                paymentAmountText.setValue(String.format("%,d", totalPrice.longValue()));
                                balanceAfterText.setValue(String.format("%,d VND", remainingBalance));
                                
                                // Keep old format for backward compatibility
                                String paymentInfo = String.format(
                                    "Số dư hiện tại: <font color='#FF0000'>%,d</font> VND<br><br>" +
                                    "Số tiền cần thanh toán: <font color='#FF0000'>%,d</font> VND<br><br>" + 
                                    "Số dư sau thanh toán: <font color='#FF0000'>%,d</font> VND",
                                    currentBalance, totalPrice.longValue(), remainingBalance);
                                paymentInfoText.setValue(paymentInfo);
                                buttonText.setValue("Thanh toán");
                            }
                        }
                    });
                }
            });
    }

    public void processUITPayPayment(Context context, Double totalPrice, DialogInterface dialog) {
        FirebaseFirestore.getInstance()
            .collection("user")
            .document("id001")
            .get()
            .addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    Long currentBalance = documentSnapshot.getLong("sotien");
                    
                    if (currentBalance != null && totalPrice != null) {
                        if (currentBalance >= totalPrice) {
                            // Cập nhật số dư mới
                            long newBalance = currentBalance - totalPrice.longValue();
                            documentSnapshot.getReference().update("sotien", newBalance)
                                .addOnSuccessListener(aVoid -> {
                                    // Cập nhật isPaid thành true
                                    DatabaseReference userRef = databaseRef.child("phampho1103");
                                    userRef.child("isPaid").setValue(true)
                                        .addOnSuccessListener(aVoid2 -> {
                                            dialog.dismiss();
                                            moveToStage4();
                                            Toast.makeText(context, 
                                                "💰 Thanh toán UITPAY thành công!", 
                                                Toast.LENGTH_SHORT).show();
                                        });
                                });
                        } else {
                            Toast.makeText(context, 
                                "💳 Số dư không đủ để thanh toán!\nVui lòng nạp thêm tiền vào ví.", 
                                Toast.LENGTH_LONG).show();
                            dialog.dismiss();
                        }
                    }
                }
            });
    }

    public void processVNPayPayment(Context context, Double totalPrice, AlertDialog loadingDialog) {
        TextView statusText = loadingDialog.findViewById(R.id.loading_status);
        TextView subText = loadingDialog.findViewById(R.id.loading_sub_text);
        
        // Step 1: Create payment order
        if (statusText != null) {
            statusText.setText("Đang tạo đơn thanh toán...");
        }
        
        // Create payment request
        String orderInfo = "Thanh toán đơn hàng UIT Shopping - " + String.format("%,.0f VND", totalPrice);
        PaymentRequest request = new PaymentRequest(totalPrice.longValue(), orderInfo);
        
        // Call API to create payment order
        PaymentClient.getInstance().getPaymentService().createPaymentOrder(request)
            .enqueue(new Callback<PaymentResponse>() {
                @Override
                public void onResponse(Call<PaymentResponse> call, Response<PaymentResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        PaymentResponse paymentResponse = response.body();
                        
                        // Save order info
                        currentOrderId.setValue(paymentResponse.getOrderId());
                        paymentUrl.setValue(paymentResponse.getPaymentUrl());
                        
                        // Update status
                        if (statusText != null) {
                            statusText.setText("Đang chuyển hướng đến VNPAY...");
                        }
                        if (subText != null) {
                            subText.setText("Sẽ mở trình duyệt trong giây lát");
                        }
                        
                        // Step 2: Open browser after short delay
                        new android.os.Handler().postDelayed(() -> {
                            loadingDialog.dismiss();
                            openPaymentUrl(context, paymentResponse.getPaymentUrl());
                        }, 1500);
                        
                    } else {
                        loadingDialog.dismiss();
                        Toast.makeText(context, 
                            "❌ Lỗi tạo đơn thanh toán: " + response.code(), 
                            Toast.LENGTH_LONG).show();
                    }
                }
                
                @Override
                public void onFailure(Call<PaymentResponse> call, Throwable t) {
                    loadingDialog.dismiss();
                    Toast.makeText(context, 
                        "❌ Lỗi kết nối: " + t.getMessage(), 
                        Toast.LENGTH_LONG).show();
                }
            });
    }
    
    private void openPaymentUrl(Context context, String paymentUrl) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(paymentUrl));
            context.startActivity(intent);
            
            Toast.makeText(context, 
                "🌐 Đã mở trình duyệt thanh toán VNPAY", 
                Toast.LENGTH_SHORT).show();
                
        } catch (Exception e) {
            Toast.makeText(context, 
                "❌ Không thể mở trang thanh toán", 
                Toast.LENGTH_LONG).show();
        }
    }
    
    private void showVNPayResultDialog(Context context, Double totalPrice, boolean success) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        
        if (success) {
            builder.setTitle("🎉 Thanh toán thành công!")
                    .setMessage(String.format("Đã thanh toán %,.0f VND qua VNPAY", totalPrice))
                    .setPositiveButton("Hoàn tất", (dialog, id) -> {
                        // Update payment status
                        DatabaseReference userRef = databaseRef.child("phampho1103");
                        userRef.child("isPaid").setValue(true)
                            .addOnSuccessListener(aVoid -> {
                                dialog.dismiss();
                                moveToStage4();
                                Toast.makeText(context, 
                                    "💳 Thanh toán VNPAY thành công!", 
                                    Toast.LENGTH_SHORT).show();
                            });
                    });
        } else {
            builder.setTitle("❌ Thanh toán thất bại")
                    .setMessage("Có lỗi xảy ra trong quá trình thanh toán qua VNPAY")
                    .setPositiveButton("Thử lại", (dialog, id) -> {
                        dialog.dismiss();
                    });
        }
        
        builder.setCancelable(false);
        builder.create().show();
    }
    
    public void checkVNPayPaymentStatus(Context context) {
        String orderId = currentOrderId.getValue();
        if (orderId == null || orderId.isEmpty()) {
            return;
        }
        
        Toast.makeText(context, "Đang kiểm tra trạng thái thanh toán...", Toast.LENGTH_SHORT).show();
        
        PaymentClient.getInstance().getPaymentService().getPaymentStatus(orderId)
            .enqueue(new Callback<PaymentStatusResponse>() {
                @Override
                public void onResponse(Call<PaymentStatusResponse> call, Response<PaymentStatusResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        PaymentStatusResponse statusResponse = response.body();
                        
                        if (statusResponse.isPaid()) {
                            // Payment successful - create invoice and update status
                            createInvoiceAndComplete(context, statusResponse.getAmount());
                        } else {
                            Toast.makeText(context, 
                                "💳 Thanh toán chưa hoàn tất", 
                                Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(context, 
                            "❌ Lỗi kiểm tra thanh toán: " + response.code(), 
                            Toast.LENGTH_SHORT).show();
                    }
                }
                
                @Override
                public void onFailure(Call<PaymentStatusResponse> call, Throwable t) {
                    Toast.makeText(context, 
                        "❌ Lỗi kết nối kiểm tra thanh toán", 
                        Toast.LENGTH_SHORT).show();
                }
            });
    }

    private void createInvoiceAndComplete(Context context, long totalAmount) {
        // Clear payment tracking
        currentOrderId.setValue(null);
        paymentUrl.setValue(null);
        
        // Get total price and products from Firebase Realtime Database
        DatabaseReference userRef = databaseRef.child("phampho1103");
        userRef.child("totalprice").get().addOnCompleteListener(priceTask -> {
            if (priceTask.isSuccessful()) {
                Double actualTotalPrice = priceTask.getResult().getValue(Double.class);
                
                // Get products
                DatabaseReference productsRef = databaseRef.child("phampho1103").child("products");
                productsRef.get().addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        DataSnapshot snapshot = task.getResult();
                        
                        // Create invoice data
                        Map<String, Object> invoice = new HashMap<>();
                        String invoiceId = "INV" + System.currentTimeMillis();
                        String currentDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
                        
                        invoice.put("invoiceID", invoiceId);
                        // Use actual total price from shopping cart, not payment amount
                        invoice.put("cost", actualTotalPrice != null ? actualTotalPrice.doubleValue() : totalAmount);
                        invoice.put("date", currentDate);
                        invoice.put("description", "Thanh toán đơn hàng qua VNPAY");
                        invoice.put("userid", "id001");
                
                        // Convert products to invoice format
                        Map<String, Object> products = new HashMap<>();
                        for (DataSnapshot productSnapshot : snapshot.getChildren()) {
                            String productId = productSnapshot.child("productId").getValue(String.class);
                            if (productId != null) {
                                Map<String, Object> productData = new HashMap<>();
                                productData.put("name", productSnapshot.child("name").getValue(String.class));
                                productData.put("price", productSnapshot.child("price").getValue(Double.class));
                                productData.put("quantity", productSnapshot.child("quantity").getValue(Integer.class));
                                products.put(productId, productData);
                            }
                        }
                        invoice.put("product", products);
                        
                        // Save invoice to Firestore
                        FirebaseFirestore.getInstance()
                            .collection("invoice")
                            .document(invoiceId)
                            .set(invoice)
                            .addOnSuccessListener(aVoid -> {
                                // Update payment status
                                databaseRef.child("phampho1103").child("isPaid").setValue(true)
                                    .addOnSuccessListener(aVoid2 -> {
                                        Toast.makeText(context, 
                                            "🎉 Thanh toán VNPAY thành công!\n📄 Hóa đơn đã được lưu.", 
                                            Toast.LENGTH_LONG).show();
                                        moveToStage4();
                                    });
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(context, 
                                    "⚠️ Thanh toán thành công nhưng lỗi lưu hóa đơn", 
                                    Toast.LENGTH_LONG).show();
                                moveToStage4();
                            });
                    }
                });
            }
        });
    }

    public DatabaseReference getDatabaseRef() {
        return databaseRef;
    }

    public void moveToStage4() {
        currentStage.setValue(4);
        mText.setValue("Cảm ơn bạn đã mua hàng tại UIT STORE");
        buttonText.setValue("Quay trở về trang chủ");
        stageImageResource.setValue(R.drawable.stage4);
        muahangImageResource.setValue(R.drawable.muahang5);
        muahangImageVisibility.setValue(View.VISIBLE);
        summaryLayoutVisibility.setValue(View.GONE);
        paymentCardVisibility.setValue(View.GONE);
    }

    public void resetDashboard() {
        // Reset về trạng thái ban đầu
        currentStage.setValue(1);
        buttonText.setValue("Bắt đầu mua hàng");
        stageImageResource.setValue(R.drawable.stage1);
        muahangImageResource.setValue(R.drawable.muahang1);
        muahangImageVisibility.setValue(View.VISIBLE);
        summaryLayoutVisibility.setValue(View.GONE);
        mText.setValue("Đi đến các UIT Store để bắt đầu mua hàng");
        
        // Xóa toàn bộ node user
        if (databaseRef != null) {
            databaseRef.child("phampho1103").removeValue()
                .addOnSuccessListener(aVoid -> {
                    Log.d("Firebase", "Đã xóa thành công node user");
                })
                .addOnFailureListener(e -> {
                    Log.e("Firebase", "Lỗi khi xóa node user: " + e.getMessage());
                });
        }
    }
}