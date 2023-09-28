package com.example.vfms.police_officer;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.vfms.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Police_Officers_Update_Register_Details extends AppCompatActivity {

    private EditText userIdInput;
    private TextInputEditText pName, pNIC, pContact, pRegEmail;
    private Button searchButton, updateButton;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_police_officers_update_register_details);

        userIdInput = findViewById(R.id.userIdInput); // Assuming you have a user ID input field
        pName = findViewById(R.id.pName);
        pNIC = findViewById(R.id.pNIC);
        pContact = findViewById(R.id.pContact);
        pRegEmail = findViewById(R.id.pRegEmail);
        searchButton = findViewById(R.id.searchButton);
        updateButton = findViewById(R.id.updateButton);

        // Initialize Firebase Database
        databaseReference = FirebaseDatabase.getInstance().getReference("PoliceOfficers");

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String userId = userIdInput.getText().toString();

                // Search for the police officer's Officer ID using User ID
                databaseReference.orderByChild("userId").equalTo(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                PoliceOfficer policeOfficer = snapshot.getValue(PoliceOfficer.class);
                                if (policeOfficer != null) {
                                    // Populate the EditText fields with the officer's information
                                    pName.setText(policeOfficer.getName());
                                    pNIC.setText(policeOfficer.getNic());
                                    pContact.setText(policeOfficer.getContact());
                                    pRegEmail.setText(policeOfficer.getEmail());
                                }
                            }
                        } else {
                            Toast.makeText(Police_Officers_Update_Register_Details.this, "Officer not found", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(Police_Officers_Update_Register_Details.this, "Database Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

//        updateButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Get updated information from EditText fields
//                String name = pName.getText().toString();
//                String nic = pNIC.getText().toString();
//                String contact = pContact.getText().toString();
////                String email = pRegEmail.getText().toString();
//
//                // Update the officer's information in Firebase
//                PoliceOfficer updatedOfficer = new PoliceOfficer(name, nic, contact);
//                // Assuming you have the Officer ID as a child in your database
//                databaseReference.child(updatedOfficer.getOfficerId()).setValue(updatedOfficer);
//
//                Toast.makeText(Police_Officers_Update_Register_Details.this, "Officer information updated", Toast.LENGTH_SHORT).show();
//            }
//        });
    }
}
