package com.example.uitpay.ui.account;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ImageView;
import android.util.Log;
import android.content.Intent;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.uitpay.R;
import com.example.uitpay.databinding.FragmentAccountBinding;
import com.example.uitpay.ui.home.HomeViewModel;
import com.example.uitpay.ui.login.LoginActivity;
import com.example.uitpay.ui.invoice.InvoiceActivity;

import com.google.firebase.auth.FirebaseAuth;

public class AccountFragment extends Fragment {
    private FragmentAccountBinding binding;
    private HomeViewModel homeViewModel;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState){

        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentAccountBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        observeUserData();
        callHotline();
        logoutUserAccount();
        issueInvoice();

        return root;
    }

    private void observeUserData() {
        TextView nameView = binding.userName;
        ImageView userImageView = binding.userImage;

        homeViewModel.getName().observe(getViewLifecycleOwner(), nameView::setText);

        homeViewModel.getUserImage().observe(getViewLifecycleOwner(), imageUrl -> {
            if (imageUrl != null && !imageUrl.isEmpty()) {
                Log.d("AccountFragment", "Loading image URL: " + imageUrl);
                Glide.with(requireContext())
                        .load(imageUrl)
                        .centerCrop()
                        .override(100, 100) // ảnh nhỏ, tránh lag
                        .error(R.drawable.avatar_test) // fallback nếu ảnh lỗi
                        .into(userImageView);
            } else {
                userImageView.setImageResource(R.drawable.avatar_test);
            }
        });
    }
    private void callHotline() {
        LinearLayout hotlineView = binding.itemHotline;
        hotlineView.setOnClickListener(v -> {
            //Chuyển sang App call
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:0898856496"));
            startActivity(intent);
        });
    }

    private void logoutUserAccount() {
        Button logoutButton = binding.logoutButton;
        logoutButton.setOnClickListener(v -> {
            //Logout khỏi Firebase
            FirebaseAuth.getInstance().signOut();

            //Chuyển về màn hình Login
            Intent intent = new Intent(requireContext(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }

    private void issueInvoice(){
        LinearLayout invoiceView = binding.itemInvoice;
        invoiceView.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), InvoiceActivity.class);
            startActivity(intent);
        });
    }
    private void readTermsConditions(){
        
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
