package com.example.vfms.user;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
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

public class User_licence_image_View extends AppCompatActivity {

    private ImageView licenseFrontImageView, licenseBackImageView;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_licence_image_view);




        ImageButton backButton = findViewById(R.id.Back_btn);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Implement code to go back to the previous page (Profile_page)
                onBackPressed();
            }
        });

        licenseFrontImageView = findViewById(R.id.imageView1);
        licenseBackImageView = findViewById(R.id.imageView2);

        mAuth = FirebaseAuth.getInstance();

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
                            String frontImageBase64 = userSnapshot.child("Licence Front Side").getValue(String.class);
                            String backImageBase64 = userSnapshot.child("Licence Back Side").getValue(String.class);

                            if (frontImageBase64 != null && backImageBase64 != null) {
                                // Decode Base64 strings into Bitmaps and set them to ImageViews
                                Bitmap frontBitmap = decodeBase64(frontImageBase64);
                                Bitmap backBitmap = decodeBase64(backImageBase64);

                                licenseFrontImageView.setImageBitmap(frontBitmap);
                                licenseBackImageView.setImageBitmap(backBitmap);
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // You can log the error for debugging purposes
                    Log.e("FirebaseDatabase", "Database error: " + databaseError.getMessage());

                    // You can also show a user-friendly error message to the user
                    Toast.makeText(User_licence_image_View.this, "Database error. Please try again later.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    // Utility method to decode Base64 string into a Bitmap
    private Bitmap decodeBase64(String base64) {
        byte[] decodedImage = android.util.Base64.decode(base64, android.util.Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedImage, 0, decodedImage.length);
    }
}
