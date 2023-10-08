package com.example.vfms.admin;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vfms.R;
import com.example.vfms.police_officer.PoliceOfficer;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class RegisteredPoliceOfficerListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private OfficersRegisteredAdapter adapter;
    private List<PoliceOfficer> officerList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registered_police_officer_list);


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
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        officerList = new ArrayList<>();
        adapter = new OfficersRegisteredAdapter(officerList, this);
        recyclerView.setAdapter(adapter);

        // Initialize Firebase Database
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("police_officers");

        // Fetch data from Firebase
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                officerList.clear();
                for (DataSnapshot officerSnapshot : dataSnapshot.getChildren()) {
                    PoliceOfficer officer = officerSnapshot.getValue(PoliceOfficer.class);
                    officerList.add(officer);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(RegisteredPoliceOfficerListActivity.this, "Failed to fetch data", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
