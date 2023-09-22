package com.example.vfms.user;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.vfms.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class Profile_page extends AppCompatActivity {

    private TextView usernameTextView;
    private TextView emailTextView;
    private TextView nicTextView;
    private TextView contactTextView;
    private ImageView profileImageView;

    private DatabaseReference userDbRef;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page);
        View btnSignIn = findViewById(R.id.Advance_Profile);

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Profile_page.this, Advance_Profile_Page.class));
                finish(); // Close this activity to prevent going back to registration after login
            }
        });
        View uploadButton = findViewById(R.id.uploadButton);
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Profile_page.this, Update_Profile.class));
                finish(); // Close this activity to prevent going back to registration after login
            }
        });

        // Initialize views
        usernameTextView = findViewById(R.id.Username);
        emailTextView = findViewById(R.id.emailText);
        nicTextView = findViewById(R.id.nicText);
        contactTextView = findViewById(R.id.contactText);
        profileImageView = findViewById(R.id.profilePictureImageView);

        // Initialize Firebase
        userDbRef = FirebaseDatabase.getInstance().getReference("Users");
        mAuth = FirebaseAuth.getInstance();

        // Fetch and display user profile data
        displayUserProfile();
    }

    private void displayUserProfile() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();

            // Reference to the "Users" node in the Realtime Database
            DatabaseReference usersReference = FirebaseDatabase.getInstance().getReference("Users");

            // Query to find the user with the corresponding "userId" for the current user
            Query query = usersReference.orderByChild("userId").equalTo(userId);

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                            Users userProfile = userSnapshot.getValue(Users.class);
                            if (userProfile != null) {
                                usernameTextView.setText(userProfile.getUsername());
                                emailTextView.setText(userProfile.getEmail());
                                nicTextView.setText(userProfile.getNic());
                                contactTextView.setText(userProfile.getContact());

                                // Load and set the profile image from Base64 string
                                String profileImageBase64 = userProfile.getProfileImageBase64();
                                if (profileImageBase64 != null && !profileImageBase64.isEmpty()) {
                                    byte[] decodedString = Base64.decode(profileImageBase64, Base64.DEFAULT);
                                    profileImageView.setImageBitmap(BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length));
                                }
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle database error
                    if (databaseError != null) {
                        // Log the error message
                        Log.e("DatabaseError", "Error: " + databaseError.getMessage());

                        // Show a Toast message to the user
                        Toast.makeText(Profile_page.this, "Database Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}
