package com.example.vfms.user;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.vfms.R;
import com.example.vfms.admin.Admin_Home_page;
import com.example.vfms.police_officer.PoliceOfficer;
import com.example.vfms.police_officer.Police_Officer_Homepage;
import com.example.vfms.admin.Police_Register_admin;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class LoginPage extends AppCompatActivity {

    EditText userEmail;
    EditText userPassword;
    Button btnSignUp;
    Button btnLogin;
    Button btnForgotPassword; // Added for Forgot Password functionality
    FirebaseAuth mAuth;
    DatabaseReference usersDbRef;
    DatabaseReference policeOfficersDbRef;

    private FirebaseAuth.AuthStateListener mAuthStateListener;

    @SuppressLint("CutPasteId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        userEmail = findViewById(R.id.etLoginEmail);
        userPassword = findViewById(R.id.etLoginPass);
        btnSignUp = findViewById(R.id.btnRegister);
        btnLogin = findViewById(R.id.btnLogin);
        btnForgotPassword = findViewById(R.id.btnForgotPassword); // Initialize Forgot Password button

        mAuth = FirebaseAuth.getInstance();
        usersDbRef = FirebaseDatabase.getInstance().getReference("Users");
        policeOfficersDbRef = FirebaseDatabase.getInstance().getReference("police_officers");

        // Set up the AuthStateListener to handle always sign-in part
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is already signed in, redirect to appropriate activity
                    checkUserTypeAndRedirect(user.getUid());
                } else {
                    // User is not signed in
                    // You can handle this case if needed, e.g., display login options or continue with login flow.
                }
            }
        };

        btnLogin.setOnClickListener(view -> {
            loginUser();
        });

        // Clicking SignUp button will go to the Registration Form
        btnSignUp.setOnClickListener(view -> {
            startActivity(new Intent(LoginPage.this, RegistrationPage.class));
        });

        btnForgotPassword.setOnClickListener(view -> {
            showForgotPasswordDialog();
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthStateListener != null) {
            mAuth.removeAuthStateListener(mAuthStateListener);
        }
    }

    private void loginUser() {
        String email = userEmail.getText().toString();
        String password = userPassword.getText().toString();

        if (TextUtils.isEmpty(email)) {
            userEmail.setError("Email cannot be empty");
            userEmail.requestFocus();
        } else if (TextUtils.isEmpty(password)) {
            userPassword.setError("Password cannot be empty");
            userPassword.requestFocus();
        } else {
            if (email.equals("1") && password.equals("1")) {
                // If the admin credentials are provided, directly open the admin page
                openAdminPage();
            } else {
                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            assert user != null;
                            checkUserTypeAndRedirect(user.getUid());
                        } else {
                            Toast.makeText(LoginPage.this, "Log in Error: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }
    }

    private void checkUserTypeAndRedirect(String userId) {
        usersDbRef.orderByChild("userId").equalTo(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // User exists in "Users" table
                    Users userData = dataSnapshot.getValue(Users.class);
                    // Redirect to appropriate activity for Users
                    assert userData != null;
                    redirectToUserActivity(userData);
                } else {
                    // User does not exist in "Users" table, check "police_officers" table
                    policeOfficersDbRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                // User exists in "police_officers" table
                                PoliceOfficer policeOfficerData = snapshot.getValue(PoliceOfficer.class);
                                // Redirect to appropriate activity for police officers
                                assert policeOfficerData != null;
                                redirectToPoliceOfficerActivity(policeOfficerData);
                            } else {
                                // Neither in "Users" nor in "police_officers" table
                                Toast.makeText(LoginPage.this, "User data not found.", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(LoginPage.this, "Database Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(LoginPage.this, "Database Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void redirectToUserActivity(Users userData) {
        Intent profileIntent = new Intent(getApplicationContext(), Home_Page.class);
        profileIntent.putExtra("username", userData.getUsername());
        // Add other relevant user data to the intent if needed
        startActivity(profileIntent);
        finish(); // Close the LoginActivity
    }

    private void redirectToPoliceOfficerActivity(PoliceOfficer policeOfficerData) {
        Intent profileIntent = new Intent(getApplicationContext(), Police_Officer_Homepage.class);
        profileIntent.putExtra("policeOfficerName", policeOfficerData.getName());
        // Add other relevant police officer data to the intent if needed
        startActivity(profileIntent);
        finish(); // Close the LoginActivity
    }

    private void openAdminPage() {
        Intent adminIntent = new Intent(getApplicationContext(), Admin_Home_page.class);
        startActivity(adminIntent);
        finish(); // Close the LoginActivity
    }

    private void showForgotPasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Forgot Password");

        // Set up the input
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("Reset", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String email = input.getText().toString().trim();
                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(LoginPage.this, "Please enter your email address", Toast.LENGTH_SHORT).show();
                } else {
                    sendPasswordResetEmail(email);
                }
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

    private void sendPasswordResetEmail(String email) {
        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(LoginPage.this, "Password reset email sent. Please check your email.", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(LoginPage.this, "Failed to send password reset email. Please try again.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
