package com.example.uitpay.ui.login;


import android.content.Context;
import android.content.Intent;

/**
 * LoginModule provides a simple interface to launch the login functionality
 * from other modules or the main application.
 */
public class LoginModule {

    /**
     * Launch the login screen
     *
     * @param context The context to use for launching the activity
     */
    public static void launchLogin(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
    }

    /**
     * Interface for login callbacks
     */
    public interface LoginCallback {
        void onLoginSuccess(String userId);
        void onLoginFailure(String errorMessage);
    }

    // Singleton instance for the login callback
    private static LoginCallback loginCallback;

    /**
     * Set the login callback to receive login results
     *
     * @param callback The callback to set
     */
    public static void setLoginCallback(LoginCallback callback) {
        loginCallback = callback;
    }

    /**
     * Get the current login callback
     *
     * @return The current login callback
     */
    public static LoginCallback getLoginCallback() {
        return loginCallback;
    }
}

