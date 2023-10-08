package com.example.vfms.admin;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.vfms.R;
import com.example.vfms.police_officer.PoliceOfficer;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class Police_Officers_Update_Register_Details extends AppCompatActivity {

    private EditText userIdInput;
    private EditText pName;
    private EditText pNIC;
    private EditText pContact;
    private EditText pOfficerNumber;
    private Button searchButton;
    private Button updateButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_police_officers_update_register_details);



        // Add this code to set up the back button functionality
        ImageButton backButton = findViewById(R.id.Back_btn);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Implement code to go back to the previous page (Profile_page)
                onBackPressed();
            }
        });

        // Initialize UI elements
        userIdInput = findViewById(R.id.userIdInput);
        pName = findViewById(R.id.pName);
        pNIC = findViewById(R.id.pNIC);
        pContact = findViewById(R.id.pContact);
        pOfficerNumber = findViewById(R.id.pOfficerNumber);
        searchButton = findViewById(R.id.searchButton);
        updateButton = findViewById(R.id.updateButton);

        // Set a click listener for the search button
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchPoliceOfficer();
            }
        });

        // Set a click listener for the update button
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updatePoliceOfficer();
            }
        });
    }

    private void searchPoliceOfficer() {
        EditText userIdInput = findViewById(R.id.userIdInput);
        String officerNumber = userIdInput.getText().toString().trim();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("police_officers");
        databaseReference.orderByChild("officerNumber").equalTo(officerNumber).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Officer with the specified officerNumber found
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        PoliceOfficer policeOfficer = snapshot.getValue(PoliceOfficer.class);
                        // Now, you can display the details in your UI (e.g., pName, pNIC, etc.)
                        EditText pName = findViewById(R.id.pName);
                        EditText pNIC = findViewById(R.id.pNIC);
                        EditText pContact = findViewById(R.id.pContact);
                        EditText pOfficerNumber = findViewById(R.id.pOfficerNumber);

                        pName.setText(policeOfficer.getName());
                        pNIC.setText(policeOfficer.getNic());
                        pContact.setText(policeOfficer.getContact());
                        pOfficerNumber.setText(policeOfficer.getOfficerNumber());
                    }
                } else {
                    showMessage("Officer not found with Officer Number: " + officerNumber);
                    // Clear UI fields
                    clearFields();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                showMessage("Database error: " + databaseError.getMessage());
            }
        });
    }

    private void updatePoliceOfficer() {
        String officerNumber = userIdInput.getText().toString().trim();

        DatabaseReference officerReference = FirebaseDatabase.getInstance().getReference("police_officers");
        officerReference.orderByChild("officerNumber").equalTo(officerNumber).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        PoliceOfficer policeOfficer = snapshot.getValue(PoliceOfficer.class);

                        // Update the fields of the policeOfficer object based on user input
                        policeOfficer.setName(pName.getText().toString().trim());
                        policeOfficer.setNic(pNIC.getText().toString().trim());
                        policeOfficer.setContact(pContact.getText().toString().trim());
                        policeOfficer.setOfficerNumber(pOfficerNumber.getText().toString().trim());

                        // Update the data in Firebase
                        snapshot.getRef().setValue(policeOfficer);
                        // Provide feedback to the user with a Toast message
                        showMessage("Police officer details updated successfully");

                        // Clear UI fields
                        clearFields();
                        Intent intent = new Intent(Police_Officers_Update_Register_Details.this, Admin_Home_page.class);
                        startActivity(intent);
                    }
                } else {
                    showMessage("Officer not found with Officer Number: " + officerNumber);

                    // Clear UI fields
                    clearFields();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                showMessage("Database error: " + databaseError.getMessage());
            }
        });
    }

    private void showMessage(String message) {
        // You can display the message in a TextView or a Toast, or use any other UI feedback mechanism
        // For example, displaying a Toast message:
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void clearFields() {
        pName.setText("");
        pNIC.setText("");
        pContact.setText("");
        pOfficerNumber.setText("");
    }
}
