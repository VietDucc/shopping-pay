package com.example.uitpay.ui.start;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.uitpay.R;
import com.example.uitpay.ui.login.LoginActivity;
import com.example.uitpay.ui.signup.UserSignUpActivity;

public class Start extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        Button login = findViewById(R.id.login_start_button);
        Button signupUser = findViewById(R.id.signup_user_button);
        ImageView logoImage = findViewById(R.id.logo_image);

        // Thêm animation cho logo
        addLogoAnimation(logoImage);

        // Thêm hiệu ứng lightsweep cho các button
        addLightSweepAnimation(login);
        addLightSweepAnimation(signupUser);

        login.setOnClickListener(v ->
                startActivity(new Intent(Start.this, LoginActivity.class))
        );
        signupUser.setOnClickListener(v ->
                startActivity(new Intent(Start.this, UserSignUpActivity.class))
        );
    }

    private void addLightSweepAnimation(Button button) {
        // Tạo hiệu ứng alpha animation cho lightsweep
        ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(button, "alpha", 0.7f, 1.0f, 0.7f);
        alphaAnimator.setDuration(2000);
        alphaAnimator.setRepeatCount(ValueAnimator.INFINITE);
        alphaAnimator.setRepeatMode(ValueAnimator.REVERSE);
        alphaAnimator.start();

        // Tạo hiệu ứng scale nhẹ
        ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(button, "scaleX", 1.0f, 1.02f, 1.0f);
        ObjectAnimator scaleYAnimator = ObjectAnimator.ofFloat(button, "scaleY", 1.0f, 1.02f, 1.0f);
        scaleXAnimator.setDuration(2000);
        scaleYAnimator.setDuration(2000);
        scaleXAnimator.setRepeatCount(ValueAnimator.INFINITE);
        scaleYAnimator.setRepeatCount(ValueAnimator.INFINITE);
        scaleXAnimator.setRepeatMode(ValueAnimator.REVERSE);
        scaleYAnimator.setRepeatMode(ValueAnimator.REVERSE);
        scaleXAnimator.start();
        scaleYAnimator.start();
    }

    private void addLogoAnimation(ImageView logo) {
        // Tạo hiệu ứng fade nhẹ cho logo
        ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(logo, "alpha", 0.8f, 1.0f, 0.8f);
        alphaAnimator.setDuration(4000);
        alphaAnimator.setRepeatCount(ValueAnimator.INFINITE);
        alphaAnimator.setRepeatMode(ValueAnimator.REVERSE);
        alphaAnimator.start();
    }
}
