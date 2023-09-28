package com.example.vfms.police_officer;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import com.example.vfms.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.List;

public class OfficerFineDataAdapter extends RecyclerView.Adapter<OfficerFineDataAdapter.FineViewHolder> {

    private List<FineData> fineDataList;
    private Context context;

    public OfficerFineDataAdapter(List<FineData> fineDataList) {
        this.fineDataList = fineDataList;
    }

    @NonNull
    @Override
    public FineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.officer_list_item_fine, parent, false);
        return new FineViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FineViewHolder holder, int position) {
        FineData fineData = fineDataList.get(position);

        // Bind data to the TextViews
        holder.textViewDriverName.setText("Driver Name: " + fineData.getDriverName());
        holder.textViewLicenseNumber.setText("License Number: " + fineData.getLicenseNumber());
        holder.textViewFineAmount.setText("Fine Amount: " + fineData.getFineAmount());
        holder.textViewNatureOfOffence.setText("Nature of Offence: " + fineData.getNatureOfOffence());
        holder.textViewDate.setText("Date: " + fineData.getDate());
        holder.textViewTime.setText("Time: " + fineData.getTime());
        holder.textViewAddress.setText("Address: " + fineData.getAddress());
        holder.textViewVehicleNumber.setText("Vehicle Number: " + fineData.getVehicleNumber());

        // Modify the onClick listener for the "Update" button
        holder.buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the "Update" button click here
                Intent intent = new Intent(context, Officer_Impose_fine_Update.class);

                // Pass the necessary details to the Officer_Impose_fine_Update activity
                intent.putExtra("fineId", fineData.getFineId()); // Add the Fine ID
                intent.putExtra("driverName", fineData.getDriverName());
                intent.putExtra("licenseNumber", fineData.getLicenseNumber());
                intent.putExtra("fineAmount", fineData.getFineAmount());
                intent.putExtra("natureOfOffence", fineData.getNatureOfOffence());
                intent.putExtra("date", fineData.getDate());
                intent.putExtra("time", fineData.getTime());
                intent.putExtra("address", fineData.getAddress());
                intent.putExtra("vehicleNumber", fineData.getVehicleNumber());

                // Start the activity to update the fine
                context.startActivity(intent);
            }
        });


        // Set a click listener for the "Delete" button
        holder.buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the "Delete" button click here
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                if (currentUser != null && fineData != null) {
                    String loggedInUserId = currentUser.getUid();
                    String fineOwnerId = fineData.getPolicemenId();

                    // Check if the logged-in user is authorized to delete the fine
                    if (loggedInUserId.equals(fineOwnerId)) {
                        // The logged-in user is authorized to delete the fine
                        showDeleteConfirmationDialog(fineData); // Show confirmation dialog
                    } else {
                        // The logged-in user is not authorized to delete this fine
                        showAlert("Authorization Error", "You are not authorized to delete this fine.");
                    }
                }
            }
        });
    }

    public void setData(List<FineData> newData) {
        fineDataList.clear();
        fineDataList.addAll(newData);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return fineDataList.size();
    }

    private void showDeleteConfirmationDialog(final FineData fineData) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Confirm Deletion");
        builder.setMessage("Are you sure you want to delete this fine?");

        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // User confirmed the deletion, proceed with deletion logic
                deleteFineFromDatabase(fineData);
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // User canceled the deletion, do nothing
                dialog.dismiss();
            }
        });

        builder.create().show();
    }

    private void deleteFineFromDatabase(FineData fineData) {
        DatabaseReference deleteRef = FirebaseDatabase.getInstance().getReference("Impose_Fine");
        String fineId = fineData.getFineId();

        if (fineId != null) {
            Log.d("Delete Fine", "Deleting fine with ID: " + fineId); // Log the fine ID
            DatabaseReference fineToDeleteRef = deleteRef.child(fineId);

            fineToDeleteRef.removeValue()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            showAlert("Deletion Success", "Fine deleted successfully");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            showAlert("Deletion Error", "Error deleting fine: " + e.getMessage());
                            Log.e("Deletion Error", "Error deleting fine: " + e.getMessage());
                        }
                    });
        }
    }

    private void showAlert(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }

    public class FineViewHolder extends RecyclerView.ViewHolder {
        TextView textViewDriverName;
        TextView textViewLicenseNumber;
        TextView textViewFineAmount;
        TextView textViewNatureOfOffence;
        TextView textViewDate;
        TextView textViewTime;
        TextView textViewAddress;
        TextView textViewVehicleNumber;
        Button buttonDelete,buttonUpdate;

        public FineViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewDriverName = itemView.findViewById(R.id.textViewDriverName);
            textViewLicenseNumber = itemView.findViewById(R.id.textViewLicenseNumber);
            textViewFineAmount = itemView.findViewById(R.id.textViewFineAmount);
            textViewNatureOfOffence = itemView.findViewById(R.id.textViewNatureOfOffence);
            textViewDate = itemView.findViewById(R.id.textViewDate);
            textViewTime = itemView.findViewById(R.id.textViewTime);
            textViewAddress = itemView.findViewById(R.id.textViewAddress);
            textViewVehicleNumber = itemView.findViewById(R.id.textViewVehicleNumber);
            buttonDelete = itemView.findViewById(R.id.Delete);
            buttonUpdate = itemView.findViewById(R.id.Update);
        }
    }
}
