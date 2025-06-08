package com.example.uitpay.ui.profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.AndroidViewModel;
import android.app.Application;
import com.google.firebase.firestore.FirebaseFirestore;
import android.util.Log;
import android.content.SharedPreferences;
import android.content.Context;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileViewModel extends AndroidViewModel {

    private static final String TAG = "ProfileViewModel";
    private static final String USER_ID = "id001";
    private static final String PREFS_NAME = "payment_prefs";
    private static final String KEY_ORDER_ID = "pending_order_id";
    private static final String KEY_PAYMENT_URL = "pending_payment_url";
    private static final String KEY_PAYMENT_AMOUNT = "pending_payment_amount";
    
    private final MutableLiveData<String> userName;
    private final MutableLiveData<String> userBalance;
    private final MutableLiveData<String> userImage;
    private final MutableLiveData<String> paymentUrl;
    private final MutableLiveData<String> currentOrderId;
    private final MutableLiveData<Long> currentPaymentAmount;
    private final MutableLiveData<Boolean> paymentSuccess;
    private final MutableLiveData<String> paymentError;
    private final FirebaseFirestore db;
    private final SharedPreferences prefs;

    public ProfileViewModel(Application application) {
        super(application);
        userName = new MutableLiveData<>();
        userBalance = new MutableLiveData<>();
        userImage = new MutableLiveData<>();
        paymentUrl = new MutableLiveData<>();
        currentOrderId = new MutableLiveData<>();
        currentPaymentAmount = new MutableLiveData<>();
        paymentSuccess = new MutableLiveData<>();
        paymentError = new MutableLiveData<>();
        db = FirebaseFirestore.getInstance();
        prefs = application.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        
        // Load pending payment data from SharedPreferences
        loadPendingPaymentData();
        loadUserData();
    }

    private void loadUserData() {
        Log.d(TAG, "Loading user data for ID: " + USER_ID);
        
        db.collection("user")
            .document(USER_ID)
            .get()
            .addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    Log.d(TAG, "User document found");
                    
                    try {
                        String name = documentSnapshot.getString("name");
                        Long sotien = documentSnapshot.getLong("sotien");
                        String userimg = documentSnapshot.getString("userimage");
                        
                        // Set user name
                        if (name != null && !name.isEmpty()) {
                            userName.setValue(name);
                        } else {
                            userName.setValue("Người dùng");
                        }
                        
                        // Set balance with formatting
                        if (sotien != null) {
                            userBalance.setValue(String.format("%,d VND", sotien));
                        } else {
                            userBalance.setValue("0 VND");
                        }
                        
                        // Set user image
                        userImage.setValue(userimg);
                        
                        Log.d(TAG, "User data loaded successfully - Name: " + name + ", Balance: " + sotien);
                        
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing user data", e);
                        setDefaultValues();
                    }
                } else {
                    Log.w(TAG, "User document not found");
                    setDefaultValues();
                }
            })
            .addOnFailureListener(e -> {
                Log.e(TAG, "Error loading user data", e);
                setDefaultValues();
            });
    }
    
    private void setDefaultValues() {
        userName.setValue("Người dùng");
        userBalance.setValue("0 VND");
        userImage.setValue(null);
    }

    public void topUpBalance(long amount) {
        Log.d(TAG, "Topping up balance: " + amount);
        
        db.collection("user")
            .document(USER_ID)
            .get()
            .addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    Long currentBalance = documentSnapshot.getLong("sotien");
                    if (currentBalance == null) currentBalance = 0L;
                    
                    final long previousBalance = currentBalance; // Make final for lambda
                    final long newBalance = currentBalance + amount;
                    
                    // Update balance in Firebase
                    db.collection("user")
                        .document(USER_ID)
                        .update("sotien", newBalance)
                        .addOnSuccessListener(aVoid -> {
                            Log.d(TAG, "=== Balance updated successfully in Firebase ===");
                            Log.d(TAG, "Previous balance: " + previousBalance);
                            Log.d(TAG, "Added amount: " + amount);
                            Log.d(TAG, "New balance: " + newBalance);
                            // Update UI
                            userBalance.setValue(String.format("%,d VND", newBalance));
                            Log.d(TAG, "UI balance updated");
                        })
                        .addOnFailureListener(e -> {
                            Log.e(TAG, "Error updating balance in Firebase", e);
                        });
                }
            })
            .addOnFailureListener(e -> {
                Log.e(TAG, "Error getting current balance", e);
            });
    }

    public LiveData<String> getUserName() {
        return userName;
    }

    public LiveData<String> getUserBalance() {
        return userBalance;
    }

    public LiveData<String> getUserImage() {
        return userImage;
    }

    public LiveData<String> getPaymentUrl() {
        return paymentUrl;
    }

    public LiveData<String> getCurrentOrderId() {
        return currentOrderId;
    }

    public LiveData<Boolean> getPaymentSuccess() {
        return paymentSuccess;
    }

    public void resetPaymentSuccess() {
        paymentSuccess.setValue(false);
    }

    public void checkPendingPayment() {
        String orderId = currentOrderId.getValue();
        Log.d(TAG, "=== Manual check pending payment called ===");
        Log.d(TAG, "Current orderId: " + orderId);
        
        if (orderId != null && !orderId.isEmpty()) {
            checkPaymentStatus(orderId);
        } else {
            Log.d(TAG, "No pending payment to check");
        }
    }

    public LiveData<String> getPaymentError() {
        return paymentError;
    }

    private void loadPendingPaymentData() {
        String savedOrderId = prefs.getString(KEY_ORDER_ID, null);
        String savedPaymentUrl = prefs.getString(KEY_PAYMENT_URL, null);
        long savedAmount = prefs.getLong(KEY_PAYMENT_AMOUNT, 0);
        
        Log.d(TAG, "=== Loading pending payment data from SharedPreferences ===");
        Log.d(TAG, "Saved OrderId: " + savedOrderId);
        Log.d(TAG, "Saved PaymentUrl: " + savedPaymentUrl);
        Log.d(TAG, "Saved Amount: " + savedAmount);
        
        if (savedOrderId != null) {
            currentOrderId.setValue(savedOrderId);
            Log.d(TAG, "Restored orderId from SharedPreferences");
        }
        
        if (savedPaymentUrl != null) {
            paymentUrl.setValue(savedPaymentUrl);
            Log.d(TAG, "Restored paymentUrl from SharedPreferences");
        }
        
        if (savedAmount > 0) {
            currentPaymentAmount.setValue(savedAmount);
            Log.d(TAG, "Restored payment amount from SharedPreferences");
        }
    }

    private void savePendingPaymentData(String orderId, String paymentUrl, long amount) {
        Log.d(TAG, "=== Saving payment data to SharedPreferences ===");
        Log.d(TAG, "Saving OrderId: " + orderId);
        Log.d(TAG, "Saving PaymentUrl: " + paymentUrl);
        Log.d(TAG, "Saving Amount: " + amount);
        
        prefs.edit()
            .putString(KEY_ORDER_ID, orderId)
            .putString(KEY_PAYMENT_URL, paymentUrl)
            .putLong(KEY_PAYMENT_AMOUNT, amount)
            .apply();
        
        Log.d(TAG, "Payment data saved to SharedPreferences");
    }

    private void clearPendingPaymentData() {
        Log.d(TAG, "=== Clearing payment data from SharedPreferences ===");
        
        prefs.edit()
            .remove(KEY_ORDER_ID)
            .remove(KEY_PAYMENT_URL)
            .remove(KEY_PAYMENT_AMOUNT)
            .apply();
        
        Log.d(TAG, "Payment data cleared from SharedPreferences");
    }

    public void createPaymentOrder(long amount) {
        Log.d(TAG, "Creating payment order for amount: " + amount);
        
        String orderInfo = "Nạp tiền vào ví UIT Shopping - " + String.format("%,d VND", amount);
        PaymentRequest request = new PaymentRequest(amount, orderInfo);
        
        PaymentClient.getInstance().getPaymentService().createPaymentOrder(request)
            .enqueue(new Callback<PaymentResponse>() {
                @Override
                public void onResponse(Call<PaymentResponse> call, Response<PaymentResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        PaymentResponse paymentResponse = response.body();
                        Log.d(TAG, "=== Payment order created successfully ===");
                        Log.d(TAG, "OrderId: " + paymentResponse.getOrderId());
                        Log.d(TAG, "PaymentUrl: " + paymentResponse.getPaymentUrl());
                        
                        currentOrderId.setValue(paymentResponse.getOrderId());
                        paymentUrl.setValue(paymentResponse.getPaymentUrl());
                        currentPaymentAmount.setValue(amount);
                        
                        // Save to SharedPreferences for persistence
                        savePendingPaymentData(paymentResponse.getOrderId(), paymentResponse.getPaymentUrl(), amount);
                        
                        Log.d(TAG, "OrderId, PaymentUrl and Amount set in LiveData and saved to SharedPreferences");
                    } else {
                        Log.e(TAG, "Failed to create payment order: " + response.code());
                    }
                }
                
                @Override
                public void onFailure(Call<PaymentResponse> call, Throwable t) {
                    Log.e(TAG, "Error creating payment order", t);
                }
            });
    }

    public void checkPaymentStatus(String orderId) {
        Log.d(TAG, "Checking payment status for order: " + orderId);
        
        PaymentClient.getInstance().getPaymentService().getPaymentStatus(orderId)
            .enqueue(new Callback<PaymentStatusResponse>() {
                @Override
                public void onResponse(Call<PaymentStatusResponse> call, Response<PaymentStatusResponse> response) {
                    Log.d(TAG, "Payment status API response code: " + response.code());
                    
                    if (response.isSuccessful() && response.body() != null) {
                        PaymentStatusResponse statusResponse = response.body();
                        Log.d(TAG, "Payment status response - Status: " + statusResponse.getStatus() + 
                              ", OrderId: " + statusResponse.getOrderId() + 
                              ", Amount: " + statusResponse.getAmount());
                        
                        if (statusResponse.isPaid()) {
                            Log.d(TAG, "Payment is PAID, processing...");
                            // Payment successful, update user balance
                            Long amount = currentPaymentAmount.getValue();
                            if (amount != null && amount > 0) {
                                topUpBalance(amount);
                                
                                // Clear current order ID and trigger success notification
                                currentOrderId.setValue(null);
                                paymentUrl.setValue(null);
                                currentPaymentAmount.setValue(null);
                                paymentSuccess.setValue(true);
                                
                                // Clear from SharedPreferences
                                clearPendingPaymentData();
                                
                                Log.d(TAG, "Payment successful, balance updated with amount: " + amount);
                            } else {
                                Log.w(TAG, "Payment successful but amount is missing or invalid: " + amount);
                                // Still clear the pending payment data
                                currentOrderId.setValue(null);
                                paymentUrl.setValue(null);
                                currentPaymentAmount.setValue(null);
                                clearPendingPaymentData();
                                paymentError.setValue("Thanh toán thành công nhưng không thể cập nhật số dư");
                            }
                        } else {
                            Log.d(TAG, "Payment status is: " + statusResponse.getStatus() + " (not PAID)");
                        }
                    } else {
                        String errorMsg = "Lỗi kiểm tra thanh toán: " + response.code();
                        Log.e(TAG, "Failed to check payment status: " + response.code());
                        try {
                            String errorBody = response.errorBody() != null ? response.errorBody().string() : "No error body";
                            Log.e(TAG, "Error body: " + errorBody);
                            errorMsg += " - " + errorBody;
                        } catch (Exception e) {
                            Log.e(TAG, "Error reading error body", e);
                        }
                        paymentError.setValue(errorMsg);
                    }
                }
                
                @Override
                public void onFailure(Call<PaymentStatusResponse> call, Throwable t) {
                    String errorMsg = "Lỗi kết nối kiểm tra thanh toán: " + t.getMessage();
                    Log.e(TAG, "Error checking payment status", t);
                    paymentError.setValue(errorMsg);
                }
            });
    }
} 