package com.example.uitpay.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.uitpay.R;
import com.example.uitpay.adapter.PostAdapter;
import com.example.uitpay.adapter.BannerAdapter;
import com.example.uitpay.adapter.ShopAdapter;
import com.example.uitpay.databinding.FragmentHomeBinding;
import com.example.uitpay.model.Post;
import android.util.Log;
import android.os.Handler;
import androidx.viewpager2.widget.ViewPager2;
import android.widget.PopupMenu;

public class HomeFragment extends Fragment implements PostAdapter.OnPostClickListener {
    private static final String TAG = "HomeFragment";
    private FragmentHomeBinding binding;
    private HomeViewModel homeViewModel;
    private PostAdapter postAdapter;
    private BannerAdapter bannerAdapter;
    private ShopAdapter shopAdapter;
    private Handler sliderHandler = new Handler();
    private Runnable sliderRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                ViewPager2 viewPager = binding.viewPagerBanner;
                if (viewPager != null && bannerAdapter != null && bannerAdapter.getItemCount() > 0) {
                    int nextItem = (viewPager.getCurrentItem() + 1) % bannerAdapter.getItemCount();
                    viewPager.setCurrentItem(nextItem);
                }
                sliderHandler.postDelayed(this, 5000);
            } catch (Exception e) {
                Log.e(TAG, "Error in slider runnable: ", e);
            }
        }
    };

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: Starting fragment creation");
        try {
            homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
            Log.d(TAG, "onCreateView: ViewModel initialized");

            binding = FragmentHomeBinding.inflate(inflater, container, false);
            View root = binding.getRoot();
            Log.d(TAG, "onCreateView: Binding initialized");

            // Khởi tạo Banner Slider
            try {
                ViewPager2 viewPagerBanner = binding.viewPagerBanner;
                bannerAdapter = new BannerAdapter();
                viewPagerBanner.setAdapter(bannerAdapter);
                Log.d(TAG, "onCreateView: Banner ViewPager initialized");
            } catch (Exception e) {
                Log.e(TAG, "Error initializing banner: ", e);
            }
            
            // Observe banners
            homeViewModel.getBanners().observe(getViewLifecycleOwner(), banners -> {
                try {
                    if (banners != null) {
                        Log.d(TAG, "Updating banners in adapter: " + banners.size() + " items");
                        bannerAdapter.setBanners(banners);
                        if (banners.size() > 0) {
                            startAutoSlide();
                        }
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error updating banners: ", e);
                }
            });

            // Khởi tạo views cho thông tin user
            try {
                final TextView textView = binding.textHome;
                final TextView nameView = binding.textName;
                final ImageView userImageView = binding.userImage;
                Log.d(TAG, "onCreateView: User views initialized");

                // Observe thông tin user
                homeViewModel.getText().observe(getViewLifecycleOwner(), text -> {
                    try {
                        textView.setText(text);
                    } catch (Exception e) {
                        Log.e(TAG, "Error setting text: ", e);
                    }
                });
                
                homeViewModel.getName().observe(getViewLifecycleOwner(), name -> {
                    try {
                        nameView.setText(name);
                    } catch (Exception e) {
                        Log.e(TAG, "Error setting name: ", e);
                    }
                });
                
                homeViewModel.getUserImage().observe(getViewLifecycleOwner(), imageUrl -> {
                    try {
                        if (imageUrl != null && !imageUrl.isEmpty()) {
                            Glide.with(requireContext())
                                .load(imageUrl)
                                .centerCrop()
                                .into(userImageView);
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error loading user image: ", e);
                    }
                });
            } catch (Exception e) {
                Log.e(TAG, "Error initializing user views: ", e);
            }

            // Xử lý nút filter
            try {
                binding.filterButton.setOnClickListener(v -> {
                    try {
                        PopupMenu popup = new PopupMenu(requireContext(), v);
                        popup.getMenu().add("Trong tuần");
                        popup.getMenu().add("Trong tháng");
                        
                        popup.setOnMenuItemClickListener(item -> {
                            binding.filterText.setText(item.getTitle());
                            return true;
                        });
                        
                        popup.show();
                    } catch (Exception e) {
                        Log.e(TAG, "Error showing filter popup: ", e);
                    }
                });
                Log.d(TAG, "onCreateView: Filter button initialized");
            } catch (Exception e) {
                Log.e(TAG, "Error setting up filter button: ", e);
            }

            // Khởi tạo ViewPager cho bài viết
            try {
                ViewPager2 viewPagerPosts = binding.viewPagerPosts;
                postAdapter = new PostAdapter(this);
                viewPagerPosts.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);
                viewPagerPosts.setAdapter(postAdapter);
                Log.d(TAG, "onCreateView: Posts ViewPager initialized");

                // Observe bài viết
                homeViewModel.getPosts().observe(getViewLifecycleOwner(), posts -> {
                    try {
                        if (posts != null) {
                            Log.d(TAG, "Updating posts in adapter: " + posts.size() + " items");
                            postAdapter.setPosts(posts);
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error updating posts: ", e);
                    }
                });
            } catch (Exception e) {
                Log.e(TAG, "Error initializing posts ViewPager: ", e);
            }

            // Khởi tạo RecyclerView cho cửa hàng
            try {
                RecyclerView recyclerViewStores = binding.recyclerViewStores;
                shopAdapter = new ShopAdapter();
                recyclerViewStores.setLayoutManager(new LinearLayoutManager(getContext()));
                recyclerViewStores.setAdapter(shopAdapter);
                Log.d(TAG, "onCreateView: Shops RecyclerView initialized");

                // Observe cửa hàng
                homeViewModel.getShops().observe(getViewLifecycleOwner(), shops -> {
                    try {
                        if (shops != null) {
                            Log.d(TAG, "Updating shops in adapter: " + shops.size() + " items");
                            shopAdapter.setShops(shops);
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error updating shops: ", e);
                    }
                });
            } catch (Exception e) {
                Log.e(TAG, "Error initializing shops RecyclerView: ", e);
            }

            Log.d(TAG, "onCreateView: Fragment creation completed successfully");
            return root;
            
        } catch (Exception e) {
            Log.e(TAG, "Fatal error in onCreateView: ", e);
            throw e;
        }
    }

    private void startAutoSlide() {
        try {
            sliderHandler.postDelayed(sliderRunnable, 5000);
            Log.d(TAG, "Auto slide started");
        } catch (Exception e) {
            Log.e(TAG, "Error starting auto slide: ", e);
        }
    }

    private void stopAutoSlide() {
        try {
            sliderHandler.removeCallbacks(sliderRunnable);
            Log.d(TAG, "Auto slide stopped");
        } catch (Exception e) {
            Log.e(TAG, "Error stopping auto slide: ", e);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        stopAutoSlide();
        Log.d(TAG, "onPause called");
    }

    @Override
    public void onResume() {
        super.onResume();
        startAutoSlide();
        Log.d(TAG, "onResume called");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        stopAutoSlide();
        binding = null;
        Log.d(TAG, "onDestroyView called");
    }

    @Override
    public void onPostClick(Post post) {
        try {
            Log.d(TAG, "Post clicked: " + post.getTitle());
            
            // Tạo dialog bình thường với style hiện đại
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            View dialogView = getLayoutInflater().inflate(R.layout.dialog_post_detail, null);
            builder.setView(dialogView);

            // Tìm các view trong layout
            TextView titleTextView = dialogView.findViewById(R.id.textView_detail_title);
            ImageView imageView = dialogView.findViewById(R.id.imageView_detail_post);
            TextView dateTextView = dialogView.findViewById(R.id.textView_date);
            TextView contentTextView = dialogView.findViewById(R.id.textView_content);
            ImageView closeButton = dialogView.findViewById(R.id.close_button);

            // Set dữ liệu
            titleTextView.setText(post.getTitle());
            dateTextView.setText(post.getDate());
            contentTextView.setText(post.getContent());

            // Load hình ảnh
            if (post.getImageUrl() != null && !post.getImageUrl().isEmpty()) {
                Glide.with(requireContext())
                    .load(post.getImageUrl())
                    .centerCrop()
                    .into(imageView);
            }

            AlertDialog dialog = builder.create();
            
            // Xử lý nút đóng
            closeButton.setOnClickListener(v -> dialog.dismiss());
            
            // Hiển thị dialog với kích thước lớn nhưng không fullscreen
            dialog.show();
            
            // Tùy chỉnh window để dialog lớn hơn và có background trong suốt để hiển thị bo góc
            if (dialog.getWindow() != null) {
                dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                dialog.getWindow().setLayout(
                    (int) (getResources().getDisplayMetrics().widthPixels * 0.96), 
                    (int) (getResources().getDisplayMetrics().heightPixels * 0.93)
                );
            }
            
        } catch (Exception e) {
            Log.e(TAG, "Error showing post detail dialog: ", e);
        }
    }
}