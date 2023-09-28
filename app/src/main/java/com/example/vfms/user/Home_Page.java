package com.example.vfms.user;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.vfms.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class Home_Page extends AppCompatActivity {

    private TextView textViewUserName;
    private ImageButton profileImageView;
    private FirebaseAuth firebaseAuth;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        Button profileButton1 = findViewById(R.id.Profile);
        profileButton1.setOnClickListener(view -> {
            startActivity(new Intent(Home_Page.this, Profile_page.class));
        });

        // Profile page open code
        ImageButton profileButton = findViewById(R.id.profilePictureImageView);
        profileButton.setOnClickListener(view -> {
            startActivity(new Intent(Home_Page.this, Profile_page.class));
        });

        Button PayFine_Btn = findViewById(R.id.PayFine_Btn);
        PayFine_Btn.setOnClickListener(view -> {
            startActivity(new Intent(Home_Page.this, Fine_History.class));
        });

        // Open map button
        Button openMapButton = findViewById(R.id.Nearest_location);
        openMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home_Page.this, MapActivity.class);
                startActivity(intent);
            }
        });




        // Profile page open code
        Button LicenceButton = findViewById(R.id.LicenceButton);
        LicenceButton.setOnClickListener(view -> {
            startActivity(new Intent(Home_Page.this, User_licence_image_View.class));
        });

        textViewUserName = findViewById(R.id.ViewText);
        profileImageView = findViewById(R.id.profilePictureImageView);
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        if (currentUser != null) {
            String userId = currentUser.getUid();
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
            Query query = databaseReference.orderByChild("userId").equalTo(userId);

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                            String licenceNo = userSnapshot.child("licenceNo").getValue(String.class);
                            String username = userSnapshot.child("username").getValue(String.class);
                            String base64Image = userSnapshot.child("profileImageBase64").getValue(String.class);

                            if (base64Image != null && !base64Image.isEmpty()) {
                                byte[] imageBytes = Base64.decode(base64Image, Base64.DEFAULT);
                                Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                                profileImageView.setImageBitmap(bitmap);
                            }

                            textViewUserName.setText("Welcome, " + username);

                            TextView licenseExpirationDateTextView = findViewById(R.id.licence_date);
                            String licenseExpirationDate = userSnapshot.child("expireDate").getValue(String.class);

                            if (licenseExpirationDate != null && !licenseExpirationDate.isEmpty()) {
                                licenseExpirationDateTextView.setText(licenseExpirationDate);
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(Home_Page.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

            TextView licenseExpirationDateTextView = findViewById(R.id.licence_date);
            databaseReference.child(userId).child("expireDate").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String licenseExpirationDate = dataSnapshot.getValue(String.class);

                    if (licenseExpirationDate != null && !licenseExpirationDate.isEmpty()) {
                        licenseExpirationDateTextView.setText(licenseExpirationDate);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle the error, if any
                }
            });
        }

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
        firebaseAuth.signOut();
        startActivity(new Intent(Home_Page.this, LoginPage.class));
        finish();
    }
}
