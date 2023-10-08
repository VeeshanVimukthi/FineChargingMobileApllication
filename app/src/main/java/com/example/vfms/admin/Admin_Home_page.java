package com.example.vfms.admin;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;

import com.example.vfms.R;
import com.example.vfms.user.Home_Page;
import com.example.vfms.user.LoginPage;
import com.example.vfms.user.Profile_page;

public class Admin_Home_page extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home_page);


        Button profileButton1 = findViewById(R.id.Register_Officers);
        profileButton1.setOnClickListener(view -> {
            startActivity(new Intent(Admin_Home_page.this, Police_Register_admin.class));
        });

        // Profile page open code
        Button profileButton = findViewById(R.id.Update_Officers);
        profileButton.setOnClickListener(view -> {
            startActivity(new Intent(Admin_Home_page.this, Police_Officers_Update_Register_Details.class));
        });

        // registered officer page open code
        Button officerlistofregister = findViewById(R.id.View_Register_Officers);
        officerlistofregister.setOnClickListener(view -> {
            startActivity(new Intent(Admin_Home_page.this, RegisteredPoliceOfficerListActivity.class));
        });

        ImageButton logoutButton = findViewById(R.id.logoutBtn);
        logoutButton.setOnClickListener(view -> {
            showLogoutConfirmationDialog();
        });


    }

    private void showLogoutConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Logout");
        builder.setMessage("Are you sure you want to log out?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                logout();
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
    private void logout() {
        startActivity(new Intent(Admin_Home_page.this, LoginPage.class));
        finish();
    }
}