package com.example.vfms.user;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.vfms.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Advance_Profile_Page extends AppCompatActivity {

    private static final int REQUEST_IMAGE1 = 1;
    private static final int REQUEST_IMAGE2 = 2;
    private static final int DESIRED_IMAGE_WIDTH = 800;
    private static final int DESIRED_IMAGE_HEIGHT = 600;

    private ImageView imageView1, imageView2;
    private Button button1, button2, uploadButton1;
    private TextInputEditText etName;

    private DatabaseReference usersRef;
    private FirebaseUser currentUser;
    private StorageReference storageReference;

    private Bitmap selectedBitmap1;
    private Bitmap selectedBitmap2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advance_profile_page);

        imageView1 = findViewById(R.id.imageView1);
        imageView2 = findViewById(R.id.imageView2);
        button1 = findViewById(R.id.button1);
        button2 = findViewById(R.id.button2);
        uploadButton1 = findViewById(R.id.uploadButton1);
        etName = findViewById(R.id.etName);

        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference();

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, REQUEST_IMAGE1);
            }
        });
        ImageButton backButton = findViewById(R.id.backButton);
        // Set an OnClickListener to the ImageButton
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Implement code to go back to the previous page
//                onBackPressed();
                Intent intent = new Intent(Advance_Profile_Page.this, Profile_page.class);
                startActivity(intent);
                finish();
            }
        });


        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, REQUEST_IMAGE2);
            }
        });

        uploadButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPasswordVerificationDialog();
            }
        });
    }

    private void showPasswordVerificationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter Your Current Password");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final String password = input.getText().toString().trim();

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), password);
                    user.reauthenticate(credential)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        String expireDate = etName.getText().toString().trim();
                                        if (!expireDate.isEmpty() && selectedBitmap1 != null && selectedBitmap2 != null) {
                                            uploadImagesAndSaveExpireDateToFirebase(selectedBitmap1, selectedBitmap2, expireDate);
                                        } else {
                                            showToast("Please select both images and enter an expiration date");
                                        }
                                    } else {
                                        showToast("Incorrect password. Image saving canceled.");
                                    }
                                }
                            });
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void uploadImagesAndSaveExpireDateToFirebase(Bitmap bitmap1, Bitmap bitmap2, String expireDate) {
        String image1String = encodeBitmap(bitmap1);
        String image2String = encodeBitmap(bitmap2);

        DatabaseReference usersReference = FirebaseDatabase.getInstance().getReference("Users");

        Query query = usersReference.orderByChild("userId").equalTo(currentUser.getUid());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    DatabaseReference userRef = userSnapshot.getRef();

                    Map<String, Object> updates = new HashMap<>();
                    updates.put("Licence Front Side", image1String);
                    updates.put("Licence Back Side", image2String);
                    updates.put("expireDate", expireDate);

                    userRef.updateChildren(updates)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    showToast("Images and expire date uploaded to Firebase");
                                    openProfilePage();
                                } else {
                                    showToast("Error uploading data: " + task.getException().getMessage());
                                }
                            });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                showToast("Database Error: " + databaseError.getMessage());
            }
        });
    }

    private String encodeBitmap(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, baos); // You can adjust the compression quality (0-100) as needed
        byte[] byteArray = baos.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            Bitmap bitmap = null;

            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (requestCode == REQUEST_IMAGE1) {
                selectedBitmap1 = bitmap;
                imageView1.setImageBitmap(selectedBitmap1);
            } else if (requestCode == REQUEST_IMAGE2) {
                selectedBitmap2 = bitmap;
                imageView2.setImageBitmap(selectedBitmap2);
            }
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void openProfilePage() {
        Intent intent = new Intent(this, Profile_page.class);
        startActivity(intent);
        finish();
    }
}
