package com.example.vfms.police_officer;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vfms.R;
import com.example.vfms.user.Users;
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

public class Officer_Impose_Fine_History extends AppCompatActivity {

    RecyclerView recyclerViews;
    OfficerFineDataAdapter adapter;

    // Declare a global variable to store the user ID
    private String userId;

    DatabaseReference databaseReference;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_officer_impose_fine_history);

        // Add this code to set up the back button functionality
        ImageButton backButton = findViewById(R.id.Back_btn);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Implement code to go back to the previous page (Profile_page)
                onBackPressed();
            }
        });

        recyclerViews = findViewById(R.id.recyclerViews);
        adapter = new OfficerFineDataAdapter(new ArrayList<>());

        // Set the layout manager and adapter for the recyclerView
        recyclerViews.setLayoutManager(new LinearLayoutManager(this));
        recyclerViews.setAdapter(adapter);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            userId = user.getUid();

            Log.w("Police Officer ID: ", userId);

            // Initialize the databaseReference here
            databaseReference = FirebaseDatabase.getInstance().getReference("Impose_Fine");

            // Query fines based on the police officer's ID
            Query fineQuery = databaseReference.orderByChild("PolicemenId").equalTo(userId);

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
}
