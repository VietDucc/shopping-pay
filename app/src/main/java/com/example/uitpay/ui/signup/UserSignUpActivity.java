package com.example.uitpay.ui.signup;


import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.uitpay.R;
import com.example.uitpay.ui.login.LoginActivity;
import com.example.uitpay.ui.start.Start;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.AuthResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.User;

import java.util.HashMap;
import java.util.Map;

public class UserSignUpActivity extends AppCompatActivity {

    private TextInputEditText emailTextView, passwordTextView;

    private TextView textView;
    private Button button;
    private ImageButton backButton;
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize Firebase Auth & Firestore
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Initialize UI components
        emailTextView = findViewById(R.id.email_edit_text);
        passwordTextView = findViewById(R.id.password_edit_text);
        button = findViewById(R.id.signup_button);
        textView = findViewById(R.id.already_have_account_text);
        backButton = findViewById(R.id.back_button);
        // Initialize ProgressDialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Registering...");

        // Thêm hiệu ứng lightsweep cho button signup
        addLightSweepAnimation(button);

        button.setOnClickListener(v -> registerNewUser());
        textView.setOnClickListener(v ->{
            Intent intent = new Intent(UserSignUpActivity.this, LoginActivity.class);
            startActivity(intent);
        });
        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(UserSignUpActivity.this, Start.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });
    }

    private void registerNewUser() {
        String email = emailTextView.getText().toString().trim();
        String password = passwordTextView.getText().toString().trim();

        // Validate email and password
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Please enter a valid email", Toast.LENGTH_LONG).show();
            return;
        }

        if (password.isEmpty() || password.length() < 6) {
            Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_LONG).show();
            return;
        }

        progressDialog.show(); // Show loading dialog

        // Register new user with Firebase
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss(); // Hide loading dialog
                        if (task.isSuccessful() && auth.getCurrentUser() != null) {
                            // Get User UID
                            String uid = auth.getCurrentUser().getUid();

                            // Create user data
                            Map<String, Object> user = new HashMap<>();
                            user.put("user_id", uid);
                            user.put("email", email);
                            user.put("created_at", System.currentTimeMillis());

                            // Save user data to Firestore
                            db.collection("users").document(uid)
                                    .set(user)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(UserSignUpActivity.this, "Registration successful!", Toast.LENGTH_LONG).show();
                                        startActivity(new Intent(UserSignUpActivity.this, Start.class));
                                        finish();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(UserSignUpActivity.this, "Failed to save user data", Toast.LENGTH_LONG).show();
                                    });
                        } else {
                            Toast.makeText(UserSignUpActivity.this, "Registration failed! Please try again later", Toast.LENGTH_LONG).show();
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
