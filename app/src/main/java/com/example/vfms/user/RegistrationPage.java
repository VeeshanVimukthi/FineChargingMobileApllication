package com.example.vfms.user;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.vfms.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class  RegistrationPage extends AppCompatActivity {

    private EditText userName;
    private EditText userNIC;
    private EditText userContact;
    private EditText userLicenceNo;
    private EditText userRegEmail;
    private EditText userRegPassword;
    private EditText userCPass;
    private Button btnSignIn;
    private Button btnRegister;
    private ImageView profileImageView;
    private Button selectImageButton;
    private Bitmap selectedImageBitmap;

    private ProgressBar progressBar;

    private DatabaseReference userDbRef;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_page);

        // Initialize views
        userName = findViewById(R.id.etName);
        userNIC = findViewById(R.id.etNIC);
        userContact = findViewById(R.id.etContact);
        userLicenceNo = findViewById(R.id.etLicenceNo);
        userRegEmail = findViewById(R.id.etRegEmail);
        userRegPassword = findViewById(R.id.etRegPass);
        userCPass = findViewById(R.id.etRegPasswordC);
        btnSignIn = findViewById(R.id.btnSignIn);
        btnRegister = findViewById(R.id.registerButton);
        profileImageView = findViewById(R.id.profileImageView);
        selectImageButton = findViewById(R.id.selectImageButton);

        progressBar = findViewById(R.id.progressBar);

        // Initialize Firebase
        userDbRef = FirebaseDatabase.getInstance().getReference("Users");
        mAuth = FirebaseAuth.getInstance();

        // Set click listeners
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createUserAccount();
            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegistrationPage.this, LoginPage.class));
                finish(); // Close this activity to prevent going back to registration after login
            }
        });

        selectImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Open image selection intent
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 1);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == Activity.RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            try {
                // Load the selected image into a Bitmap
                selectedImageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                // Set the ImageView to display the selected image
                profileImageView.setImageBitmap(selectedImageBitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void createUserAccount() {
        String username = userName.getText().toString().trim();
        String email = userRegEmail.getText().toString().trim();
        String password = userRegPassword.getText().toString().trim();
        String confirmPassword = userCPass.getText().toString().trim();
        String Licence = userLicenceNo.getText().toString().trim();
        String nic = userNIC.getText().toString().trim();
        String contact = userContact.getText().toString().trim();

        // Validate input fields
        if (TextUtils.isEmpty(username)) {
            userName.setError("Username cannot be empty");
            userName.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(nic)) {
            userNIC.setError("NIC cannot be empty");
            userNIC.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(contact)) {
            userContact.setError("Contact number cannot be empty");
            userContact.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(Licence)) {
            userLicenceNo.setError("Licence no cannot be empty");
            userLicenceNo.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(email)) {
            userRegEmail.setError("Email cannot be empty");
            userRegEmail.requestFocus();
            return;
        }

        if (!isValidEmail(email)) {
            userRegEmail.setError("Invalid email format");
            userRegEmail.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            userRegPassword.setError("Password cannot be empty");
            userRegPassword.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(confirmPassword)) {
            userCPass.setError("Confirm password cannot be empty");
            userCPass.requestFocus();
            return;
        }
        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }
        if (selectedImageBitmap == null) {
            Toast.makeText(this, "Please select a profile image", Toast.LENGTH_SHORT).show();
            return;
        }


        progressBar.setVisibility(View.VISIBLE);

        // Create user account with Firebase Authentication
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Get the user ID of the created user
                            String userId = mAuth.getCurrentUser().getUid();
                            // Create a Users object with the user's information
                            Users user = new Users(userId, username, nic, email, Licence, contact);

                            // Convert the selected image to a Base64 encoded string
                            if (selectedImageBitmap != null) {
                                String imageBase64 = convertBitmapToBase64(selectedImageBitmap);
                                user.setProfileImageBase64(imageBase64);
                            }

                            // Save user object to Firebase Realtime Database
                            userDbRef.child(Licence).setValue(user)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                // Registration successful, navigate to home page


                                                progressBar.setVisibility(View.GONE);

                                                Toast.makeText(RegistrationPage.this, "User registered successfully", Toast.LENGTH_SHORT).show();
                                                mAuth.signOut();
                                                startActivity(new Intent(RegistrationPage.this, LoginPage.class));
                                                finish(); // Close this activity to prevent going back to registration
                                            } else {
                                                // Error saving user data to database
                                                Toast.makeText(RegistrationPage.this, "Failed to save user data: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        } else {
                            // Error creating user account
                            Toast.makeText(RegistrationPage.this, "Registration Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private boolean isValidEmail(String email) {
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        return email.matches(emailPattern);
    }

    private String convertBitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 60, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }
}
