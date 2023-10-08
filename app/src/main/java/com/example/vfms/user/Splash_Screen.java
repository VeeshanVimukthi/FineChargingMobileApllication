package com.example.vfms.user;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import com.github.ybq.android.spinkit.SpinKitView; // Import the SpinKitView class

import com.example.vfms.R;

public class Splash_Screen extends AppCompatActivity {

    private SpinKitView spinKitView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        // Initialize the SpinKitView
        spinKitView = findViewById(R.id.spin_kit);

        // Start the animation
        spinKitView.setVisibility(View.VISIBLE); // Show the SpinKitView

        // Set a delay before transitioning to the login screen
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            // Hide the SpinKitView
            spinKitView.setVisibility(View.GONE);

            // Start the LoginActivity
            Intent intent = new Intent(Splash_Screen.this, LoginPage.class);
            startActivity(intent);

            // Finish the Splash_Screen activity
            finish();
        }, 3000); // Delay in milliseconds
    }
}