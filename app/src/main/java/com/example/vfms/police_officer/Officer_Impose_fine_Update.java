package com.example.vfms.police_officer;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.vfms.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.android.material.textfield.TextInputEditText;

public class Officer_Impose_fine_Update extends AppCompatActivity {

    // Define your TextInputEditText fields and Fine ID
    private TextInputEditText editTextDriverName;
    private TextInputEditText editTextLicenseNumber;
    private TextInputEditText editTextVehicleNumber;
    private TextInputEditText editTextFineAmount;
    private TextInputEditText editTextAddress;
    private TextInputEditText editTextNatureOfOffence;
    private String fineId; // To store the Fine ID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_officer_impose_fine_update);

        // Initialize TextInputEditText fields and retrieve Fine ID from Intent
        editTextDriverName = findViewById(R.id.editTextDriverName);
        editTextLicenseNumber = findViewById(R.id.editTextLicenseNumber);
        editTextVehicleNumber = findViewById(R.id.editTextVehicleNumber);
        editTextFineAmount = findViewById(R.id.editTextFineAmount);
        editTextAddress = findViewById(R.id.editTextAddress);
        editTextNatureOfOffence = findViewById(R.id.editTextNatureOfOffence);

        // Retrieve Fine ID from Intent extras
        Intent intent = getIntent();
        if (intent != null) {
            fineId = intent.getStringExtra("fineId"); // Make sure "fineId" matches the key used in the previous activity

            // Retrieve other data and set it to the TextInputEditText fields
            String driverName = intent.getStringExtra("driverName");
            String licenseNumber = intent.getStringExtra("licenseNumber");
            String vehicleNumber = intent.getStringExtra("vehicleNumber");
            String fineAmount = intent.getStringExtra("fineAmount");
            String address = intent.getStringExtra("address");
            String natureOfOffence = intent.getStringExtra("natureOfOffence");

            editTextDriverName.setText(driverName);
            editTextLicenseNumber.setText(licenseNumber);
            editTextVehicleNumber.setText(vehicleNumber);
            editTextFineAmount.setText(fineAmount);
            editTextAddress.setText(address);
            editTextNatureOfOffence.setText(natureOfOffence);
        }

        // Set an OnClickListener for the "Save" button
        Button saveButton = findViewById(R.id.saveButton); // Replace with the ID of your "Save" button
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Collect the updated data from TextInputEditText fields
                String updatedDriverName = editTextDriverName.getText().toString();
                String updatedLicenseNumber = editTextLicenseNumber.getText().toString();
                String updatedVehicleNumber = editTextVehicleNumber.getText().toString();
                String updatedFineAmount = editTextFineAmount.getText().toString();
                String updatedAddress = editTextAddress.getText().toString();
                String updatedNatureOfOffence = editTextNatureOfOffence.getText().toString();

                // Update the data in Firebase based on the Fine ID
                DatabaseReference fineRef = FirebaseDatabase.getInstance().getReference("Impose_Fine").child(fineId);

                // Use `setValue` to update the existing data without creating a new record
                fineRef.child("DriverName").setValue(updatedDriverName);
                fineRef.child("LicenseNumber").setValue(updatedLicenseNumber);
                fineRef.child("VehicleNumber").setValue(updatedVehicleNumber);
                fineRef.child("FineAmount").setValue(updatedFineAmount);
                fineRef.child("Address").setValue(updatedAddress);
                fineRef.child("NatureOfOffence").setValue(updatedNatureOfOffence);

                // Notify the user that the data has been updated
                Toast.makeText(Officer_Impose_fine_Update.this, "Fine data updated successfully", Toast.LENGTH_SHORT).show();

                // Finish the activity to return to the previous screen
                finish();
            }
        });
    }
}
