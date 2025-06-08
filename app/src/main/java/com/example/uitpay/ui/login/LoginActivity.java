package com.example.uitpay.ui.login;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import com.example.uitpay.MainActivity;
import com.example.uitpay.R;
import com.example.uitpay.ui.start.Start;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText emailTextView, passwordTextView;
    private Button button;
    private ImageButton backButton;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize FirebaseAuth instance
        auth = FirebaseAuth.getInstance();

        emailTextView = findViewById(R.id.email_edit_text);
        passwordTextView = findViewById(R.id.password_edit_text);
        button = findViewById(R.id.login_button);
        backButton = findViewById(R.id.back_button);

        // Thêm hiệu ứng lightsweep cho button login
        addLightSweepAnimation(button);

        button.setOnClickListener(v -> loginUserAccount());
        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, Start.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });
    }

    private void loginUserAccount() {
        String email = emailTextView.getText().toString().trim();
        String password = passwordTextView.getText().toString().trim();

        // Validate input
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter credentials", Toast.LENGTH_LONG).show();
            return;
        }

        // Login existing user
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Login successful
                            Toast.makeText(LoginActivity.this, "Login successful!!", Toast.LENGTH_LONG).show();
                            
                            // Chuyển sang MainActivity với flags mới
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        } else {
                            // Login failed
                            Toast.makeText(LoginActivity.this, 
                                "Login failed: " + task.getException().getMessage(), 
                                Toast.LENGTH_LONG).show();
                        }
                    }
                });
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
}
