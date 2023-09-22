package com.example.vfms.police_officer;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import com.example.vfms.config.RandomIdGenerator;

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

    private RandomIdGenerator randomIdGenerator;

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
//        String policemenId = policemenIdEditText.getText().toString();

        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        String currentDate = sdfDate.format(new Date());
        String currentTime = sdfTime.format(new Date());

        // Generate a random ID
        String randomId = RandomIdGenerator.generateRandomId(10);

        DatabaseReference userRef = databaseReference.child(randomId);

        userRef.child("DriverName").setValue(driverName);
        userRef.child("Address").setValue(address);
        userRef.child("VehicleNumber").setValue(vehicleNumber);
        userRef.child("LicenseNumber").setValue(licenseNumber);
        userRef.child("NatureOfOffence").setValue(natureOfOffence);
        userRef.child("FineAmount").setValue(fineAmount);
        userRef.child("PolicemenId").setValue(userId);
        userRef.child("Date").setValue(currentDate);
        userRef.child("Time").setValue(currentTime, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@NonNull DatabaseError error, @NonNull DatabaseReference ref) {
                if (error == null) {
                    Toast.makeText(Officer_Impose_Fine.this, "Data saved successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(Officer_Impose_Fine.this, "Failed to save data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
