package com.example.vfms.user;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.example.vfms.Image_Slider.ImageSliderAdapter;
import com.example.vfms.R;
import com.example.vfms.police_officer.FineData;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import android.os.Handler;
import android.os.Looper;

public class Home_Page extends AppCompatActivity {

    private TextView textViewUserName;
    private ImageButton profileImageView;
    private FirebaseAuth firebaseAuth;
    private BottomNavigationView bottomNavigationView;
    private RecyclerView recyclerView;
    private ImposeFineAdapter adapter;
    private DatabaseReference imposeFineRef;

    private ViewPager imageSlider;
    private ImageSliderAdapter sliderAdapter;
    private int currentPage = 0;
    private Timer timer;
    private final long DELAY_MS = 3000; // Delay in milliseconds before auto-advancing to the next slide
    private final long PERIOD_MS = 3000; // Time in milliseconds between auto-advancing slides

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        // Initialize ViewPager and adapter
        imageSlider = findViewById(R.id.image_slider);
        int[] imageResources = {R.drawable.img1, R.drawable.img2, R.drawable.img3}; // Replace with your image resources
        sliderAdapter = new ImageSliderAdapter(this, imageResources);
        imageSlider.setAdapter(sliderAdapter);

        // Start auto-scrolling timer
        startAutoSlider();

        // Initialize RecyclerView and adapter
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ImposeFineAdapter();
        recyclerView.setAdapter(adapter);

        // Initialize Firebase Database reference for "Impose Fine" node
        imposeFineRef = FirebaseDatabase.getInstance().getReference("Impose_Fine");

        Button profileButton1 = findViewById(R.id.Profile);
        profileButton1.setOnClickListener(view -> {
            startActivity(new Intent(Home_Page.this, Profile_page.class));
        });

        ImageButton profileButton = findViewById(R.id.profilePictureImageView);
        profileButton.setOnClickListener(view -> {
            startActivity(new Intent(Home_Page.this, Profile_page.class));
        });

        Button PayFine_Btn = findViewById(R.id.PayFine_Btn);
        PayFine_Btn.setOnClickListener(view -> {
            startActivity(new Intent(Home_Page.this, Fine_History.class));
        });

        Button openMapButton = findViewById(R.id.Nearest_location);
        openMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home_Page.this, MapActivity.class);
                startActivity(intent);
            }
        });

        ImageButton refreshButton = findViewById(R.id.refreshButton);
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Call a method to refresh the page
                refreshPage();
            }

            private void refreshPage() {
                adapter.notifyDataSetChanged();
                finish();
                startActivity(getIntent());
            }
        });

        Button LicenceButton = findViewById(R.id.LicenceButton);
        LicenceButton.setOnClickListener(view -> {
            startActivity(new Intent(Home_Page.this, User_licence_image_View.class));
        });

        textViewUserName = findViewById(R.id.ViewText);
        profileImageView = findViewById(R.id.profilePictureImageView);
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        if (currentUser != null) {
            String userId = currentUser.getUid();
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
            Query query = databaseReference.orderByChild("userId").equalTo(userId);

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                            String licenceNo = userSnapshot.child("licenceNo").getValue(String.class);
                            String username = userSnapshot.child("username").getValue(String.class);
                            String base64Image = userSnapshot.child("profileImageBase64").getValue(String.class);

                            if (base64Image != null && !base64Image.isEmpty()) {
                                byte[] imageBytes = Base64.decode(base64Image, Base64.DEFAULT);
                                Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                                profileImageView.setImageBitmap(bitmap);
                            }

                            textViewUserName.setText("Welcome, " + username);

                            TextView licenseExpirationDateTextView = findViewById(R.id.licence_date);
                            String licenseExpirationDate = userSnapshot.child("expireDate").getValue(String.class);

                            if (licenseExpirationDate != null && !licenseExpirationDate.isEmpty()) {
                                licenseExpirationDateTextView.setText(licenseExpirationDate);
                                calculateAndShowReminderDialog(licenseExpirationDate);
                            }

                            // Query the database for the most recent "Impose Fine" data
                            Query recentFineQuery = imposeFineRef.orderByChild("LicenseNumber").equalTo(licenceNo).limitToLast(1);
                            recentFineQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot fineDataSnapshot) {
                                    if (fineDataSnapshot.exists()) {
                                        // Get the most recent fine data
                                        for (DataSnapshot fineSnapshot : fineDataSnapshot.getChildren()) {
                                            FineData recentFine = fineSnapshot.getValue(FineData.class);

                                            // Update your RecyclerView adapter with the most recent offense
                                            adapter.addData(recentFine);
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    // Handle database error
                                    Toast.makeText(Home_Page.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(Home_Page.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }

        ImageButton logoutButton = findViewById(R.id.logoutBtn);
        logoutButton.setOnClickListener(view -> {
            showLogoutConfirmationDialog();
        });

        // Initialize BottomNavigationView
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.action_home) {
                    // Handle the Home action
                    // You can start a new activity or perform any other action here
                    return true;
                } else if (item.getItemId() == R.id.action_fine_history) {
                    // Handle the Fine History action
                    startActivity(new Intent(Home_Page.this, Fine_History.class));
                    finish();
                    return true;
                } else if (item.getItemId() == R.id.action_profile) {
                    // Handle the Profile action
                    startActivity(new Intent(Home_Page.this, Profile_page.class));
                    finish();
                    return true;
                }
                return false;
            }
        });
    }

    private void startAutoSlider() {
        final Handler handler = new Handler(Looper.getMainLooper());
        final Runnable update = new Runnable() {
            public void run() {
                if (currentPage == sliderAdapter.getCount()) {
                    currentPage = 0;
                }
                imageSlider.setCurrentItem(currentPage++, true);
            }
        };

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(update);
            }
        }, DELAY_MS, PERIOD_MS);
    }

    // ... (Rest of your existing code)

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.cancel();
        }
    }

    private void showLogoutConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Logout");
        builder.setMessage("Are you sure you want to log out?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
                logout();

            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void logout() {
        firebaseAuth.signOut();
        startActivity(new Intent(Home_Page.this, LoginPage.class));
        finish();
    }

    private void calculateAndShowReminderDialog(String licenseExpirationDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        try {
            Date expirationDate = dateFormat.parse(licenseExpirationDate);
            Calendar calendar = Calendar.getInstance();

            long differenceInMillis = expirationDate.getTime() - calendar.getTimeInMillis();
            long daysRemaining = TimeUnit.MILLISECONDS.toDays(differenceInMillis);

            showReminderDialog(daysRemaining);

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void showReminderDialog(long daysRemaining) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("License Expiration Reminder");

        TextView licenseExpirationDateTextView = findViewById(R.id.licence_date);

        if (daysRemaining > 0) {
            builder.setMessage("Your license will expire in " + daysRemaining + " days. Don't forget to renew it soon!");
        } else {
            builder.setMessage("Your license has expired before " + daysRemaining +  " days. Please renew it.");
            licenseExpirationDateTextView.setTextColor(getResources().getColor(android.R.color.holo_red_light));

            // Set the dialog box background color for the "else" part
            AlertDialog dialog = builder.create();
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.holo_red_light);

            // Remove OK and Cancel buttons for the "else" part
            dialog.setCancelable(true);

            dialog.show();
            return; // Return to avoid showing the dialog twice
        }

        // Remove OK and Cancel buttons for the "if" part
        builder.setCancelable(true);

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
