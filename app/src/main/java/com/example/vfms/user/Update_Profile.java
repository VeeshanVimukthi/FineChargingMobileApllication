package com.example.vfms.user;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.vfms.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import android.util.Base64;

public class Update_Profile extends AppCompatActivity {

    private EditText usernameEditText, nicEditText, LicenNoEditText, contactEditText;
    private Button saveButton, selectImageButton;
    private ImageView profileImageView;

    private DatabaseReference userDbRef;
    private FirebaseAuth mAuth;
    private Bitmap selectedImageBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);


        ImageButton backButton = findViewById(R.id.Back_btn);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Implement code to go back to the previous page
//                onBackPressed();
                Intent intent = new Intent(Update_Profile.this, Profile_page.class);
                startActivity(intent);
                finish();
            }
        });





        // Initialize views and Firebase
        usernameEditText = findViewById(R.id.editTextUsername);
        nicEditText = findViewById(R.id.editTextNIC);
        contactEditText = findViewById(R.id.editTextContact);
        LicenNoEditText = findViewById(R.id.editTextLicense);
        saveButton = findViewById(R.id.buttonSave);
        selectImageButton = findViewById(R.id.selectImageButton);
        profileImageView = findViewById(R.id.profileImageView);

//        userDbRef = FirebaseDatabase.getInstance().getReference("Users");
        mAuth = FirebaseAuth.getInstance();

        // Set a click listener for the select image button
        selectImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
            }
        });

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");

            // Query to find the user with the corresponding "licenceNo" for the current user
            Query query = databaseReference.orderByChild("userId").equalTo(userId);

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        DataSnapshot userSnapshot = dataSnapshot.getChildren().iterator().next();

                        // Retrieve user data from the database
                        String currentUsername = userSnapshot.child("username").getValue(String.class);
                        String currentNIC = userSnapshot.child("nic").getValue(String.class);
                        String currentContact = userSnapshot.child("contact").getValue(String.class);
                        String currentLNO = userSnapshot.child("licenceNo").getValue(String.class);

                        // Populate EditText fields with the retrieved data
                        usernameEditText.setText(currentUsername);
                        nicEditText.setText(currentNIC);
                        contactEditText.setText(currentContact);
                        LicenNoEditText.setText(currentLNO);

                        // Retrieve and set the profile image
                        String currentProfileImageBase64 = userSnapshot.child("profileImageBase64").getValue(String.class);
                        if (currentProfileImageBase64 != null) {
                            byte[] decodedImage = android.util.Base64.decode(currentProfileImageBase64, android.util.Base64.DEFAULT);
                            Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedImage, 0, decodedImage.length);
                            profileImageView.setImageBitmap(decodedBitmap);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle specific types of errors
                    if (databaseError.getCode() == DatabaseError.PERMISSION_DENIED) {
                        // Handle permission denied error
                        Toast.makeText(Update_Profile.this, "Permission Denied: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    } else {
                        // Handle other errors
                        Toast.makeText(Update_Profile.this, "Database Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPasswordInputDialog(); // Show the password input dialog
            }
        });
    }
    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {
            try {
                InputStream inputStream = getContentResolver().openInputStream(data.getData());
                selectedImageBitmap = BitmapFactory.decodeStream(inputStream);
                profileImageView.setImageBitmap(selectedImageBitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Show a dialog for entering the current password
    private void showPasswordInputDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter Current Password");

        // Inflate the layout for the dialog
        View viewInflated = LayoutInflater.from(this).inflate(R.layout.password_input_dialog, null);
        final EditText passwordEditText = viewInflated.findViewById(R.id.editTextPassword);
        builder.setView(viewInflated);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String currentPassword = passwordEditText.getText().toString();
                // Verify the current password before updating
                verifyPasswordAndUpdate(currentPassword);
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    // Verify the current password and update profile if correct
    private void verifyPasswordAndUpdate(final String currentPassword) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();

            // Verify the current password before proceeding
            AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), currentPassword);
            user.reauthenticate(credential)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // Current password is correct, update the user's profile data in the database
                            updateProfile();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Current password is incorrect, show an error message
                            Toast.makeText(Update_Profile.this, "Incorrect current password", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    // Update the user's profile
    private void updateProfile() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();

            // Get values from EditText fields
            String newUsername = usernameEditText.getText().toString();
            String newNIC = nicEditText.getText().toString();
            String newContact = contactEditText.getText().toString();
            String newLicenceNo = LicenNoEditText.getText().toString();

            // Convert the selected image (Bitmap) to a base64 string
            String imageBase64 = "";
            if (selectedImageBitmap != null) {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                selectedImageBitmap.compress(Bitmap.CompressFormat.JPEG, 60, byteArrayOutputStream);
                byte[] imageBytes = byteArrayOutputStream.toByteArray();
                imageBase64 = Base64.encodeToString(imageBytes, Base64.DEFAULT);
            } else {
                // If no new image is selected, use the current image if available
                Bitmap currentImageBitmap = ((BitmapDrawable) profileImageView.getDrawable()).getBitmap();
                if (currentImageBitmap != null) {
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    currentImageBitmap.compress(Bitmap.CompressFormat.JPEG, 60, byteArrayOutputStream);
                    byte[] imageBytes = byteArrayOutputStream.toByteArray();
                    imageBase64 = Base64.encodeToString(imageBytes, Base64.DEFAULT);
                }
            }

            // Reference to the "Users" node in the Realtime Database
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");

            // Query to find the user with the corresponding "licenceNo" for the current user
            Query query = databaseReference.orderByChild("userId").equalTo(userId);

            String finalImageBase6 = imageBase64;
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        DataSnapshot userSnapshot = dataSnapshot.getChildren().iterator().next();

                        // Update the user's profile data in the database
                        userSnapshot.getRef().child("username").setValue(newUsername);
                        userSnapshot.getRef().child("nic").setValue(newNIC);
                        userSnapshot.getRef().child("contact").setValue(newContact);
                        userSnapshot.getRef().child("licenceNo").setValue(newLicenceNo);
                        userSnapshot.getRef().child("profileImageBase64").setValue(finalImageBase6);
                        userSnapshot.getRef().child("timestamp").setValue(ServerValue.TIMESTAMP);

                        // Show a success message to the user
                        Toast.makeText(Update_Profile.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();

                        // Redirect to the profile page after updating
                        startActivity(new Intent(Update_Profile.this, Profile_page.class));
                        finish(); // Close this activity to prevent going back to the update page
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle the database error here if needed
                }
            });
        }
    }

}