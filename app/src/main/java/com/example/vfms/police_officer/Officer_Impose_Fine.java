package com.example.vfms.police_officer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.vfms.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Officer_Impose_Fine extends AppCompatActivity {

    private EditText driverNameEditText;
    private EditText addressEditText;
    private EditText vehicleNumberEditText;
    private EditText licenseNumberEditText;
    private EditText natureOfOffenceEditText;
    private EditText fineAmountEditText;
    private EditText policemenIdEditText;

    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_officer_impose_fine);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("Impose_Fine");
        firebaseAuth = FirebaseAuth.getInstance();

        driverNameEditText = findViewById(R.id.Offence_Driver_Name);
        addressEditText = findViewById(R.id.Offence_Address);
        vehicleNumberEditText = findViewById(R.id.Vehicle_Number);
        licenseNumberEditText = findViewById(R.id.Licensee_Number);
        natureOfOffenceEditText = findViewById(R.id.Nature_Of_Offence);
        fineAmountEditText = findViewById(R.id.FineAmount);
        policemenIdEditText = findViewById(R.id.Policemen_Id);

        Button submitButton = findViewById(R.id.buttonSubmit);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveDataToFirebase();
            }
        });

        // Add this code to set up the back button functionality
        ImageButton backButton = findViewById(R.id.Back_btn);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Implement code to go back to the previous page (Profile_page)
                onBackPressed();
            }
        });
    }

    private void saveDataToFirebase() {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser == null) {
            // User not logged in, handle accordingly
            return;
        }
        String userId = currentUser.getUid();

        String driverName = driverNameEditText.getText().toString();
        String address = addressEditText.getText().toString();
        String vehicleNumber = vehicleNumberEditText.getText().toString();
        String licenseNumber = licenseNumberEditText.getText().toString();
        String natureOfOffence = natureOfOffenceEditText.getText().toString();
        String fineAmount = fineAmountEditText.getText().toString();
        // String policemenId = policemenIdEditText.getText().toString();

        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        String currentDate = sdfDate.format(new Date());
        String currentTime = sdfTime.format(new Date());

        // Generate a unique FineId based on date and time
        String fineId = generateFineId();

        DatabaseReference fineRef = databaseReference.child(fineId);

        fineRef.child("FineId").setValue(fineId);
        fineRef.child("DriverName").setValue(driverName);
        fineRef.child("Address").setValue(address);
        fineRef.child("VehicleNumber").setValue(vehicleNumber);
        fineRef.child("LicenseNumber").setValue(licenseNumber);
        fineRef.child("NatureOfOffence").setValue(natureOfOffence);
        fineRef.child("FineAmount").setValue(fineAmount);
        fineRef.child("PolicemenId").setValue(userId);
        fineRef.child("Date").setValue(currentDate);
        fineRef.child("Time").setValue(currentTime, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@NonNull DatabaseError error, @NonNull DatabaseReference ref) {
                if (error == null) {
                    Toast.makeText(Officer_Impose_Fine.this, "Data saved successfully", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(Officer_Impose_Fine.this, Police_Officer_Homepage.class);
                    startActivity(intent);
                    finish(); // Finish the current activity to prevent going back to it
                } else {
                    Toast.makeText(Officer_Impose_Fine.this, "Failed to save data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Generate a unique FineId based on date and time (you can implement your own logic here)
    private String generateFineId() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
        return "FINE" + sdf.format(new Date());
    }
}
