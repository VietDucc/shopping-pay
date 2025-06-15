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
import com.bumptech.glide.Glide;
import com.example.uitpay.R;
import com.example.uitpay.adapter.PostAdapter;
import com.example.uitpay.adapter.BannerAdapter;
import com.example.uitpay.databinding.FragmentHomeBinding;
import com.example.uitpay.model.Post;
import com.example.uitpay.model.Shop;
import android.util.Log;
import android.os.Handler;
import androidx.viewpager2.widget.ViewPager2;
import android.widget.PopupMenu;
import android.content.Intent;
import android.net.Uri;

public class HomeFragment extends Fragment implements PostAdapter.OnPostClickListener {
    private static final String TAG = "HomeFragment";
    private FragmentHomeBinding binding;
    private HomeViewModel homeViewModel;
    private PostAdapter postAdapter;
    private BannerAdapter bannerAdapter;
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

            // Khởi tạo cửa hàng với layout riêng biệt
            try {
                // Observe danh sách shops và hiển thị d liệu
                homeViewModel.getShops().observe(getViewLifecycleOwner(), shops -> {
                    try {
                        if (shops != null && shops.size() > 0) {
                            Log.d(TAG, "Updating individual shop layouts: " + shops.size() + " shops");
                            
                            // Hiển thị shop đầu tiên
                            if (shops.size() > 0) {
                                populateShopData(binding.storeUit1.getRoot(), shops.get(0));
                            }
                            // Hiển thị shop thứ hai
                            if (shops.size() > 1) {
                                populateShopData(binding.storeUit2.getRoot(), shops.get(1));
                            }
                            // Hiển thị shop thứ ba
                            if (shops.size() > 2) {
                                populateShopData(binding.storeUit3.getRoot(), shops.get(2));
                            }
                            
                            // Ẩn các shop không có data
                            if (shops.size() < 3) {
                                binding.storeUit3.getRoot().setVisibility(View.GONE);
                            }
                            if (shops.size() < 2) {
                                binding.storeUit2.getRoot().setVisibility(View.GONE);
                            }
                            if (shops.size() < 1) {
                                binding.storeUit1.getRoot().setVisibility(View.GONE);
                            }
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error updating shop layouts: ", e);
                    }
                });
                
                Log.d(TAG, "onCreateView: Individual shop layouts initialized");
            } catch (Exception e) {
                Log.e(TAG, "Error initializing shop layouts: ", e);
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

    private void populateShopData(View shopView, Shop shop) {
        try {
            // Tìm các view trong shop layout
            ImageView imageShop = shopView.findViewById(R.id.imageShop);
            TextView textShopName = shopView.findViewById(R.id.textShopName);
            TextView textShopAddress = shopView.findViewById(R.id.textShopAddress);
            View layoutMapButton = shopView.findViewById(R.id.layoutMapButton);

            // Set dữ liệu
            if (textShopName != null) {
                textShopName.setText(shop.getName());
            }
            if (textShopAddress != null) {
                textShopAddress.setText(shop.getAddress());
            }

            // Load hình ảnh
            if (imageShop != null && shop.getImageUrl() != null && !shop.getImageUrl().isEmpty()) {
                Glide.with(requireContext())
                    .load(shop.getImageUrl())
                    .centerCrop()
                    .placeholder(R.drawable.logo_uit) // placeholder nếu không load được
                    .into(imageShop);
            }

            // Set click listener cho map button
            if (layoutMapButton != null) {
                layoutMapButton.setOnClickListener(v -> {
                    try {
                        Log.d(TAG, "Map button clicked for shop: " + shop.getName());
                        openMap(shop);
                    } catch (Exception e) {
                        Log.e(TAG, "Error handling map button click: ", e);
                    }
                });
            }

            Log.d(TAG, "Shop data populated successfully: " + shop.getName());
        } catch (Exception e) {
            Log.e(TAG, "Error populating shop data: ", e);
        }
    }

    private void openMap(Shop shop) {
        try {
            String mapUrl = shop.getMapUrl();
            
            if (mapUrl != null && !mapUrl.isEmpty()) {
                // Nếu có URL map từ database, sử dụng URL đó
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(mapUrl));
                intent.setPackage("com.google.android.apps.maps"); // Ưu tiên Google Maps
                
                // Kiểm tra xem có Google Maps app không
                if (intent.resolveActivity(requireActivity().getPackageManager()) != null) {
                    startActivity(intent);
                } else {
                    // Nếu không có Google Maps app, mở bằng browser
                    intent.setPackage(null);
                    startActivity(intent);
                }
                
                Log.d(TAG, "Opened map with URL: " + mapUrl);
            } else {
                // Nếu không có URL, tạo Google Maps search query từ tên và địa chỉ
                String query = shop.getName();
                if (shop.getAddress() != null && !shop.getAddress().isEmpty()) {
                    query += " " + shop.getAddress();
                }
                
                // Tạo URI cho Google Maps search
                String encodedQuery = Uri.encode(query);
                String geoUri = "geo:0,0?q=" + encodedQuery;
                
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(geoUri));
                intent.setPackage("com.google.android.apps.maps");
                
                if (intent.resolveActivity(requireActivity().getPackageManager()) != null) {
                    startActivity(intent);
                } else {
                    // Fallback: mở Google Maps web
                    String webUrl = "https://maps.google.com/?q=" + encodedQuery;
                    Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(webUrl));
                    startActivity(webIntent);
                }
                
                Log.d(TAG, "Opened map with search query: " + query);
            }
            
        } catch (Exception e) {
            Log.e(TAG, "Error opening map: ", e);
            // Hiển thị thông báo lỗi cho user nếu cần
        }
    }
}