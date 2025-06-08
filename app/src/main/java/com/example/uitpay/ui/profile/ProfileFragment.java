package com.example.uitpay.ui.profile;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.bumptech.glide.Glide;
import com.example.uitpay.ui.login.LoginActivity;
import com.example.uitpay.R;
import com.example.uitpay.databinding.FragmentProfileBinding;
import com.google.android.material.textfield.TextInputEditText;
import android.util.Log;

public class ProfileFragment extends Fragment {

    private static final String TAG = "ProfileFragment";
    private FragmentProfileBinding binding;
    private ProfileViewModel profileViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        profileViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Observe user data
        observeUserData();
        
        // Setup click listeners
        setupClickListeners();

        return root;
    }

    private void observeUserData() {
        // Observe user name
        profileViewModel.getUserName().observe(getViewLifecycleOwner(), name -> {
            if (name != null) {
                binding.userName.setText(name);
            }
        });

        // Observe user balance
        profileViewModel.getUserBalance().observe(getViewLifecycleOwner(), balance -> {
            if (balance != null) {
                binding.userBalance.setText(balance);
            }
        });

        // Observe user image
        profileViewModel.getUserImage().observe(getViewLifecycleOwner(), imageUrl -> {
            if (imageUrl != null && !imageUrl.isEmpty()) {
                Glide.with(requireContext())
                    .load(imageUrl)
                    .centerCrop()
                    .placeholder(R.drawable.ic_person_profile)
                    .error(R.drawable.ic_person_profile)
                    .into(binding.userAvatar);
            } else {
                binding.userAvatar.setImageResource(R.drawable.ic_person_profile);
            }
        });

        // Observe payment URL
        profileViewModel.getPaymentUrl().observe(getViewLifecycleOwner(), paymentUrl -> {
            Log.d(TAG, "PaymentURL observer triggered: " + paymentUrl);
            if (paymentUrl != null && !paymentUrl.isEmpty()) {
                Log.d(TAG, "Opening payment URL: " + paymentUrl);
                openPaymentUrl(paymentUrl);
            }
        });

        // Observe payment success
        profileViewModel.getPaymentSuccess().observe(getViewLifecycleOwner(), success -> {
            if (success != null && success) {
                Toast.makeText(requireContext(), "🎉 Nạp tiền thành công! Số dư đã được cập nhật.", Toast.LENGTH_LONG).show();
                // Reset the success flag
                profileViewModel.resetPaymentSuccess();
            }
        });

        // Observe payment errors
        profileViewModel.getPaymentError().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(requireContext(), "❌ " + error, Toast.LENGTH_LONG).show();
                Log.e(TAG, "Payment error: " + error);
            }
        });

        // Observe orderId changes for debugging
        profileViewModel.getCurrentOrderId().observe(getViewLifecycleOwner(), orderId -> {
            Log.d(TAG, "OrderId observer triggered: " + orderId);
        });
    }

    private void setupClickListeners() {
        // Top up button - simplified
        binding.topUpButton.setOnClickListener(v -> showTopUpDialog());
        
        // Add long click for manual payment check (for debugging)
        binding.topUpButton.setOnLongClickListener(v -> {
            String orderId = profileViewModel.getCurrentOrderId().getValue();
            if (orderId != null && !orderId.isEmpty()) {
                Log.d(TAG, "Manual payment check triggered via long click");
                Toast.makeText(requireContext(), "Kiểm tra thanh toán thủ công...", Toast.LENGTH_SHORT).show();
                profileViewModel.checkPaymentStatus(orderId);
            } else {
                Toast.makeText(requireContext(), "Không có orderId để kiểm tra", Toast.LENGTH_SHORT).show();
            }
            return true;
        });
        
        // Invoice card
        binding.invoiceCard.setOnClickListener(v -> {
            // Navigate to invoice list screen
            Intent intent = new Intent(requireContext(), com.example.uitpay.ui.invoice.InvoiceListActivity.class);
            startActivity(intent);
        });
        
        // Call center card
        binding.callCenterCard.setOnClickListener(v -> {
            // TODO: Make a call or show call options
            Toast.makeText(requireContext(), "Tính năng gọi tổng đài sẽ được cập nhật", Toast.LENGTH_SHORT).show();
        });
        
        // Terms card
        binding.termsCard.setOnClickListener(v -> {
            // TODO: Navigate to terms screen
            Toast.makeText(requireContext(), "Tính năng điều khoản sẽ được cập nhật", Toast.LENGTH_SHORT).show();
        });
        
        // Logout button
        binding.logoutButton.setOnClickListener(v -> showLogoutDialog());
    }

    private void showTopUpDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_top_up, null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();
        
        // Make dialog background transparent for rounded corners
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        // Get views from dialog
        TextInputEditText editTextAmount = dialogView.findViewById(R.id.editTextAmount);
        View amount50k = dialogView.findViewById(R.id.amount50k);
        View amount100k = dialogView.findViewById(R.id.amount100k);
        View amount200k = dialogView.findViewById(R.id.amount200k);
        View btnCancel = dialogView.findViewById(R.id.btnCancel);
        View btnTopUp = dialogView.findViewById(R.id.btnTopUp);

        // Quick amount buttons
        amount50k.setOnClickListener(v -> editTextAmount.setText("50000"));
        amount100k.setOnClickListener(v -> editTextAmount.setText("100000"));
        amount200k.setOnClickListener(v -> editTextAmount.setText("200000"));

        // Cancel button
        btnCancel.setOnClickListener(v -> dialog.dismiss());

        // Top up button
        btnTopUp.setOnClickListener(v -> {
            String amountStr = editTextAmount.getText().toString().trim();
            if (TextUtils.isEmpty(amountStr)) {
                Toast.makeText(requireContext(), "Vui lòng nhập số tiền", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                long amount = Long.parseLong(amountStr);
                if (amount <= 0) {
                    Toast.makeText(requireContext(), "Số tiền phải lớn hơn 0", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Create payment order via API
                profileViewModel.createPaymentOrder(amount);
                Toast.makeText(requireContext(), "Đang tạo đơn thanh toán...", Toast.LENGTH_SHORT).show();
                dialog.dismiss();

            } catch (NumberFormatException e) {
                Toast.makeText(requireContext(), "Số tiền không hợp lệ", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }

    private void showLogoutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Đăng xuất")
                .setMessage("Bạn có chắc chắn muốn đăng xuất?")
                .setPositiveButton("Đăng xuất", (dialog, which) -> {
                    logout();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void logout() {
        try {
            // Clear any user session data here if needed
            Log.d(TAG, "User logged out");
            
            // Navigate back to login screen
            Intent intent = new Intent(requireContext(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            
            // Finish current activity
            if (getActivity() != null) {
                getActivity().finish();
            }
            
        } catch (Exception e) {
            Log.e(TAG, "Error during logout", e);
            Toast.makeText(requireContext(), "Lỗi khi đăng xuất", Toast.LENGTH_SHORT).show();
        }
    }

    private void openPaymentUrl(String paymentUrl) {
        try {
            Log.d(TAG, "=== Opening payment URL ===");
            Log.d(TAG, "URL: " + paymentUrl);
            
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(paymentUrl));
            startActivity(intent);
            
            Log.d(TAG, "Browser launched, waiting for user to return...");
            
            // Show dialog to check payment status after user returns
            // Commented out for now to test auto-check in onResume
            // showPaymentStatusDialog();
            
        } catch (Exception e) {
            Log.e(TAG, "Error opening payment URL", e);
            Toast.makeText(requireContext(), "Không thể mở trang thanh toán", Toast.LENGTH_SHORT).show();
        }
    }

    private void showPaymentStatusDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Thanh toán")
                .setMessage("Bạn đã hoàn thành thanh toán chưa?")
                .setPositiveButton("Đã thanh toán", (dialog, which) -> {
                    checkPaymentStatus();
                })
                .setNeutralButton("Test Status", (dialog, which) -> {
                    // Test với orderId giả để debug
                    String testOrderId = "test-order-123";
                    Log.d(TAG, "Testing payment status with orderId: " + testOrderId);
                    profileViewModel.checkPaymentStatus(testOrderId);
                    Toast.makeText(requireContext(), "Đang test kiểm tra status...", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void checkPaymentStatus() {
        String orderId = profileViewModel.getCurrentOrderId().getValue();
        Log.d(TAG, "checkPaymentStatus called with orderId: " + orderId);
        
        if (orderId != null && !orderId.isEmpty()) {
            profileViewModel.checkPaymentStatus(orderId);
            Toast.makeText(requireContext(), "Đang kiểm tra trạng thái thanh toán...", Toast.LENGTH_SHORT).show();
        } else {
            Log.w(TAG, "No orderId found for payment status check");
            Toast.makeText(requireContext(), "Không tìm thấy mã đơn hàng để kiểm tra", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "=== ProfileFragment onResume() called ===");
        
        // Check if there's a pending payment when user returns to the app
        String orderId = profileViewModel.getCurrentOrderId().getValue();
        String paymentUrl = profileViewModel.getPaymentUrl().getValue();
        
        Log.d(TAG, "onResume - orderId: " + orderId);
        Log.d(TAG, "onResume - paymentUrl: " + paymentUrl);
        
        if (orderId != null && !orderId.isEmpty()) {
            Log.d(TAG, "Found pending orderId, auto-checking payment status...");
            // Auto check payment status when user returns
            profileViewModel.checkPendingPayment();
            Toast.makeText(requireContext(), "Đang kiểm tra trạng thái thanh toán tự động...", Toast.LENGTH_SHORT).show();
        } else {
            Log.d(TAG, "No pending orderId found in onResume, trying manual check...");
            // Try to check anyway in case SharedPreferences has data
            profileViewModel.checkPendingPayment();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "=== ProfileFragment onPause() called ===");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "=== ProfileFragment onStop() called ===");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "=== ProfileFragment onStart() called ===");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "=== ProfileFragment onDestroyView() called ===");
        binding = null;
    }
} 