package com.example.uitpay.ui.cart;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.uitpay.R;
import com.google.android.material.appbar.MaterialToolbar;

public class CartActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        MaterialToolbar toolbar = findViewById(R.id.cart_toolbar);
        toolbar.setNavigationOnClickListener(v -> {
            finish();
        });
    }
}