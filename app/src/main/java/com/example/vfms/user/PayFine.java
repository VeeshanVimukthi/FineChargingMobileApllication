package com.example.vfms.user;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.example.vfms.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;

public class PayFine extends AppCompatActivity {
    private DatabaseReference databaseReference;
    private TextInputEditText editTextCardNumber;
    private TextInputEditText editTextCardHolderName;
    private TextInputEditText editTextExpiryDate;
    private TextInputEditText editTextCCV;
    private TextInputLayout textInputLayoutCardNumber;
    private TextInputLayout textInputLayoutCardHolderName;
    private TextInputLayout textInputLayoutExpiryDate;
    private TextInputLayout textInputLayoutCCV;
    private Button postOfficeImageButton;
    private ImageView postOfficeImageView;
    private RadioButton cardPaymentRadioButton;
    private RadioButton postOfficePaymentRadioButton;
    private Bitmap capturedImage;  // To store the captured image

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_fine);

        // Initialize your UI elements
        editTextCardNumber = findViewById(R.id.editTextCardNumber);
        textInputLayoutCardNumber = findViewById(R.id.textInputLayoutCardNumber);
        editTextCardHolderName = findViewById(R.id.editTextCardHolderName);
        textInputLayoutCardHolderName = findViewById(R.id.textInputLayoutCardHolderName);
        editTextExpiryDate = findViewById(R.id.editTextExpiryDate);
        textInputLayoutExpiryDate = findViewById(R.id.textInputLayoutExpiryDate);
        editTextCCV = findViewById(R.id.editTextCCV);
        textInputLayoutCCV = findViewById(R.id.textInputLayoutCCV);
        postOfficeImageButton = findViewById(R.id.postOfficeImageButton);
        postOfficeImageView = findViewById(R.id.postOfficeImageView);
        cardPaymentRadioButton = findViewById(R.id.cardPaymentRadioButton);
        postOfficePaymentRadioButton = findViewById(R.id.postOfficePaymentRadioButton);

        // Set up the RadioGroup to handle payment type selection
        RadioGroup paymentTypeRadioGroup = findViewById(R.id.paymentTypeRadioGroup);
        paymentTypeRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // Handle payment type selection
                if (checkedId == R.id.cardPaymentRadioButton) {
                    // Show the card payment related elements
                    showCardPaymentElements();
                } else if (checkedId == R.id.postOfficePaymentRadioButton) {
                    // Show the post office payment related elements
                    showPostOfficePaymentElements();
                }
            }

            private void showCardPaymentElements() {
                // Show the elements related to card payment (e.g., card number, expiry date, CCV)
                editTextCardNumber.setVisibility(View.VISIBLE);
                textInputLayoutCardNumber.setVisibility(View.VISIBLE);
                editTextCardHolderName.setVisibility(View.VISIBLE);
                textInputLayoutCardHolderName.setVisibility(View.VISIBLE);
                editTextExpiryDate.setVisibility(View.VISIBLE);
                textInputLayoutExpiryDate.setVisibility(View.VISIBLE);
                editTextCCV.setVisibility(View.VISIBLE);
                textInputLayoutCCV.setVisibility(View.VISIBLE);
                postOfficeImageButton.setVisibility(View.GONE);
                postOfficeImageView.setVisibility(View.GONE);
            }

            private void showPostOfficePaymentElements() {
                // Show the elements related to post office payment (e.g., image capture)
                postOfficeImageButton.setVisibility(View.VISIBLE);
                postOfficeImageView.setVisibility(View.VISIBLE);
                editTextCardNumber.setVisibility(View.GONE);
                textInputLayoutCardNumber.setVisibility(View.GONE);
                editTextCardHolderName.setVisibility(View.GONE);
                textInputLayoutCardHolderName.setVisibility(View.GONE);
                editTextExpiryDate.setVisibility(View.GONE);
                textInputLayoutExpiryDate.setVisibility(View.GONE);
                editTextCCV.setVisibility(View.GONE);
                textInputLayoutCCV.setVisibility(View.GONE);
            }
        });

        // Retrieve fine details passed from the previous activity (assuming you've passed these details)
        String driverName = getIntent().getStringExtra("driverName");
        String licenseNumber = getIntent().getStringExtra("licenseNumber");
        String fineAmount = getIntent().getStringExtra("fineAmount");
        String date = getIntent().getStringExtra("date");
        String vehicleNumber = getIntent().getStringExtra("vehicleNumber");
        String fineId = getIntent().getStringExtra("fineId");

        // Initialize TextViews to display the details
        EditText textViewDriverName = findViewById(R.id.textViewDriverName);
        EditText textViewLicenseNumber = findViewById(R.id.textViewLicenseNumber);
        EditText textViewFineAmount = findViewById(R.id.ViewFineAmount);
        EditText textViewDate = findViewById(R.id.textViewDate);
        EditText textViewVehicleNumber = findViewById(R.id.editTextVehicleNumber);

        // Set the text of TextViews with the retrieved details
        textViewDriverName.setText("Driver Name: " + driverName);
        textViewLicenseNumber.setText("License Number: " + licenseNumber);
        textViewFineAmount.setText("Fine Amount: " + fineAmount);
        textViewDate.setText("Date: " + date);
        textViewVehicleNumber.setText("Vehicle Number: " + vehicleNumber);

        // Initialize the Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("Impose_Fine");

        // Set up the back button functionality
        ImageButton backButton = findViewById(R.id.Back_btn);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // Add a TextWatcher to automatically insert dashes in the card number field
        editTextCardNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Remove any previously added dashes to avoid duplication
                String cardNumberWithoutDashes = charSequence.toString().replace("-", "");

                // Add dashes after every four digits
                StringBuilder formattedCardNumber = new StringBuilder();
                for (int j = 0; j < cardNumberWithoutDashes.length(); j++) {
                    if (j > 0 && j % 4 == 0) {
                        formattedCardNumber.append("-");
                    }
                    formattedCardNumber.append(cardNumberWithoutDashes.charAt(j));
                }

                editTextCardNumber.removeTextChangedListener(this); // Prevent infinite loop
                editTextCardNumber.setText(formattedCardNumber.toString());
                editTextCardNumber.setSelection(formattedCardNumber.length()); // Move the cursor to the end
                editTextCardNumber.addTextChangedListener(this); // Restore TextWatcher
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        // Add a TextWatcher to automatically insert "/" in the expiry date field
        editTextExpiryDate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() == 2 && i2 == 1) {
                    // Automatically insert "/" after the first two characters (MM)
                    String newText = charSequence.toString() + "/";
                    editTextExpiryDate.setText(newText);
                    editTextExpiryDate.setSelection(newText.length()); // Move the cursor to the end
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        // Handle the image capture button
        Button captureImageButton = findViewById(R.id.postOfficeImageButton);
        captureImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Capture an image using the camera
                captureImage();
            }
        });

        // Handle the payment button
        Button buttonPay = findViewById(R.id.buttonPay);
        buttonPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Reset error messages
                textInputLayoutCardNumber.setError(null);
                textInputLayoutExpiryDate.setError(null);
                textInputLayoutCCV.setError(null);

                // Validate card number, expiry date, and CCV
                String cardNumber = editTextCardNumber.getText().toString().trim();
                String expiryDate = editTextExpiryDate.getText().toString().trim();
                String ccv = editTextCCV.getText().toString().trim();

                if (cardPaymentRadioButton.isChecked()) {
                    // Card payment selected
                    if (cardNumber.isEmpty() || cardNumber.length() != 19) {
                        // Set an error message for invalid card number
                        textInputLayoutCardNumber.setError("Invalid card number");
                    } else if (expiryDate.isEmpty() || !isValidExpiryDate(expiryDate)) {
                        // Set an error message for invalid expiry date
                        textInputLayoutExpiryDate.setError("Invalid expiry date (use MM/YY)");
                    } else if (ccv.isEmpty() || ccv.length() != 3) {
                        // Set an error message for invalid CCV
                        textInputLayoutCCV.setError("Invalid CCV");
                    } else {
                        // Payment is valid, set the payment status to true (paid)
                        savePaymentStatusToFirebase(fineId, true);
                        // You can also add additional logic for payment processing here
                        // For example, call a payment gateway API to process the payment
                        // Show a success message to the user

                        Toast.makeText(PayFine.this, "Payment Successful", Toast.LENGTH_SHORT).show();
                        // Finish the activity or navigate to a success page as needed
                        finish();
                    }
                } else if (postOfficePaymentRadioButton.isChecked()) {
                    // Post office payment selected
                    if (capturedImage != null) {
                        // Convert the captured image to base64
                        String imageBase64 = captureAndConvertImageToBase64(capturedImage);

                        if (imageBase64 != null) {
                            // Payment is valid, set the payment status to true (paid)
                            savePaymentStatusAndImageToFirebase(fineId, true, imageBase64);
                            // Show a success message to the user
                            Toast.makeText(PayFine.this, "Payment Successful", Toast.LENGTH_SHORT).show();
                            // Finish the activity or navigate to a success page as needed
                            finish();
                        } else {
                            // Handle image capture or conversion error
                            Toast.makeText(PayFine.this, "Image capture or conversion error", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // No image captured, show an error message
                        Toast.makeText(PayFine.this, "Please capture an image", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    // Function to validate the expiry date
    private boolean isValidExpiryDate(String expiryDate) {
        // Check if the expiry date is in MM/YY format
        if (expiryDate != null && expiryDate.matches("\\d{2}/\\d{2}")) {
            // Split the date into month and year parts
            String[] parts = expiryDate.split("/");
            if (parts.length == 2) {
                try {
                    int month = Integer.parseInt(parts[0]);
                    int year = Integer.parseInt(parts[1]);

                    // Check if it's a future date (current year is 21, future years should be 22, 23, etc.)
                    int currentYear = Calendar.getInstance().get(Calendar.YEAR) % 100; // Get the last two digits of the current year

                    if (year > currentYear || (year == currentYear && month >= Calendar.getInstance().get(Calendar.MONTH) + 1)) {
                        return true; // Valid expiry date
                    }
                } catch (NumberFormatException e) {
                    // Parsing error, invalid expiry date format
                }
            }
        }

        return false; // Invalid expiry date
    }

    // Function to capture an image using the camera
    private void captureImage() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    // Constants
    private static final int REQUEST_IMAGE_CAPTURE = 1;

    // Handle the result of the image capture
    // Handle the result of the image capture
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data); // Call super.onActivityResult

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            // Get the captured image as a Bitmap
            Bundle extras = data.getExtras();
            capturedImage = (Bitmap) extras.get("data");

            // Update the ImageView to display the captured image
            postOfficeImageView.setImageBitmap(capturedImage);
        }
    }

    // Function to convert the image to base64
    private String captureAndConvertImageToBase64(Bitmap imageBitmap) {
        if (imageBitmap != null) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            return Base64.encodeToString(byteArray, Base64.DEFAULT);
        }
        return null;
    }

    // Function to save the payment status to Firebase
    private void savePaymentStatusToFirebase(String fineId, boolean paymentStatus) {
        DatabaseReference paymentRef = databaseReference.child(fineId);
        paymentRef.child("paymentStatus").setValue(paymentStatus);
    }

    // Function to save the payment status and image to Firebase
    private void savePaymentStatusAndImageToFirebase(String fineId, boolean paymentStatus, String imageBase64) {
        DatabaseReference paymentRef = databaseReference.child(fineId);
        paymentRef.child("paymentStatus").setValue(paymentStatus);
        paymentRef.child("imageBase64").setValue(imageBase64);
    }
}
