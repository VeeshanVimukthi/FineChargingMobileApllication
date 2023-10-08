package com.example.vfms.police_officer;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
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
import com.google.firebase.database.ValueEventListener;

public class Police_Officer_Profile extends AppCompatActivity {

    private TextView usernameTextView;
    private TextView emailTextView;
    private TextView nicTextView;
    private TextView OfficerNumber;
    private TextView contactTextView;
    private ImageView profileImageView;

    private DatabaseReference officerDbRef; // Updated database reference
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_police_officer_profile);

//        View btnSignIn = findViewById(R.id.Advance_Profile);


        // Add this code to set up the back button functionality
        ImageButton backButton = findViewById(R.id.Back_btn);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Implement code to go back to the previous page (Profile_page)
                onBackPressed();
            }
        });

//        btnSignIn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                startActivity(new Intent(Police_Officer_Profile.this, Advance_Profile_Page.class));
//                finish(); // Close this activity to prevent going back to registration after login
//            }
//        });

        // Initialize views
        usernameTextView = findViewById(R.id.Username);
        emailTextView = findViewById(R.id.emailText);
        nicTextView = findViewById(R.id.nicText);
        contactTextView = findViewById(R.id.contactText);
        profileImageView = findViewById(R.id.profilePictureImageView);
        OfficerNumber = findViewById(R.id.OfficerIDText);

        // Initialize Firebase
        officerDbRef = FirebaseDatabase.getInstance().getReference("police_officers"); // Update the reference
        mAuth = FirebaseAuth.getInstance();

        // Fetch and display user profile data
        displayUserProfile();
    }

    private void displayUserProfile() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            officerDbRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        PoliceOfficer officerProfile = dataSnapshot.getValue(PoliceOfficer.class); // Use PoliceOfficer class
                        if (officerProfile != null) {
                            usernameTextView.setText(officerProfile.getName());
                            emailTextView.setText(officerProfile.getEmail());
                            nicTextView.setText(officerProfile.getNic());
                            contactTextView.setText(officerProfile.getContact());
                            OfficerNumber.setText(officerProfile.getOfficerNumber());
                            // Load and set the profile image from Base64 string
                            String profileImageBase64 = officerProfile.getprofileImage();
                            if (profileImageBase64 != null && !profileImageBase64.isEmpty()) {
                                byte[] decodedString = Base64.decode(profileImageBase64, Base64.DEFAULT);
                                profileImageView.setImageBitmap(BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length));
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
                        Toast.makeText(Police_Officer_Profile.this, "Database Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}
