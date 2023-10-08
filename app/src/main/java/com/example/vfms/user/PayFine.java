package com.example.vfms.user;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.vfms.R;
import com.example.vfms.police_officer.FineData;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class PayFine extends AppCompatActivity {
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_fine);

        // Retrieve fine details passed from the previous activity
        String driverName = getIntent().getStringExtra("driverName");
        String licenseNumber = getIntent().getStringExtra("licenseNumber");
        String fineAmount = getIntent().getStringExtra("fineAmount");
        String date = getIntent().getStringExtra("date");
        String vehicleNumber = getIntent().getStringExtra("vehicleNumber");
        String fineId = getIntent().getStringExtra("fineId"); // Add this line to retrieve the FineId

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

        // Get references to your payment views and their TextInputLayouts
        TextInputEditText editTextCardNumber = findViewById(R.id.editTextCardNumber);
        TextInputEditText editTextExpiryDate = findViewById(R.id.editTextExpiryDate);
        TextInputEditText editTextCCV = findViewById(R.id.editTextCCV);
        TextInputLayout textInputLayoutCardNumber = findViewById(R.id.textInputLayoutCardNumber);
        TextInputLayout textInputLayoutExpiryDate = findViewById(R.id.textInputLayoutExpiryDate);
        TextInputLayout textInputLayoutCCV = findViewById(R.id.textInputLayoutCCV);

        // Add this code to set up the back button functionality
        ImageButton backButton = findViewById(R.id.Back_btn);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Implement code to go back to the previous page (Profile_page)
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

        // Initialize the Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("Impose_Fine");

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
            }
        });
    }

    // Function to validate the expiry date (you can customize this)
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

    // Function to save the payment status to Firebase
    private void savePaymentStatusToFirebase(String fineId, boolean paymentStatus) {
        // Create a new payment node under the fine_payments node with the FineId as the key
        DatabaseReference paymentRef = databaseReference.child(fineId);

        // Save the payment status as a Boolean value
        paymentRef.child("paymentStatus").setValue(paymentStatus);
    }
}
