package com.example.vfms.user;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.vfms.R;
import com.google.firebase.database.DatabaseReference;

public class PayFine extends AppCompatActivity {

    DatabaseReference fineRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_fine);


    }
}