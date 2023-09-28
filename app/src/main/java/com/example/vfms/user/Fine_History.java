package com.example.vfms.user;

import static android.service.controls.ControlsProviderService.TAG;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vfms.R;
import com.example.vfms.police_officer.FineData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Fine_History extends AppCompatActivity {

    RecyclerView recyclerView;
    FineDataAdapter adapter;

    // Declare a global variable to store the license number
    private String licenseNumber;


    DatabaseReference databaseReference;

    DatabaseReference userReference;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fine_history);



        // Add this code to set up the back button functionality
        ImageButton backButton = findViewById(R.id.Back_btn);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Implement code to go back to the previous page (Profile_page)
                onBackPressed();
            }
        });

        recyclerView = findViewById(R.id.recyclerView);
        adapter = new FineDataAdapter(new ArrayList<>());

        // Set the layout manager and adapter for the RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            String userId = user.getUid();

            Log.w("User ID: ", userId);

            // Initialize the databaseReference here
            databaseReference = FirebaseDatabase.getInstance().getReference("Impose_Fine");

            // Assuming 'userReference' is a DatabaseReference
            userReference = FirebaseDatabase.getInstance().getReference("Users");

            // Assuming 'userId' and 'license' are already defined

            Query query = userReference.orderByChild("userId").equalTo(userId);

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                            Users userProfile = userSnapshot.getValue(Users.class);
                            if (userProfile != null) {
                                licenseNumber = userProfile.getLicenceNo();
                                Log.w("License Number: ", licenseNumber);

                                // Query fines based on the license number
                                Query fineQuery = databaseReference.orderByChild("LicenseNumber").equalTo(licenseNumber);

                                fineQuery.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        // Initialize a list to store FineData objects
                                        List<FineData> fineDataList = new ArrayList<>();

                                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                            // Retrieve fine data as a FineData object
                                            FineData fineData = snapshot.getValue(FineData.class);

                                            if (fineData != null) {
                                                fineDataList.add(fineData);
                                            }
                                        }

                                        // Ensure that the adapter is initialized correctly before setting data
                                        if (adapter != null) {
                                            adapter.setData(fineDataList);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        // Handle errors here
                                        Log.e("FirebaseError", "onCancelled: " + databaseError.getMessage());
                                    }
                                });
                            }
                        }
                    } else {
                        // Handle the case where the user data doesn't exist
                        Log.e("UserDataError", "User data not found for UserID: " + userId);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle database error
                    if (databaseError != null) {
                        // Log the error message
                        Log.e("DatabaseError", "Error: " + databaseError.getMessage());

                        // Show a Toast message to the user
                        Toast.makeText(Fine_History.this, "Database Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

    }
}
