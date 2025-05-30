package com.example.uitpay.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.uitpay.model.Post;
import com.example.uitpay.model.Banner;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;
import android.util.Log;

public class HomeViewModel extends ViewModel {
    private static final String TAG = "HomeViewModel";
    private final MutableLiveData<List<Post>> posts;
    private final MutableLiveData<String> mText;
    private final MutableLiveData<String> mName;
    private final MutableLiveData<String> mUserImage;
    private final MutableLiveData<List<Banner>> banners;
    private final FirebaseFirestore db;

    public HomeViewModel() {
        Log.d(TAG, "HomeViewModel initialized");
        posts = new MutableLiveData<>(new ArrayList<>());
        mText = new MutableLiveData<>();
        mName = new MutableLiveData<>();
        mUserImage = new MutableLiveData<>();
        banners = new MutableLiveData<>(new ArrayList<>());
        db = FirebaseFirestore.getInstance();
        
        loadUserData();
        loadPosts();
        loadBanners();
    }

    private void loadUserData() {
        Log.d(TAG, "Starting to load user data");
        if (db == null) {
            Log.e(TAG, "FirebaseFirestore instance is null");
            return;
        }

        db.collection("user")
            .document("id001")
            .get()
            .addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    Log.d(TAG, "User document exists");
                    try {
                        Long sotien = documentSnapshot.getLong("sotien");
                        String name = documentSnapshot.getString("name");
                        String userImage = documentSnapshot.getString("userimage");
                        
                        Log.d(TAG, "Raw user data - sotien: " + sotien + ", name: " + name + ", userimage: " + userImage);
                        
                        // Null checks
                        if (sotien == null) {
                            Log.w(TAG, "sotien is null, using default value 0");
                            sotien = 0L;
                        }
                        if (name == null || name.isEmpty()) {
                            Log.w(TAG, "name is null or empty, using default value");
                            name = "Người dùng";
                        }
                        
                        mText.setValue(sotien + " VND");
                        mName.setValue("Xin chào, " + name);
                        mUserImage.setValue(userImage); // Có thể null, Glide sẽ xử lý
                        
                        Log.d(TAG, "User data set successfully");
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing user data", e);
                        setDefaultValues();
                    }
                } else {
                    Log.e(TAG, "User document does not exist");
                    setDefaultValues();
                }
            })
            .addOnFailureListener(e -> {
                Log.e(TAG, "Error loading user data: " + e.getMessage(), e);
                setDefaultValues();
            });
    }

    private void setDefaultValues() {
        mText.setValue("Số tiền hiện tại: 0 VND");
        mName.setValue("Xin chào, Người dùng");
        mUserImage.setValue(null);
    }

    private void loadPosts() {
        Log.d(TAG, "Starting to load posts");
        if (db == null) {
            Log.e(TAG, "FirebaseFirestore instance is null");
            return;
        }

        db.collection("post")
            .get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                if (queryDocumentSnapshots == null) {
                    Log.e(TAG, "queryDocumentSnapshots is null");
                    posts.setValue(new ArrayList<>());
                    return;
                }

                List<Post> postList = new ArrayList<>();
                Log.d(TAG, "Number of posts found: " + queryDocumentSnapshots.size());
                
                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    try {
                        Log.d(TAG, "Processing document: " + document.getId());
                        Log.d(TAG, "Document data: " + document.getData().toString());
                        
                        String id = document.getId();
                        String title = document.getString("title");
                        String content = document.getString("content");
                        String imageUrl = document.getString("imageUrl");
                        // Lấy timestamp và chuyển thành string
                        String date = document.getTimestamp("date") != null ? 
                            document.getTimestamp("date").toDate().toString() : "";

                        if (title == null) {
                            Log.e(TAG, "Title is null for document: " + id);
                            continue;
                        }

                        Post post = new Post(id, title, imageUrl, content, date);
                        postList.add(post);
                        Log.d(TAG, "Post processed successfully - ID: " + id + ", Title: " + title);
                    } catch (Exception e) {
                        Log.e(TAG, "Error processing post document: " + document.getId(), e);
                    }
                }
                
                posts.setValue(postList);
                Log.d(TAG, "Total posts loaded: " + postList.size());
            })
            .addOnFailureListener(e -> {
                Log.e(TAG, "Error loading posts: " + e.getMessage(), e);
                posts.setValue(new ArrayList<>());
            });
    }

    private void loadBanners() {
        Log.d(TAG, "Starting to load banners");
        if (db == null) {
            Log.e(TAG, "FirebaseFirestore instance is null");
            return;
        }

        db.collection("banner")
            .get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                if (queryDocumentSnapshots == null) {
                    Log.e(TAG, "queryDocumentSnapshots is null");
                    banners.setValue(new ArrayList<>());
                    return;
                }

                List<Banner> bannerList = new ArrayList<>();
                Log.d(TAG, "Number of banners found: " + queryDocumentSnapshots.size());
                
                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    try {
                        String id = document.getId();
                        String imageUrl = document.getString("imageUrl");
                        
                        if (imageUrl == null || imageUrl.isEmpty()) {
                            Log.e(TAG, "ImageUrl is null or empty for banner: " + id);
                            continue;
                        }

                        Banner banner = new Banner(id, imageUrl);
                        bannerList.add(banner);
                        Log.d(TAG, "Banner processed successfully - ID: " + id);
                    } catch (Exception e) {
                        Log.e(TAG, "Error processing banner document: " + document.getId(), e);
                    }
                }
                
                banners.setValue(bannerList);
                Log.d(TAG, "Total banners loaded: " + bannerList.size());
            })
            .addOnFailureListener(e -> {
                Log.e(TAG, "Error loading banners: " + e.getMessage(), e);
                banners.setValue(new ArrayList<>());
            });
    }

    public LiveData<List<Post>> getPosts() {
        return posts;
    }

    public LiveData<String> getText() {
        return mText;
    }

    public LiveData<String> getName() {
        return mName;
    }

    public LiveData<String> getUserImage() {
        return mUserImage;
    }

    public LiveData<List<Banner>> getBanners() {
        return banners;
    }
}