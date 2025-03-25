package com.example.uitpay.ui.start;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

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
        Button signupBusiness = findViewById(R.id.signup_business_button);

        login.setOnClickListener(v ->
                startActivity(new Intent(Start.this, LoginActivity.class))
        );
        signupUser.setOnClickListener(v ->
                startActivity(new Intent(Start.this, UserSignUpActivity.class))
        );
    }
}
