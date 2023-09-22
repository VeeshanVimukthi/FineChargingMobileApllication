package com.example.vfms.police_officer;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.vfms.user.LoginPage;
import com.example.vfms.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class  Police_Register_admin extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    private TextInputLayout layoutID, layoutFuleName, layoutStartPlace, layoutLicenceNo,
            layoutEmail, layoutStartPlace1, layoutConformPassword;

    private TextInputEditText pName, pNIC, pContact, pId, pRegEmail, pRegPass, pRegPasswordC;
    private Button p_registerButton;
    private ImageView profileImageView;
    private Button selectImageButton;
    private Bitmap selectedImageBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_police_register_admin);

        // Initialize Firebase Auth and Database
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("police_officers");

        // Initialize views
        layoutID = findViewById(R.id.layoutID);
        layoutFuleName = findViewById(R.id.layoutFuleName);
        layoutStartPlace = findViewById(R.id.layoutStartPlace);
        layoutLicenceNo = findViewById(R.id.layoutLicenceNo);
        layoutEmail = findViewById(R.id.layoutEmail);
        layoutStartPlace1 = findViewById(R.id.layoutStartPlace1);
        layoutConformPassword = findViewById(R.id.layoutConformPassword);

        pName = findViewById(R.id.pName);
        pNIC = findViewById(R.id.pNIC);
        pContact = findViewById(R.id.pContact);
        pId = findViewById(R.id.pId);
        pRegEmail = findViewById(R.id.pRegEmail);
        pRegPass = findViewById(R.id.pRegPass);
        pRegPasswordC = findViewById(R.id.pRegPasswordC);

        p_registerButton = findViewById(R.id.p_registerButton);
        profileImageView = findViewById(R.id.profileImageView);
        selectImageButton = findViewById(R.id.selectImageButton);

        selectImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Open image selection intent
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 1);
            }
        });

        p_registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerPoliceOfficer();
            }
        });

        ImageButton logoutButton = findViewById(R.id.logoutBtn);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLogoutConfirmationDialog();
            }
        });
        Button openNextPageButton = findViewById(R.id.btnUpdate);
        openNextPageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openNextActivity();
            }
        });
    }

    private void registerPoliceOfficer() {
        final String name = pName.getText().toString().trim();
        final String nic = pNIC.getText().toString().trim();
        final String contact = pContact.getText().toString().trim();
        final String officerId = pId.getText().toString().trim();
        final String email = pRegEmail.getText().toString().trim();
        final String password = pRegPass.getText().toString().trim();
        String confirmPassword = pRegPasswordC.getText().toString().trim();

        // Validate fields
        if (TextUtils.isEmpty(name)) {
            pName.setError("Name cannot be empty");
            pName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(nic)) {
            pNIC.setError("NIC cannot be empty");
            pNIC.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(contact)) {
            pContact.setError("Contact number cannot be empty");
            pContact.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(officerId)) {
            pId.setError("Officer ID cannot be empty");
            pId.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(email)) {
            pRegEmail.setError("Email cannot be empty");
            pRegEmail.requestFocus();
            return;
        }

        if (!isValidEmail(email)) {
            pRegEmail.setError("Invalid email format");
            pRegEmail.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            pRegPass.setError("Password cannot be empty");
            pRegPass.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(confirmPassword)) {
            pRegPasswordC.setError("Confirm password cannot be empty");
            pRegPasswordC.requestFocus();
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


        // Create user with email and password
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // User registration success
                            FirebaseUser currentUser = mAuth.getCurrentUser();
                            if (currentUser != null) {
                                String userId = currentUser.getUid();

                                // Save user data to Firebase Database
                                PoliceOfficer policeOfficer = new PoliceOfficer(userId, name, nic, contact);

                                // Convert the selected image to a Base64 encoded string
                                if (selectedImageBitmap != null) {
                                    String imageBase64 = convertBitmapToBase64(selectedImageBitmap);
                                    policeOfficer.setprofileImage(imageBase64);
                                }

                                mDatabase.child(userId).setValue(policeOfficer);
                                Toast.makeText(Police_Register_admin.this, "Registration successful.", Toast.LENGTH_SHORT).show();
                                mAuth.signOut();
                                startActivity(new Intent(Police_Register_admin.this, LoginPage.class));
                                finish();
                            }
                        } else {
                            // If registration fails, display a message to the user.
                            Toast.makeText(Police_Register_admin.this, "Registration failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    // Check if the email is valid
    private boolean isValidEmail(String email) {
        // You can use a regular expression for a basic email format check
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        // Use the matches method to perform the validation
        return email.matches(emailPattern);
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
        mAuth.signOut();
        startActivity(new Intent(Police_Register_admin.this, LoginPage.class));
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
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

    private String convertBitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 60, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    private void openNextActivity() {
        Intent intent = new Intent(Police_Register_admin.this, Police_Officers_Update_Register_Details.class);
        startActivity(intent);
    }
}
