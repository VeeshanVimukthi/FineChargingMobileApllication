package com.example.vfms.police_officer;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.example.vfms.R;
import com.example.vfms.user.LoginPage;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Police_Officer_Homepage extends AppCompatActivity {

    private TextView textViewUserName;
    private ImageView profileImageView; // ImageView for the profile image
    private Dialog popupDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_police_officer_homepage);

        textViewUserName = findViewById(R.id.ViewTextP);
        profileImageView = findViewById(R.id.profilePictureImageView); // Initialize the ImageView

        ImageButton profileButton = findViewById(R.id.profilePictureImageView);
        profileButton.setOnClickListener(view -> {
            startActivity(new Intent(Police_Officer_Homepage.this, Police_Officer_Profile.class));
        });

        // Get the current user from Firebase Authentication
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            // Get the user ID
            String userId = currentUser.getUid();

            // Get a reference to the user's data in Firebase Realtime Database
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("police_officers").child(userId);

            // Read the user's name from the database
            databaseReference.child("name").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String userName = dataSnapshot.getValue(String.class);

                    // Update the TextView with the user's name
                    textViewUserName.setText("Welcome Officer, " + userName + "");
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle the error, if any
                }
            });

            // Read the user's profile image as a Base64 string from the database
            databaseReference.child("profileImage").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String profileImageBase64 = dataSnapshot.getValue(String.class);

                    // Load and set the profile image using Base64 decoding
                    if (profileImageBase64 != null && !profileImageBase64.isEmpty()) {
                        byte[] decodedString = android.util.Base64.decode(profileImageBase64, android.util.Base64.DEFAULT);
                        Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                        profileImageView.setImageBitmap(decodedBitmap);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle the error, if any
                }
            });
        }

        // Add a button and set its click listener to navigate to Officer_Impose_Fine activity
        Button btnImposeFine = findViewById(R.id.ImposeFine);
        btnImposeFine.setOnClickListener(view -> {
            startActivity(new Intent(Police_Officer_Homepage.this, Officer_Impose_Fine.class));
        });

        // Add another button and set its click listener to navigate to Officer_Profile activity
        Button btnOfficerProfile = findViewById(R.id.Officer_Profile);
        btnOfficerProfile.setOnClickListener(view -> {
            startActivity(new Intent(Police_Officer_Homepage.this, Police_Officer_Profile.class));
        });

        // Add another button and set its click listener to navigate to Officer_Profile activity
        Button updateImposeFine = findViewById(R.id.updateImposeFine);
        updateImposeFine.setOnClickListener(view -> {
            startActivity(new Intent(Police_Officer_Homepage.this, Officer_Impose_Fine_History.class));
        });

        // Add logout button click listener
        ImageButton btnLogout = findViewById(R.id.logoutBtn);
        btnLogout.setOnClickListener(view -> {
            // Show the logout confirmation dialog
            showLogoutConfirmationDialog();
        });

        // Find a button to trigger the pop-up window
        Button openPopupButton = findViewById(R.id.infoButton);

        // Set a click listener to display the pop-up window
        openPopupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayPopup(v);
            }
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
                finish();
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
        FirebaseAuth.getInstance().signOut();

        // Navigate back to the login page or any other desired page
        startActivity(new Intent(Police_Officer_Homepage.this, LoginPage.class));
        finish(); // Optional: This will close the current activity, so the user cannot go back to the homepage using the back button.
    }

    public void displayPopup(View view) {
        // Create a Dialog with your custom layout
        popupDialog = new Dialog(this);
        popupDialog.setContentView(R.layout.popup_layout);

        // Show the pop-up window
        popupDialog.show();
    }

    public void closePopup(View view) {
        // Close the pop-up window when the "Close" button is clicked
        if (popupDialog != null && popupDialog.isShowing()) {
            popupDialog.dismiss();
        }
    }
}
