package com.example.vfms.user;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

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

    DatabaseReference databaseReference;

    DatabaseReference userReference;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fine_history);

        recyclerView = findViewById(R.id.recyclerView);
        adapter = new FineDataAdapter(new ArrayList<>());

        // Set the layout manager and adapter for the RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            String userId = user.getUid();

            // Initialize the databaseReference here
            databaseReference = FirebaseDatabase.getInstance().getReference("Impose_Fine");

            // Assuming 'userReference' is a DatabaseReference
            Query userQuery;
            userReference = FirebaseDatabase.getInstance().getReference("Users");

            userQuery = userReference.orderByChild("userId").equalTo(userId);

            // Add a ValueEventListener to retrieve user-specific data
            userQuery.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    // Initialize a list to store FineData objects
                    List<FineData> fineDataList = new ArrayList<>();

                    // Iterate through the results
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        // Retrieve the user-specific data as a FineData object
                        FineData fineData = userSnapshot.getValue(FineData.class);

                        if (fineData != null) {
                            // Retrieve the LicenseNumber from the FineData object
                            String license = fineData.getLicenseNumber();
                            // Now, you have the LicenseNumber in the 'license' variable
                            // You can use it as needed
//                            Log.d("LicenseNumber", license);

                            Query query = databaseReference.orderByChild("licenceNo").equalTo(license);

                            query.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    // Clear the list before adding new data
                                    fineDataList.clear();

                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                        FineData fineData = snapshot.getValue(FineData.class);
                                        fineDataList.add(fineData);
                                    }

                                    // Update the RecyclerView adapter with the new data
                                    adapter.setData(fineDataList);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    Log.d("FirebaseError", "onCancelled: Database Error");
                                }
                            });
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle errors here
                    Log.d("FirebaseError", "onCancelled: " + databaseError.getMessage());
                }
            });
        }
    }
}
