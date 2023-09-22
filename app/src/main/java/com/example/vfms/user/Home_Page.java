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
import android.widget.ImageView;
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
    private ImageView profileImageView; // Added ImageView
    private FirebaseAuth firebaseAuth;

    private BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        // Profile page open code
        Button profileButton = findViewById(R.id.Profile);
        profileButton.setOnClickListener(view -> {
            // Open the profile page
            startActivity(new Intent(Home_Page.this, Profile_page.class));
        });

        Button PayFine_Btn = findViewById(R.id.PayFine_Btn);
        PayFine_Btn.setOnClickListener(view -> {
            // Open the profile page
            startActivity(new Intent(Home_Page.this, Fine_History.class));
        });



        // Inside your activity's onCreate method or wherever you want to set up the button
        Button openMapButton = findViewById(R.id.Nearest_location);

        openMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to open the MapActivity
                Intent intent = new Intent(Home_Page.this, MapActivity.class);
                startActivity(intent);
            }
        });


        // Profile page open code
        Button LicenceButton = findViewById(R.id.LicenceButton);
        LicenceButton.setOnClickListener(view -> {
            // Open the profile page
            startActivity(new Intent(Home_Page.this, User_licence_image_View.class));
        });

        textViewUserName = findViewById(R.id.ViewText);
        profileImageView = findViewById(R.id.profilePictureImageView); // Initialize ImageView
        firebaseAuth = FirebaseAuth.getInstance();

        // Get the current user from Firebase Authentication
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        if (currentUser != null) {
            // Get the user ID
            String userId = currentUser.getUid();

            // Reference to the "Users" node in the Realtime Database
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");

            // Query to find the user with the corresponding "licenceNo" for the current user
            Query query = databaseReference.orderByChild("userId").equalTo(userId);

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    // Check if there is a match for the current user's "userId"
                    if (dataSnapshot.exists()) {
                        // Iterate through the matching users (there should be only one)
                        for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                            // Retrieve the "licenceNo" field for the current user
                            String licenceNo = userSnapshot.child("licenceNo").getValue(String.class);


                            // For example, to access the username:
                            String username = userSnapshot.child("username").getValue(String.class);




                            // Retrieve the "profileImage" field (assuming it contains a Base64 encoded image)
                            String base64Image = userSnapshot.child("profileImageBase64").getValue(String.class);

                            // If you have the ImageView in your layout XML with id "profilePictureImageView"
                            ImageView profileImageView = findViewById(R.id.profilePictureImageView);

                            if (base64Image != null && !base64Image.isEmpty()) {
                                // Convert the Base64 string back to a byte array
                                byte[] imageBytes = Base64.decode(base64Image, Base64.DEFAULT);

                                // Create a Bitmap from the byte array
                                Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);

                                // Set the ImageView with the retrieved profile picture
                                profileImageView.setImageBitmap(bitmap);
                            }

                            // Update the TextView with the user's name
                            textViewUserName.setText("Welcome, " + username);

                            // View licence Expire date
                            TextView licenseExpirationDateTextView = findViewById(R.id.licence_date);
                            String licenseExpirationDate = userSnapshot.child("expireDate").getValue(String.class);

                            if (licenseExpirationDate != null && !licenseExpirationDate.isEmpty()) {
                                // Update the TextView with the license expiration date
                                licenseExpirationDateTextView.setText(licenseExpirationDate);
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle the error
                    Toast.makeText(Home_Page.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });



// Initialize the TextView for license expiration date
            TextView licenseExpirationDateTextView = findViewById(R.id.licence_date);

// Read the license expiration date from the database
            databaseReference.child("expireDate").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String licenseExpirationDate = dataSnapshot.getValue(String.class);

                    if (licenseExpirationDate != null && !licenseExpirationDate.isEmpty()) {
                        // Update the TextView with the license expiration date
                        licenseExpirationDateTextView.setText(licenseExpirationDate);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle the error, if any
                }
            });

        }

        // Add logout button click listener
        ImageButton logoutButton = findViewById(R.id.logoutBtn);
        logoutButton.setOnClickListener(view -> {
            // Show the logout confirmation dialog
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
                // Call the logout method to sign out the user
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
        // Sign out the current user from Firebase Authentication
        firebaseAuth.signOut();

        // Navigate back to the login page or any other desired page
        startActivity(new Intent(Home_Page.this, LoginPage.class));
        finish(); // Optional: This will close the current activity, so the user cannot go back to the home page using the back button.
    }
}
