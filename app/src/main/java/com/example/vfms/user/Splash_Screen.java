package com.example.vfms.user;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.vfms.R;

public class Splash_Screen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        // Splash
        View rootView = getWindow().getDecorView().getRootView();
        rootView.postDelayed(() -> {
            Intent intent = new Intent(Splash_Screen.this, LoginPage.class);
            startActivity(intent);
            finish(); // Optional: Depending on your use case, you might want to finish the Splash_Screen activity here.
        }, 1000);
    }
}
