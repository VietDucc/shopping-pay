package com.example.uitpay.ui.account;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageView;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.uitpay.R;
import com.example.uitpay.databinding.FragmentAccountBinding;
import com.example.uitpay.ui.home.HomeViewModel;

public class AccountFragment extends Fragment {
    private FragmentAccountBinding binding;
    private HomeViewModel homeViewModel;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState){

        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        homeViewModel.loadUserData();

        binding = FragmentAccountBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        observeUserData();

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
                        .override(100, 100) // 👌 ảnh nhỏ, tránh lag
                        .error(R.drawable.avatar_test) // fallback nếu ảnh lỗi
                        .into(userImageView);
            } else {
                userImageView.setImageResource(R.drawable.avatar_test);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
